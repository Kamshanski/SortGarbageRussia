package edu.kamshanski.tpuclassschedule.utils.primitives

import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

fun Int.toFormatString(leadingZeros: Int) : String {
    return String.format("%0" + leadingZeros + "d", this)
}
inline fun Int.not() : Int = if (this == 0) 1 else 0
inline fun Int.length() = when(this) {
    0 -> 1
    else -> log10(abs(toDouble())).toInt() + 1
}
inline fun Int.pow(radical: Int) = this.toDouble().pow(radical.toDouble()).toInt()
/** @param fast - if true, digits and this length is not compared */
inline fun Int.round(digits: Int, fast: Boolean = true) : Int {
    require(digits > 0)
    if (fast) {
        require(digits < 33)
    } else {
        require(this.length() > digits)
    }
    val exp = 10.pow(digits - 1)
    val significant = (this / exp )
    var base = significant / 10
    val indicator = significant - (base * 10)
    when(indicator) { 5, 6, 7, 8, 9 -> base += 1 }
    return base * exp * 10

}

inline fun Long.length() = when(this) {
    0L -> 1L
    else -> log10(abs(toDouble())).toLong() + 1L
}
inline fun Long.pow(radical: Int) = this.toDouble().pow(radical.toDouble()).toLong()
/** @param fast - if true, digits and this length is not compared */
inline fun Long.round(digits: Int, fast: Boolean = true) : Long {
    require(digits > 0)
    if (fast) {
        require(digits < 33)
    } else {
        require(this.length() > digits)
    }
    val exp = 10L.pow(digits - 1)
    val significant = (this / exp )
    var base = significant / 10
    val indicator = significant - (base * 10)
    when(indicator) { 5L, 6L, 7L, 8L, 9L -> base += 1L }
    return base * exp * 10

}

inline fun Int.isEven() : Boolean = this and 1 == 0
inline fun Int.isOdd() : Boolean = this and 1 == 1
inline fun Long.isEven() : Boolean = this and 1L == 0L
inline fun Long.isOdd() : Boolean = this and 1L == 1L

inline infix fun Char.xor(other: Char): Char {
    return (this.toInt() xor other.toInt()).toChar()
}
inline infix fun Byte.xor(other: Byte): Byte {
    return (this.toInt() xor other.toInt()).toByte()
}

/** Does nothing. Use as a placeholder.
 * For example. If there exist some branch in when() that is better to be considered
 * affects nothing actually
 */
inline fun pass() {}

inline fun Boolean.toInt() : Int = if (this) 1 else 0