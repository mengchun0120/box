package com.mcdane.box

import android.content.res.AssetManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class Board {
    private val board: Array<Array<Color?>>
    private val boundary: Rectangle
    private val boudaryColor = Color(0, 0, 255, 255)
    private val center = Vector(2)
    private val boxStartPos = Vector(2)

    val rowCount: Int
        get() = board.size
    val colCount: Int
        get() = board[0].size
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

    constructor(_rowCount: Int, _colCount: Int) {
        board = Array<Array<Color?>>(_rowCount) {
            Array<Color?>(_colCount){ null}
        }
        boundary = createRect()
    }

    constructor(content: List<List<Int?>>, palette: List<Color>) {
        if (!validate(content)) throw IllegalArgumentException("content is invalid")
        board = createBoard(content, palette)
        boundary = createRect()
    }

    constructor(assetManager: AssetManager, path: String, palette: List<Color>) :
        this(Json.decodeFromStream<List<List<Int?>>>(assetManager.open(path)), palette)

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

    private fun validate(content: List<List<Int?>>): Boolean =
        content.size >= Box.BOX_ROWS &&
        content[0].size >= Box.BOX_COLS &&
        content.all{ it.size == content[0].size }

    private fun createBoard(content: List<List<Int?>>, palette: List<Color>): Array<Array<Color?>> =
        Array<Array<Color?>>(content.size) { rowIdx ->
            Array<Color?>(content[rowIdx].size) { colIdx ->
                content[rowIdx][colIdx]?.let { palette[it] }
            }
        }

    private fun createRect(): Rectangle =
        Rectangle(
            colCount * Box.BOX_SPAN + Box.BOX_SPACING + 2f,
            rowCount * Box.BOX_SPAN + Box.BOX_SPACING + 2f,
            false
        )

    private fun drawBoxes(program: SimpleProgram) {
        val p = boxStartPos.copy()
        for (row in board) {
            p[0] = boxStartPos[0]
            for (color in row) {
                color?.let {
                    Box.rect.draw(program, p, null, color, null)
                }
                p[0] += Box.BOX_SPAN
            }
            p[1] += Box.BOX_SPAN
        }
    }

    private fun drawBoundary(program: SimpleProgram) {
        boundary.draw(program, center, null, null, boudaryColor)
    }
}