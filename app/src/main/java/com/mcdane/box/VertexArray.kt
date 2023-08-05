package com.mcdane.box

import java.nio.FloatBuffer

class VertexArray(a: FloatArray, val floatsPerVertex: Int) {
    val data: FloatBuffer = a.toFloatBuffer()
    val numVertices: Int = a.size / floatsPerVertex
}