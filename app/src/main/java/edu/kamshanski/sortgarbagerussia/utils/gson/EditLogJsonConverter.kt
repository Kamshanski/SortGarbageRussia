package edu.kamshanski.sortgarbagerussia.utils.gson

import com.google.gson.*
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities.EditLog
import edu.kamshanski.sortgarbagerussia.utils.decodeRecycleTimestamp
import java.lang.reflect.Type

class EditLogJsonConverter : JsonSerializer<EditLog>, JsonDeserializer<EditLog> {
    override fun serialize(src: EditLog?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val obj = JsonObject()
        if (src != null) {
            for(record in src) {
                obj.addProperty(record.key.toString(), record.value)
            }
        }
        return obj
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): EditLog {
        val log = EditLog()
        if (json != null) {
            val obj = json.asJsonObject
            for (record in obj.entrySet()) {
                log[decodeRecycleTimestamp(record.key)] = record.value.asString
            }
        }
        return log
    }
}