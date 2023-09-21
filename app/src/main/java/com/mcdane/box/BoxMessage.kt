package com.mcdane.box

import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class BoxMessage: Copyable<BoxMessage> {
    companion object {
        const val BUFFER_LEN = 1000
    }

    val data = ByteBuffer.allocateDirect(BUFFER_LEN).apply{
        order(ByteOrder.LITTLE_ENDIAN)
    }

    var size: Int = 0
        private set

    override fun copy(other: BoxMessage) {
        clear()
        with(other.data) {
            data.put(array(), arrayOffset(), arrayOffset() + position())
            size = position()
        }
    }

    fun read(stream: InputStream) {
        clear()
        with(data) {
            size = stream.read(array(), arrayOffset(), BUFFER_LEN)
            limit(size)
        }
    }

    fun write(stream: OutputStream) {
        with(data) {
            stream.write(array(), arrayOffset(), arrayOffset() + position())
            stream.flush()
        }
    }

    fun clear() {
        data.clear()
        size = 0
    }
}