package com.mcdane.box

import org.junit.Test

import org.junit.Assert.*

class VectorTest {
    @Test
    fun accessor_works() {
        val t1 = Vector(1.0f, 12.0f, 23.0f, 5.0f, 9.0f)

        assertTrue(t1[0] == 1.0f)

        t1[2] = 4.0f
        assertTrue(t1[2] == 4.0f)

        val (a1, a2, a3, a4, a5) = t1
        assertTrue(a1 == 1.0f && a2 == 12.0f && a3 == 4.0f && a4 == 5.0f && a5 == 9.0f)
    }

    @Test
    fun equals_works() {
        val t1 = Vector(1.0f, 2.0f)
        val t2 = Vector(2.0f, 3.0f)
        val t3 = Vector(1.0f, 2.0f)
        val t4 = Vector(1.0000001f, 2.000001f)
        val t5 = Vector(1.00004f, 2.000005f)

        assertTrue(t1 == t3)
        assertTrue(t1 != t2)
        assertTrue(t1.fuzzyEqual(t4))
        assertTrue(!t1.fuzzyEqual(t5))
    }

    @Test
    fun arithmetic_works() {
        val t1 = Vector(1.0f, 2.0f)
        val t2 = Vector(2.0f, 3.0f)
        val t3 = Vector(3.0f, 4.0f)
        val t4 = Vector(4.0f, 6.0f)

        assertTrue(t1 + t2 == Vector(3.0f, 5.0f))
        assertTrue(t1 - t2 == Vector(-1.0f, -1.0f))
        assertTrue(t1 * 2.0f == Vector(2.0f, 4.0f))
        assertTrue(t1 * t2 == Vector(2.0f, 6.0f))
        assertTrue(t1 / 2.0f == Vector(0.5f, 1.0f))
        assertTrue(t2 / t1 == Vector(2.0f, 1.5f))
        assertTrue(-t1 == Vector(-1.0f, -2.0f))
        assertTrue(t3.norm == 5.0f)
        t3.normalize()
        assertTrue(t3 == Vector(0.6f, 0.8f))
        t2.negate()
        assertTrue(t2 == Vector(-2.0f, -3.0f))
        assertTrue(t2.abs() == Vector(2.0f, 3.0f))
        assertTrue(t1.dist(t4) == 5.0f)
    }

    @Test
    fun assign_copy_works() {
        var t1 = Vector(1.0f, 2.0f)

        t1.assign(Vector(2.0f, 5.0f))
        assertTrue(t1 == Vector(2.0f, 5.0f))

        t1.assign(listOf(3.0f, 4.0f))
        assertTrue(t1 == Vector(3.0f, 4.0f))

        var t2 = t1.copy()
        assertTrue(t2 == Vector(3.0f, 4.0f))
    }
}