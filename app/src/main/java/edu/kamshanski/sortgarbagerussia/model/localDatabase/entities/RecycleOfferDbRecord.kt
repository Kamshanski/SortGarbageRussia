package edu.kamshanski.sortgarbagerussia.model.localDatabase.entities

import edu.kamshanski.sortgarbagerussia.model.constants.BarcodeType
import edu.kamshanski.sortgarbagerussia.model.constants.OfferStatus
import edu.kamshanski.sortgarbagerussia.model.coreentities.Recycle
import edu.kamshanski.sortgarbagerussia.model.coreentities.RecycleOffer
import edu.kamshanski.sortgarbagerussia.model.utils.objectboxConverters.BarcodeTypeConverter
import edu.kamshanski.sortgarbagerussia.model.utils.objectboxConverters.OfferStatusConverter
import edu.kamshanski.sortgarbagerussia.utils.decodeRecycleTimestamp
import edu.kamshanski.sortgarbagerussia.utils.encodeRecycleTimestamp
import edu.kamshanski.sortgarbagerussia.utils.nowUtc
import edu.kamshanski.tpuclassschedule.utils.string.EMPTY
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

@Entity
class RecycleOfferDbRecord(
        @Id var _objId: Long = 0,
        @Unique var offerId: String = EMPTY,
        var globalId: String = EMPTY,       // Empty is for offer created from assumption or from scratch
        var name: String = EMPTY,
        var barcode: String = EMPTY,
        @Convert(converter = BarcodeTypeConverter::class, dbType = String::class)
        var barcodeType: BarcodeType = BarcodeType.UNKNOWN,
        var barcodeInfo: String = EMPTY,
        var productInfo: String = EMPTY,
        var utilizeInfo: String = EMPTY,
        var time: String = encodeRecycleTimestamp(nowUtc()),
        var imagePath: String = EMPTY,
        @Convert(converter = OfferStatusConverter::class, dbType = String::class)
        var progressStatus: OfferStatus = OfferStatus.UNSTUDIED,
        var progressComment: String? = null
) {
    override fun toString(): String {
        return "Offer: $_objId, '$globalId', '$name', '$barcode', $barcodeType, '$productInfo', '$utilizeInfo', '$time', '$imagePath' ${progressStatus.serverValue}, '$progressComment'"
    }

    val asOffer: RecycleOffer
        get() = RecycleOffer(globalId, name, barcode, barcodeType, barcodeInfo, productInfo, utilizeInfo, imagePath)


    val asRecycle: Recycle
        get() = Recycle(
                globalId = globalId,
                name = name,
                barcode = barcode,
                barcodeType = barcodeType,
                barcodeInfo = barcodeInfo,
                productInfo = productInfo,
                utilizeInfo = utilizeInfo,
                lastEdit = decodeRecycleTimestamp(time))


    companion object {
        fun fromOffer(offerId: String, offer: RecycleOffer) = with(offer) {
            RecycleOfferDbRecord(
                    0, offerId, globalId, name, barcode, barcodeType, barcodeInfo, productInfo, utilizeInfo, encodeRecycleTimestamp(nowUtc()), imagePath
            )
        }
    }
}
