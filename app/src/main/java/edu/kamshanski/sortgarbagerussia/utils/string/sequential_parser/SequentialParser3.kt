package edu.kamshanski.tpuclassschedule.utils.string.sequential_parser

import com.company.sequential_parser.NoDataFoundOnTokenException3
import com.company.sequential_parser.SearchToken


/**
 * @param src text.html to search data inside
 * @param acceptor object that appears in consumer to consume calculated data (Just to keep consumer as a lambda, not an
 * anonymous object)
 * @param tokens data tokens to search. Describes how to parse it. Used in sequential order. Any source inconsistency
 * gives [IllegalFormarException]
 */
class SequentialParser3<A>(
    private val src: String,
    private val acceptor: A? = null,
    private vararg val tokens: SearchToken<A>
) {
    var index: Int = 0


    fun parse() : A? {
        var index = 0
        for (token in tokens) {
            index = search(index, token, acceptor)
        }
        return acceptor
    }

    /**
     * @param begin - begin index to search from
     * @param token to search
     * @return end index of found token
     * @throws
     */
    private inline fun search(begin: Int, token: SearchToken<A>, acceptor: A?) : Int {
        var time = 0
        var end = begin
        // search begin and end index
        while (mayContinue(time, token)) {
            var beginIndex = src.indexOf(token.beginMarker, end, false)
            checkIndex(beginIndex) {
                if (unlimitedComplete(token, time)) {
                    return end
                }
                throw NoDataFoundOnTokenException3(token, time)
            }
            beginIndex += token.beginMarker.length

            val endIndex = src.indexOf(token.endMarker, beginIndex)
            checkIndex(endIndex) {
                if (unlimitedComplete(token, time)) {
                    return end
                }
                throw NoDataFoundOnTokenException3(token, time)
            }

            token.consumer?.invoke(src.substring(beginIndex, endIndex), beginIndex, time, acceptor)

            end = endIndex
            time++
        }

        return end
    }

    private fun unlimitedComplete(token: SearchToken<A>, time: Int) : Boolean =
        token.isUnlimited || (token.shouldBeAtLeastOne && time >= 1)

    private inline fun checkIndex(index: Int, lazyError: () -> Unit) {
        if (index < 0)
            lazyError()
    }

    private fun mayContinue(time: Int, token: SearchToken<A>) : Boolean {
        if (token.times > 0) {
            return time < token.times
        }
        else if (token.limit > 0) {
            return time < token.limit
        }
        return true
    }
}