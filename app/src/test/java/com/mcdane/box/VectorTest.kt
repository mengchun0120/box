package com.mcdane.box

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class VectorTest {
    @Test
    fun equals_works() {
        val t1 = Vector(1.0f, 2.0f)
        val t2 = Vector(2.0f, 3.0f)
        val t3 = Vector(1.0f, 2.0f)

        assertTrue(t1 == t3)
        assertTrue(t1 != t2)
    }
}