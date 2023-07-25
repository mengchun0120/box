package com.mcdane.box

import android.util.Log
import android.opengl.GLES30 as GL
import java.nio.ByteBuffer
import java.nio.IntBuffer

class BufferBlock(
    val buf: ByteBuffer,
    val numVertices: Int,
    val vertexSize: Int,
    val stride: Int
)

class BufferDescriptor(
    val numVertices: Int,
    val vertexSize: Int,
    val stride: Int,
    val offset: Int
) {
    val totalSize = numVertices * vertexSize

    constructor(b: BufferBlock, offset: Int) :
        this(b.numVertices, b.vertexSize, b.stride, offset) {
    }
}

class VertexArray private constructor() {
    private val arrayObjBuf = IntBuffer.allocate(1)
    private val bufferObjBuf = IntBuffer.allocate(1)
    private val blocks = mutableListOf<BufferDescriptor>()

    var arrayObj: Int
        get() = arrayObjBuf[0]
        private set(value) {
            arrayObjBuf.put(0, value)
        }

    var bufferObj: Int
        get() = bufferObjBuf[0]
        private set(value) {
            bufferObjBuf.put(0, value)
        }

    val numBlocks: Int
        get() = blocks.size

    companion object {
        fun create(buffers: List<BufferBlock>): VertexArray? =
           VertexArray().takeIf{ it.init(buffers) }
    }

    fun close() {
        if (bufferObj != 0) {
            GL.glDeleteBuffers(1, bufferObjBuf)
            bufferObj = 0
        }
        if (arrayObj != 0) {
            GL.glDeleteVertexArrays(1, arrayObjBuf)
            arrayObj = 0
        }
    }

    fun offset(index: Int) = blocks[index].offset

    fun numVertices(index: Int) = blocks[index].numVertices

    fun vertexSize(index: Int) = blocks[index].vertexSize

    fun stride(index: Int) = blocks[index].stride

    private fun init(buffers: List<BufferBlock>): Boolean =
        if (initArrayObj() && initBufferObj()) {
            initBlocks(buffers)
            true
        } else {
            close()
            false
        }

    private fun initArrayObj(): Boolean {
        GL.glGenVertexArrays(1, arrayObjBuf)
        return if (arrayObj != 0) {
            true
        } else {
            Log.e(TAG, "glGenVertexArrays failed")
            false
        }
    }

    private fun initBufferObj(): Boolean {
        GL.glGenBuffers(1, bufferObjBuf)
        return if (bufferObj != 0) {
            true
        } else {
            Log.e(TAG, "glGenBuffers failed")
            false
        }
    }

    private fun initBlocks(buffers: Collection<BufferBlock>) {
        var offset = 0
        for (blk in buffers)
            storeBlock(blk, offset).apply {
                blocks.add(this)
                offset += this.totalSize
            }
    }

    private fun storeBlock(blk: BufferBlock, offset: Int): BufferDescriptor =
        BufferDescriptor(blk, offset).also {
            GL.glBufferSubData(GL.GL_ARRAY_BUFFER, offset, it.totalSize, blk.buf)
        }
}