package edu.kamshanski.sortgarbagerussia.model.localDatabase.interfaces

import edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities.OfferReport
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.GetLocalOffersResponse
import edu.kamshanski.sortgarbagerussia.model.constants.OfferStatus
import edu.kamshanski.sortgarbagerussia.model.coreentities.ProductCode
import edu.kamshanski.sortgarbagerussia.model.coreentities.RecycleOffer
import edu.kamshanski.sortgarbagerussia.model.localDatabase.entities.RecycleOfferDbRecord
import edu.kamshanski.sortgarbagerussia.model.utils.wrappers.Progress
import kotlinx.coroutines.flow.Flow
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
interface RecycleOfferApi {
    suspend fun getOffering(productCode: ProductCode) : RecycleOfferDbRecord?

    suspend fun getOffering(globalId: String): RecycleOfferDbRecord?

    @Throws(Exception::class)
    suspend fun offer(offerId: String, offer: RecycleOffer, status: OfferStatus = OfferStatus.UNSTUDIED): RecycleOfferDbRecord

    suspend fun getOfferFlow(productCode: ProductCode) : Flow<Progress<RecycleOfferDbRecord?>>

    suspend fun getAll() : Flow<Progress<GetLocalOffersResponse>>

    suspend fun deleteOffer(offer: RecycleOfferDbRecord)

    suspend fun updateCheckState(offerReports: List<OfferReport>) : List<RecycleOfferDbRecord>
}