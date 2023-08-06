package com.mcdane.box

import android.content.res.Resources
import android.opengl.GLES30 as GL

class Texture(res: Resources, resId: Int) {
    private val ids: IntArray = createTexture(res, resId)
    val id: Int
        get() = ids[0]

    fun close() {
        GL.glDeleteTextures(1, ids, 0)
    }
}