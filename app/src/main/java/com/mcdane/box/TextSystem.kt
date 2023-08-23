package com.mcdane.box

import android.content.res.AssetManager
import java.io.File

private val SCALE_FACTORS = floatArrayOf(2.0f, 1.75f, 1f, 0.5f)

enum class TextSize(val index: Int) {
    BIG(0),
    MEDIUM(1),
    SMALL(2),
    TINY(3);

    val scaleFactor: Float = SCALE_FACTORS[index]
}

const val TEXT_SIZE_COUNT = 4

class TextSystem(mgr: AssetManager, fontDir: String) {
    val minCharCode: Int = 32
    val maxCharCode: Int = 126
    val charCount: Int = maxCharCode - minCharCode + 1

    private val textures = List<Texture>(charCount) {
        Texture(mgr, fontFilePath(it, fontDir))
    }

    private val fontHeights  = List<Float>(TEXT_SIZE_COUNT) {
        textures[0].height * SCALE_FACTORS[it]
    }

    private val fontRects = ArrayList<ArrayList<Rectangle>>()
    private val fontRectIndices = ArrayList<IntArray>()

    init {
        for (sz in 0 until TEXT_SIZE_COUNT) {
            initFontRectIndices(sz)
        }
    }

    fun height(sz: TextSize): Float =
        fontHeights[sz.index]

    fun width(sz: TextSize, ch: Char): Float =
        rect(sz, ch).width

    fun rect(sz: TextSize, ch: Char): Rectangle {
        val idx = fontRectIndices[sz.index][ch.code - minCharCode]
        return fontRects[sz.index][idx]
    }

    fun width(sz: TextSize, s: String): Float {
        var w = 0.0f
        s.forEach { w += width(sz, it) }
        return w
    }

    fun texture(ch: Char): Texture = textures[ch.code - minCharCode]

    fun draw(
        program: SimpleProgram,
        s: String,
        x: Float,
        y: Float,
        sz: TextSize,
        color: Color,
    ) {
        val y1 = y + height(sz) / 2f
        var x1 = x

        for (ch in s) {
            val r = rect(sz, ch)
            val w = r.width / 2.0f

            x1 += w
            r.draw(program, x1, y1, null, null, texture(ch).id, color)
            x1 += w
        }
    }

    private fun fontFilePath(index: Int, fontDir: String): String =
        fontDir + File.separator + "ascii_" + (minCharCode + index) + ".png"

    private fun initFontRectIndices(sz: Int) {
        val height = fontHeights[sz]
        val widthRectMap = HashMap<Int, Int>()
        val rectArr = ArrayList<Rectangle>()
        val idxArr = IntArray(charCount)

        for (i in 0 until charCount) {
            val w = (textures[i].width * SCALE_FACTORS[sz]).toInt()
            val rectIdx = widthRectMap[w]
            if (rectIdx == null) {
                rectArr.add(Rectangle(w.toFloat(), height, true))
                widthRectMap[w] = rectArr.size - 1
                idxArr[i] = rectArr.size - 1
            } else {
                idxArr[i] = rectIdx
            }
        }

        fontRects.add(rectArr)
        fontRectIndices.add(idxArr)
    }

    private fun width(sz: Int, charIdx: Int): Int =
        (textures[charIdx].width * SCALE_FACTORS[sz]).toInt()
}