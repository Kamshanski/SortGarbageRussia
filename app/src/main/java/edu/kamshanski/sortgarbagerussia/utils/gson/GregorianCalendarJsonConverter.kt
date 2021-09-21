package edu.kamshanski.sortgarbagerussia.utils.gson

import com.google.gson.*
import edu.kamshanski.sortgarbagerussia.utils.NULL_DATETIME
import edu.kamshanski.sortgarbagerussia.utils.decodeRecycleTimestamp
import edu.kamshanski.sortgarbagerussia.utils.encodeRecycleTimestamp
import java.lang.reflect.Type
import java.util.*

class GregorianCalendarJsonConverter : JsonSerializer<GregorianCalendar>, JsonDeserializer<GregorianCalendar> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): GregorianCalendar {
        return if (json != null) decodeRecycleTimestamp(json.asNonNullString) else NULL_DATETIME
    }

    override fun serialize(src: GregorianCalendar?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val time = src ?: NULL_DATETIME
        return JsonPrimitive(encodeRecycleTimestamp(time))

    }

}