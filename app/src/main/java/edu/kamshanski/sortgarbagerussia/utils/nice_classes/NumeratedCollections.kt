package edu.kamshanski.sortgarbagerussia.utils.nice_classes

import edu.kamshanski.sortgarbagerussia.R

public class Quartet<T1,T2,T3,T4>(
        val first: T1,
        val second: T2,
        val third: T3,
        val fourth: T4)

public class Quintet<T1,T2,T3,T4,T5>(
        val first: T1,
        val second: T2,
        val third: T3,
        val fourth: T4,
        val fifth: T5)


public fun <T1, T2>          multi(a: T1, b: T2)                       = Pair(a, b)
public fun <T1,T2,T3>        multi(a: T1, b: T2, c: T3)                = Triple(a, b, c)
public fun <T1,T2,T3,T4>     multi(a: T1, b: T2, c: T3, d: T4)         = Quartet(a, b, c, d)
public fun <T1,T2,T3,T4, T5> multi(a: T1, b: T2, c: T3, d: T4, e: T5)  = Quintet(a, b, c, d, e)


public inline fun <R, T1,T2,T3,T4,T5> Quintet<T1,T2,T3,T4,T5>.letEvery(block: (a: T1, b: T2, c: T3, d: T4, e: T5) -> R) = block(first, second, third, fourth, fifth)
public inline fun <R, T1,T2,T3,T4> Quartet<T1,T2,T3,T4>.letEvery(block: (a: T1, b: T2, c: T3, d: T4) -> R) = block(first, second, third, fourth)
public inline fun <R, T1,T2,T3> Triple<T1,T2,T3>.letEvery(block: (a: T1, b: T2, c: T3) -> R) = block(first, second, third)
public inline fun <R, T1,T2> Pair<T1,T2>.letEvery(block: (a: T1, b: T2) -> R) = block(first, second)
