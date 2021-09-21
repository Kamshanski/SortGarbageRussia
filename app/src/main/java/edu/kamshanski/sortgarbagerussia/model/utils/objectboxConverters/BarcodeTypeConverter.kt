package edu.kamshanski.sortgarbagerussia.model.utils.objectboxConverters

import edu.kamshanski.sortgarbagerussia.model.constants.BarcodeType
import io.objectbox.converter.PropertyConverter

class BarcodeTypeConverter : PropertyConverter<BarcodeType, String> {
    override fun convertToEntityProperty(databaseValue: String): BarcodeType {
        return BarcodeType.byServerId(databaseValue)
    }

    override fun convertToDatabaseValue(entityProperty: BarcodeType?): String {
        return entityProperty?.serverFormat ?: BarcodeType.UNKNOWN.serverFormat
    }
}