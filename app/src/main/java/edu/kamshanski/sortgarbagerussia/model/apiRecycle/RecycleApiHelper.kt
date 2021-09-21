package edu.kamshanski.sortgarbagerussia.model.apiRecycle

import edu.kamshanski.sortgarbagerussia.model.apiRecycle.bodies.OfferPostBody
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities.User
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.CheckOffersApiResponse
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.OfferPostApiResponse
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.RecycleSearchResponse
import edu.kamshanski.sortgarbagerussia.model.coreentities.ProductCode
import edu.kamshanski.sortgarbagerussia.model.coreentities.RecycleOffer
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
class RecycleApiHelper(val api: RecycleServerApi) {
    suspend fun getRecycleInfo(productCode: ProductCode) : RecycleSearchResponse {
        return try {
            api.getRecycleInfo(productCode.barcode, productCode.barcodeType.serverFormat)
        } catch (ex: Exception) {
            ex.printStackTrace()
            RecycleSearchResponse.ERROR("${ex::class.simpleName} : ${ex.message}")
        }
    }

    suspend fun getRecycleInfo(globalId: String) : RecycleSearchResponse {
        return try {
            api.getRecycleInfo(globalId)
        } catch (ex: Exception) {
            ex.printStackTrace()
            RecycleSearchResponse.ERROR(ex.message)
        }
    }

    suspend fun offer(offer: RecycleOffer, user: User, encodedImage: String) : OfferPostApiResponse {
        return try {
            api.offerRecycleInfo(OfferPostBody(user.userId, user.login, offer, encodedImage))
        } catch (ex: Exception) {
            ex.printStackTrace()
            OfferPostApiResponse(user.login, ex.message ?: "Exception caught while sending offer to server")
        }
    }

    suspend fun checkOffers(offerIds: List<String>, login: String) : CheckOffersApiResponse {
        return try {
            api.checkOffers(login, offerIds.joinToString(prefix = "[", postfix = "]", separator = ", "))
        } catch (ex: Exception) {
            CheckOffersApiResponse(emptyList(), login, ex.message ?: "Exception caught while checking offers")
        }
    }
}