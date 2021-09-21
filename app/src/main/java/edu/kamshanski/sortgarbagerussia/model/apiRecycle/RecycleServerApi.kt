package edu.kamshanski.sortgarbagerussia.model.apiRecycle

import edu.kamshanski.sortgarbagerussia.model.apiRecycle.bodies.OfferPostBody
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.bodies.SignOutBody
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.CheckOffersApiResponse
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.OfferPostApiResponse
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.RecycleSearchResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
interface RecycleServerApi {
    @GET("info.php?")
    suspend fun getRecycleInfo(@Query("barcode") barcode: String, @Query("barcodeType") barcodeType: String) : RecycleSearchResponse

    @GET("info.php?")
    suspend fun getRecycleInfo(@Query("id") globalId: String) : RecycleSearchResponse

    @POST("offer.php")
    suspend fun offerRecycleInfo(@Body offer: OfferPostBody) : OfferPostApiResponse

    @POST("registration.php")
    suspend fun signOut(@Body offer: SignOutBody) : OfferPostApiResponse

    @GET("checkOffers.php?")
    suspend fun checkOffers(@Query("login") login: String, @Query("offerIds") offerIds: String) : CheckOffersApiResponse
}