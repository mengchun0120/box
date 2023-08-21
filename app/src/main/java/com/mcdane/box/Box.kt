package com.mcdane.box

import android.content.res.AssetManager
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

typealias BoxFrames = List<List<String>>

class Box {
    companion object {
        const val BOX_BITMAPS_FILE = "box_bitmaps.json"
        const val BOX_COLORS_FILE = "box_colors.json"
        const val BOX_ROWS = 4
        const val BOX_COLS = 4
        const val BOX_BREATH = 50.0f
        const val BOX_SPACING = 1.0f
        const val BOX_SPAN = BOX_BREATH + BOX_SPACING
        const val INDEX_COUNT = 4
        const val MAX_INDEX = INDEX_COUNT - 1

        val colTestMask = initColTestMask()

        private lateinit var bitmaps: List<List<Int>>
        private lateinit var palette: List<Color>

        val typeCount: Int
            get() = bitmaps.size

        val maxType: Int
            get() = typeCount - 1

        val rect = Rectangle(BOX_BREATH, BOX_BREATH, false)

        fun init(assetMgr: AssetManager, bitmapFile: String, paletteFile: String) {
            bitmaps = readBitmaps(assetMgr, bitmapFile)
            palette = readPalette(assetMgr, paletteFile)
        }

        fun init(_bitmaps: List<List<Int>>, _palette: List<Color>) {
            if (_bitmaps.any{ it.size !=  INDEX_COUNT}) {
                throw IllegalArgumentException("Invalid _bitmaps")
            }

            if (_bitmaps.size != _palette.size) {
                throw IllegalArgumentException("Size of _bitmaps doesn't match _palette")
            }

            bitmaps = _bitmaps
            palette = _palette
        }

        fun bitmap(type: Int, index: Int): Int = bitmaps[type][index]

        fun color(type: Int): Color = palette[type]

        fun readBitmaps(assetMgr: AssetManager, filePath: String): List<List<Int>> =
            Json.decodeFromStream<List<BoxFrames>>(assetMgr.open(filePath))
                .map { frames ->
                    if(frames.size == INDEX_COUNT) {
                        frames.map { toBitmap(it) }
                    } else {
                        throw IllegalArgumentException("Size of frames is invalid")
                    }
                }

        fun readPalette(assetMgr: AssetManager, filePath: String): List<Color> =
            Json.decodeFromStream<List<List<Int>>>(assetMgr.open(filePath)).run {
                return if(this.size == typeCount) {
                    this.map { Color(it) }
                } else {
                    throw IllegalArgumentException("Number of colors doesn't match box types")
                }
            }

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

        fun validateBitmaps(_bitmaps: List<List<Int>>) {

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

    val bitmap: Int
        get() = bitmaps[type][index]

    val color: Color
        get() = palette[index]

    val firstRow: Int
        get() = firstRow(bitmap)

    val lastRow: Int
        get() = lastRow(bitmap)

    val firstCol: Int
        get() = firstCol(bitmap)

    val lastCol: Int
        get() = lastCol(bitmap)

    val rows: Int
        get() = lastRow - firstRow + 1

    val cols: Int
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
        val startPos = board.boxPos(rowIdx, colIdx)
        val p = startPos.copy()
        var bmp = bitmap

        for (row in rowIdx until (rowIdx + BOX_ROWS)) {
            p[0] = startPos[0]
            for (col in colIdx  until (colIdx + BOX_COLS)) {
                if (board.contains(row, col) && (bmp and 1 != 0)) {
                    rect.draw(program, p, null, color, null)
                }
                bmp = bmp ushr 1
                p[0] += BOX_SPAN
            }
            p[1] += BOX_SPAN
        }
    }

    fun draw(program: SimpleProgram, pos: Vector) {
        val displacement = BOX_SPACING + BOX_BREATH / 2f
        val startX = pos[0] + displacement
        val p = Vector(startX, pos[1] + displacement)
        var bmp = bitmap

        for (row in 0 until  BOX_ROWS) {
            p[0] = startX
            for (col in 0  until BOX_COLS) {
                if (bmp and 1 != 0) {
                    rect.draw(program, p, null, color, null)
                }
                bmp = bmp ushr 1
                p[0] += BOX_SPAN
            }
            p[1] += BOX_SPAN
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
}