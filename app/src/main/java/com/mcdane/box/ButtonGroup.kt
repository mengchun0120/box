package com.mcdane.box

import android.util.Log

class ButtonGroup {
    val buttons = arrayListOf<Button>()

    private var focusButtonIndex: Int = -1

    constructor()

    constructor(vararg _buttons: Button) {
        buttons.addAll(_buttons)
    }

    constructor(_buttons: Collection<Button>) {
        buttons.addAll(_buttons)
    }

    fun draw(program: SimpleProgram) {
        buttons.forEach{ it.draw(program) }
    }

    fun onPointerDown(x: Float, y: Float): Boolean {
        Log.i(TAG, "down $x, $y")
        var focusIndex = -1
        for ((index, button) in buttons.withIndex()) {
            if (button.onPointerDown(x, y)) {
                focusIndex = index
                break
            }
        }

        if (focusIndex != focusButtonIndex) {
            if (focusButtonIndex != -1) {
                buttons[focusButtonIndex].onPointerOut()
            }
            focusButtonIndex = focusIndex
        }

        return focusIndex != -1
    }

    fun onPointerUp() {
        if (focusButtonIndex != -1) {
            buttons[focusButtonIndex].onPointerOut()
            focusButtonIndex = -1
        }
    }
}