package com.mcdane.box

import android.content.res.AssetManager
import android.util.Log
import java.util.Scanner

class Box {
    companion object {
        private const val BOX_FILE = "boxes.txt"
        private const val BOX_RADIX = 16
        private val boxes = mutableListOf<MutableList<Int>>()
        val boxBreath = 50.0f
        val boxSpacing = 1.0f
        val boxSpan = boxBreath + boxSpacing
        val rect = Rectangle(boxBreath, boxBreath, false)
        val maxType: Int
            get() = boxes.size - 1
        val maxIndex = 3
        val boxRows = 4
        val boxCols = 4

        fun init(mgr: AssetManager) {
            mgr.open(BOX_FILE).use {
                val scanner = Scanner(it)
                var idx = 0
                var row = mutableListOf<Int>()
                while (scanner.hasNextInt(BOX_RADIX)) {
                    row.add(scanner.nextInt(BOX_RADIX))
                    ++idx

                    if (idx == maxIndex) {
                        boxes.add(row)
                        row = mutableListOf<Int>()
                        idx = 0
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
            if (value !in 0..maxIndex) {
                throw IllegalArgumentException("New index $value is not in [0, $maxIndex]")
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
        val displacement = boxSpacing + boxBreath / 2.0f
        val startX = pos[0] + displacement
        val p = Vector(startX, pos[1] + displacement)
        var box = boxes[type][index]

        for (row in 0 until boxRows) {
            p[0] = startX
            for (col in 0 until boxCols) {
                if (box and 1 != 0) {
                    rect.draw(program, p, null, color, null)
                }
                box = box ushr 1
                p[0] += boxSpan
            }
            p[1] += boxSpan
        }
    }
}