package com.mcdane.box

class Board {
    val cols = 12
    val rows = 34
    private val boxBreath = 50.0f
    private val boxSpacing = 1.0f
    private val boxSpan = boxBreath + boxSpacing
    private val board = Array<Array<Color?>>(rows) {
        Array<Color?>(cols){ null }
    }
    private val rect = Rectangle(boxBreath, boxBreath, false)
    private val boundary = Rectangle(
        cols * boxSpan + boxSpacing + 2.0f,
        rows * boxSpan + boxSpacing + 2.0f
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
            boxStartPos[0] = value[0] + 1.0f + boxSpacing + boxBreath / 2.0f
            boxStartPos[1] = value[1] + 1.0f + boxSpacing + boxBreath / 2.0f
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
                    rect.draw(program, p, null, color, null)
                }
                p[0] += boxSpan
            }
            p[1] += boxSpan
        }
    }

    private fun drawBoundary(program: SimpleProgram) {
        boundary.draw(program, center, null, null, boudaryColor)
    }
}