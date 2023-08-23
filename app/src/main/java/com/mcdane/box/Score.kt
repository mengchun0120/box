package com.mcdane.box

class Score(textSys: TextSystem, val initScore: Long = 0L) {
    private val textSize = TextSize.BIG
    private val maxTextWidth = 400f
    private val textMargin = 4f
    private val frame = Rectangle(
        maxTextWidth + textMargin * 2,
        textSys.height(textSize) + textMargin * 2,
        false
    )
    private val framePos = Vector(2)
    private val frameColor = Color(0, 0, 0, 255)
    private var score: Long = initScore
        set(value) {
            field = value
            scoreStr = value.toString()
        }
    private var scoreStr = score.toString()
    private val scoreColor = Color(0, 0, 255, 255)
    private val textPos = Vector(2)
    private var textRight = 0f

    val width: Float
        get() = frame.width

    val height: Float
        get() = frame.height

    init {
        resetTextPos(textSys)
    }

    fun draw(program: SimpleProgram, textSys: TextSystem) {
        frame.draw(program, framePos[0], framePos[1], null, frameColor)
        textSys.draw(program, scoreStr, textPos[0], textPos[1], textSize, scoreColor)
    }

    fun addScore(delta: Long, textSys: TextSystem) {
        score += delta
        resetTextPos(textSys)
    }

    fun resetPos(x: Float, y: Float, textSys: TextSystem) {
        framePos.assign(x, y)
        textRight = x + maxTextWidth / 2f
        textPos[1] = y - textSys.height(textSize) / 2f
        resetTextPos(textSys)
    }

    private fun resetTextPos(textSys: TextSystem) {
        textPos[0] = textRight - textSys.width(textSize, scoreStr)
    }
}