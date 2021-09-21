package edu.kamshanski.sortgarbagerussia.utils.gson

import com.google.gson.*
import edu.kamshanski.sortgarbagerussia.model.constants.BarcodeType
import java.lang.reflect.Type

class BarcodeTypeJsonConverter: JsonSerializer<BarcodeType>, JsonDeserializer<BarcodeType> {
    override fun serialize(src: BarcodeType?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return context!!.serialize(src?.serverFormat ?: BarcodeType.UNKNOWN.serverFormat)
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): BarcodeType {
        return json?.asString
                ?.let { BarcodeType.byServerId(it) }
                ?: BarcodeType.UNKNOWN
    }
}