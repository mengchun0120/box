package com.mcdane.box

enum class PointerAction {
    DOWN,
    UP,
}

data class PointerEvent(
    var action: PointerAction=PointerAction.DOWN,
    var x: Float=0f,
    var y: Float=0f
)

class EventQueue(val capacity: Int) {
    private val data = Array(capacity) { PointerEvent() }
    private var firstIdx = -1
    private var lastIdx = -1

    var size = 0
        private set

    val full: Boolean
        get() = size == data.size

    val empty: Boolean
        get() = size == 0

    fun add(action:PointerAction, x:Float, y:Float) {
        if (full) throw RuntimeException("EventQueue is full")

        if (size > 0) {
            lastIdx = (lastIdx + 1) % data.size
        } else {
            firstIdx = 0
            lastIdx = 0
        }

        data[lastIdx].action = action
        data[lastIdx].x = x
        data[lastIdx].y = y
        ++size
    }

    fun peek(): PointerEvent =
        if (size > 0) {
            data[firstIdx]
        } else {
            throw IndexOutOfBoundsException("Queue is empty")
        }

    fun remove() {
        if (empty) throw RuntimeException("EventQueue is empty")

        firstIdx = (firstIdx + 1) % data.size
        --size
    }
}