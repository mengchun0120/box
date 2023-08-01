package com.mcdane.box

import android.content.Context
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES30 as GL

class GameRenderer(private val context: Context): GLSurfaceView.Renderer {
    private lateinit var program: SimpleProgram
    private lateinit var triangle: Polygon

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GL.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)


    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GL.glViewport(0, 0, width, height)

        val viewportSize = Vector(width.toFloat(), height.toFloat())
        val viewportOrigin = viewportSize / 2.0f

        program.setViewportSize(viewportSize.data)
        program.setViewportOrigin(viewportOrigin.data)
    }

    override fun onDrawFrame(p0: GL10?) {
        GL.glClear(GL.GL_COLOR_BUFFER_BIT)
    }

    private fun initShader() {
        program = SimpleProgram(context.resources)
        program.use()
    }

    private fun initShape() {

    }
}