package edu.kamshanski.sortgarbagerussia.ui.main.saved

import android.app.Application
import androidx.lifecycle.viewModelScope
import edu.kamshanski.sortgarbagerussia.model.RecycleRepository
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.GetLocalOffersResponse
import edu.kamshanski.sortgarbagerussia.model.localDatabase.entities.RecycleOfferDbRecord
import edu.kamshanski.sortgarbagerussia.model.utils.wrappers.InProgress
import edu.kamshanski.sortgarbagerussia.model.utils.wrappers.Progress
import edu.kamshanski.sortgarbagerussia.ui.App
import edu.kamshanski.tpuclassschedule.activities._abstract.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.contracts.ExperimentalContracts

@ExperimentalCoroutinesApi
@ExperimentalContracts
class SavedViewModel(app: Application) : BaseViewModel(app) {
    suspend fun getOffers(): StateFlow<Progress<GetLocalOffersResponse>> {
        return repo.getOffers().stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                InProgress()
        )
    }

    fun deleteOffer(offer: RecycleOfferDbRecord) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            repo.deleteOffer(offer)
        }
    }

    fun sendOffer(offer: RecycleOfferDbRecord) {
        viewModelScope.launch(Dispatchers.Main.immediate) {
            repo.offerRecycleInfo(offer.asOffer)
            repo.deleteOffer(offer)
        }
    }

    val repo = (app as App).repository
}