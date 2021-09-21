package com.company.sequential_parser

/**
 * @param beginMarker prefix of data of interest
 * @param endMarker postfix of data of interest
 * @param times how many times this type of data appears in a text.html to parse. Negative number means unknown amount of
 * data. [UNLIMITED] means 0 or more items. [MANY] means 1 or more items.
 * @param limit how many data values could be found. Ignored if [times] is certain or [limit] is [UNLIMITED] or [MANY]
 * (that means no limit)
 * @param consumer accept found data, it's index in the text.html, appearance time and acceptor to put data in
 *
 * @throws IllegalAccessException if [limit] or [times] are not positive or equal [UNLIMITED] or [MANY]
 */
class SearchToken<A>(val beginMarker: String,
                     val endMarker: String,
                     val times: Int = 1,
                     val limit: Int = UNLIMITED,
                     val consumer: SearchConsumer3<A>? = null) {

    companion object {
        const val UNLIMITED = 0     // 0 or more items
        const val ANY = 0           // 0 or more items
        const val MANY = -1         // 1 or more items. Equals to MANY in [limit] property
    }

    val isUnlimited
        get() = times == ANY && limit <= UNLIMITED

    val shouldBeAtLeastOne: Boolean
        get() = times == MANY



    init {
        if (times < MANY) {
            throw IllegalArgumentException("Property time in search token must be positive (except for (MANY = -1) and (UNKNOWN = 0) constants)")
        }
        if ((times == ANY || times == MANY) && limit < MANY) {
            throw IllegalArgumentException("Property limit in search token must be positive (except for no limit constants (MANY = -1) and (UNKNOWN = 0))")
        }
    }
}