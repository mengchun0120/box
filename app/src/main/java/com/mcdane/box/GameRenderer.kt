package com.mcdane.box

import android.app.Activity
import android.app.AlertDialog
import android.opengl.GLSurfaceView
import android.os.SystemClock
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLES30 as GL

class GameRenderer(
    private val activity: Activity,
    val playerName: String,
    val maxLevel: Int = 0,
    val isMultiplayer: Boolean,
    val serverOrClient: Boolean,
): GLSurfaceView.Renderer {
    enum class GameState {
        RUNNING,
        PAUSED,
        FLASHING,
        STOPPED,
    }

    companion object {
        private const val BUTTON_BREATH = 120f
        private const val QUEUE_CAPACITY = 10
        private const val DROP_DOWN_INTERVAL = 1000
        private const val FLASHING_INTERVAL = 300
        private const val MAX_FLASH_COUNT = 3
        private const val MAX_DOWN_STEPS = 4
        private val playerNameColor = Color(0, 0, 0, 255)
    }

    private lateinit var program: SimpleProgram
    private lateinit var textSys: TextSystem
    private val viewportSize = Vector(2)
    private val viewportOrigin = Vector(2)
    private lateinit var board: Board
    private var smallBoard: Board? = null
    private val boardLeft = 20.0f
    private val buttonGrp = GameButtonGroup()
    private lateinit var preview: Preview
    private lateinit var score: Score
    private var state = GameState.STOPPED
    private var queue = EventQueue(QUEUE_CAPACITY)
    private var lastDownTime: Long = 0L
    private var lastFlashTime: Long = 0L
    private var flashCount = 0
    private val playerNamePos = Vector(2)
    private val remotePlayerNamePos = Vector(2)

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        initOpenGL()
        initGame()
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        resetViewport(width, height)
        resetGamePos()
        resetButtonPos()
        resetPreviewPos()
        resetScorePos()
    }

    override fun onDrawFrame(p0: GL10?) {
        processEvent()
        update()
        draw()
    }

    fun enqueueEvent(action: PointerAction, x: Float, y: Float) {
        if (!queue.full) queue.add(action, x, y)
    }

    fun close() {
        program.close()
    }

    private fun draw() {
        GL.glClear(GL.GL_COLOR_BUFFER_BIT)
        board.draw(program)
        textSys.draw(program, playerName, playerNamePos[0], playerNamePos[1], TextSize.MEDIUM, playerNameColor)
        smallBoard?.apply{ draw(program) }
        score.draw(program, textSys)
        preview.draw(program, state != GameState.STOPPED)
        buttonGrp.draw(program)
    }

    private fun processEvent() {
        while (!queue.empty) {
            val e = queue.peek()
            when (e.action) {
                PointerAction.DOWN -> handlePointerDown(e.x, e.y)
                PointerAction.UP -> handlePointerUp()
            }
            queue.remove()
        }
    }

    private fun update() {
        val curTime = SystemClock.uptimeMillis()

        when (state) {
            GameState.RUNNING -> {
                updateRunning(curTime)
            }
            GameState.FLASHING -> {
                updateFlashing(curTime)
            }
            else -> {

            }
        }
    }

    private fun handlePointerDown(x: Float, y: Float) {
        if (state == GameState.RUNNING) {
            buttonGrp.onPointerDown(x, viewportSize[1] - y)
        }
    }

    private fun handlePointerUp() {
        if (state == GameState.RUNNING) {
            buttonGrp.onPointerUp()
        }
    }

    private fun initOpenGL() {
        GL.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GL.glEnable(GL.GL_BLEND)
        GL.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA)

        program = SimpleProgram(activity.resources)
        program.use()
        program.setAlpha(1.0f)

        textSys = TextSystem(activity.assets, "font")
    }

    private fun initGame() {
        Box.init(
            activity.assets,
            "box_config.json"
        )
        initBoard()
        initPreview()
        initScore()
        initButtons()
        initAlert()
        if (!isMultiplayer) {
            state = GameState.RUNNING
            lastDownTime = SystemClock.uptimeMillis()
            lastFlashTime = lastDownTime
        }
    }

    private fun initBoard() {
        val rowCount = 32
        val colCount = 12
        board = Board(rowCount, colCount)
        if (isMultiplayer) {
            smallBoard = Board(rowCount, colCount, true)
        }
    }

    private fun initCurBox() {
        board.resetCurBox(preview.box.type, preview.box.index)
        preview.randomize()
    }

    private fun initButtons() {
        val names = listOf("down", "rotate", "right", "left")
        val actions = listOf(
            { handleDownButton() },
            { handleRotateButton() },
            { handleRightButton() },
            { handleLeftButton() }
        )

        for (i in names.indices) {
            buttonGrp.buttons.add(
                GameButton(
                    Texture(activity.assets, "${names[i]}.png"),
                    Texture(activity.assets, "${names[i]}_active.png"),
                    _width = BUTTON_BREATH,
                    _height = BUTTON_BREATH,
                    action = actions[i],
                )
            )
        }
    }

    private fun initPreview() {
        preview = Preview(maxLevel)
    }

    private fun initScore() {
        score = Score(textSys)
    }

    private fun initAlert() {

    }

    private fun resetViewport(width: Int, height: Int) {
        GL.glViewport(0, 0, width, height)

        viewportSize[0] = width.toFloat()
        viewportSize[1] = height.toFloat()
        viewportOrigin[0] = viewportSize[0] / 2.0f
        viewportOrigin[1] = viewportSize[1] / 2.0f

        program.setViewportSize(viewportSize[0], viewportSize[1])
        program.setViewportOrigin(viewportOrigin[0], viewportOrigin[1])
    }

    private fun resetGamePos() {
        board.pos = Vector(
            boardLeft,
            (viewportSize[1] - board.height) / 2.0f
        )
        playerNamePos[0] = board.pos[0]
        playerNamePos[1] = board.pos[1] + board.height + 10f

        smallBoard?.let {
            it.pos = Vector(
                (viewportSize[0] + boardLeft + board.width - it.width) / 2.0f,
                (viewportSize[1] - it.height) / 2.0f
            )
            remotePlayerNamePos[0] = it.pos[0]
            remotePlayerNamePos[1] = it.pos[1] + it.height + 10f
        }
    }

    private fun resetButtonPos() {
        val downButtonY = 200f
        val downButtonX = (viewportSize[0] + boardLeft + board.width) / 2.0f
        val buttonSpacing = BUTTON_BREATH + 10f
        val rotateButtonY = downButtonY + buttonSpacing
        val positions = listOf(
            Vector(downButtonX, downButtonY),
            Vector(downButtonX, rotateButtonY),
            Vector(downButtonX + buttonSpacing, rotateButtonY),
            Vector(downButtonX - buttonSpacing, rotateButtonY),
        )

        buttonGrp.buttons.forEachIndexed { index, button ->
            button.pos.assign(positions[index])
        }
    }

    private fun resetPreviewPos() {
        val spacingFromTop = 300f
        preview.resetPos(
            (viewportSize[0] + boardLeft + board.width) / 2.0f,
            viewportSize[1] - spacingFromTop
        )
    }

    private fun resetScorePos() {
        val spacingFromTop = 100f
        score.resetPos(
            (viewportSize[0] + boardLeft + board.width) / 2f,
            viewportSize[1] - spacingFromTop,
            textSys
        )
    }

    private fun handleLeftButton() {
        board.moveLeftCurBox()
    }

    private fun handleRightButton() {
        board.moveRightCurBox()
    }

    private fun handleDownButton() {
        for (i in 0 until MAX_DOWN_STEPS) {
            if (!board.moveDownCurBox()) {
                break;
            }
        }
    }

    private fun handleRotateButton() {
        board.rotateCurBox()
    }

    private fun updateRunning(curTime: Long) {
        if (curTime - lastDownTime < DROP_DOWN_INTERVAL) {
            return
        }

        if (!board.moveDownCurBox()) {
            board.placeCurBox()
            score.addScore(board.curBox.score, textSys)
            checkForFullRows(curTime)
            if (state == GameState.RUNNING && !checkForGameOver()) {
                resetCurBox()
            }
        }

        lastDownTime = curTime
    }

    private fun checkForFullRows(curTime: Long) {
        if (board.checkFullRows()) {
            state = GameState.FLASHING
            flashCount = 0
            lastFlashTime = curTime
            board.setFullRowVisible(false)
        }
    }

    private fun updateFlashing(curTime: Long) {
        if (curTime - lastFlashTime < FLASHING_INTERVAL) {
            return
        }

        ++flashCount
        if (flashCount < MAX_FLASH_COUNT) {
            board.setFullRowVisible((flashCount % 2) == 0)
            lastFlashTime = curTime
        } else {
            board.removeFullRows()
            score.addScore(fullRowScore(), textSys)
            if (!checkForGameOver()) {
                state = GameState.RUNNING
                resetCurBox()
            }
        }
    }

    private fun fullRowScore(): Long =
        (1 shl (board.fullRowCount - 1)) * 100L

    private fun resetCurBox(): Boolean =
        if (!board.resetCurBox(preview.box.type, preview.box.index)) {
            showGameOver()
            false
        } else {
            preview.randomize()
            true
        }

    private fun checkForGameOver(): Boolean =
        if (!board.reachTop()) {
            false
        } else {
            showGameOver()
            true
        }

    private fun showGameOver() {
        state = GameState.STOPPED

        activity.runOnUiThread {
            AlertDialog.Builder(activity).apply {
                setMessage("Game Over")
                setPositiveButton(R.string.ok) { dialog, which ->
                    activity.finish()
                }
            }.show()
        }

        BoxDatabase.handler.post {
            BoxDatabase.instance?.scoreDao()?.add(
                ScoreRecord(0, playerName, score.score, Date(System.currentTimeMillis()))
            )
        }
    }
}