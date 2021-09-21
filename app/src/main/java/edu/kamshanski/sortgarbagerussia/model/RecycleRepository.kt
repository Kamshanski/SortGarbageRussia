package edu.kamshanski.sortgarbagerussia.model

import android.app.Application
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.RecycleApiHelper
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.RetrofitClient
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities.User
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.GetLocalOffersResponse
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.OfferPostApiResponse
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.base.OfferPostResponse
import edu.kamshanski.sortgarbagerussia.model.coreentities.ProductCode
import edu.kamshanski.sortgarbagerussia.model.coreentities.RecycleOffer
import edu.kamshanski.sortgarbagerussia.model.coreentities.RecycleRepoResponse
import edu.kamshanski.sortgarbagerussia.model.localDatabase.ObjectBox
import edu.kamshanski.sortgarbagerussia.model.localDatabase.OfferingsAndBookmarkDatabase
import edu.kamshanski.sortgarbagerussia.model.localDatabase.entities.RecycleOfferDbRecord
import edu.kamshanski.sortgarbagerussia.model.localDatabase.interfaces.BookmarkApi
import edu.kamshanski.sortgarbagerussia.model.localDatabase.interfaces.RecycleOfferApi
import edu.kamshanski.sortgarbagerussia.model.utils.wrappers.Failure
import edu.kamshanski.sortgarbagerussia.model.utils.wrappers.InProgress
import edu.kamshanski.sortgarbagerussia.model.utils.wrappers.Progress
import edu.kamshanski.sortgarbagerussia.model.utils.wrappers.Success
import edu.kamshanski.tpuclassschedule.utils.collections.isNotNullOrEmpty
import edu.kamshanski.tpuclassschedule.utils.string.isNotNullOrBlank
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.contracts.ExperimentalContracts


@ExperimentalCoroutinesApi
@ExperimentalContracts
class RecycleRepository(application: Application) {
    // Сервер. Но пока нет сервера, будет обычной БД
    private val webStorage: RecycleApiHelper = RecycleApiHelper(RetrofitClient.getRecycleApi(application))
    private val user: User = User("admin", 1) // TODO: авторизаця
    // Для сохранений, истории посика и т.д.
    private val localBookmarksStorage: BookmarkApi
    private val localOfferStorage: RecycleOfferApi
    init {
        val localDb = OfferingsAndBookmarkDatabase(ObjectBox.mainStore)
        localBookmarksStorage = localDb
        localOfferStorage = localDb
    }

    // Todo добавть автоподгрузку isBookmarked
    suspend fun getRecycleInfo(code: ProductCode): Flow<Progress<RecycleRepoResponse?>> {
        return coroutineScope {
            // start loading info from server
            val serverDeferred = async {
                withContext(Dispatchers.IO) {
                    webStorage.getRecycleInfo(code)
                }
            }
            // get saved offers on local storage
            val localDbOffersFlow = localOfferStorage.getOfferFlow(code)

            // get server info loaded.
            // it's stated to contain at least one empty, assumption or complete record.
            // first item is the most probable
            val serverResponse = serverDeferred.await()
            check(serverResponse.fullMatch.isNotNullOrEmpty()) { "Server response cannot be empty. Server error" }

            localDbOffersFlow.map {
                return@map when(it) {
                    is InProgress -> InProgress()
                    is Failure -> Failure(it.error ?: UnknownError(it.toString()))
                    is Success -> Success(RecycleRepoResponse(it.value, serverResponse.fullMatch, serverResponse.partialMatch))
                }
            }
        }
    }

    // TODO: сделать подгрузку картинки
    suspend fun offerRecycleInfo(offer: RecycleOffer) : OfferPostResponse {
        return withContext(Dispatchers.IO) {
            val offerPostApiResponse: OfferPostApiResponse
            try {
                val apiOfferResponse = webStorage.offer(offer, user, "")
                if (apiOfferResponse.error.isNotNullOrBlank()) {
                    return@withContext OfferPostResponse(null, false, "Server error: ${apiOfferResponse.error}\n")
                } else
                    offerPostApiResponse = apiOfferResponse
            } catch (ex: Exception) {
                return@withContext OfferPostResponse(null, false, "Internal error when offer to server: ${ex.message}")
            }

            val offerToLocalDb = try {
                localOfferStorage.offer(offerPostApiResponse.offerId, offer)
            } catch (ex: Exception) {
                return@withContext OfferPostResponse(null, false, "Local database error: ${ex.message}")
            }
            return@withContext OfferPostResponse(offerToLocalDb, offerPostApiResponse.isSuccessful, offerPostApiResponse.error)
        }
    }

    suspend fun getOffers() : Flow<Progress<GetLocalOffersResponse>> {
        return localOfferStorage.getAll()
    }

    suspend fun checkOffers(offerIds: List<String>) : Flow<Progress<GetLocalOffersResponse>> {
        return flow {
            emit(InProgress())
            val result = withContext(Dispatchers.IO) {
                val checkResponse = try {
                    webStorage.checkOffers(offerIds, user.login)
                } catch (ex: Exception) {
                    return@withContext Failure<GetLocalOffersResponse>(ex)
                }
                if (checkResponse.error != null) {
                    return@withContext Failure<GetLocalOffersResponse>(UnknownError(checkResponse.error ?: "Servers serror whe Check offers"))
                }

                val reports = checkResponse.offerReports
                val offers = try {
                    localOfferStorage.updateCheckState(reports)
                } catch (ex: Exception) {
                    return@withContext Failure<GetLocalOffersResponse>(ex)
                }
                if (offers.size != reports.size) {
                    return@withContext Failure<GetLocalOffersResponse>(UnknownError("local db returned list of smaller size than size of passes reports list"))
                }

                return@withContext Success(GetLocalOffersResponse(offers))
            }
            emit(result)
        }
    }

    suspend fun deleteOffer(offer: RecycleOfferDbRecord) {
        withContext(Dispatchers.IO) {
            localOfferStorage.deleteOffer(offer)
        }
    }

}