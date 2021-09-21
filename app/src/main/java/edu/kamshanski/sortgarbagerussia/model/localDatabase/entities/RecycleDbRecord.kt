package edu.kamshanski.sortgarbagerussia.model.localDatabase.entities

import edu.kamshanski.sortgarbagerussia.model.constants.BarcodeType
import edu.kamshanski.sortgarbagerussia.model.coreentities.Recycle
import edu.kamshanski.sortgarbagerussia.model.coreentities.RecycleForm
import edu.kamshanski.sortgarbagerussia.model.coreentities.RecycleOffer
import edu.kamshanski.sortgarbagerussia.model.utils.objectboxConverters.BarcodeTypeConverter
import edu.kamshanski.sortgarbagerussia.utils.decodeRecycleTimestamp
import edu.kamshanski.sortgarbagerussia.utils.encodeRecycleTimestamp
import edu.kamshanski.sortgarbagerussia.utils.nowUtc
import edu.kamshanski.tpuclassschedule.utils.string.isNotNullOrBlank
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
@Entity
open class RecycleDbRecord (
    @Id var _objId: Long = 0,
    @Unique var globalId: String = "",    // id in db on server. Empty stands for empty object
    var name: String = "",
    var barcode: String = "",
    @Convert(converter = BarcodeTypeConverter::class, dbType = String::class)
        var barcodeType: BarcodeType = BarcodeType.UNKNOWN,
    var barcodeInfo: String = "",
    var barcodeLink: String = "",
    var productInfo: String = "",
    var productType: String = "",
    var productLink: String = "",
    var utilizeInfo: String = "",
    var utilizeLink: String = "",
    var lastEditDate: String = encodeRecycleTimestamp(nowUtc()),
) : RecycleForm {
    val isEmpty get() = globalId.isEmpty() && name.isBlank()
    val isAssumption get() = globalId.isEmpty() && name.isNotNullOrBlank()
    val isRecord get() = globalId.isNotEmpty() && name.isNotNullOrBlank()

    override val asRecycle: Recycle
        get() = Recycle(globalId, name, barcode, barcodeType, barcodeInfo, barcodeLink, productInfo,
            productType, productLink, utilizeInfo, utilizeLink, decodeRecycleTimestamp(lastEditDate))

    override val asOffer: RecycleOffer
        get() = RecycleOffer(globalId, name, barcode, barcodeType, barcodeInfo, productInfo, utilizeInfo)

    override fun toString(): String {
        return "Offer: $_objId,'$globalId', '$name', '$barcode', $barcodeType, '$productInfo', '$utilizeInfo', '$lastEditDate'"
    }
}