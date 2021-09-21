package edu.kamshanski.sortgarbagerussia.ui.main.recycle_scan

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.View
import android.view.View.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import edu.kamshanski.sortgarbagerussia.R
import edu.kamshanski.sortgarbagerussia.databinding.FragmentRecycleScanBinding
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities.RecycleApiRecord
import edu.kamshanski.sortgarbagerussia.model.localDatabase.ObjectBox
import edu.kamshanski.sortgarbagerussia.model.coreentities.ProductCode
import edu.kamshanski.sortgarbagerussia.model.coreentities.Recycle
import edu.kamshanski.sortgarbagerussia.model.localDatabase.entities.RecycleDbRecord
import edu.kamshanski.sortgarbagerussia.model.localDatabase.entities.RecycleOfferDbRecord
import edu.kamshanski.sortgarbagerussia.model.utils.BarcodeEncoder
import edu.kamshanski.sortgarbagerussia.ui.main.recycle_scan.RecycleScanViewModel.Companion.RecycleUiState.*
import edu.kamshanski.sortgarbagerussia.ui.main.recycle_scan.ShowInfoUi.ContentHolder.OfferHolder
import edu.kamshanski.sortgarbagerussia.ui.main.recycle_scan.ShowInfoUi.ContentHolder.RecycleHolder
import edu.kamshanski.sortgarbagerussia.ui.utils.*
import edu.kamshanski.sortgarbagerussia.utils.nice_classes.letEvery
import edu.kamshanski.sortgarbagerussia.utils.nice_classes.multi
import edu.kamshanski.tpuclassschedule.activities._abstract.BaseFragment
import edu.kamshanski.tpuclassschedule.utils.collections.forEachNonNull
import edu.kamshanski.tpuclassschedule.utils.collections.printAll
import edu.kamshanski.tpuclassschedule.utils.lg
import edu.kamshanski.tpuclassschedule.utils.nice_classes.x
import edu.kamshanski.tpuclassschedule.utils.primitives.toInt
import edu.kamshanski.tpuclassschedule.utils.startCommon
import edu.kamshanski.tpuclassschedule.utils.stopCommon
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.contracts.ExperimentalContracts

@ExperimentalCoroutinesApi
@ExperimentalContracts
class RecycleScanFragment: BaseFragment() {
    lateinit var binding: FragmentRecycleScanBinding
    private val vm: RecycleScanViewModel by viewModels()
    lateinit var cameraExecutor: ExecutorService
    var cameraTask: ListenableFuture<ProcessCameraProvider?>? = null
    var presentedProductCode: ProductCode? = null


    private val permissionsRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.all { it.value }) {
            startCamera()
            vm.toggleScanningState()
        }
    }

    override fun initFragment() = repeat {
        vm.uiState.collect {
            when (it) {
                is ScanUi -> setScanningUi(it)
                is IdleUi -> setIdleUi(it)
                is LoadingUi -> setLoadingUi(it)
                is ShowInfoUi -> setLoadingCompletedUi(it)
                is ExceptionUi -> setExceptionUi(it)
            }
        }
    }

    override fun initViews() = with(binding) {
        txScannerLog.background = getDrawableColor(R.color.white)
    }

    override fun initListeners() = with(binding) {
        btnCamera.setOnClickListener {
            val state = vm.currentState
            if (state !is ExceptionUi) {
                vm.toggleScanningState()
            } else {
                when (state.uiStateException) {
                    is UiStateException.PERMISSION -> requestPermissions()
                    else -> vm.toggleScanningState() // TODO: вообще надо как-то реагировать
                }
            }
        }

        btmDebug2.setOnClickListener {
            ObjectBox.tempWebStore.runInReadTx {
                lg("RecycleInfoOffer in tempWebStore:")
                ObjectBox.tempWebStore.boxFor(RecycleOfferDbRecord::class.java).all.printAll(::lg)
                lg("RecycleInfo in tempWebStore:")
                ObjectBox.tempWebStore.boxFor(RecycleDbRecord::class.java).all.printAll(::lg)
            }

            ObjectBox.mainStore.runInReadTx {
                lg("RecycleInfoOffer in mainStore:")
                ObjectBox.mainStore.boxFor(RecycleOfferDbRecord::class.java).all.printAll(::lg)
                lg("RecycleInfo in mainStore:")
                ObjectBox.mainStore.boxFor(RecycleDbRecord::class.java).all.printAll(::lg)
            }
        }

        btmDebug2.setOnLongClickListener {
            ObjectBox.mainStore.removeAllObjects()
            ObjectBox.tempWebStore.removeAllObjects()
            lg("ObjectBox was cleared")
            return@setOnLongClickListener true
        }
    }

    private fun setScanningUi(uiState: ScanUi) {
        setUi(
                doScanning = true,
                scanBtnText = "Остановить",
        )
    }

    private fun setIdleUi(uiState: IdleUi) {
        setUi()
    }

    private fun setLoadingUi(uiState: LoadingUi) {
        setUi(
                productCode = uiState.productCode,
                isScanBtnClickable = false,
                title = "Загрука...",
        )
    }

    private fun setLoadingCompletedUi(uiState: ShowInfoUi) {
        val productCode = uiState.productCode
        val content = uiState.showContent
        // TODO: ПРОДОЛЖИТЬ. Удалять предложения, если их дата меньше даты Recycle с сервера
        // TODO: Repository не сохранять при ошибке сервера
        // TODO: сделать возможность fullMatch быть пустым = Empty

        when (content) {
            is OfferHolder -> setOfferingUi(content, uiState)
            is RecycleHolder -> {
                val record = content.recycle
                when {
                    record.isEmpty -> setEmptyUi(uiState)
                    record.isAssumption -> setAssumptionUi(content, uiState)
                    record.isFullRecord -> setRecordUi(content, uiState)
                }
            }
        }
    }

    private fun setOfferingUi(content: OfferHolder, uiState: ShowInfoUi) {
        val info = content.offer
        val productCode = uiState.productCode
        val title = SpannableStringBuilder().apply {
            append("Вы недавно предложили: ")
            appendSpan(info.name, StyleSpan(Typeface.ITALIC))
        }
        setUi(
                productCode = productCode,
                title = title,
                upperText = info.productInfo,
                lowerText = info.utilizeInfo,
                imageResId = R.drawable.ic_satisfied,
                reportListener = offerReportListener,
                reportBtnText = "Изменить",
                offerDbRecord = null, // offer must not be a selected item!!!
                mayMatchList = uiState.mayMatch,
                relatedList = uiState.related,
        )
    }
    private fun setEmptyUi(uiState: ShowInfoUi) {
        setUi(
                productCode = uiState.productCode,
                title = "Ничего не найдено. Совсем(",
                lowerText = "Вы можете помочь другим, подсказав, как нужно утилизировть этот " +
                        "продукт. Отправьте отсканированный код нам и мы внесём его в базу. " +
                        "Если вы знаете, как утилизировать этот продукт, вы можете написать нам, " +
                        "чтобы запрос был рассмотрен быстрее ",
                imageResId = R.drawable.ic_very_dissatisfied,
                reportListener = offerReportListener,
                reportBtnText = "Предложить",
                relatedList = uiState.related,
        )
    }
    private fun setAssumptionUi(content: RecycleHolder, uiState: ShowInfoUi) {
        val info = content.record
        val title = SpannableStringBuilder().apply {
            append("Кажется это ")
            appendSpan(info.name, StyleSpan(Typeface.ITALIC))
        }
        setUi(
                productCode = uiState.productCode,
                title = title,
                upperText = info.productInfo,
                lowerText = info.utilizeInfo,
                imageResId = R.drawable.ic_dissatisfied,
                reportListener = offerReportListener,
                reportBtnText = "Предложить",
                offerDbRecord = uiState.offer,
                relatedList = uiState.related,
        )
    }
    private fun setRecordUi(content: RecycleHolder, uiState: ShowInfoUi) {
        val info = content.record
        setUi(
                productCode = uiState.productCode,
                title = info.name,
                upperText = info.productInfo,
                lowerText = info.utilizeInfo,
                imageResId = R.drawable.ic_satisfied,
                reportListener = offerReportListener,
                reportBtnText = "Изменить",
                bookmarkBtnText = "В закладки",
                offerDbRecord = uiState.offer,
                mayMatchList = uiState.mayMatch.filter { it.globalId != content.record.globalId },
                relatedList = uiState.related.filter { it.globalId != content.record.globalId },
        )
    }

    private fun setExceptionUi(uiState: ExceptionUi) {
        val exception = uiState.uiStateException
        when(exception) {
            is UiStateException.PERMISSION -> multi(
                    "Кажется вы не предоставили права приложения. Запустите сканирование снова и " +
                            "предоставьте права, когда вас попросят.",
                    "Предоставить")
            is UiStateException.LOADING -> multi(
                    "Ошибка при скачивании. Проверьте подключение " +
                            "к интеренту или попробуйте позже.",
                    "Пожаловаться")
            is UiStateException.CONNECTION -> multi(
                    "Подключение к интернету отсутствует",
                    "Пожаловаться")
            is UiStateException.UNPREDICTED -> multi(
                    "Непредвиденная ошибка: ${exception.message}",
                    "Пожаловаться")
        }.letEvery { title, reportBtnText ->
            setUi(
                    title = title,
                    lowerText ="An error while ${exception.reason}",
                    scanBtnText = "Скан",
                    reportListener = sendExceptionReportListener,
                    reportBtnText = reportBtnText,
                    isException = true
            )
        }

    }

    private fun setUi(
            doScanning: Boolean = false,
            productCode: ProductCode? = null,
            isScanBtnClickable: Boolean = true,
            scanBtnText: String = "Скан",
            title: CharSequence? = null,
            upperText: CharSequence? = null,
            lowerText: CharSequence? = null,
            imageResId: Int? = null,
            reportBtnText: String? = null,   // null is gone view, visible otherwise
            reportListener: View.OnClickListener? = null,
            bookmarkBtnText: String? = null, // null is gone view, visible otherwise
            bookmarkListener: View.OnClickListener? = null,
            offerDbRecord: RecycleOfferDbRecord? = null, // offer must not be a selected item!!!
            mayMatchList: List<RecycleApiRecord> = emptyList(), // list must not contain selected item!!!
            relatedList: List<RecycleApiRecord> = emptyList(), // list must not contain selected item!!!
            isException: Boolean = false
    ) = with (binding) {
        if (doScanning) {
            tryToStartCamera()
            cameraBarcodePreview.foreground = null
            cameraBarcodePreview.foreground = null
            txScannerLog.text = ""
        } else {
            stopCamera()
            // Unnecessary expensive bitmap building if foreground is already set
            when {
                isException -> {
                    cameraBarcodePreview.foreground = ColorDrawable(resources.getColor(R.color.white, requireContext().theme))
                    txScannerLog.visibility = GONE
                    txScannerLog.text = null
                    presentedProductCode = null
                }
                productCode != presentedProductCode || cameraBarcodePreview.foreground == null -> {
                    if (productCode == null) {// scanning, idle
                        cameraBarcodePreview.foreground = null
                        txScannerLog.visibility = GONE
                        txScannerLog.text = null
                    } else {
                        cameraBarcodePreview.post {     // размер контейнера не всегда готов к моменту создания штрихкода, поэтоу откложить создание
                            val bitmap = BarcodeEncoder.encode(
                            productCode.barcode,
                            productCode.barcodeType,
                            cameraBarcodePreview.width,
                            cameraBarcodePreview.height)

                            cameraBarcodePreview.foreground = BitmapDrawable(resources, bitmap)

                            txScannerLog.text = "${productCode.barcode} (${productCode.barcodeType})"
                            txScannerLog.visibility = VISIBLE
                        }
                    }
                    presentedProductCode = productCode
                }
            }
        }

        btnCamera.isClickable = isScanBtnClickable
        btnCamera.text = scanBtnText

        txTitle.visibleIf(title != null) {
            txTitle.text = title
        }

        txUpperContent.visibleIf(upperText != null) {
            txUpperContent.text = upperText
        }

        txLowerContent.visibleIf(lowerText != null) {
            txLowerContent.text = lowerText
        }

        imgIcon.visibleIf(imageResId != null && imageResId != 0) {
            imgIcon.setImageDrawable(getDrawable(imageResId!!))
        }

        when(reportBtnText) {
            null -> multi(false,       GONE,       null)
            else -> multi(true,        VISIBLE,    reportBtnText)
        }.letEvery {      isClickable, visibility, text  ->
            btnReportOffering.isClickable = isClickable
            btnReportOffering.visibility = visibility
            btnReportOffering.text = text
            btnReportOffering.setOnClickListener(reportListener)
        }

        when(bookmarkBtnText) {
            null ->  multi(false,       GONE,       null)
            else ->  multi(true,        VISIBLE,    bookmarkBtnText)
        }.letEvery {       isClickable, visibility, text ->
            btnBookmark.isClickable = isClickable
            btnBookmark.visibility = visibility
            btnBookmark.text = text
            btnBookmark.setOnClickListener(bookmarkListener)
        }

        llMayMatch.removeAllViews()
        if (mayMatchList.isNotEmpty() || offerDbRecord != null) {
            llMayMatch.visibility = VISIBLE
            txMayMatchLabel.visibility = VISIBLE

            if (offerDbRecord != null) {
                llMayMatch.addView(createVariantRecycleTextView(offerDbRecord.name, offerDbRecord.offerId, true))
            }

            for (record in mayMatchList) {
                llMayMatch.addView(createVariantRecycleTextView(record.name, record.globalId))
            }
        } else {
            llMayMatch.visibility = GONE
            txMayMatchLabel.visibility = GONE
        }

        llRelated.removeAllViews()
        if (relatedList.isNotEmpty()) {
            llRelated.visibility = VISIBLE
            txRelatedLabel.visibility = VISIBLE

            for (record in mayMatchList) {
                llRelated.addView(createVariantRecycleTextView(record.name, record.globalId))
            }
        } else {
            llRelated.removeAllViews()
            llRelated.visibility = GONE
            txRelatedLabel.visibility = GONE
        }
    }

    private fun createVariantRecycleTextView(title: String, id: String, isOffer: Boolean = false) : TextView {
        return TextView(context).apply {
            text = title
            val color = if (isOffer) R.color.yellow else R.color.light_gray
            setBackgroundColor(getColor(color))
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(fromDp(78), fromDp(110)).also {
                it.rightMargin = fromDp(7)
            }
            setOnClickListener {
                vm.selectId(id)
            }
        }
    }

    val sendExceptionReportListener = View.OnClickListener {
        lg("TODO") // TODO
    }

    val offerReportListener = View.OnClickListener {
        if (vm.currentState is ShowInfoUi) {
            val sampleOffer = vm.currentRecycle()

            findNavController().navigate(RecycleScanFragmentDirections
                    .actionRecycleScanFragmentToOfferEditDialog((it as Button).text.toString(), sampleOffer))
        }
    }

    private fun tryToStartCamera() {
        val context = requireContext()
        val allPermissionsGranted = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (allPermissionsGranted) {
            startCamera()
        } else {
            vm.exception(UiStateException.PERMISSION(REQUIRED_PERMISSIONS))
        }
    }


    private fun startCamera() {
        if (!this::cameraExecutor.isInitialized) {
            cameraExecutor = Executors.newSingleThreadExecutor()
        }
        // Used to bind the lifecycle of cameras to the lifecycle owner
        cameraTask = ProcessCameraProvider.getInstance(requireContext())

        cameraTask!!.addListener( Runnable {
            val cameraTask = this.cameraTask ?: return@Runnable
            val cameraProvider = cameraTask.get() ?: return@Runnable

            // Если сканирование отменено, а runable запущен, его стоит остановить
            if (vm.currentState !is ScanUi) {
                cameraProvider.unbindAll()
                return@Runnable
            }

            val preview = Preview.Builder()
                .build()
                .apply {
                    setSurfaceProvider(cameraExecutor, binding.cameraBarcodePreview.surfaceProvider)
                }

            val imageAnalyser = ImageAnalysis.Builder()
                .setTargetResolution(640 x 480)
                .build()
            imageAnalyser.setAnalyzer(
                cameraExecutor,
                BarcodeAnalyser(vm.scannerOptions, this@RecycleScanFragment::processBarcodes))


            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyser)
            } catch (ex: Throwable) {
                ex.printStackTrace()
                lg(ex.message)
            }
        }, ContextCompat.getMainExecutor(requireContext()))

    }

    private fun stopCamera() {
        cameraTask?.apply {
            get()?.unbindAll()
            cancel(true)
        }
        cameraTask = null

    }

    private fun processBarcodes(barcodes: List<Barcode?>) {
        barcodes.forEachNonNull { barcode ->
            barcode.rawValue?.also { code ->
                vm.postScannedBarcode(barcode)
                lg("--------------Found code: $code (${barcode.format})")
            }
        }
    }

    private fun requestPermissions() {
        permissionsRequest.launch(REQUIRED_PERMISSIONS)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (this::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
        }
    }

    companion object {
        val REQUIRED_PERMISSIONS
            get() = arrayOf(Manifest.permission.CAMERA)
    }

    private class BarcodeAnalyser(private val scannerOptions: BarcodeScannerOptions,
                                  private val listener: (List<Barcode?>) -> Unit
    ) : ImageAnalysis.Analyzer {
        // Based on ML Kit https://developers.google.com/ml-kit/vision/barcode-scanning/android
        @ExperimentalGetImage
        override fun analyze(imageProxy: ImageProxy)  { // must be closed
            val mediaImage = imageProxy.image
            if (mediaImage == null) {
                lg("Media Image is null. Barcode scanning is unavailable")
                return
            }

            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            val scanner = BarcodeScanning.getClient(scannerOptions)
            startCommon()
            scanner.process(image)
                .addOnSuccessListener { barcodes -> listener(barcodes) }
                .addOnFailureListener {
                    it.printStackTrace()
                    lg("scanning failed(((")
                }.addOnCompleteListener {
                    imageProxy.close()
                    stopCommon()
                }

        }
    }
}