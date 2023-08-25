package com.mcdane.box

import android.app.Activity
import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent

class GameView(activity: Activity): GLSurfaceView(activity) {
    private val renderer = GameRenderer(activity)

    init {
        setEGLContextClientVersion(3)
        setRenderer(renderer)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val pos = IntArray(2) {0}
        getLocationOnScreen(pos)
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                queueEvent {
                    renderer.enqueueEvent(
                        PointerAction.DOWN,
                        event.rawX - pos[0],
                        event.rawY - pos[1]
                    )
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                queueEvent {
                    renderer.enqueueEvent(
                        PointerAction.UP,
                        event.rawX - pos[0],
                        event.rawY - pos[1]
                    )
                }
                return true
            }
        }

        return false
    }

    fun close() {
        renderer.close()
    }
}