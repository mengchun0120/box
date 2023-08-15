package com.mcdane.box

import android.content.res.AssetManager
import android.util.Log
import java.util.Scanner

class Box {
    companion object {
        private const val BOX_FILE = "boxes.txt"
        private const val BOX_RADIX = 16
        val boxes = mutableListOf<MutableList<Int>>()
        const val BOX_BREATH = 50.0f
        const val BOX_SPACING = 1.0f
        const val BOX_SPAN = BOX_BREATH + BOX_SPACING
        val rect = Rectangle(BOX_BREATH, BOX_BREATH, false)
        val maxType: Int
            get() = boxes.size - 1
        const val MAX_INDEX = 3
        const val BOX_ROWS = 4
        const val BOX_COLS = 4

        fun init(mgr: AssetManager) {
            mgr.open(BOX_FILE).use {
                val scanner = Scanner(it)
                var row = mutableListOf<Int>()
                while (scanner.hasNextInt(BOX_RADIX)) {
                    row.add(scanner.nextInt(BOX_RADIX))
                    if (row.size == (MAX_INDEX + 1)) {
                        boxes.add(row)
                        if (scanner.hasNextInt(BOX_RADIX)) {
                            row = mutableListOf<Int>()
                        }
                    }
                }
            }
        }
    }

    var type: Int = 0
        set(value) {
            if (value !in 0..maxType) {
                throw IllegalArgumentException("New type $value is not in [0, $maxType]")
            }
            field = value
        }

    var index: Int = 0
        set(value) {
            if (value !in 0..MAX_INDEX) {
                throw IllegalArgumentException("New index $value is not in [0, $MAX_INDEX]")
            }
            field = value
        }

    constructor() {
        type = 0
        index = 0
    }

    constructor(newType: Int, newIndex: Int) {
        type = newType
        index = newIndex
    }

    fun draw(
        program: SimpleProgram,
        pos: Vector,
        color: Color
    ) {
        val displacement = BOX_SPACING + BOX_BREATH / 2.0f
        val startX = pos[0] + displacement
        val p = Vector(startX, pos[1] + displacement)
        var box = boxes[type][index]

        for (row in 0 until BOX_ROWS) {
            p[0] = startX
            for (col in 0 until BOX_COLS) {
                if (box and 1 != 0) {
                    rect.draw(program, p, null, color, null)
                }
                box = box ushr 1
                p[0] += BOX_SPAN
            }
            p[1] += BOX_SPAN
        }
    }
}