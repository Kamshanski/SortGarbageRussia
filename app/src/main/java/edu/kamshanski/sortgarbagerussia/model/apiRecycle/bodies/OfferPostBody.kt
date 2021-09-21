package edu.kamshanski.sortgarbagerussia.model.apiRecycle.bodies

import edu.kamshanski.sortgarbagerussia.model.constants.BarcodeType
import edu.kamshanski.sortgarbagerussia.model.coreentities.RecycleOffer

class OfferPostBody(
        val userId: Int,
        val login: String,
        val globalId: String? = null,
        val name: String? = null,
        val barcode: String,
        barcodeType: BarcodeType,
        val productInfo: String?,
        val utilizeInfo: String?,
        val image: String?
) {
    constructor(userId: Int, login: String, offer: RecycleOffer, encodedImage: String) : this(
            userId, login,
            offer.globalId, offer.name,
            offer.barcode, offer.barcodeType,
            offer.productInfo, offer.utilizeInfo,
            encodedImage
    )
    val barcodeType: String = barcodeType.serverFormat
}