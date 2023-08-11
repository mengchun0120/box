package com.mcdane.box

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import org.w3c.dom.Text
import java.io.File
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random
import android.opengl.GLES30 as GL

class GameRenderer(private val context: Context): GLSurfaceView.Renderer {
    private lateinit var program: SimpleProgram
    private lateinit var textSys: TextSystem
    private val board = Board()
    private val colors = listOf(
        Color(255, 0, 0, 255),
        Color(0, 0, 255, 255),
    )

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GL.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GL.glEnable(GL.GL_BLEND)
        GL.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA)

        program = SimpleProgram(context.resources)
        program.use()

        textSys = TextSystem(context.assets, "font")


    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GL.glViewport(0, 0, width, height)

        val viewportSize = Vector(width.toFloat(), height.toFloat())
        val viewportOrigin = viewportSize / 2.0f

        program.setViewportSize(viewportSize.data)
        program.setViewportOrigin(viewportOrigin.data)

        randomizeBoard()
        board.pos = Vector(20.0f, 20.0f)
    }

    override fun onDrawFrame(p0: GL10?) {
        GL.glClear(GL.GL_COLOR_BUFFER_BIT)

        program.use()
        program.setAlpha(1.0f)
        board.draw(program)
    }

    fun close() {
        program.close()
    }

    fun randomizeBoard() {
        for (r in 0 until board.rows) {
            for (c in 0 until board.cols) {
                board[r, c] = if (Random.nextBoolean()) {
                    colors[Random.nextInt(0, 2)]
                } else {
                    null
                }
            }
        }
    }
}