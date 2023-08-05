package com.mcdane.box

import android.content.res.Resources
import android.util.Log
import android.opengl.GLES30 as GL

open class ShaderProgram(
    res: Resources,
    vertexShaderResId: Int,
    fragShaderResId: Int
) {

    protected val program: Int
    protected val vertexShader: Int
    protected val fragShader: Int

    init {
        vertexShader = createShader(ShaderType.VERTEX_SHADER, res, vertexShaderResId)
        fragShader = createShader(ShaderType.FRAG_SHADER, res, fragShaderResId)
        program = createProgram(vertexShader, fragShader)
    }

    open fun close() {
        destroyProgramAndShader(program, vertexShader, fragShader)
    }

    fun use() = GL.glUseProgram(program)

    protected fun getUniformLocation(name: String): Int =
        GL.glGetUniformLocation(program, name).let {
            if (it >= 0) {
                it
            } else {
                throw RuntimeException("Failed to find uniform variable $name")

            }
        }

    protected fun getAttributeLocation(name: String): Int =
        GL.glGetAttribLocation(program, name).let {
            if (it >= 0) {
                it
            } else {
                throw RuntimeException("Failed to find attribute $name")
            }
        }
}