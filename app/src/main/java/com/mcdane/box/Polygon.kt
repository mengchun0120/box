package com.mcdane.box

import android.opengl.GLES30 as GL

class Polygon private constructor(val va: VertexArray) {

    companion object {
        fun create(
            posData: FloatArray,
            texPosData: FloatArray?,
            floatsPerPos: Int = FLOATS_PER_POS_2D,
            floatsPerTexPos: Int = FLOATS_PER_TEXPOS_2D
        ): Polygon? =
            VertexArray.create(
                prepareBlocks(posData, texPosData, floatsPerPos, floatsPerTexPos)
            )?.run { Polygon(this) }

        private fun prepareBlocks(
            posData: FloatArray,
            texPosData: FloatArray?,
            floatsPerPos: Int,
            floatsPerTexPos: Int
        ): List<BufferBlock> =
            mutableListOf( BufferBlock(posData, floatsPerPos) ).also {
                if (texPosData != null) {
                    it.add( BufferBlock(texPosData, floatsPerTexPos) )
                }
            }
    }

    fun draw(
        program: SimpleProgram,
        objRef: Vector?,
        direction: Vector?,
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
        if (objRef != null) {
            program.setUseColor(true)
            program.setObjRef(objRef.data)
        } else {
            program.setUseColor(false)
        }

        if (direction != null) {
            program.setUseDirection(true)
            program.setDirection(direction.data)
        } else {
            program.setUseDirection(false)
        }

        program.setPositionTexPos(va);
        program.setUseColor(textureId == 0);

        if (textureId == 0) {
            if (fillColor != null) {
                program.setColor(fillColor.data)

                val count = if (fillVertexCount > 0) {
                    fillVertexCount
                } else {
                    va.vertexSize(0)
                }

                GL.glDrawArrays(fillMode, fillStart, count)
            }

            if (borderColor != null) {
                program.setColor(borderColor.data)

                val count = if (borderVertexCount > 0) {
                    borderVertexCount
                } else {
                    va.vertexSize(0) - 2
                }

                GL.glDrawArrays(borderMode, borderStart, count)
            }
        } else {
            program.setTexture(textureId)

            if (texColor != null) {
                program.setUseTexColor(true)
                program.setTexColor(texColor.data)
            } else {
                program.setUseTexColor(false)
            }

            val count = if (fillVertexCount > 0) {
                fillVertexCount
            } else {
                va.vertexSize(0)
            }

            GL.glDrawArrays(fillMode, fillStart, count)
        }
    }
}