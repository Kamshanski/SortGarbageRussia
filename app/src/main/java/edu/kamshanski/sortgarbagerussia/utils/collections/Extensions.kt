package edu.kamshanski.tpuclassschedule.utils.collections

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

public inline fun <I, reified O> Array<I>.map(transform: (int: Int, value: I) -> O) : Array<O> {
    return Array(this.size) { i -> transform(i, this[i]) }
}

public inline fun <T> MutableSet<T>.append(value: T) : MutableSet<T> {
    add(value)
    return this
}

public inline fun <T> MutableList<T>.append(value: T) : MutableList<T> {
    add(value)
    return this
}

public inline fun <T> MutableList<T>.append(vararg values: T) : MutableList<T> {
    for (value in values) {
        add(value)
    }
    return this
}

public fun <T> arrayListOf(size: Int, block: (Int) -> T) : ArrayList<T> {
    return ArrayList<T>(size).apply {
        for (i in 0 until size) {
            add(block(i))
        }
    }
}

public inline fun <T> Iterable<T?>.forEachNonNull(consumer: (T) -> Unit) {
    for (i in this) {
        if (i != null) {
            consumer(i)
        }
    }
}

@ExperimentalContracts
@OptIn(ExperimentalContracts::class)
public inline fun <T> Collection<T>?.isNotNullOrEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrEmpty != null)
    }
    return this != null && this.isNotEmpty()
}

public inline fun <T> Collection<T>.printAll(printFunction: (String) -> Unit = ::print) {
    for (i in this) {
        printFunction(i.toString())
    }
}