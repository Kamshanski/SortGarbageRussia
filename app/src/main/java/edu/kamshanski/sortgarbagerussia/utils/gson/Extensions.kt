package edu.kamshanski.sortgarbagerussia.utils.gson

import com.google.gson.JsonElement

public val JsonElement?.asNonNullString : String
    get() {
        if (this == null || this.isJsonNull) {
            return "";
        }
        if (this.isJsonPrimitive) {
            return this.asString
        }
        if (this.isJsonArray) {
            return this.asJsonArray.joinToString(prefix = "[", postfix = "]") { it.asNonNullString }
        }
        if (this.isJsonObject) {
            return this.asJsonObject.entrySet().joinToString(prefix = "{", postfix = "}") { "\"${it.key}\" : \"${it.value.asNonNullString}\"" }
        }
        throw IllegalStateException("JsonElement cannot be transformed into non-null string")
    }

public val JsonElement?.asLongOrNegative : Long
    get() = if (this != null && this.isJsonPrimitive) this.asLong else -1L
public val JsonElement?.asLongOrZero : Long
    get() = if (this != null && this.isJsonPrimitive) this.asLong else 0L
