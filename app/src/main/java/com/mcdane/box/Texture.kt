package com.mcdane.box

import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLUtils
import android.opengl.GLES30 as GL

class Texture {
    private val ids: IntArray
    val id: Int
        get() = ids[0]
    val width: Float
    val height: Float

    fun close() {
        GL.glDeleteTextures(1, ids, 0)
    }

    constructor(bitmap: Bitmap) {
        ids = createTexture(bitmap)
        width = bitmap.width.toFloat()
        height = bitmap.height.toFloat()
    }

    constructor(res: Resources, resId: Int) {
        val bitmap = BitmapFactory.decodeResource(res, resId)
        ids = createTexture(bitmap)
        width = bitmap.width.toFloat()
        height = bitmap.height.toFloat()
        bitmap.recycle()
    }

    constructor(mgr: AssetManager, path: String) {
        val bitmap = BitmapFactory.decodeStream(mgr.open(path))
        ids = createTexture(bitmap)
        width = bitmap.width.toFloat()
        height = bitmap.height.toFloat()
        bitmap.recycle()
    }

    private fun createTexture(bitmap: Bitmap): IntArray {
        val textureIds = IntArray(1)
        GL.glGenTextures(1, textureIds, 0)
        if (textureIds[0] == 0) throw RuntimeException("glGenTextures failed")

        GL.glBindTexture( GL.GL_TEXTURE_2D, textureIds[0] );
        GL.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR );
        GL.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR );
        GL.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        GL.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GL.GL_TEXTURE_2D, 0, bitmap, 0)
        GL.glGenerateMipmap(GL.GL_TEXTURE_2D)

        return textureIds
    }

}