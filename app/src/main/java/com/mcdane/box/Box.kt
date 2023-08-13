package com.mcdane.box

import android.content.res.AssetManager
import android.util.Log
import java.util.Scanner

class Box(val type: Int, val index: Int) {
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
        val maxIndex = 4
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

    init {
        validate()
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
            pos[0] = startX
            for (col in 0 until boxCols) {
                if (box and 1 != 0) {
                    rect.draw(program, p, null, color, null)
                }
                box = box ushr 1
                pos[0] += boxSpan
            }
            pos[1] += boxSpan
        }
    }

    private fun validate() {
        if (type !in 0..maxType) {
            throw IllegalArgumentException("Invalid type: $type is not in [0, $maxType]")
        }

        if (index !in 0..maxIndex) {
            throw IllegalArgumentException("Invalid index: $index is not in [0, $maxIndex]")
        }
    }
}