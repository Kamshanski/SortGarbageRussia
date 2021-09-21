package edu.kamshanski.tpuclassschedule.utils.primitives

class MutablePair<A, B> (var a: A, var b: B) {
    val asImmutable : Pair<A, B>
        get() = a to b
}