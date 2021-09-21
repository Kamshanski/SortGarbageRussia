package edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses

import edu.kamshanski.sortgarbagerussia.model.localDatabase.entities.RecycleOfferDbRecord

class GetLocalOffersResponse(
        val offers: List<RecycleOfferDbRecord> = emptyList(),
        val error: String? = null
)