package com.mcdane.box

import android.content.Context
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES30 as GL

class GameRenderer(private val context: Context): GLSurfaceView.Renderer {
    private lateinit var program: SimpleProgram
    private lateinit var rect1: Rectangle
    private lateinit var rectPos1: Vector
    private lateinit var rect2: Rectangle
    private lateinit var rectPos2: Vector
    private lateinit var fillColor: Color
    private lateinit var borderColor: Color
    private lateinit var baby: Texture

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GL.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)

        initShader()
        initShapes()
        initPos()
        initColors()
        initTextures()
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
        rect1.draw(program, rectPos1, null, fillColor, borderColor)
        rect2.draw(program, rectPos2, null, null, null, baby.id, null)
    }

    fun close() {
        program.close()
    }

    private fun initShader() {
        program = SimpleProgram(context.resources)
        program.use()
    }

    private fun initShapes() {
        rect1 = Rectangle(200.0f, 300.0f, false)
        rect2 = Rectangle(300.0f, 168.0f, true)
    }

    private fun initPos() {
        rectPos1 = Vector(200.0f, 200.0f)
        rectPos2 = Vector(400.0f, 800.0f)
    }

    private fun initColors() {
        fillColor = Color(255, 255, 0, 255)
        borderColor = Color(0, 0, 255, 255)
    }

    private fun initTextures() {
        baby = Texture(context.resources, R.raw.baby)
    }
}