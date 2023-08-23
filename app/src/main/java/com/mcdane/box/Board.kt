package com.mcdane.box

import android.content.res.AssetManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class Board {
    companion object {
        private val boudaryColor = Color(0, 0, 255, 255)
        const val HIDDEN_ROWS = Box.BOX_ROWS
        const val MIN_VISIBLE_ROWS = Box.BOX_ROWS
        const val MIN_ROWS = MIN_VISIBLE_ROWS + HIDDEN_ROWS
        const val MIN_COLS = Box.BOX_COLS
    }

    private val board: Array<Array<Color?>>
    private val visible: BooleanArray
    private val boundary: Rectangle
    private val center = Vector(2)
    private val boxStartPos = Vector(2)

    var topRow: Int = -1
        private set

    val rowCount: Int
        get() = board.size

    val colCount: Int
        get() = board[0].size

    val centerColIdx: Int
        get() = (colCount - 1) / 2

    val visibleRowCount: Int
        get() = rowCount - Box.BOX_ROWS

    val width: Float
        get() = boundary.width

    val height: Float
        get() = boundary.height

    var pos = Vector(2)
        set(value) {
            field = value
            center[0] = value[0] + width / 2.0f
            center[1] = value[1] + height / 2.0f
            boxStartPos[0] = value[0] + 1.0f + Box.BOX_SPACING + Box.BOX_BREATH / 2.0f
            boxStartPos[1] = value[1] + 1.0f + Box.BOX_SPACING + Box.BOX_BREATH / 2.0f
        }

    constructor(_visibleRowCount: Int, _colCount: Int) {
        if (_visibleRowCount < MIN_VISIBLE_ROWS) {
            throw IllegalArgumentException("Invalid _visibleRowCount ($_visibleRowCount)")
        }

        if (_colCount < MIN_COLS) {
            throw IllegalArgumentException("Invalid _colCount ($_colCount)")
        }

        board = Array<Array<Color?>>(_visibleRowCount + Box.BOX_ROWS) {
            Array<Color?>(_colCount){ null}
        }
        visible = createVisibleArray()
        boundary = createRect()
    }

    constructor(content: List<List<Int?>>, configs: List<BoxConfig>) {
        if (!validate(content)) throw IllegalArgumentException("content is invalid")
        board = createBoard(content, configs)
        visible = createVisibleArray()
        boundary = createRect()
    }

    constructor(assetManager: AssetManager, path: String, configs: List<BoxConfig>) :
        this(Json.decodeFromStream<List<List<Int?>>>(assetManager.open(path)), configs)

    fun draw(program: SimpleProgram) {
        drawBoxes(program)
        drawBoundary(program)
    }

    operator fun get(row: Int, col: Int): Color? = board[row][col]

    operator fun set(row: Int, col: Int, color: Color?) {
        board[row][col] = color
    }

    override fun toString(): String =
        board.joinToString(prefix="[", postfix="]") { row ->
            row.joinToString(prefix="[", postfix="]")
        }

    fun contains(rowIdx: Int, colIdx: Int): Boolean =
        (rowIdx in 0 until rowCount) &&
        (colIdx in 0 until colCount)

    fun visible(rowIdx: Int, colIdx: Int): Boolean =
        (rowIdx in 0 until visibleRowCount) &&
        (colIdx in 0 until colCount)

    fun boxPos(rowIdx: Int, colIdx: Int): Vector =
        Vector(
            boxStartPos[0] + colIdx * Box.BOX_SPAN,
            boxStartPos[1] + rowIdx * Box.BOX_SPAN
        )

    fun isFullRow(rowIdx: Int): Boolean = board[rowIdx].all { it != null }

    fun setVisibleRow(rowIdx: Int, _visible: Boolean) {
        visible[rowIdx] = _visible
    }

    fun removeRows(rows: IntArray, count: Int) {
        for (i in 0 until count) {
            copyRows(
                rows[i] + 1,
                if (i < count - 1) rows[i + 1] - 1 else topRow,
                rows[i] - i
            )
        }
        clearRows(topRow - count + 1, topRow)
        topRow -= count
    }

    fun copyRows(srcStartRowIdx: Int, srcEndRowIdx: Int, dstRowIdx: Int) {
        var dstIdx = dstRowIdx
        for (rowIdx in srcStartRowIdx..srcEndRowIdx) {
            copyRow(rowIdx, dstIdx++)
        }
    }

    fun copyRow(srcRowIdx: Int, dstRowIdx: Int) {
        val srcRow = board[srcRowIdx]
        val dstRow = board[dstRowIdx]
        for (i in 0 until colCount) {
            dstRow[i] = srcRow[i]
        }
    }

    fun updateTopRow(rowIdx: Int) {
        if (rowIdx > topRow) {
            topRow = rowIdx
        }
    }

    fun clearRows(startRowIdx: Int, endRowIdx: Int) {
        for (i in startRowIdx..endRowIdx) {
            board[i].fill(null)
        }
    }

    private fun validate(content: List<List<Int?>>): Boolean =
        content.size >= MIN_ROWS &&
        content[0].size >= MIN_COLS &&
        content.all{ it.size == content[0].size }

    private fun createBoard(content: List<List<Int?>>, configs: List<BoxConfig>): Array<Array<Color?>> =
        Array<Array<Color?>>(content.size) { rowIdx ->
            Array<Color?>(content[rowIdx].size) { colIdx ->
                content[rowIdx][colIdx]?.let { configs[it].color }
            }
        }

    private fun createVisibleArray(): BooleanArray =
        BooleanArray(visibleRowCount) { true }

    private fun createRect(): Rectangle =
        Rectangle(
            colCount * Box.BOX_SPAN + Box.BOX_SPACING + 2f,
            visibleRowCount * Box.BOX_SPAN + Box.BOX_SPACING + 2f,
            false
        )

    private fun drawBoxes(program: SimpleProgram) {
        val p = boxStartPos.copy()
        for (rowIdx in 0 until visibleRowCount) {
            if (visible[rowIdx]) {
                p[0] = boxStartPos[0]
                drawRow(program, rowIdx, p)
            }
            p[1] += Box.BOX_SPAN
        }
    }

    private fun drawRow(program: SimpleProgram, rowIdx: Int, pos: Vector) {
        for (color in board[rowIdx]) {
            color?.let {
                Box.rect.draw(program, pos, null, it, null)
            }
            pos[0] += Box.BOX_SPAN
        }
    }

    private fun drawBoundary(program: SimpleProgram) {
        boundary.draw(program, center, null, null, boudaryColor)
    }
}