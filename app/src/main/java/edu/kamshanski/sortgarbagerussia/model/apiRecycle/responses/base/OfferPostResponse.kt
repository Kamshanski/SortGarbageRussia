package edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.base

import edu.kamshanski.sortgarbagerussia.model.localDatabase.entities.RecycleOfferDbRecord

class OfferPostResponse(val offerDbRecord: RecycleOfferDbRecord?,
                        val posted: Boolean,
                        val error: String?) {
}