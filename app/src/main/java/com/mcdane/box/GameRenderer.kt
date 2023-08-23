package com.mcdane.box

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import kotlin.math.min
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random
import android.opengl.GLES30 as GL

enum class GameState {
    RUNNING,
    PAUSED,
    FLASHING,
    STOPPED,
}

class GameRenderer(private val context: Context): GLSurfaceView.Renderer {
    companion object {
        private const val BUTTON_BREATH = 120f
        private const val QUEUE_CAPACITY = 10
        private const val DROP_DOWN_INTERVAL = 1000
        private const val FLASHING_INTERVAL = 300
        private const val MAX_FLASH_COUNT = 3
        private const val MAX_DOWN_STEPS = 4
    }

    private lateinit var program: SimpleProgram
    private lateinit var textSys: TextSystem
    private val viewportSize = Vector(2)
    private val viewportOrigin = Vector(2)
    private lateinit var board: Board
    private val boardLeft = 20.0f
    private lateinit var curBox: Box
    private var curBoxRow = 10
    private var curBoxCol = 8
    private val curBoxPos = Vector(2)
    private val buttonGrp = ButtonGroup()
    private lateinit var preview: Preview
    private lateinit var score: Score
    private var state = GameState.STOPPED
    private var queue = EventQueue(QUEUE_CAPACITY)
    private var lastDownTime: Long = 0L
    private var lastFlashTime: Long = 0L
    private var flashCount = 0
    private var flashRows = IntArray(Box.BOX_ROWS) { -1 }
    private var flashRowCount = 0

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
        if (state == GameState.RUNNING) {
            curBox.draw(program, board, curBoxRow, curBoxCol)
        }
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
                dropDownCurBox(curTime)
            }
            GameState.FLASHING -> {
                updateFlashing(curTime)
            }
            else -> {

            }
        }

    }

    private fun handlePointerDown(x: Float, y: Float) {
        buttonGrp.onPointerDown(x, viewportSize[1] - y)
    }

    private fun handlePointerUp() {
        buttonGrp.onPointerUp()
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
        Box.init(
            context.assets,
            "box_bitmaps.json",
            "box_colors.json"
        )
        initBoard()
        initCurBox()
        initPreview()
        initScore()
        initButtons()
        state = GameState.RUNNING
        lastDownTime = SystemClock.uptimeMillis()
        lastFlashTime = lastDownTime
    }

    private fun initBoard() {
        val rowCount = 32
        val colCount = 12
        board = Board(rowCount, colCount)
    }

    private fun initCurBox() {
        curBox = Box()
        resetCurBox(
            Random.nextInt(0, Box.typeCount),
            Random.nextInt(0, Box.INDEX_COUNT)
        )
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
                Button(
                    Texture(context.assets, "${names[i]}.png"),
                    Texture(context.assets, "${names[i]}_active.png"),
                    _width = BUTTON_BREATH,
                    _height = BUTTON_BREATH,
                    action = actions[i],
                )
            )
        }
    }

    private fun initPreview() {
        preview = Preview()
    }

    private fun initScore() {
        val maxScoreWidth = 300f
        score = Score(textSys, 1000L)
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

        curBoxPos.assign(
            board.pos[0] + 1.0f + curBoxCol * Box.BOX_SPAN,
            board.pos[1] + 1.0f + curBoxRow * Box.BOX_SPAN
        )
    }

    private fun resetButtonPos() {
        val downButtonY = 400f
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
        val spacingFromTop = 400f
        preview.resetPos(
            (viewportSize[0] + boardLeft + board.width) / 2.0f,
            viewportSize[1] - spacingFromTop
        )
    }

    private fun resetScorePos() {
        val spacingFromTop = 200f
        score.resetPos(
            (viewportSize[0] + boardLeft + board.width) / 2f,
            viewportSize[1] - spacingFromTop,
            textSys
        )
    }

    private fun handleLeftButton() {
        if (curBox.canBePlaced(board, curBoxRow, curBoxCol - 1)) {
            --curBoxCol
        }
    }

    private fun handleRightButton() {
        if (curBox.canBePlaced(board, curBoxRow, curBoxCol + 1)) {
            ++curBoxCol
        }
    }

    private fun handleDownButton() {
        for (i in 0 until MAX_DOWN_STEPS) {
            if (curBox.canBePlaced(board, curBoxRow - 1, curBoxCol)) {
                --curBoxRow
            }
        }
    }

    private fun handleRotateButton() {
        val prevIndex = curBox.index
        curBox.rotate()
        if (!curBox.canBePlaced(board, curBoxRow, curBoxCol)) {
            curBox.index = prevIndex
        }
    }

    private fun resetCurBox(newType: Int, newIndex: Int) {
        curBox.type = newType
        curBox.index = newIndex
        resetCurBoxCol()
        resetCurBoxRow()
    }

    private fun resetCurBoxCol() {
        curBoxCol = board.centerColIdx - (curBox.cols - 1) / 2
    }

    private fun resetCurBoxRow() {
        val startRow = board.visibleRowCount - curBox.rows

        for (rowIdx in startRow until board.visibleRowCount) {
            if (curBox.canBePlaced(board, rowIdx, curBoxCol)) {
                curBoxRow = rowIdx
                return
            }
        }

        state = GameState.STOPPED
    }

    private fun dropDownCurBox(curTime: Long) {
        if (curTime - lastDownTime < DROP_DOWN_INTERVAL) {
            return
        }

        if (curBox.canBePlaced(board, curBoxRow - 1, curBoxCol)) {
            --curBoxRow
        } else {
            curBox.placeInBoard(board, curBoxRow, curBoxCol)
            checkForFullRows(curTime)
            if (state == GameState.RUNNING) {
                resetCurBox(preview.box.type, preview.box.index)
                preview.randomize()
            }
        }

        lastDownTime = curTime
    }

    private fun checkForFullRows(curTime: Long) {
        flashRowCount = 0
        for (rowIdx in curBoxRow until min(curBoxRow + Box.BOX_ROWS, board.rowCount)) {
            if (board.isFullRow(rowIdx)) {
                flashRows[flashRowCount++] = rowIdx
            }
        }

        if (flashRowCount > 0) {
            state = GameState.FLASHING
            flashCount = 0
            lastFlashTime = curTime
            updateFlashRows(false)
        }
    }

    private fun updateFlashing(curTime: Long) {
        if (curTime - lastFlashTime < FLASHING_INTERVAL) {
            return
        }

        ++flashCount
        if (flashCount < MAX_FLASH_COUNT) {
            updateFlashRows((flashCount % 2) == 0)
            lastFlashTime = curTime
        } else {
            board.removeRows(flashRows, flashRowCount)
            state = GameState.RUNNING
            resetCurBox(preview.box.type, preview.box.index)
            preview.randomize()
        }
    }

    private fun updateFlashRows(visible: Boolean) {
        for (i in 0 until flashRowCount) {
            board.setVisibleRow(flashRows[i], visible)
        }
    }
}