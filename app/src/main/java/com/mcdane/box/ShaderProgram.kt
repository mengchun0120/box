package com.mcdane.box

import android.content.res.Resources
import android.util.Log
import android.opengl.GLES30 as GL

open class ShaderProgram {
    protected var program: Int = 0
    protected var vertexShader: Int = 0
    protected var fragShader: Int = 0

    protected fun init(res: Resources, vertexShaderResId: Int, fragShaderResId: Int): Boolean {
        vertexShader = createShader(ShaderType.VERTEX_SHADER, res, vertexShaderResId)
        if (vertexShader == 0) {
            return false
        }

        fragShader = createShader(ShaderType.FRAG_SHADER, res, fragShaderResId)
        if (fragShader == 0) {
            return false
        }

        program = createProgram(vertexShader, fragShader)
        return true
    }

    open fun close() {
        destroyProgramAndShader(program, vertexShader, fragShader)
        program = 0
        vertexShader = 0
        fragShader = 0
    }

    fun use() = GL.glUseProgram(program)

    fun getUniformLocation(name: String): Int? =
        GL.glGetUniformLocation(program, name).let {
            if (it >= 0) {
                it
            } else {
                Log.e(TAG, "Failed to find uniform variable $name")
                null
            }
        }

    fun getAttributeLocation(name: String): Int? =
        GL.glGetAttribLocation(program, name).let {
            if (it >= 0) {
                it
            } else {
                Log.e(TAG, "Failed to find attribute $name")
                null
            }
        }
}