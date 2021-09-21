package edu.kamshanski.sortgarbagerussia.model.localDatabase

import edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities.OfferReport
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.GetLocalOffersResponse
import edu.kamshanski.sortgarbagerussia.model.constants.BarcodeType
import edu.kamshanski.sortgarbagerussia.model.constants.OfferStatus
import edu.kamshanski.sortgarbagerussia.model.coreentities.ProductCode
import edu.kamshanski.sortgarbagerussia.model.coreentities.RecycleOffer
import edu.kamshanski.sortgarbagerussia.model.localDatabase.entities.RecycleDbRecord
import edu.kamshanski.sortgarbagerussia.model.localDatabase.entities.RecycleDbRecord_
import edu.kamshanski.sortgarbagerussia.model.localDatabase.entities.RecycleOfferDbRecord
import edu.kamshanski.sortgarbagerussia.model.localDatabase.entities.RecycleOfferDbRecord_
import edu.kamshanski.sortgarbagerussia.model.localDatabase.interfaces.BookmarkApi
import edu.kamshanski.sortgarbagerussia.model.localDatabase.interfaces.RecycleOfferApi
import edu.kamshanski.sortgarbagerussia.model.utils.isNotEmpty
import edu.kamshanski.sortgarbagerussia.model.utils.wrappers.InProgress
import edu.kamshanski.sortgarbagerussia.model.utils.wrappers.Progress
import edu.kamshanski.sortgarbagerussia.model.utils.wrappers.Success
import edu.kamshanski.tpuclassschedule.utils.lg
import io.objectbox.BoxStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.contracts.ExperimentalContracts

@ExperimentalCoroutinesApi
@ExperimentalContracts
class OfferingsAndBookmarkDatabase(val store: BoxStore) : BookmarkApi, RecycleOfferApi {
    val bookmarksBox = store.boxFor(RecycleDbRecord::class.java)
    val offeringBox = store.boxFor(RecycleOfferDbRecord::class.java)

    override suspend fun isBookmarked(info: RecycleDbRecord): Boolean {
        return bookmarksBox.query()
                .equal(RecycleDbRecord_.globalId, info.globalId)
                .build().use {
                    it.findUnique() != null
                }
    }

    override suspend fun getOffering(productCode: ProductCode): RecycleOfferDbRecord? {
        return offeringBox.query()
                .equal(RecycleOfferDbRecord_.barcode, productCode.barcode)
                .equal(RecycleOfferDbRecord_.barcodeType, productCode.barcodeType.serverFormat)
                .build().use {
                    it.findFirst()
                }
    }

    override suspend fun getOffering(globalId: String): RecycleOfferDbRecord? {
        return offeringBox.query()
                .equal(RecycleOfferDbRecord_.globalId, globalId)
                .build().use {
                    it.findFirst()
                }
    }

    /** Set offerId to offer, save it into a DB and return saved offer */
    @Throws(Exception::class)
    override suspend fun offer(offerId: String, offer: RecycleOffer, status: OfferStatus) : RecycleOfferDbRecord {
        if(offerId.isBlank()) { "offerId from server cannot be empty" }
        val dbOffer: RecycleOfferDbRecord? = store.callInTx {
            deleteOffer(null, offer.globalId, offer.barcode, offer.barcodeType)
            RecycleOfferDbRecord.fromOffer(offerId, offer).apply {
                progressStatus = status
                offeringBox.put(this)
            }
        }
        return requireNotNull(dbOffer) { "dbOffer is null when offer to local db" }
    }

    //https://github.com/objectbox/objectbox-java/issues/807#issuecomment-613376563
    //https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/callback-flow.html
    override suspend fun getOfferFlow(productCode: ProductCode) : Flow<Progress<RecycleOfferDbRecord?>> {
        return callbackFlow {
            val query = offeringBox.query()
                .equal(RecycleOfferDbRecord_.barcode, productCode.barcode)
                .equal(RecycleOfferDbRecord_.barcodeType, productCode.barcodeType.serverFormat)
                .build()
            query.subscribe().observer {
                val item = it.firstOrNull()
                trySendBlocking(Success(item))
            }
            awaitClose { query.close() }
        }
    }

    override suspend fun getAll(): Flow<Progress<GetLocalOffersResponse>> {
        return callbackFlow {
            // initial empty response
            trySend(InProgress(GetLocalOffersResponse(emptyList())))

            val query = offeringBox.query().build()
            query.subscribe().observer { list ->
                trySendBlocking(Success(GetLocalOffersResponse(list)))
            }
            awaitClose { query.close() }
        }
    }

    override suspend fun deleteOffer(offer: RecycleOfferDbRecord) {
        if (offer._objId != 0L) {
            offeringBox.remove(offer._objId)
        } else {
            deleteOffer(offer.offerId, offer.globalId, offer.barcode, offer.barcodeType)
        }
    }

    // WARNING! This function has no .runInTx() !!!
    fun deleteOffer(offerId: String?, globalId: String, barcode: String, barcodeType: BarcodeType) {
        offeringBox.query()
                .apply { if (offerId != null) equal(RecycleOfferDbRecord_.offerId, offerId) }
                .equal(RecycleOfferDbRecord_.barcode, barcode)
                .equal(RecycleOfferDbRecord_.barcodeType, barcodeType.serverFormat)
                .equal(RecycleOfferDbRecord_.globalId, globalId)
                .build()
                .use  { query ->
                    if (query.isNotEmpty()) {
                        query.remove()
                    }
                }
    }

    override suspend fun updateCheckState(offerReports: List<OfferReport>): List<RecycleOfferDbRecord> {
        return store.callInTx {
            val list = mutableListOf<RecycleOfferDbRecord>()
            for (report in offerReports) {
                if (report.isPresent) {
                    offeringBox.query()
                            .equal(RecycleOfferDbRecord_.offerId, report.offerId)
                            .build()
                            .use {
                                val offer = it.findUnique()!!
                                require(report.isPresent) { "Offer ${report.offerId} is unknown to the server" }
                                offer.time = report.date
                                offer.progressStatus = OfferStatus.byServerValue(report.processStatus)
                                offer.progressComment = report.processComment
                                list.add(offer)
                            }
                } else lg("OfferId ${report.offerId} is not present on the server")
            }
            return@callInTx list
        }
    }
}