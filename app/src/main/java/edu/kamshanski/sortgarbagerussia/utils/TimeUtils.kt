package edu.kamshanski.sortgarbagerussia.utils

import edu.kamshanski.tpuclassschedule.utils.nice_classes.attempt
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

public fun nowUtc() = GregorianCalendar(UTC_TIMEZONE)

public val NULL_DATETIME: GregorianCalendar get() = GregorianCalendar(0,0,0)
public val UTC_TIMEZONE: TimeZone get() = SimpleTimeZone(0, "UTC")

// "2021-01-01 01:01:01" UTC is standard
private val RECYCLE_DATE_FORMAT: DateFormat get() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

fun decodeRecycleTimestamp(formattedDateTime: String) : GregorianCalendar {
    val date = attempt {
        val cal = GregorianCalendar(UTC_TIMEZONE)
        cal.time = RECYCLE_DATE_FORMAT.parse(formattedDateTime)!!
        return@attempt cal
    } ?: NULL_DATETIME

    return date
}

fun encodeRecycleTimestamp(calendar: Calendar) : String {
    return RECYCLE_DATE_FORMAT.format(calendar.time)
}