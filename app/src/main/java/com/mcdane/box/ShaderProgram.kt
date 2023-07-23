package com.mcdane.box

import android.content.res.Resources
import android.util.Log
import android.opengl.GLES30 as GL

open class ShaderProgram {
    protected var program: Int = 0
    protected var vertexShader: Int = 0
    protected var fragShader: Int = 0

    val valid = program != 0 && vertexShader != 0 && fragShader != 0

    fun init(res: Resources, vertexShaderResId: Int, fragShaderResId: Int): Boolean {
        vertexShader = createShader(ShaderType.VERTEX_SHADER, res, vertexShaderResId)
        if (vertexShader == 0) {
            return false
        }

        fragShader = createShader(ShaderType.FRAG_SHADER, res, fragShaderResId)
        if (fragShader == 0) {
            return false
        }

        program = createProgram(vertexShader, fragShader)
        return program != 0
    }

    fun close() {
        destroyProgramAndShader(program, vertexShader, fragShader)
        program = 0
        vertexShader = 0
        fragShader = 0
    }

    fun use() = GL.glUseProgram(program)

    fun getUniformLocation(name: String): Int? {
        val loc = GL.glGetUniformLocation(program, name)
        return if (loc >= 0) {
            loc
        } else {
            Log.e(TAG, "Failed to find uniform variable $name")
            null
        }
    }

    fun getAttributeLocation(name: String): Int? {
        val loc = GL.glGetAttribLocation(program, name)
        return if (loc >= 0) {
            loc
        } else {
            Log.e(TAG, "Failed to find attribute $name")
            null
        }
    }
}