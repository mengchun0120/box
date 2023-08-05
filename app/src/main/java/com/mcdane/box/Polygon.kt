package com.mcdane.box

import android.opengl.GLES30 as GL

class Polygon(
    posData: FloatArray,
    floatsPerPos: Int = FLOATS_PER_POS_2D,
    texPosData: FloatArray? = null,
    floatsPerTexPos: Int = FLOATS_PER_TEXPOS_2D,
) {
    private val pos = VertexArray(posData, floatsPerPos)
    private val texPos: VertexArray? = texPosData?.run{ VertexArray(texPosData, floatsPerTexPos) }

    constructor(
        posData: Iterable<Vector>,
        floatsPerPos: Int = FLOATS_PER_POS_2D,
        texPosData: Iterable<Vector>? = null,
        floatsPerTexPos: Int = FLOATS_PER_TEXPOS_2D,
    ): this(
        posData.toFloatArray(),
        floatsPerPos,
        texPosData?.toFloatArray(),
        floatsPerTexPos,
    )

    fun draw(
        program: SimpleProgram,
        objRef: Vector?,
        direction: Vector?,
        fillColor: Color?,
        borderColor: Color?,
        textureId: Int,
        texColor: Color?,
        fillMode: Int = GL.GL_TRIANGLE_FAN,
        fillStart: Int = 0,
        fillVertexCount: Int = 0,
        borderMode: Int = GL.GL_LINE_LOOP,
        borderStart: Int = 1,
        borderVertexCount: Int = 0
    ) {
        if (objRef != null) {
            program.setUseObjRef(true)
            program.setObjRef(objRef.data)
        } else {
            program.setUseObjRef(false)
        }

        if (direction != null) {
            program.setUseDirection(true)
            program.setDirection(direction.data)
        } else {
            program.setUseDirection(false)
        }

        program.setPosition(pos)
        program.setUseColor(textureId == 0)

        if (textureId == 0) {
            fillColor?.apply {
                program.setColor(this)

                val count = if (fillVertexCount > 0) {
                    fillVertexCount
                } else {
                    pos.numVertices
                }

                GL.glDrawArrays(fillMode, fillStart, count)
            }

            borderColor?.apply {
                program.setColor(this)

                val count = if (borderVertexCount > 0) {
                    borderVertexCount
                } else {
                    pos.numVertices - 2
                }

                GL.glDrawArrays(borderMode, borderStart, count)
            }
        } else {
            program.setTexPos(texPos!!)
            program.setTexture(textureId)

            if (texColor != null) {
                program.setUseTexColor(true)
                program.setTexColor(texColor)
            } else {
                program.setUseTexColor(false)
            }

            val count = if (fillVertexCount > 0) {
                fillVertexCount
            } else {
                pos.numVertices
            }

            GL.glDrawArrays(fillMode, fillStart, count)
        }
    }
}