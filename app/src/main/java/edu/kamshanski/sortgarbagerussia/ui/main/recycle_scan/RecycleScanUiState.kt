package edu.kamshanski.sortgarbagerussia.ui.main.recycle_scan

import edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities.RecycleApiRecord
import edu.kamshanski.sortgarbagerussia.model.coreentities.ProductCode
import edu.kamshanski.sortgarbagerussia.model.coreentities.Recycle
import edu.kamshanski.sortgarbagerussia.model.localDatabase.entities.RecycleOfferDbRecord
import edu.kamshanski.sortgarbagerussia.ui.utils.UiStateException
import kotlin.contracts.ExperimentalContracts

/*
        val mostRelevant: Recycle,
        val mainResult: RecycleApiRecord,    // first from fullMatches
        val offer: RecycleOfferDbRecord? = null,
        val secondaryResults: List<RecycleApiRecord> = emptyList(), // the second and the rest from fullMatches
        val related: List<RecycleApiRecord> = emptyList(), // TODO: на будущее partialMatches
 */
sealed class RecycleScanUiState

@ExperimentalContracts
class ScanUi() : RecycleScanUiState()
class IdleUi() : RecycleScanUiState()
class LoadingUi(val productCode: ProductCode) : RecycleScanUiState()
open class ShowInfoUi(
        val productCode: ProductCode,
        val mayMatch: List<RecycleApiRecord> = emptyList(),
        val related: List<RecycleApiRecord> = emptyList(),
        val offer: RecycleOfferDbRecord?,
        val showContent: ContentHolder,
) : RecycleScanUiState() {

    sealed class ContentHolder(val recycle: Recycle, val obj: Any) {
        class OfferHolder(recycle: Recycle, obj: RecycleOfferDbRecord) : ContentHolder(recycle, obj) {
            val offer: RecycleOfferDbRecord = obj
        }
        class RecycleHolder(recycle: Recycle, obj: RecycleApiRecord, val isBookmarked: Boolean = false) : ContentHolder(recycle, obj) {
            val record: RecycleApiRecord = obj
        }
    }
}
class ExceptionUi(val uiStateException: UiStateException) : RecycleScanUiState()