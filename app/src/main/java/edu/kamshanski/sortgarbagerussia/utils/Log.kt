package edu.kamshanski.tpuclassschedule.utils

import android.util.Log

/**
 * Печатает объект в виде строки в лог. Для массивов, списков и null использует особое формативраоние
 * @param o - объект для вывода в лог
 */
fun <T> lg(o: T) {
    lg(when(o) {
        is Collection<*> -> o.joinToString { it.toString() }
        is Array<*> -> o.contentToString()
        is Map<*, *> -> o.asIterable().joinToString { it.key.toString() + ":" + it.value.toString() }
        null -> "null"
        else -> o.toString()
    })
}

/**
 * Печатает строку в лог
 * @param o - строка для вывода в лог
 */
fun lg(o: String) {
    Log.d("Dawan", o)
}

private val TIMER_MAP = HashMap<Any, Long>()
private var COMMON_KEY = "COMMON_KEY"
fun startTimer(key: Any) {
    TIMER_MAP[key] = System.nanoTime()
}

fun startCommon() = startTimer(COMMON_KEY)
fun stopCommon() = stopTimerAndPrint(COMMON_KEY)

// Negative no timer was started with [key]
fun stopTimerAndPrint(key: Any) {
    val dt = System.nanoTime() - (TIMER_MAP[key] ?: Long.MAX_VALUE)
    lg("$key: ${dt / 1000000000L} s, ${(dt/1000000L) % 1000L} ms, ${(dt/1000L) % 1000L} mcs, ${dt % 1000L} ns")
}

