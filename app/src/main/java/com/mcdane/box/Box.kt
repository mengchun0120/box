package com.mcdane.box

import android.content.res.AssetManager
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
@Serializable
data class BoxItem(val bitmaps: List<List<String>>, val color: List<Int>)

class BoxConfig(val bitmaps: List<Int>, val color: Color) {
    val firstRows = List<Int>(bitmaps.size) { firstRow(bitmaps[it]) }
    val lastRows = List<Int>(bitmaps.size) { lastRow(bitmaps[it]) }
    val firstCols = List<Int>(bitmaps.size) { firstCol(bitmaps[it]) }
    val lastCols = List<Int>(bitmaps.size) { lastCol(bitmaps[it]) }

    constructor(item: BoxItem):
        this(
            item.bitmaps.map{ transformBitmap(it) },
            Color(item.color)
        )

    companion object {
        val colTestMask = initColTestMask()

        fun transformRow(rowStr: String): Int {
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

        fun transformBitmap(bitmap: List<String>): Int {
            if (bitmap.size != Box.BOX_ROWS) {
                throw IllegalArgumentException("Invalid bitmap size (${bitmap.size})")
            }

            var retval = 0

            for (rowStr in bitmap) {
                retval = (retval shl 4) or transformRow(rowStr)
            }

            return retval
        }

        fun firstRow(bmp: Int): Int {
            var b = bmp
            for (i in 0 until Box.BOX_ROWS) {
                if ((b and 0xf) != 0) return i
                b = b shr Box.BOX_COLS
            }
            return -1
        }

        fun lastRow(bmp: Int): Int {
            var mask = 0xf shl ((Box.BOX_ROWS - 1) * Box.BOX_COLS)
            for (i in (Box.BOX_ROWS - 1) downTo 0) {
                if ((bmp and mask ) != 0) return i
                mask = mask shr Box.BOX_COLS
            }
            return -1
        }

        fun firstCol(bmp: Int): Int {
            var mask = colTestMask
            for (i in 0 until Box.BOX_COLS) {
                if ((bmp and mask) != 0) return i
                mask = mask shl 1
            }
            return -1
        }

        fun lastCol(bmp: Int): Int {
            var mask = colTestMask shl (Box.BOX_COLS - 1)
            for (i in (Box.BOX_COLS - 1) downTo 0) {
                if ((bmp and mask) != 0) return i
                mask = mask shr 1
            }
            return -1
        }

        fun initColTestMask(): Int {
            var mask = 1
            var ret = 0
            for (i in 0 until Box.BOX_ROWS) {
                ret = ret or mask
                mask = mask shl Box.BOX_COLS
            }
            return ret
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
        val rect = Rectangle(BOX_BREATH, BOX_BREATH, false)
        lateinit var boxConfigs: List<BoxConfig>
        val typeCount: Int
            get() = boxConfigs.size
        val maxType: Int
            get() = boxConfigs.size - 1
        const val INDEX_COUNT = 4
        const val MAX_INDEX = INDEX_COUNT - 1

        fun init(assetMgr: AssetManager) {
            boxConfigs = readBoxItems(assetMgr).map{ BoxConfig(it) }
        }

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

    inline val bitmap: Int
        get() = boxConfigs[type].bitmaps[index]

    inline val color: Color
        get() = boxConfigs[type].color

    inline val firstRow: Int
        get() = boxConfigs[type].firstRows[index]

    inline val lastRow: Int
        get() = boxConfigs[type].lastRows[index]

    inline val firstCol: Int
        get() = boxConfigs[type].firstCols[index]

    inline val lastCol: Int
        get() = boxConfigs[type].lastCols[index]

    fun assign(other: Box) {
        type = other.type
        index = other.index
    }

    fun draw(
        program: SimpleProgram,
        pos: Vector
    ) {
        val displacement = BOX_SPACING + BOX_BREATH / 2.0f
        val startX = pos[0] + displacement
        val p = Vector(startX, pos[1] + displacement)
        var bmp = bitmap

        for (row in 0 until BOX_ROWS) {
            p[0] = startX
            for (col in 0 until BOX_COLS) {
                if (bmp and 1 != 0) {
                    rect.draw(program, p, null, color, null)
                }
                bmp = bmp ushr 1
                p[0] += BOX_SPAN
            }
            p[1] += BOX_SPAN
        }
    }
}