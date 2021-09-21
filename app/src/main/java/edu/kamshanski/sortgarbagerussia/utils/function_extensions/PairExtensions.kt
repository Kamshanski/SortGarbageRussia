package edu.kamshanski.tpuclassschedule.utils.function_extensions

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// EXTENSION FOR Kotlin Standard.kt

/**
 * Calls the specified function [block] with the given [argument] as its receiver and returns its result.
 *
 * For detailed usage information see the documentation for [scope functions](https://kotlinlang.org/docs/reference/scope-functions.html#with).
 */

@ExperimentalContracts
public inline fun <A, B, R> withBoth(argument: Pair<A, B>, block: (a: A, b: B) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(argument.first, argument.second)
}

/**
 * Calls the specified function [block] with the given [receiver] as its receiver and returns its result.
 *
 * For detailed usage information see the documentation for [scope functions](https://kotlinlang.org/docs/reference/scope-functions.html#with).
 */

@ExperimentalContracts
public inline fun <A, B, R> withBoth(r1: A, r2: B, block: (a: A, b: B) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(r1, r2)
}

///**
// * Calls the specified function [block] with `this` value as its receiver and returns `this` value.
// *
// * For detailed usage information see the documentation for [scope functions](https://kotlinlang.org/docs/reference/scope-functions.html#apply).
// */
//@ExperimentalContracts
//public inline fun <A, B> Pair<A, B>.applyBoth(block: Pair<A, B>.() -> Unit): Pair<A, B> {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }
//    block()
//    return this
//}

/**
 * Calls the specified function [block] with `this` value as its argument and returns `this` value.
 *
 * For detailed usage information see the documentation for [scope functions](https://kotlinlang.org/docs/reference/scope-functions.html#also).
 */
@ExperimentalContracts
public inline fun <A, B> Pair<A, B>.alsoBoth(block: (a: A, b: B) -> Unit): Pair<A, B> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    block(first, second)
    return this
}

/**
 * Calls the specified function [block] with `this` value as its argument and returns its result.
 *
 * For detailed usage information see the documentation for [scope functions](https://kotlinlang.org/docs/reference/scope-functions.html#let).
 */
@ExperimentalContracts
public inline fun <A, B, R> Pair<A, B>.letBoth(block: (a: A, b: B) -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(first, second)
}

@SuppressWarnings("unchecked")
public fun <A, B> Pair<A?, B?>.asNonNull() : Pair<A, B>? {
    if (first != null && second != null) {
        return this as Pair<A, B>
    }
    return null
}

