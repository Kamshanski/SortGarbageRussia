package edu.kamshanski.tpuclassschedule.utils.nice_classes

import android.util.Size
import com.google.gson.Gson
import edu.kamshanski.tpuclassschedule.utils.lg
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

val gson = Gson()

/**
 * Tries to invoke [block] as many times as written in [allowedErrors]
 *
 * @param R - result param
 * @param block - block to be invoked
 * @param allowedErrors contains exceptions classes that may be invoked
 * @throws Throwable if it's actual class is out of [allowedErrors]
 * @return result of [block] invocation or null if all limits in [allowedErrors] are over and
 *  no exception was thrown after that
 */

inline fun <R> manyTry(
        allowedErrors: ArrayList<AccessLimiter<Class<out Throwable>>>,
        block: () -> R,
) : R? {
    while (true) {
        try {
            return block()
        } catch (ex: Throwable) {
            val counter = allowedErrors.find { it.contains(ex::class.java) }
                    ?: throw UnpredictedException("Unacceptable error: ${ex::class}", ex)
            if (counter.avaliable()) {
                counter.access()!!
            } else {
                throw TriesLimitExceededException("Error was thrown more than ${counter.limit}", ex)
            }
        }
    }
}

inline fun simpleTry(block: () -> Unit) {
    try {
        block()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

inline fun lgTry(errorMsg: () -> String, block: () -> Unit) {
    try {
        block()
    } catch (ex: Exception) {
        lg(errorMsg())
        ex.printStackTrace()
    }
}

inline fun <R> tryToGet(defaultValue: R, block: () -> R) : R {
    try {
        return block()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return defaultValue
}

/** Attempts to get return value from [block]. Returns null if any exceptions caught */
inline fun <R> attempt(block: () -> R) : R? {
    try {
        return block()
    } catch (ex: Throwable) {
        ex.printStackTrace()
    }
    return null
}

class TriesLimitExceededException(msg: String? = null, cause: Throwable? = null) : RuntimeException(msg, cause)
class UnpredictedException(msg: String? = null, cause: Throwable? = null) : RuntimeException(msg, cause)

/**
 * Require non-null and non-blank string
 * @param value
 * @param lazyMsg error message
 * @return value
 */
inline fun requireString(value: String?, lazyMsg: () -> String = { "Required string value is null or blank" }) : String {
    if (value.isNullOrBlank())  {
        val message = lazyMsg()
        throw IllegalStateException(message)
    }
    return value
}

inline fun checkNonNegative(
    value: Int,
    lazyMsg: () -> String = { "Required value is negative" }
) : Int = checkInRange(value, lazyMsg = lazyMsg)
// all values inclusive
inline fun checkInRange(
    value: Int,
    from: Int = 0,
    to: Int = Int.MAX_VALUE,
    lazyMsg: () -> String = { "Required int value is out of range" }
) : Int {
    if (value !in from..to) {
        val message = lazyMsg()
        throw IllegalStateException(message)
    }
    return value
}

inline fun <T> checkSize (
    collection: Collection<T>,
    size: Int,
    lazyMsg: () -> String = { "Required value is negative" }
) = checkInRange(collection.size, size, size, lazyMsg)

@ExperimentalContracts
inline fun <A,B,R> letBoth(pair: Pair<A, B>, block: (A, B) -> R) : R? {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(pair.first, pair.second)
}

inline infix fun Int.x(height: Int) = Size(this, height)