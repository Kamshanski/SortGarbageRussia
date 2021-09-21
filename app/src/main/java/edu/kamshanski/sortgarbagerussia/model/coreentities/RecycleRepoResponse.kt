package edu.kamshanski.sortgarbagerussia.model.coreentities

import edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities.RecycleApiRecord
import edu.kamshanski.sortgarbagerussia.model.localDatabase.entities.RecycleOfferDbRecord
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
class RecycleRepoResponse (
        val offer: RecycleOfferDbRecord? = null,
        val fullMatch: List<RecycleApiRecord> = emptyList(), // the second and the rest from fullMatches
        val partialMatch: List<RecycleApiRecord> = emptyList(), // TODO: на будущее partialMatches
)