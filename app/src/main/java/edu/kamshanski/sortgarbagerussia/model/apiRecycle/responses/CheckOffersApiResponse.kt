package edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses

import edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities.OfferReport
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.base.LoginApiResponse

class CheckOffersApiResponse(
        val offerReports: List<OfferReport>,
        login: String,
        error: String?
) : LoginApiResponse(login, error)