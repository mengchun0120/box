package com.mcdane.box

import android.content.Context
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES30 as GL

class GameRenderer(private val context: Context): GLSurfaceView.Renderer {
    private lateinit var program: SimpleProgram
    private lateinit var triangle: Polygon
    private lateinit var trianglePos: Vector
    private lateinit var fillColor: Color
    private lateinit var borderColor: Color

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GL.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)

        initShader()
        initShapes()
        initPos()
        initColors()
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

        program.use()
        program.setAlpha(1.0f)
        triangle.draw(
            program,
            trianglePos,
            null,
            fillColor,
            borderColor,
            0,
            null,
            GL.GL_TRIANGLES,
            0,
            3,
            GL.GL_LINE_LOOP,
            0,
            3
        )
    }

    fun close() {
        program.close()
    }

    private fun initShader() {
        program = SimpleProgram(context.resources)
        program.use()
    }

    private fun initShapes() {
        triangle = Polygon(
            posData = listOf(
                Vector(0.0f, 0.0f),
                Vector(100.0f, 0.0f),
                Vector(100.0f, 100.0f),
            )
        )
    }

    private fun initPos() {
        trianglePos = Vector(200.0f, 200.0f)
    }

    private fun initColors() {
        fillColor = Color(255, 255, 0, 255)
        borderColor = Color(0, 0, 255, 255)
    }
}