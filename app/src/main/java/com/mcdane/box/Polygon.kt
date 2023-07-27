package com.mcdane.box

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
}