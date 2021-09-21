package edu.kamshanski.tpuclassschedule.utils.string.sequential_parser

import com.company.sequential_parser.NoDataFoundOnTokenException
import com.company.sequential_parser.SearchConsumer
import com.company.sequential_parser.SearchToken
import edu.kamshanski.tpuclassschedule.utils.string.isIndexOf

class SequentialParser(val src: String)  {
    var index: Int = 0
        private set

    var lastFoundValue: String? = null

    /**
     * Tries to move [index] after [marker]. Search starts from current [index]
     *
     * @param marker
     * @return true on successful move
     */
    fun moveTo(marker: String) : Boolean {
        val i = src.indexOf(marker, index)
        if (i.isIndexOf(src)) {
            index = i + marker.length
            return true
        }
        return false
    }

    // returns last found value. Use on non-critical text parts
    fun searchOneOrNull(beginMarker: String,
               endMarker: String,
               times: Int = SearchToken.ANY,
               limit: Int = 1,
               consumer: SearchConsumer? = null)
            = searchFrom(index, beginMarker, endMarker, times, limit, consumer)
    // returns last found value. Use on critical txt parts (thus this will throw an Exception
    fun search(beginMarker: String,
               endMarker: String,
               times: Int = 1,
               limit: Int = SearchToken.UNLIMITED,
               consumer: SearchConsumer? = null)
    = searchFrom(index, beginMarker, endMarker, times, limit, consumer)
    // returns last found value. Use on critical txt parts (thus this will throw an Exception
    fun search(markers: Pair<String, String>,
               times: Int = 1,
               limit: Int = SearchToken.UNLIMITED,
               consumer: SearchConsumer? = null)
    = searchFrom(index, markers.first, markers.second, times, limit, consumer)

    /**
     * @param begin - begin index to search from
     * @param token to search
     * @return end index of found token
     * @throws
     */
    private fun searchFrom(begin: Int,
                           beginMarker: String,
                           endMarker: String,
                           times: Int = 1,
                           limit: Int = SearchToken.UNLIMITED,
                           consumer: SearchConsumer? = null
    ) : String? {
        var time = 0
        var end = begin
        // search begin and end index
        while (mayContinue(time, times, limit)) {
            var beginIndex = src.indexOf(beginMarker, end, false)
            checkIndex(beginIndex) {
                if (mayFinish(time, times, limit)) {
                    return saveAndReturn(end)
                }
                throw NoDataFoundOnTokenException(beginMarker, endMarker, limit, time)
            }
            beginIndex += beginMarker.length

            val endIndex = src.indexOf(endMarker, beginIndex)
            checkIndex(endIndex) {
                if (mayFinish(time, times, limit)) {
                    return saveAndReturn(end)
                }
                throw NoDataFoundOnTokenException(beginMarker, endMarker, limit, time)
            }

            val found = src.substring(beginIndex, endIndex)
            lastFoundValue = found
            consumer?.invoke(found, time)

            end = endIndex
            time++
        }

        return saveAndReturn(end)
    }

    private fun mayFinish(time: Int, exactTimes: Int, limit: Int) : Boolean {
        val isUnlimited = isUnlimited(exactTimes)
        val atLeastOne = (shouldBeAtLeastOne(exactTimes) && time >= 1)
        return isUnlimited || atLeastOne
    }

    private fun isUnlimited(times: Int) = times == SearchToken.ANY
    private fun shouldBeAtLeastOne(times: Int) = times == SearchToken.MANY

    private  inline fun checkIndex(index: Int, lazyError: () -> Unit) {
        if (index < 0)
            lazyError()
    }

    private fun mayContinue(time: Int, exactTimes: Int, limit: Int) : Boolean {
        if (exactTimes > 0) {
            return time < exactTimes
        }
        else if (limit > 0) {
            return time < limit
        }
        return true
    }

    private fun saveAndReturn(end: Int) : String? {
        index = end
        val found = lastFoundValue
        lastFoundValue = null
        return found
    }
}