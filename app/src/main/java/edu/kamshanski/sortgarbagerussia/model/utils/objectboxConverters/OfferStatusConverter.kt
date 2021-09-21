package edu.kamshanski.sortgarbagerussia.model.utils.objectboxConverters

import edu.kamshanski.sortgarbagerussia.model.constants.BarcodeType
import edu.kamshanski.sortgarbagerussia.model.constants.OfferStatus
import io.objectbox.converter.PropertyConverter

class OfferStatusConverter : PropertyConverter<OfferStatus, String> {
    override fun convertToEntityProperty(databaseValue: String?): OfferStatus {
        return OfferStatus.byServerValue(databaseValue)
    }

    override fun convertToDatabaseValue(entityProperty: OfferStatus?): String {
        return entityProperty?.serverValue ?: OfferStatus.UNSTUDIED.serverValue
    }
}