package edu.kamshanski.sortgarbagerussia.utils.collections

import android.os.LimitExceededException
import java.util.*
import kotlin.math.min

// based on https://commons.apache.org/proper/commons-collections/jacoco/org.apache.commons.collections4.queue/CircularFifoQueue.java.html
class CircularFifoQueue<E>(val limit: Int): AbstractCollection<E>(), Queue<E> {
    private val list = LinkedList<E>()
    override val size: Int
        get() = list.size

    override fun iterator(): MutableIterator<E> = list.iterator()

    fun isAtFullCapacity() = list.size == limit

    fun freeSize() = limit - list.size

    private fun validateSize() {
        if (limit < size) {
            throw LimitExceededException("$size out of $limit")
        }
    }

    override fun add(element: E): Boolean {
        if (isAtFullCapacity()) {
            list.removeLast()
        }

        list.addFirst(element)
        validateSize()
        return true
    }

    override fun addAll(elements: Collection<E>): Boolean {
        val toInsert = min(elements.size, limit)
        val toRemove = min(size, (elements.size - freeSize()).coerceAtLeast(0))

        // remove
        if (toRemove == size) {
            clear()
        } else {
            for (i in 0 until toRemove) {
                list.removeLast()
            }
        }

        // insert
        val range = (elements.size - toInsert) until elements.size
        elements.forEachIndexed { i, e ->
            if (i in range) {
                list.addFirst(e)
            }
        }

        validateSize()
        return true
    }

    override fun clear() {
        list.clear()
    }

    override fun remove(): E = list.removeFirst()

    override fun remove(element: E): Boolean = list.remove(element)

    override fun removeAll(elements: Collection<E>): Boolean = list.removeAll(elements)

    override fun retainAll(elements: Collection<E>): Boolean = list.retainAll(elements)

    override fun offer(e: E): Boolean = add(e)

    override fun poll(): E? = list.poll()

    override fun element(): E = list.element()

    override fun peek(): E? = list.peek()
}