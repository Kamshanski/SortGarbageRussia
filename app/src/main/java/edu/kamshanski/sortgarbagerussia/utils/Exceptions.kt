package edu.kamshanski.tpuclassschedule.utils

import edu.kamshanski.tpuclassschedule.utils.nice_classes.UnpredictedException
import java.net.MalformedURLException


open class InconsistentSavings: RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super()
}

open class NoSuchFileException(fileName: String?) : InconsistentSavings(fileName)
class NoWidgetPreferencesFileException(appWidgetId: Int) : NoSuchFileException("Widget Preferences of ID $appWidgetId is absent")


class LoadingException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super()
}


// Passed arguments format is wrong
class IllegalFormatException : IllegalArgumentException {
    constructor() : super() {}
    constructor(s: String?) : super(s) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
    constructor(cause: Throwable?) : super(cause) {}
}

open class ValueConflictException(
        firstValueName: String,
        firstValue: Any?,
        secondValueName: String,
        secondValue: Any?,
        desiredRelation: String
) : RuntimeException("Expected $firstValueName $desiredRelation $secondValueName, but it's not. " +
        "$firstValueName: $firstValue. $secondValueName: $secondValue")

class SamePropertyValueConflictException (
        firstValueName: String,
        firstValue: Any?,
        secondValue: Any?,
        desiredRelation: String
) : ValueConflictException(firstValueName, firstValue, firstValueName, secondValue, desiredRelation)


class IllegalYearException(msg: String? = null) : MalformedURLException(msg)
class IllegalWeekException(msg: String? = null) : MalformedURLException(msg)

fun unpredicted(msg: String? = null) : Throwable = UnpredictedException(msg)