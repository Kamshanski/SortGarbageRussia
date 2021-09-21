package edu.kamshanski.tpuclassschedule.utils.string

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


const val EMPTY = ""

fun StringBuilder.remove(piece: String) : StringBuilder {
    val i = indexOf(piece)
    if (i > -1) {
        replace(i, i + piece.length, "")
    }
    return this
}

fun StringBuilder.removePrefix(s: String, isPrefixEnding: Boolean = false) : StringBuilder {
    val i = indexOf(s)
    val fitsBeginning = i == 0
    val endingOfPrefix = i > 0 && isPrefixEnding
    if (fitsBeginning || endingOfPrefix) {
        replace(0, i + s.length, "")
    }
    return this
}

fun removePostfixInStringBuilder(sb: StringBuilder, s: String, isPostfixBeginning: Boolean = false) : StringBuilder {
    val i = sb.indexOf(s)
    val found = i > -1
    val fitsSrcEnding = (i+s.length) == sb.length
    val beginningOfPostfix = (i+s.length < sb.length) && isPostfixBeginning
    if (found && (fitsSrcEnding || beginningOfPostfix)) {
        sb.replace(i, sb.length, "")
    }
    return sb
}

fun <T> T.removePostfix(s: String, isPostfixBeginning: Boolean = false): T where T : CharSequence{
    val i = indexOf(s)
    val found = i > -1
    val fitsSrcEnding = (i+s.length) == length
    val beginningOfPostfix = (i+s.length < length) && isPostfixBeginning
    if (found && (fitsSrcEnding || beginningOfPostfix)) {
         return when(this)  {
            is StringBuilder -> this.replace(i, length, "")
            is StringBuffer -> this.replace(i, length, "")
            else -> this.replaceRange(i, length, "")
        } as T
    }
    return this


}


fun String.formatOfNull(vararg args: Any?) : String? {
    var str: String? = null
    try {
        str = this.format(args)
    } catch (ex: Exception) {
        ex.printStackTrace()
    } finally {
        return str
    }
}


inline fun Int.isIndexOf(str: String) = this > -1 && this < str.length
inline fun Int.isNotIndexOf(str: String) = !this.isIndexOf(str)

//https://www.techiedelight.com/remove-whitespaces-string-kotlin/?__cf_chl_managed_tk__=pmd_521c9a9517ec248db04ea2bcd343d0a59a8ab4e9-1626851727-0-gqNtZGzNAs2jcnBszQi6
// replaces all whitespaces arrays to a single space char
fun String.clearWhitespaces() : String {
    return replace(Regex("\\s+"), " ").trim()
}

@ExperimentalContracts
@OptIn(ExperimentalContracts::class)
public inline fun CharSequence?.isNotNullOrBlank(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrBlank != null)
    }
    return this != null && this.isNotBlank()
}

public inline fun CharSequence.nextCharIs(
    char: Char,
    charPos: Int = 0,
    to: Int = this.lastIndex,
    skipWhitespaces: Boolean = true
) : Boolean {
    if (skipWhitespaces) {
        for (i in (charPos+1)..to) {
            val c = this[i]
            if (c == ' ' || c == '\n' || c == '\t') {
                continue
            }
            return c == char
        }
        return false
    } else {
        return this[charPos + 1] == char
    }
}

// check if string contains [other] on [onPosition] place
public fun CharSequence.contains(other: CharSequence, onPosition: Int) : Boolean {
    if (onPosition + other.length > this.length) {
        return false
    }
    for (i in other.indices) {
        if (this[i + onPosition] != other[i])
            return false
    }
    return true
}

public inline fun Any?.toStringOrEmpty() = this?.toString() ?: ""