package edu.kamshanski.sortgarbagerussia.model.localDatabase.interfaces

import edu.kamshanski.sortgarbagerussia.model.localDatabase.entities.RecycleDbRecord
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
interface BookmarkApi {
    suspend fun isBookmarked(info: RecycleDbRecord) : Boolean
}