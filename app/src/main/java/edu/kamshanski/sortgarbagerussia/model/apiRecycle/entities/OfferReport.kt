package edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities

import java.util.*

class OfferReport(
        val offerId: String = "",
        val isPresent: Boolean = false,
        val processStatus: String = "UNSTUDIED",
        val processComment: String? = null,
        val date: String = ""   // 'YYYY-MM-DD hh:mm:ss' format
) {
}