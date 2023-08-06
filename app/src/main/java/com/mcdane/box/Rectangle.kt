package com.mcdane.box

import android.opengl.GLES30 as GL

private fun createRectPosData(width: Float, height: Float): FloatArray =
    floatArrayOf(
        width / 2.0f, height / 2.0f,
        0.0f, 0.0f,
        width, 0.0f,
        width, height,
        0.0f, height,
        0.0f, 0.0f,
    )

private fun createRectTexData(): FloatArray =
    floatArrayOf(
        0.5f, 0.5f,
        0.0f, 0.0f,
        1.0f, 0.0f,
        1.0f, 1.0f,
        0.0f, 1.0f,
        0.0f, 0.0f,
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