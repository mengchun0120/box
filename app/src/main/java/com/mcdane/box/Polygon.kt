package com.mcdane.box

import android.opengl.GLES30 as GL

class Polygon(
    posData: FloatArray,
    floatsPerPos: Int = FLOATS_PER_POS_2D,
    texPosData: FloatArray?,
    floatsPerTexPos: Int = FLOATS_PER_TEXPOS_2D,
) {

    private val va = VertexArray(prepareBlocks(posData, floatsPerPos, texPosData, floatsPerTexPos))

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

        program.setPositionTexPos(va)
        program.setUseColor(textureId == 0)

        if (textureId == 0) {
            if (fillColor != null) {
                program.setColor(fillColor.data)

                val fillSize = if (fillVertexCount > 0) {
                    fillVertexCount
                } else {
                    va.numVertices(0)
                }

                GL.glDrawArrays(fillMode, fillStart, fillSize)
            }

            if (borderColor != null) {
                program.setColor(borderColor.data)
                val borderSize = if (borderVertexCount > 0) {
                    borderVertexCount
                } else {
                    va.numVertices(0) - 2
                }

                GL.glDrawArrays(borderMode, borderStart, borderSize)
            }
        } else {
            program.setTexture(textureId)

            if (texColor != null) {
                program.setUseTexColor(true)
                program.setTexColor(texColor.data)
            } else {
                program.setUseTexColor(false)
            }

            val fillSize = if (fillVertexCount > 0) {
                fillVertexCount
            } else {
                va.numVertices(0)
            }

            GL.glDrawArrays(fillMode, fillStart, fillSize)
        }
    }

    fun close() {
        va.close()
    }

    private fun prepareBlocks(
        posData: FloatArray,
        floatsPerPos: Int,
        texPosData: FloatArray?,
        floatsPerTexPos: Int
    ): List<BufferBlock> =
        mutableListOf( BufferBlock(posData, floatsPerPos) ).also {
            if (texPosData != null) {
                it.add( BufferBlock(texPosData, floatsPerTexPos) )
            }
        }
}