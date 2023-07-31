package com.mcdane.box

import android.content.res.Resources
import android.util.Log
import android.opengl.GLES30 as GL
import java.nio.IntBuffer

enum class ShaderType{
    VERTEX_SHADER, FRAG_SHADER;

    fun toGLType(): Int =
        when (this) {
            VERTEX_SHADER -> GL.GL_VERTEX_SHADER
            FRAG_SHADER -> GL.GL_FRAGMENT_SHADER
        }
}

fun isShaderCompileSuccessful(shader: Int): Boolean {
    val status = IntBuffer.allocate(1)
    GL.glGetShaderiv(shader, GL.GL_COMPILE_STATUS, status)
    return status[0] != 0
}

fun shaderInfo(shader: Int): String =
    GL.glGetShaderInfoLog(shader)

fun compileShader(shader: Int, source: String):  Boolean {
    GL.glShaderSource(shader, source)
    GL.glCompileShader(shader)
    return isShaderCompileSuccessful(shader)
}

fun createShader(type: ShaderType, source: String): Int {
    val shader = GL.glCreateShader(type.toGLType())
    if (shader == 0) throw RuntimeException("glCreateShader failed")

    if (!compileShader(shader, source)) {
        GL.glDeleteShader(shader)
        throw RuntimeException("Failed to compile $type: ${shaderInfo(shader)}")
    }

    Log.i(TAG, "Shader $type created successfully")

    return shader
}

fun createShader(type: ShaderType, res: Resources, resId: Int): Int =
    createShader(type, readText(res, resId))

fun isProgramLinkSuccessful(program: Int): Boolean {
    val status = IntBuffer.allocate(1)
    GL.glGetProgramiv(program, GL.GL_LINK_STATUS, status)
    return status[0] != 0
}

fun programInfo(program: Int): String =
    GL.glGetProgramInfoLog(program)

fun linkProgram(program: Int, vertexShader: Int, fragShader: Int): Boolean {
    GL.glAttachShader(program, vertexShader)
    GL.glAttachShader(program, fragShader)
    GL.glLinkProgram(program)
    return isProgramLinkSuccessful(program)
}

fun detachShader(program: Int, shader: Int) {
    if (shader != 0) {
        GL.glDetachShader(program, shader)
    }
}

fun deleteShader(shader: Int) {
    if (shader != 0) {
        GL.glDeleteShader(shader)
    }
}

fun destroyProgramAndShader(program: Int, vertexShader: Int, fragShader: Int) {
   if (program != 0) {
        detachShader(program, vertexShader)
        detachShader(program, fragShader)
        GL.glDeleteProgram(program)
        Log.e(TAG, "Program destroyed")
    }

    deleteShader(vertexShader)
    deleteShader(fragShader)
}

fun createProgram(vertexShader: Int, fragShader: Int): Int {
    val program = GL.glCreateProgram()
    if (program == 0) throw RuntimeException("glCreateProgram failed")

    if (!linkProgram(program, vertexShader, fragShader)) {
        GL.glDeleteProgram(program)
        throw RuntimeException("Failed to link program: ${programInfo(program)}")
    }

    return program
}