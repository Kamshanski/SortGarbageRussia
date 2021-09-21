package edu.kamshanski.tpuclassschedule.utils.nice_classes

import androidx.annotation.IntRange

class AccessLimiter<T> (
        private var obj: T?,
        @IntRange(from = 1L) val limit: Int,
        private val onObjectAccessed: (T) -> Unit)
{
    private var counter = 0
    init {
        check(limit > 0) { throw IllegalArgumentException("Counter limit must be greater than 0: $limit") }
        checkNotNull(obj) { throw IllegalArgumentException("Initial object cannot be null") }
    }

    fun contains(obj: T) : Boolean = this.obj == obj

    fun access() : T? {
        if (!avaliable()) {
            return null
        }
        counter++
        val result = obj!!
        if (!avaliable()) {
            obj = null
        }
        onObjectAccessed(result)
        return result
    }

    fun avaliable() : Boolean = counter < limit

    companion object {
        fun <T> Limited(obj: T?, times: Int, onObjectAccessed: (T) -> Unit) =
                AccessLimiter(obj, times, onObjectAccessed)
    }
}