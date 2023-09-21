package com.mcdane.box

import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MiscTest {
    @Test
    fun toFloatBufferWorks() {
        val a = floatArrayOf(1.0f, 2.0f)
        val b = a.toFloatBuffer()
        assertTrue(b[0]== 1.0f && b[1] == 2.0f)
    }

    @Test
    fun byteBufferWorks() {
        val buf = ByteBuffer.allocate(100).apply{
            order(ByteOrder.LITTLE_ENDIAN)
        }

        assert(buf.hasArray())

        buf.putInt(200)
        buf.putInt(300)

        val buf2 = ByteBuffer.allocateDirect(100).apply {
            order(ByteOrder.LITTLE_ENDIAN)
        }

        with(buf) {
            buf2.put(array(), arrayOffset(), arrayOffset() + position())
        }

        buf2.rewind()
        val i1 = buf2.int
        assert(i1 == 200)
        val i2 = buf2.int
        assert(i2 == 300)
    }
}