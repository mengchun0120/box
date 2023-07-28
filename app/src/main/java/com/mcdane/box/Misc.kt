package com.mcdane.box

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

const val TAG = "Game"
const val FLOATS_PER_POS_2D = 2
const val FLOATS_PER_TEXPOS_2D = 2
const val FLOATS_PER_DIRECTION_2D = 2
const val POS_SIZE_2D = FLOATS_PER_POS_2D * Float.SIZE_BYTES
const val TEXPOS_SIZE_2D = FLOATS_PER_TEXPOS_2D * Float.SIZE_BYTES
const val DIRECTION_SIZE_2D = FLOATS_PER_DIRECTION_2D * Float.SIZE_BYTES

fun FloatArray.toFloatBuffer(): FloatBuffer =
    ByteBuffer.allocate(this.size * Float.SIZE_BYTES).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply {
            put(this)
            position(0)
        }
    }