package edu.kamshanski.sortgarbagerussia.model.coreentities

import edu.kamshanski.sortgarbagerussia.model.constants.BarcodeType
import edu.kamshanski.sortgarbagerussia.utils.nowUtc
import java.io.Serializable
import java.util.*

class Recycle (
    val globalId: String    = "",
    val name: String        = "",
    val barcode: String     = "",
    val barcodeType: BarcodeType = BarcodeType.UNKNOWN,
    val barcodeInfo: String = "",
    val barcodeLink: String = "",
    val productInfo: String = "",
    val productType: String = "",
    val productLink: String = "",
    val utilizeInfo: String = "",
    val utilizeLink: String = "",
    val lastEdit: GregorianCalendar = nowUtc()
) : Serializable {
    val isEmpty: Boolean = globalId.isBlank() && name.isBlank()
    val isAssumption: Boolean = globalId.isBlank() && (name.isNotBlank() || barcodeInfo.isNotBlank() || productInfo.isNotBlank() || utilizeInfo.isNotBlank())
    val isFullRecord: Boolean = globalId.isNotBlank()

    companion object {
        fun EMPTY(productCode: ProductCode? = null) = Recycle(
            barcode = productCode?.barcode ?: "",
            barcodeType = productCode?.barcodeType ?: BarcodeType.UNKNOWN,
        )
    }
}