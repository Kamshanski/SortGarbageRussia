package edu.kamshanski.sortgarbagerussia.model.apiRecycle

import android.content.Context
import com.google.gson.GsonBuilder
import edu.kamshanski.sortgarbagerussia.BuildConfig
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities.EditLog
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities.RecycleApiRecord
import edu.kamshanski.sortgarbagerussia.model.constants.BarcodeType
import edu.kamshanski.sortgarbagerussia.utils.gson.BarcodeTypeJsonConverter
import edu.kamshanski.sortgarbagerussia.utils.gson.EditLogJsonConverter
import edu.kamshanski.sortgarbagerussia.utils.gson.GregorianCalendarJsonConverter
import edu.kamshanski.sortgarbagerussia.utils.gson.RecycleApiRecordJsonConverter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.contracts.ExperimentalContracts


/**
 * Retrofit client
 * Guides: https://habr.com/ru/post/520544/
 * @constructor Create empty Retrofit client
 */

@ExperimentalContracts
object RetrofitClient {
    @Volatile private var retrofit: Retrofit? = null
    val BASE_URL = "http://dawande.000webhostapp.com/api/"

    private fun client(context: Context): Retrofit {

        if (retrofit == null) {
            synchronized(this) {
                if (retrofit == null) {
                    val gson = GsonBuilder()
                            .registerTypeAdapter(BarcodeType::class.java, BarcodeTypeJsonConverter())
                            .registerTypeAdapter(EditLog::class.java, EditLogJsonConverter())
                            .registerTypeAdapter(GregorianCalendar::class.java, GregorianCalendarJsonConverter())
                            .registerTypeAdapter(RecycleApiRecord::class.java, RecycleApiRecordJsonConverter())
                            .create()
                    retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(getOkHttpClient())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build()
                }
            }
        }

        return retrofit!!
    }

    private fun getOkHttpClient() : OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15000, TimeUnit.MILLISECONDS)
            .writeTimeout(15000, TimeUnit.MILLISECONDS)
            .readTimeout(15000, TimeUnit.MILLISECONDS)
            .followRedirects(true)
            .callTimeout(15000, TimeUnit.MILLISECONDS)
            .also {
                if (BuildConfig.DEBUG) {
                    val interceptor = HttpLoggingInterceptor()
                    interceptor.level = HttpLoggingInterceptor.Level.BODY
                    it.addInterceptor(interceptor)
                }
            }.build()

    }

    fun getRecycleApi(context: Context): RecycleServerApi = client(context).create(RecycleServerApi::class.java)

}
