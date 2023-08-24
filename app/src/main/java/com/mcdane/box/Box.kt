package com.mcdane.box

import android.content.res.AssetManager
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

typealias BoxFrames = List<List<String>>

fun toBitmap(frame: List<String>): Int {
    if (frame.size != Box.BOX_ROWS) {
        throw IllegalArgumentException("Invalid bitmap size (${frame.size})")
    }

    var retval = 0

    for (rowStr in frame) {
        retval = (retval shl 4) or Box.transformRow(rowStr)
    }

    return retval
}

@Serializable
data class BoxConfigJsonItem(
    val frames: BoxFrames,
    val color: List<Int>,
    val score: Long,
    val weight: Int,
    val level: Int,
)

data class BoxConfig(
    val frames: List<Int>,
    val color: Color,
    val score: Long,
    val weight: Int,
    val level: Int,
) {
    constructor(item: BoxConfigJsonItem):
        this(
            item.frames.map { toBitmap(it) },
            Color(item.color),
            item.score,
            item.weight,
            item.level,
        )

    init {
        if (level !in Box.MIN_LEVEL..Box.MAX_LEVEL) {
            throw IllegalArgumentException("Invalid level $level")
        }
    }
}

class Box {
    companion object {
        const val BOX_ROWS = 4
        const val BOX_COLS = 4
        const val BOX_BREATH = 50.0f
        const val BOX_SPACING = 1.0f
        const val BOX_SPAN = BOX_BREATH + BOX_SPACING
        const val INDEX_COUNT = 4
        const val MAX_INDEX = INDEX_COUNT - 1
        const val MIN_LEVEL = 0
        const val MAX_LEVEL = 2

        val colTestMask = initColTestMask()

        lateinit var configs: List<BoxConfig>
            private set

        val typeCount: Int
            get() = configs.size

        val maxType: Int
            get() = typeCount - 1

        val rect = Rectangle(BOX_BREATH, BOX_BREATH, false)

        fun init(assetMgr: AssetManager, configFile: String) {
            configs = Json.decodeFromStream<List<BoxConfigJsonItem>>(assetMgr.open(configFile))
                          .map { BoxConfig(it) }
        }

        fun init(_configs: List<BoxConfig>) {
            configs = _configs
        }

        fun bitmap(type: Int, index: Int): Int = configs[type].frames[index]

        fun color(type: Int): Color = configs[type].color

        fun transformRow(rowStr: String): Int {
            if (rowStr.length != BOX_COLS) {
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

        fun toBitmap(frame: List<String>): Int {

            if (frame.size != BOX_ROWS) {
                throw IllegalArgumentException("Invalid bitmap size (${frame.size})")
            }

            var retval = 0

            for (rowStr in frame) {
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
        get() = configs[type].frames[index]

    inline val color: Color
        get() = configs[type].color

    inline val score: Long
        get() = configs[type].score

    inline val firstRow: Int
        get() = firstRow(bitmap)

    inline val lastRow: Int
        get() = lastRow(bitmap)

    inline val firstCol: Int
        get() = firstCol(bitmap)

    inline val lastCol: Int
        get() = lastCol(bitmap)

    inline val rows: Int
        get() = lastRow - firstRow + 1

    inline val cols: Int
        get() = lastCol - firstCol + 1

    fun assign(other: Box) {
        type = other.type
        index = other.index
    }

    fun draw(
        program: SimpleProgram,
        board: Board,
        rowIdx: Int,
        colIdx: Int
    ) {
        val startX = board.boxPosX(colIdx)
        var y = board.boxPosY(rowIdx)
        var bmp = bitmap

        for (row in rowIdx until (rowIdx + BOX_ROWS)) {
            var x = startX
            for (col in colIdx  until (colIdx + BOX_COLS)) {
                if (board.visible(row, col) && (bmp and 1 != 0)) {
                    rect.draw(program, x, y, color)
                }
                bmp = bmp ushr 1
                x += BOX_SPAN
            }
            y += BOX_SPAN
        }
    }

    fun draw(program: SimpleProgram, x: Float, y: Float) {
        val displacement = BOX_SPACING + BOX_BREATH / 2f
        val startX = x + displacement
        var y1 = y + displacement
        var bmp = bitmap

        for (row in 0 until  BOX_ROWS) {
            var x = startX
            for (col in 0  until BOX_COLS) {
                if (bmp and 1 != 0) {
                    rect.draw(program, x, y1, color)
                }
                bmp = bmp ushr 1
                x += BOX_SPAN
            }
            y1 += BOX_SPAN
        }
    }

    fun canBePlaced(board: Board, rowIdx: Int, colIdx: Int): Boolean {
        val bmp = bitmap
        var mask = 0x01

        for (r in rowIdx until (rowIdx + BOX_ROWS)) {
            for (c in colIdx until (colIdx + BOX_COLS)) {
                if (board.contains(r, c)) {
                    if ((bmp and mask) != 0 && board[r, c] != null) return false
                } else {
                    if ((bmp and mask) != 0) return false
                }
                mask = mask shl 1
            }
        }

        return true
    }

    fun placeInBoard(board: Board, rowIdx: Int, colIdx: Int) {
        val bmp = bitmap
        var mask = 0x01
        var maxRow = -1

        for (r in rowIdx until (rowIdx + BOX_ROWS)) {
            for (c in colIdx until (colIdx + BOX_COLS)) {
                if (board.contains(r, c)) {
                    if ((bmp and mask) != 0) {
                        board[r, c] = color
                        if (r > maxRow) maxRow = r
                    }
                }
                mask = mask shl 1
            }
        }

        if (maxRow >= 0) {
            board.updateTopRow(maxRow)
        }
    }

    fun rotate() {
        index = (index + 1) % INDEX_COUNT
    }
}