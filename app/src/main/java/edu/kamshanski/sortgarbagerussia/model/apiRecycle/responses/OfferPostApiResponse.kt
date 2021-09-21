package edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses

import edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.base.LoginApiResponse

class OfferPostApiResponse(val offerId: String, login: String, error: String? = null) : LoginApiResponse(login, error) {
    val isSuccessful : Boolean
        get() = error.isNullOrBlank()
}