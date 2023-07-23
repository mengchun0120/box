package com.mcdane.box

import android.content.Context
import android.opengl.GLSurfaceView

class GameView(cxt: Context): GLSurfaceView(cxt) {
    private val renderer = GameRenderer(cxt)

    init {
        setEGLContextClientVersion(3)
        setRenderer(renderer)
    }
}