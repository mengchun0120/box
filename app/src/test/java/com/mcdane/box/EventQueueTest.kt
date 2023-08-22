package com.mcdane.box

import org.junit.Test
import org.junit.Assert.*

class EventQueueTest {
    @Test
    fun eventQueueWorks() {
        val queue = EventQueue(2)

        queue.add(PointerAction.DOWN, 100f, 200f)
        val e1 = queue.peek()
        assertTrue(e1.action == PointerAction.DOWN && e1.x == 100f && e1.y == 200f)

        queue.add(PointerAction.UP, 120f, 220f)
        assertTrue(queue.full && queue.size == 2)

        queue.remove()
        assertTrue(queue.size == 1)

        val e2 = queue.peek()
        assertTrue(e2.action == PointerAction.UP && e2.x == 120f && e2.y == 220f)

        queue.remove()
        assertTrue(queue.empty && queue.size == 0)
    }

    @Test
    fun removeFromEmptyQueueFails() {
        val queue = EventQueue(2)
        var exceptionHappened = false
        try {
            queue.remove()
        } catch(e: Exception) {
            exceptionHappened = true
        }
        assertTrue(exceptionHappened)
    }

    @Test
    fun peekFromEmptyQueueFails() {
        val queue = EventQueue(2)
        var exceptionHappened = false
        try {
            queue.peek()
        } catch(e: Exception) {
            exceptionHappened = true
        }
        assertTrue(exceptionHappened)
    }

    @Test
    fun addToFullQueueFails() {
        val queue = EventQueue(1)
        var exceptionHappened = false
        queue.add(PointerAction.DOWN, 100f, 200f)
        try {
            queue.add(PointerAction.DOWN, 100f, 200f)
        } catch (e: Exception) {
            exceptionHappened = true
        }
        assertTrue(exceptionHappened)
    }
}