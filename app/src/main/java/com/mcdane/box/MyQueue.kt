package com.mcdane.box

import android.util.Log
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

interface Copyable<T> {
    fun copy(other: T)
}

class MyQueue<T: Copyable<T>>(val capacity: Int) {
    private val lock = ReentrantLock()
    private val data = ArrayList<T>(capacity)
    private var head = -1
    private var last = -1
    var size = 0
        private set

    val empty: Boolean
        get() = lock.withLock{ size == 0 }

    val full: Boolean
        get() = lock.withLock { size >= capacity }

    fun peek(buf: T): Boolean = lock.withLock {
        if (empty) {
            Log.e(TAG, "MyQueue is empty")
            false
        } else {
            buf.copy(data[head])
            true
        }
    }

    fun pop(buf: T): Boolean = lock.withLock {
        if (empty) {
            Log.e(TAG, "MyQueue is empty")
            false
        } else {
            buf.copy(data[head])

            --size
            if (size == 0) {
                head = -1
                last = -1
            } else {
                head = (head + 1) % capacity
            }
            true
        }
    }

    fun push(buf: T): Boolean = lock.withLock {
        if (full) {
            Log.e(TAG, "MyQueue is full")
            false
        } else {
            last = (last + 1) % capacity
            data[last].copy(buf)

            ++size
            if (size == 1) {
                head = last
            }

            true
        }

    }

    fun iterate(action: (T)->Boolean): Unit = lock.withLock {
        var i = head
        for (c in 0 until size) {
            if (!action(data[i])) break;
            i = (i + 1) / capacity
        }
    }

    fun clear(): Unit = lock.withLock {
        size = 0
        head = -1
        last = -1
    }
}