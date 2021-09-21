package edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses

import edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities.RecycleApiRecord
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.base.BaseRecycleApiResponse

class RecycleSearchResponse(
        val fullMatch: List<RecycleApiRecord>,
        val partialMatch: List<RecycleApiRecord>,
        error: String? = null,
) : BaseRecycleApiResponse(error) {
    companion object {
        fun ERROR(error: String?) : RecycleSearchResponse {
            return RecycleSearchResponse(emptyList(), emptyList(), error)
        }
    }
}