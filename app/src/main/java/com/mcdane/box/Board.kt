package com.mcdane.box

class Board {
    val cols = 12
    val rows = 34
    private val board = Array<Array<Color?>>(rows) {
        Array<Color?>(cols){ null }
    }
    private val boundary = Rectangle(
        cols * Box.boxSpan + Box.boxSpacing + 2.0f,
        rows * Box.boxSpan + Box.boxSpacing + 2.0f
    )
    private val boudaryColor = Color(0, 0, 255, 255)
    private val center = Vector(2)
    private val boxStartPos = Vector(2)

    val width = boundary.width
    val height = boundary.height
    var pos = Vector(2)
        set(value) {
            field = value
            center[0] = value[0] + width / 2.0f
            center[1] = value[1] + height / 2.0f
            boxStartPos[0] = value[0] + 1.0f + Box.boxSpacing + Box.boxBreath / 2.0f
            boxStartPos[1] = value[1] + 1.0f + Box.boxSpacing + Box.boxBreath / 2.0f
        }

    fun draw(program: SimpleProgram) {
        drawBoxes(program)
        drawBoundary(program)
    }

    operator fun get(row: Int, col: Int): Color? = board[row][col]

    operator fun set(row: Int, col: Int, color: Color?) {
        board[row][col] = color
    }

    private fun drawBoxes(program: SimpleProgram) {
        val p = boxStartPos.copy()
        for (row in board) {
            p[0] = boxStartPos[0]
            for (color in row) {
                color?.let {
                    Box.rect.draw(program, p, null, color, null)
                }
                p[0] += Box.boxSpan
            }
            p[1] += Box.boxSpan
        }
    }

    private fun drawBoundary(program: SimpleProgram) {
        boundary.draw(program, center, null, null, boudaryColor)
    }
}