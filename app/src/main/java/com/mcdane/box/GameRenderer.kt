package com.mcdane.box

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
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
    private val boardLeft = 20.0f
    private lateinit var curBox: Box
    private var curBoxRow = 10
    private var curBoxCol = 8
    private lateinit var curBoxPos: Vector
    private val colors = listOf(
        Color(255, 0, 0, 255),
        Color(0, 0, 255, 255),
    )

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        Box.init(context.assets)

        GL.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GL.glEnable(GL.GL_BLEND)
        GL.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA)

        program = SimpleProgram(context.resources)
        program.use()

        textSys = TextSystem(context.assets, "font")

        curBox = Box(0, 0)
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GL.glViewport(0, 0, width, height)

        val viewportSize = Vector(width.toFloat(), height.toFloat())
        val viewportOrigin = viewportSize / 2.0f

        program.setViewportSize(viewportSize.data)
        program.setViewportOrigin(viewportOrigin.data)

        board.pos = Vector(
            boardLeft,
            (height.toFloat() - board.height) / 2.0f
        )

        curBoxPos = Vector(
            board.pos[0] + 1.0f + curBoxCol * Box.boxSpan,
            board.pos[1] + 1.0f + curBoxRow * Box.boxSpan
        )
        Log.i(TAG, "curBoxPos=$curBoxPos")
    }

    override fun onDrawFrame(p0: GL10?) {
        GL.glClear(GL.GL_COLOR_BUFFER_BIT)

        program.use()
        program.setAlpha(1.0f)
        //board.draw(program)

        Log.i(TAG, "curBoxPos=$curBoxPos")
        //Box.rect.draw(program, curBoxPos, null, colors[0], null)
        curBox.draw(program, curBoxPos, colors[0])
    }

    fun handlePointerDown(event: MotionEvent) {
        if (curBox.index == Box.maxIndex) {
            curBox.type = if(curBox.type == Box.maxType) 0 else curBox.type + 1
            curBox.index = 0
        } else {
            curBox.index++
        }
    }

    fun close() {
        program.close()
    }
}