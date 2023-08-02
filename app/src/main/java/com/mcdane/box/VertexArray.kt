package com.mcdane.box

import android.util.Log
import android.opengl.GLES30 as GL
import java.nio.ByteBuffer
import java.nio.IntBuffer

class BufferBlock(
    val data: FloatArray,
    val numVertices: Int,
    val vertexSize: Int,
    val stride: Int
) {
    constructor(_data: FloatArray, floatsPerVertex: Int, _stride: Int = 0):
        this(
            _data,
            _data.size / floatsPerVertex,
            floatsPerVertex * Float.SIZE_BYTES,
            _stride
        )
}

class BufferDescriptor(
    val numVertices: Int,
    val vertexSize: Int,
    val stride: Int,
    val offset: Int
) {
    val totalSize = numVertices * vertexSize

    constructor(b: BufferBlock, offset: Int) :
        this(b.numVertices, b.vertexSize, b.stride, offset)
}

class VertexArray {
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

    constructor(vararg a: BufferBlock) {
        init(a.asIterable())
    }

    constructor(a: Iterable<BufferBlock>) {
        init(a)
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

    private fun init(a: Iterable<BufferBlock>) {
        initArrayObj()
        initBufferObj()
        initBlocks(a)
    }

    private fun initArrayObj() {
        GL.glGenVertexArrays(1, arrayObjBuf)
        if (arrayObj == 0) {
            throw RuntimeException("glGenVertexArrays failed")
        }
    }

    private fun initBufferObj() {
        GL.glGenBuffers(1, bufferObjBuf)
        if (bufferObj == 0) {
            throw RuntimeException("glGenBuffers failed")
        }
    }

    private fun initBlocks(a: Iterable<BufferBlock>) {
        var offset = 0
        for (blk in a) {
            storeBlock(blk, offset).apply {
                blocks.add(this)
                offset += this.totalSize
            }
        }
    }

    private fun storeBlock(blk: BufferBlock, offset: Int): BufferDescriptor =
        BufferDescriptor(blk, offset).also {
            GL.glBufferSubData(GL.GL_ARRAY_BUFFER, offset, it.totalSize, blk.data.toFloatBuffer())
        }
}