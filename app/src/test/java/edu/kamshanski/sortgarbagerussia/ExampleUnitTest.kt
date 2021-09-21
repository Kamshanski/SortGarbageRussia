package edu.kamshanski.sortgarbagerussia

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities.EditLog
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities.RecycleApiRecord
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.RecycleSearchResponse
import edu.kamshanski.sortgarbagerussia.model.constants.BarcodeType
import edu.kamshanski.sortgarbagerussia.utils.gson.BarcodeTypeJsonConverter
import edu.kamshanski.sortgarbagerussia.utils.gson.EditLogJsonConverter
import edu.kamshanski.sortgarbagerussia.utils.gson.GregorianCalendarJsonConverter
import edu.kamshanski.sortgarbagerussia.utils.gson.RecycleApiRecordJsonConverter
import edu.kamshanski.sortgarbagerussia.utils.nowUtc
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.Executors
import kotlin.jvm.internal.Ref
import kotlin.random.Random


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalCoroutinesApi
class ExampleUnitTest {


    @Test
    fun addition_isCorrect() {
        val flow = MutableSharedFlow<Int>(5, 5, BufferOverflow.DROP_LATEST)
        val scope = CoroutineScope(Dispatchers.IO)

        val job = scope.launch {
            flow.collect {
                if (it > 3) {
                    println("finish collect")
                    this.cancel()
                    return@collect
                }
                println(it)
            }
        }
        job.invokeOnCompletion { println("finished job") }

        val endJob = scope.launch {
        }
        runBlocking {
            flow.onCompletion { println("finish launch") }
            for (i in 1..6) {
                flow.emit(i)
                yield()
            }
            println("finished emit")

            val p = async {
                var sum = 0
                for (i in 1..500000) {
                    sum += i
                }
                sum
            }
            println("p = ${p.await()}")
            println("p = ${p.await()}")
        }


    }

    //@Test
    fun sdasd() {
        val gson = GsonBuilder()
            .registerTypeAdapter(BarcodeType::class.java, BarcodeTypeJsonConverter())
            .registerTypeAdapter(EditLog::class.java, EditLogJsonConverter())
            .registerTypeAdapter(GregorianCalendar::class.java, GregorianCalendarJsonConverter())
            .registerTypeAdapter(RecycleApiRecord::class.java, RecycleApiRecordJsonConverter())
//            .registerTypeAdapter(RecycleSearchResponse::class.java, RecycleSearchResponseJsonConverter())
                .create()
        println(gson.toJson(nowUtc()))


        val str = "{\"fullMatch\":[{\"globalId\":\"12345678901234567890\",\"name\":\"\\u0412\\u043e\\u0434\\u0430 \\u043f\\u0438\\u0442\\u044c\\u0435\\u0432\\u0430\\u044f \\u041d\\u043e\\u0432\\u043e\\u0442\\u0440\\u043e\\u0438\\u0446\\u043a\\u0430\\u044f\",\"barcode\":\"4607036392886\",\"barcodeType\":\"EAN_13\",\"barcodeInfo\":\"\\u0412\\u043e\\u0434\\u0430 \\u043c\\u0438\\u043d\\u0435\\u0440\\u0430\\u043b\\u044c\\u043d\\u0430\\u044f \\u043d\\u043e\\u0432\\u043e\\u0442\\u0440\\u043e\\u0438\\u0446\\u043a\\u0430\\u044f \\u043d\\u0435\\u0433\\u0430\\u0437\\u0438\\u0440\\u043e\\u0432\\u0430\\u043d\\u043d\\u0430\\u044f 2\\u043b \\u043f\\u043b\\/\\u0431\\u0443\\u0442\\n\\u041a\\u0430\\u0442\\u0435\\u0433\\u043e\\u0440\\u0438\\u044f: \\u041f\\u0440\\u043e\\u0434\\u0443\\u043a\\u0442\\u044b \\u043f\\u0438\\u0442\\u0430\\u043d\\u0438\\u044f \\/ \\u041d\\u0430\\u043f\\u0438\\u0442\\u043a\\u0438 \\u0431\\u0435\\u0437\\u0430\\u043b\\u043a\\u043e\\u0433\\u043e\\u043b\\u044c\\u043d\\u044b\\u0435 \\/ \\u0412\\u043e\\u0434\\u0430 \\u043c\\u0438\\u043d\\u0435\\u0440\\u0430\\u043b\\u044c\\u043d\\u0430\\u044f\",\"barcodeLink\":\"https:\\/\\/service-online.su\\/text\\/shtrih-kod\\/?cod=4607036392886\",\"productInfo\":\"\\u0412\\u043e\\u0434\\u0430 \\u043f\\u0438\\u0442\\u044c\\u0435\\u0432\\u0430\\u044f \\u043d\\u043e\\u0432\\u043e\\u0442\\u0440\\u0438\\u0446\\u0430\\u044f \\u043f\\u043e\\u043b\\u0442\\u043e\\u0440\\u0430\\u0448\\u043a\\u0430\",\"productType\":\"\\u041f\\u043b\\u0430\\u0441\\u0442\\u0438\\u043a\",\"productLink\":null,\"utilizeInfo\":\"\\u041f\\u0443\\u0441\\u0442\\u0443\\u044e \\u0431\\u0443\\u0442\\u044b\\u043b\\u043a\\u0443 \\u0432 \\u041f\\u043b\\u0430\\u0441\\u0442\\u0438\\u043a\",\"utilizeLink\":null,\"editLog\":{\"2021-08-21 11-36-42\":\"admin\",\"2021-08-21 11-38-21\":\"admin\"}}],\"partialMatch\":[],\"error\":null}\n";
        val response = gson.fromJson(str, RecycleSearchResponse::class.java)
        println(response)
    }
    class Api {
        val executorService = Executors.newSingleThreadExecutor()
        var num = 0
        init {
            executorService.submit {
                while (true) {
                    callback?.onUpdate(num++)
                    println(Thread.currentThread().name)
                    Thread.sleep(100)
                }
            }
        }
        var callback: Callback? = null
        fun close() {
            executorService.shutdownNow()
        }
        interface Callback {
            fun onUpdate(newVal: Int)
        }
    }

    @Test
    fun flowTest1() {
        runBlocking {
            var sum = 0
            val api = Api()
            val fl = callbackFlow<Int> {
                api.callback = object : Api.Callback {
                    override fun onUpdate(newVal: Int) {
                        println("New val $newVal at ${Thread.currentThread().name}")
                        trySend(newVal)
                        println("Sent")
                    }
                }
                awaitClose {api.close()}
            }
            CoroutineScope(Dispatchers.IO).launch {
                fl.collect { i ->
                    println("Received $i at ${Thread.currentThread().name}")
                    sum += 1
                }
            }

            while (sum != 5) {
                yield()
            }
            api.close()
        }
    }
    @Test
    fun sort() {
        val source = IntArray(11) {Random.nextInt(10)}
        println(source.joinToString())
        var k = 0
        for (i in 0 until source.size) {
            var min = source[i]
            for (j in i until source.size) {
                if (min > source[j]) {
                    min = source[j]
                    k = j
                }
            }
            val temp = source[i]
            source[i] = min
            source[k] = temp
        }
        println(source.joinToString())
        val te = edu.kamshanski.sortgarbagerussia.Test()
        te.po();
    }
}