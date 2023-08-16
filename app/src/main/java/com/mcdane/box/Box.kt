package com.mcdane.box

import android.content.res.AssetManager
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
@Serializable
data class BoxItem(val bitmaps: List<List<String>>, val color: List<Int>)

class BoxConfig(val bitmaps: List<Int>, val color: Color) {
    constructor(item: BoxItem):
        this(
            item.bitmaps.map{ transformBitmap(it) },
            Color(item.color)
        )

    companion object {
        private fun transformRow(rowStr: String): Int {
            if (rowStr.length != Box.BOX_COLS) {
                throw IllegalArgumentException("Invalid row size: $rowStr")
            }

            var row = 0
            var mask = 1

            for (digit in rowStr) {
                if (digit == '1') row = row or mask
                mask = mask shl 1
            }

            return row
        }

        private fun transformBitmap(bitmap: List<String>): Int {
            if (bitmap.size != Box.BOX_ROWS) {
                throw IllegalArgumentException("Invalid bitmap size (${bitmap.size})")
            }

            var retval = 0

            for (rowStr in bitmap) {
                retval = (retval shl 4) or transformRow(rowStr)
            }

            return retval
        }
    }

    override fun toString() =
        "BoxConfig(bitmaps=${bitmaps.map{ it.toString(16) }}, color=$color)"
}

class Box {
    companion object {
        const val BOX_CONFIG_FILE = "boxes.json"
        const val BOX_ROWS = 4
        const val BOX_COLS = 4
        const val BOX_BREATH = 50.0f
        const val BOX_SPACING = 1.0f
        const val BOX_SPAN = BOX_BREATH + BOX_SPACING
        lateinit var boxConfigs: List<BoxConfig>
        val rect = Rectangle(BOX_BREATH, BOX_BREATH, false)
        val maxType: Int
            get() = boxConfigs.size - 1
        const val MAX_INDEX = 3

        fun init(assetMgr: AssetManager) {
            boxConfigs = readBoxItems(assetMgr).map{ BoxConfig(it) }
        }

        inline fun bitmap(type: Int, index: Int): Int =
            boxConfigs[type].bitmaps[index]

        private fun readBoxItems(mgr: AssetManager): List<BoxItem> =
            Json.decodeFromStream<List<BoxItem>>(mgr.open(BOX_CONFIG_FILE))
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
        var box = bitmap(type, index)

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