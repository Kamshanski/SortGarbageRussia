//package edu.kamshanski.sortgarbagerussia.utils.gson
//
//import com.google.gson.*
//import edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities.EditLog
//import edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities.RecycleApiRecord
//import edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.RecycleSearchResponse
//import edu.kamshanski.sortgarbagerussia.model.constants.BarcodeType
//import java.lang.reflect.Type
//
//class RecycleSearchResponseJsonConverter :
//    JsonSerializer<RecycleSearchResponse>,
//    JsonDeserializer<RecycleSearchResponse>
//{
//    override fun serialize(src: RecycleSearchResponse?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
//        return context!!.serialize(src)
//    }
//
//    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): RecycleSearchResponse {
//        if (json == null) {
//            return RecycleSearchResponse.ERROR("Json is null when converting")
//        }
//        json.asJsonObject.let {
//            val fullMatchesJson = it["fullMatches"]
//
//
//            return RecycleSearchResponse(
//                it["globalId"]?.asString ?: "",
//                it["name"]?.asString ?: "",
//                it["barcode"]?.asString ?: "",
//                it["barcodeType"]?.asString
//                    ?.let { serverValue -> BarcodeType.byServerId(serverValue) }
//                    ?: BarcodeType.UNKNOWN,
//                it["barcodeInfo"]?.asString ?: "",
//                it["barcodeLink"]?.asString ?: "",
//                it["productInfo"]?.asString ?: "",
//                it["productType"]?.asString ?: "",
//                it["productLink"]?.asString ?: "",
//                it["utilizeInfo"]?.asString ?: "",
//                it["utilizeLink"]?.asString ?: "",
//                it["editLog"]
//                    ?.let {serverValue -> context!!.deserialize(serverValue, EditLog::class.java)}
//                    ?: EditLog(),
//            )
//        }
//    }
//}