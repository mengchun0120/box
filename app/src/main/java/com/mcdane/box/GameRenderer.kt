package com.mcdane.box

import android.content.Context
import android.opengl.GLSurfaceView
import org.w3c.dom.Text
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES30 as GL

class GameRenderer(private val context: Context): GLSurfaceView.Renderer {
    private val rect1 = Rectangle(200.0f, 300.0f, false)
    private val rect2 = Rectangle(300.0f, 168.0f, true)
    private val rectPos1 = Vector(200.0f, 200.0f)
    private val rectPos2 = Vector(400.0f, 800.0f)
    private val strPos = Vector(700.0f, 900.0f)
    private val fillColor = Color(255, 100, 0, 255)
    private val borderColor = Color(0, 0, 255, 255)
    private val helloStr = "Hello World"
    private lateinit var program: SimpleProgram
    private lateinit var textSys: TextSystem
    private lateinit var baby: Texture

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GL.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)

        program = SimpleProgram(context.resources)
        program.use()

        textSys = TextSystem(context.assets, "assets")
        baby = Texture(context.resources, R.raw.baby)
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
        textSys.draw(program, helloStr, strPos, TextSize.MEDIUM, fillColor)
    }

    fun close() {
        program.close()
    }
}