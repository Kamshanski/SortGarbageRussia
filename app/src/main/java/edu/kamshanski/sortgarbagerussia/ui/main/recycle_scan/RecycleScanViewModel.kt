package edu.kamshanski.sortgarbagerussia.ui.main.recycle_scan

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import edu.kamshanski.sortgarbagerussia.model.constants.BarcodeType
import edu.kamshanski.sortgarbagerussia.model.coreentities.ProductCode
import edu.kamshanski.sortgarbagerussia.model.coreentities.RecycleOffer
import edu.kamshanski.sortgarbagerussia.model.localDatabase.entities.RecycleOfferDbRecord
import edu.kamshanski.sortgarbagerussia.model.utils.wrappers.Failure
import edu.kamshanski.sortgarbagerussia.model.utils.wrappers.InProgress
import edu.kamshanski.sortgarbagerussia.model.utils.wrappers.Success
import edu.kamshanski.sortgarbagerussia.ui.App
import edu.kamshanski.sortgarbagerussia.ui.main.recycle_scan.ShowInfoUi.ContentHolder.OfferHolder
import edu.kamshanski.sortgarbagerussia.ui.main.recycle_scan.ShowInfoUi.ContentHolder.RecycleHolder
import edu.kamshanski.sortgarbagerussia.ui.utils.UiStateException
import edu.kamshanski.sortgarbagerussia.utils.collections.CircularFifoQueue
import edu.kamshanski.sortgarbagerussia.utils.coroutines.launchOneShot
import edu.kamshanski.sortgarbagerussia.utils.coroutines.oneShot
import edu.kamshanski.sortgarbagerussia.utils.decodeRecycleTimestamp
import edu.kamshanski.tpuclassschedule.activities._abstract.BaseViewModel
import edu.kamshanski.tpuclassschedule.utils.collections.append
import edu.kamshanski.tpuclassschedule.utils.lg
import io.objectbox.reactive.DataObserver
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.contracts.ExperimentalContracts

@ExperimentalCoroutinesApi
@ExperimentalContracts
class RecycleScanViewModel(app: Application) : BaseViewModel(app) {
    private val repository = (app as App).repository

    private val _uiState = MutableStateFlow<RecycleScanUiState>(ScanUi())
    val uiState = _uiState as StateFlow<RecycleScanUiState>
    val currentState get() = uiState.value
    // BarcodeBuffer will not be appended if true, so scanning is stopped
    private var isLoading: Boolean = false
    // Validation buffer for anomaly scan prevention
    private val barcodeBuffer = CircularFifoQueue<String>(40)
    // Local db subscription to offering box
    var offerSubscription : DataObserver<RecycleOfferDbRecord>? = null
    // recycle records loading coroutine
    private var recyclerInfoByProductCodeJob: Job? = null
    // last selected shown record or offer Id or null. Set
    private var lastRecordId: String? = null

    // todo: set by preferences
    val scannerOptions = buildBarcodeScannerOptions(DEFAULT_BARCODE_OPTIONS.map { it.mlKitFormat })

    fun exception(stateException: UiStateException) {
        lg(stateException.reason)
        viewModelScope.oneShot {
            _uiState.emit(ExceptionUi(stateException))
        }
    }

    private fun buildBarcodeScannerOptions(barcodes: List<Int>) : BarcodeScannerOptions {
        val builder = BarcodeScannerOptions.Builder()
         if (barcodes.size == 1) {
            builder.setBarcodeFormats(barcodes[0])
        } else if (barcodes.size > 1) {
            builder.setBarcodeFormats(barcodes[0], *barcodes.subList(1, barcodes.size).toIntArray())
        }
        return builder.build()
    }

    /** @return true if barcode was read and decrypted successfully */
    fun postScannedBarcode(barcode: Barcode) {
        if (!isLoading) {
            // stop previous collecting
            if (recyclerInfoByProductCodeJob != null && !recyclerInfoByProductCodeJob!!.isCancelled)
                recyclerInfoByProductCodeJob?.cancel()
            // put new portion of scanned data
            val code = barcode.rawValue!!
            barcodeBuffer.add(code)
            // check data consistency
            val equalSeries = barcodeBuffer.all { it == code }
            offerSubscription = null
            if (equalSeries) {
                val finalCode = code.trim()
                val product = ProductCode(finalCode, BarcodeType.byMlKit(barcode.format))
                getRecycleInfo(product)
                barcodeBuffer.clear()
            }
        }
    }

    private fun getRecycleInfo(productCode: ProductCode) {
        recyclerInfoByProductCodeJob = viewModelScope.launch {
            isLoading = true
            _uiState.emit(LoadingUi(productCode))

            try {
                repository.getRecycleInfo(productCode).collect { progress ->
                    isLoading = false
                    when(progress) {
                        is InProgress -> return@collect
                        is Failure -> throw progress.error?: UnknownError(progress.toString())
                        is Success -> progress.value?.let { response ->
                            val offer = response.offer
                            val mayMatch = response.fullMatch.sortedByDescending { it.popularity }
                            val related = response.partialMatch

                            val mostPopular = mayMatch.firstOrNull() ?: throw IllegalStateException("Server cannot return empty list. But it is.")

                            val currentContent = if (offer == null
                                    || mostPopular.time > decodeRecycleTimestamp(offer.time)
                            ) {
                                RecycleHolder(mostPopular.asRecycle, mostPopular, false) // todo добавтб проверку bookmarked
                            } else {
                                OfferHolder(offer.asRecycle, offer)
                            }

                            _uiState.emit(ShowInfoUi(
                                    productCode,
                                    mayMatch,
                                    related,
                                    offer,
                                    currentContent
                            ))
                        }
                    }
                }
            } catch (ex: Throwable) {
                if (ex !is CancellationException) {
                    ex.printStackTrace()
                    val stateException = UiStateException.LOADING("${ex::class.simpleName} : ${ex.message}") // TODO: проанализировать ошибки и раскидать их
                    _uiState.emit(ExceptionUi(stateException))
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun toggleScanningState() = launchOneShot {
        val state = _uiState.value
        val newValue = if (state is ScanUi) IdleUi() else ScanUi()
        lastRecordId = null
        _uiState.emit(newValue)
    }

    fun currentRecycle(): RecycleOffer {
        return _uiState.value.let { state ->
            if (state is ShowInfoUi) {
                state.showContent.recycle.let {
                    val r = RecycleOffer(
                            globalId = it.globalId,
                            name = it.name,
                            barcode =
                                if (it.barcode.isNotBlank()) it.barcode
                                else state.productCode.barcode,
                            barcodeType =
                                if (it.barcodeType != BarcodeType.UNKNOWN) it.barcodeType
                                else state.productCode.barcodeType,
                            barcodeInfo = it.barcodeInfo,
                            productInfo = it.productInfo,
                            utilizeInfo = it.utilizeInfo,
                            imagePath = "")
                    return r
                }
            }
            else throw IllegalStateException("currentRecycle() cannot be called if uiState is not ShowInfoUi")
        }
    }

    fun selectId(id: String) = viewModelScope.launch {
        _uiState.value.let { state ->
            if (state is ShowInfoUi) {
                var record = state.mayMatch.firstOrNull { it.globalId == id }
                if (record == null) {
                    record = state.related.firstOrNull { it.globalId == id }
                }
                
                if (record != null) {
                    _uiState.emit(
                            ShowInfoUi(
                                    state.productCode,
                                    state.mayMatch,
                                    state.related,
                                    state.offer,
                                    RecycleHolder(record.asRecycle, record)
                            )
                    )
                } else {
                    val offer = state.offer
                    if (offer != null && offer.offerId == id) {
                        _uiState.emit(
                                ShowInfoUi(
                                        state.productCode,
                                        state.mayMatch,
                                        state.related,
                                        state.offer,
                                        OfferHolder(offer.asRecycle, offer) 
                                )
                        )
                    } else throw IllegalStateException("Id $id must point to offer, fullMatch or partialMatch item")
                }

            }
            else throw IllegalStateException("selectId($id) cannot be called if uiState is not ShowInfoUi")
        }
    }

    class StringLog(private val list: CircularFifoQueue<String>,
                    private val prefix: String? = null,
                    private val emptyMessage: String? = null) {
        val text : String
            get() {
                if (list.isEmpty() && emptyMessage != null) {
                    return emptyMessage
                } else {
                    return StringBuilder().let {
                        if (prefix != null) {
                            it.appendLine(prefix)
                        }
                        for (i in list) {
                            it.appendLine(i)
                        }
                        it.toString()
                    }
                }
            }

        private fun clear() {
            list.clear()
        }

        fun log(msg: String) {
            list.add(msg)
        }
    }

    companion object {
        val DEFAULT_BARCODE_OPTIONS
            get() = ArrayList<BarcodeType>().append(
                BarcodeType.UPC_A,
                BarcodeType.UPC_E,
                BarcodeType.EAN_8,
                BarcodeType.EAN_13,
            )
        enum class RecycleUiState {
            SCAN, LOADING, IDLE, LOADING_COMPLETED, EXCEPTION
        }
    }
}