package com.mcdane.box

import android.opengl.GLES30 as GL

private fun createRectPosData(width: Float, height: Float): FloatArray {
    val w = width / 2.0f
    val h = height / 2.0f
    return floatArrayOf(
        0.0f, 0.0f,
        w, h,
        -w, h,
        -w, -h,
        w, -h,
        w, h,
    )
}

private fun createRectTexData(): FloatArray =
    floatArrayOf(
        0.5f, 0.5f,
        1.0f, 0.0f,
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,
    )

class Rectangle(val width: Float, val height: Float, val hasTexture: Boolean = true):
    Polygon(
        createRectPosData(width, height),
        if (hasTexture) createRectTexData() else null
    )
{
    fun draw(
        program: SimpleProgram,
        objRef: Vector?,
        direction: Vector?,
        fillColor: Color?,
        borderColor: Color?,
        textureId: Int = 0,
        texColor: Color? = null,
    ) = super.draw(
        program,
        objRef,
        direction,
        fillColor,
        borderColor,
        textureId,
        texColor,
        GL.GL_TRIANGLE_FAN,
        0,
        6,
        GL.GL_LINE_LOOP,
        1,
        4,
    )
}