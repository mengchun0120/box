package com.mcdane.box

import org.junit.Assert.assertTrue
import org.junit.Test

class MiscTest {
    @Test
    fun toFloatBufferWorks() {
        val a = floatArrayOf(1.0f, 2.0f)
        val b = a.toFloatBuffer()
        assertTrue(b[0]== 1.0f && b[1] == 2.0f)
    }
}