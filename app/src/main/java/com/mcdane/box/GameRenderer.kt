package com.mcdane.box

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES30 as GL
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

class GameRenderer(private val context: Context): GLSurfaceView.Renderer {
    private lateinit var program: SimpleProgram
    private lateinit var textSys: TextSystem
    private val viewportSize = Vector(2)
    private val viewportOrigin = Vector(2)
    private val board = Board()
    private val boardLeft = 20.0f
    private lateinit var curBox: Box
    private var curBoxRow = 10
    private var curBoxCol = 8
    private lateinit var curBoxPos: Vector

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        initOpenGL()
        initGame()
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        resetViewport(width, height)
        resetGamePos()
    }

    override fun onDrawFrame(p0: GL10?) {
        GL.glClear(GL.GL_COLOR_BUFFER_BIT)
        board.draw(program)
        curBox.draw(program, curBoxPos)
    }

    fun handlePointerDown(event: MotionEvent) {
        if (curBox.index == Box.MAX_INDEX) {
            curBox.type = if(curBox.type == Box.maxType) 0 else curBox.type + 1
            curBox.index = 0
        } else {
            curBox.index++
        }
    }

    fun close() {
        program.close()
    }

    private fun initOpenGL() {
        GL.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GL.glEnable(GL.GL_BLEND)
        GL.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA)

        program = SimpleProgram(context.resources)
        program.use()
        program.setAlpha(1.0f)

        textSys = TextSystem(context.assets, "font")
    }

    private fun initGame() {
        Box.init(context.assets)
        curBox = Box(0, 0)
    }

    private fun resetViewport(width: Int, height: Int) {
        GL.glViewport(0, 0, width, height)

        viewportSize[0] = width.toFloat()
        viewportSize[1] = height.toFloat()
        viewportOrigin[0] = viewportSize[0] / 2.0f
        viewportOrigin[1] = viewportSize[1] / 2.0f

        program.setViewportSize(viewportSize.data)
        program.setViewportOrigin(viewportOrigin.data)
    }

    private fun resetGamePos() {
        board.pos = Vector(
            boardLeft,
            (viewportSize[1] - board.height) / 2.0f
        )

        curBoxPos = Vector(
            board.pos[0] + 1.0f + curBoxCol * Box.BOX_SPAN,
            board.pos[1] + 1.0f + curBoxRow * Box.BOX_SPAN
        )
    }
}