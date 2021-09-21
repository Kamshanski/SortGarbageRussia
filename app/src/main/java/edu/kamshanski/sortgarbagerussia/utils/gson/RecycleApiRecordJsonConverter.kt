package edu.kamshanski.sortgarbagerussia.utils.gson

import com.google.gson.*
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities.EditLog
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities.RecycleApiRecord
import edu.kamshanski.sortgarbagerussia.model.constants.BarcodeType
import edu.kamshanski.tpuclassschedule.utils.nice_classes.tryToGet
import java.lang.reflect.Type

class RecycleApiRecordJsonConverter : JsonSerializer<RecycleApiRecord>, JsonDeserializer<RecycleApiRecord> {
    override fun serialize(src: RecycleApiRecord?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return context!!.serialize(src)
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): RecycleApiRecord {
        if (json == null) {
            return RecycleApiRecord()
        }
        json.asJsonObject.let {
            return RecycleApiRecord(
                it["globalId"].asNonNullString,
                it["name"].asNonNullString,
                it["barcode"].asNonNullString,
                tryToGet(null) { it["barcodeType"].asString }
                    ?.let { serverValue -> BarcodeType.byServerId(serverValue) }
                    ?: BarcodeType.UNKNOWN,
                it["barcodeInfo"].asNonNullString,
                it["barcodeLink"].asNonNullString,
                it["productInfo"].asNonNullString,
                it["productType"].asNonNullString,
                it["productLink"].asNonNullString,
                it["utilizeInfo"].asNonNullString,
                it["utilizeLink"].asNonNullString,
                it["popularity"].asLongOrZero,
                tryToGet(EditLog()) {context!!.deserialize(it["editLog"], EditLog::class.java)},
            )
        }
    }
}