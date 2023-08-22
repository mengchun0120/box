package com.mcdane.box

import kotlin.random.Random

class Preview {
    val box = Box()
    private val pos = Vector(2)
    private val boxPos = Vector(2)
    private val rect = Rectangle(
        Box.BOX_COLS * Box.BOX_SPAN + Box.BOX_SPACING + 2f,
        Box.BOX_ROWS * Box.BOX_SPAN + Box.BOX_SPACING + 2f
    )
    private val borderColor = Color(0, 0, 0, 255)

    init {
        randomize()
    }

    val width: Float
        get() = rect.width

    val height: Float
        get() = rect.height

    fun resetPos(x: Float, y: Float) {
        pos.assign(x, y)
        boxPos.assign(
            x - width / 2f + 1f,
            y - height / 2f + 1f
        )
    }

    fun randomize() {
        box.type = Random.nextInt(Box.typeCount)
        box.index = Random.nextInt(Box.INDEX_COUNT)
    }

    fun draw(program: SimpleProgram, drawBox: Boolean) {
        rect.draw(program, pos, null, null, borderColor)
        if (drawBox) {
            box.draw(program, boxPos)
        }
    }
}