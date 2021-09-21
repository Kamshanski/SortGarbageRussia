package edu.kamshanski.sortgarbagerussia.model.coreentities

import edu.kamshanski.sortgarbagerussia.model.constants.BarcodeType
import edu.kamshanski.sortgarbagerussia.utils.nowUtc
import java.io.Serializable
import java.util.*

class RecycleOffer (
        val globalId: String    = "",
        val name: String        = "",
        val barcode: String     = "",
        val barcodeType: BarcodeType = BarcodeType.UNKNOWN,
        val barcodeInfo: String = "",
        val productInfo: String = "",
        val utilizeInfo: String = "",
        val imagePath: String       = "",
) : Serializable {

}