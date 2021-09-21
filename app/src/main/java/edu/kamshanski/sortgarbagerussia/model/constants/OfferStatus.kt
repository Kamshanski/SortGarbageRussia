package edu.kamshanski.sortgarbagerussia.model.constants

enum class OfferStatus(val serverValue: String) {
    UNSTUDIED("UNSTUDIED"), ACCEPTED("ACCEPTED"), DECLINED("DECLINED");

    companion object {
        fun byServerValue(serverValue: String?): OfferStatus {
            return serverValue
                    ?.let {
                        values().find { it.serverValue == serverValue }
                    }
                    ?: UNSTUDIED
        }
    }
}