package com.mcdane.box

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

class GameView(cxt: Context): GLSurfaceView(cxt) {
    private val renderer = GameRenderer(cxt)

    init {
        setEGLContextClientVersion(3)
        setRenderer(renderer)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.actionMasked == MotionEvent.ACTION_DOWN) {
            queueEvent {
                renderer.handlePointerDown(event)
            }
        }

        return false
    }

    fun close() {
        renderer.close()
    }
}