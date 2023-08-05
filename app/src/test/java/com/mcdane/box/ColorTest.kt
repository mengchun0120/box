package com.mcdane.box

import org.junit.Test
import org.junit.Assert.*

class ColorTest {
    @Test
    fun constructorWithRGBWorks() {
        val c = Color(125, 25, 30, 255)
        assertTrue(c.red == 125 / 255f && c.green == 25 / 255f &&
                   c.blue == 30 / 255f && c.alpha == 1.0f)

        var exceptionHappened = false
        try {
            Color(256, 120, 80, 20)
        } catch (e: IllegalArgumentException) {
            exceptionHappened = true
        }
        assertTrue(exceptionHappened)
    }

    @Test
    fun constructorWithCollectionWorks() {
        val c = Color(listOf(100, 200, 230, 70))
        assertTrue(c.red == 100 / 255f && c.green == 200 / 255f &&
                   c.blue == 230 / 255f && c.alpha == 70 / 255f)

        var exceptionHappened = false
        try {
            Color(listOf(100, 200, 230, 120, 50))
        } catch (e: IllegalArgumentException) {
            exceptionHappened = true
        }
        assertTrue(exceptionHappened)

        exceptionHappened = false
        try {
            Color(listOf(0, 300, 200, 120))
        } catch (e: IllegalArgumentException) {
            exceptionHappened = true
        }
        assertTrue(exceptionHappened)
    }

    @Test
    fun constructorWithArrayWorks() {
        val c = Color(floatArrayOf(1.0f, 0.5f, 0.5f, 1.0f))
        assertTrue(c.red == 1.0f && c.green == 0.5f && c.blue == 0.5f && c.alpha == 1.0f)

        var exceptionHappened = false
        try {
            Color(floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f, 1.0f))
        } catch (e: IllegalArgumentException) {
            exceptionHappened = true
        }
        assertTrue(exceptionHappened)

        exceptionHappened = false
        try {
            Color(floatArrayOf(1.0f, 1.02f, 0.5f, 1.0f))
        } catch (e: IllegalArgumentException) {
            exceptionHappened = true
        }
        assertTrue(exceptionHappened)
    }

    @Test
    fun equalsWorks() {
        val c1 = Color(100, 200, 255, 255)
        val c2 = Color(100, 200, 255, 255)
        val c3 = Color(150, 250, 120, 255)
        assertTrue(c1 == c2)
        assertTrue(c1 != c3)
    }

    @Test
    fun assignWorks() {
        val c1 = Color()
        c1.assign(100, 200, 200, 210)
        assertTrue(
            c1.red == 100f / 255f &&
            c1.green == 200f / 255f &&
            c1.blue == 200f / 255f &&
            c1.alpha == 210f / 255f
        )

        c1.assign(1.0f, 0.5f, 1.0f, 0.5f)
        assertTrue(
            c1.red == 1.0f &&
            c1.green == 0.5f &&
            c1.blue == 1.0f &&
            c1.alpha == 0.5f
        )

        c1.assign(listOf(150, 130, 220, 220))
        assertTrue(
            c1.red == 150f / 255f &&
            c1.green == 130f / 255f &&
            c1.blue == 220f / 255f &&
            c1.alpha == 220f / 255f
        )

        c1.assign(floatArrayOf(1.0f, 0.2f, 0.7f, 0.75f))
        assertTrue(
            c1.red == 1.0f &&
            c1.green == 0.2f &&
            c1.blue == 0.7f &&
            c1.alpha == 0.75f
        )
    }
}