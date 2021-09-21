package edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities

import edu.kamshanski.sortgarbagerussia.model.constants.BarcodeType
import edu.kamshanski.sortgarbagerussia.model.coreentities.Recycle
import edu.kamshanski.sortgarbagerussia.model.coreentities.RecycleForm
import edu.kamshanski.sortgarbagerussia.model.coreentities.RecycleOffer
import edu.kamshanski.sortgarbagerussia.utils.NULL_DATETIME
import edu.kamshanski.sortgarbagerussia.utils.encodeRecycleTimestamp
import edu.kamshanski.sortgarbagerussia.utils.nowUtc
import java.util.*

class RecycleApiRecord (
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
    val popularity: Long = 0L,
    private val editLog: EditLog = EditLog()
) : RecycleForm {

    val time: GregorianCalendar
        get() = editLog.last?.time ?: NULL_DATETIME

    override val asRecycle: Recycle
        get() = Recycle(
            globalId, name,
            barcode, barcodeType, barcodeInfo, barcodeLink,
            productInfo, productType, productLink,
            utilizeInfo, utilizeLink,
            editLog.last?.time ?: NULL_DATETIME
        )
    override val asOffer: RecycleOffer
        get() = RecycleOffer(globalId, name, barcode, barcodeType,
            barcodeInfo, productInfo, utilizeInfo, encodeRecycleTimestamp(editLog.last?.time ?: nowUtc())
        )
}