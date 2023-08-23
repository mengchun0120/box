package com.mcdane.box

import android.util.Log
import android.opengl.GLES30 as GL

open class Polygon(
    posData: FloatArray,
    texPosData: FloatArray? = null,
) {
    private val pos = VertexArray(posData, FLOATS_PER_POS_2D)
    private val texPos: VertexArray? = texPosData?.run {
        VertexArray(texPosData, FLOATS_PER_TEXPOS_2D)
    }

    constructor(
        posData: Iterable<Vector>,
        texPosData: Iterable<Vector>? = null,
    ): this(
        posData.toFloatArray(),
        texPosData?.toFloatArray(),
    )

    fun draw(
        program: SimpleProgram,
        x: Float,
        y: Float,
        fillColor: Color?,
        borderColor: Color?,
        textureId: Int,
        texColor: Color?,
        fillMode: Int,
        fillStart: Int,
        fillVertexCount: Int,
        borderMode: Int,
        borderStart: Int,
        borderVertexCount: Int
    ) {
        program.setObjRef(x, y)

        program.setPosition(pos)
        program.setUseColor(textureId == 0)

        if (textureId == 0) {
            fillColor?.let {
                program.setColor(it)
                GL.glDrawArrays(fillMode, fillStart, fillVertexCount)
            }
        } else {
            program.setTexPos(texPos!!)
            program.setTexture(textureId)

            program.setUseTexColor(texColor != null)
            texColor?.let {
                program.setTexColor(texColor)
            }

            GL.glDrawArrays(fillMode, fillStart, fillVertexCount)
        }

        borderColor?.let {
            program.setColor(it)
            GL.glDrawArrays(borderMode, borderStart, borderVertexCount)
        }
    }
}