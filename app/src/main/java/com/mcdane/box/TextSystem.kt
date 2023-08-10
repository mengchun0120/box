package com.mcdane.box

import android.content.res.AssetManager
import java.io.File

private val SCALE_FACTORS = floatArrayOf(1.0f, 0.75f, 0.5f, 0.36f)

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
        Texture(mgr, fontFilePath(fontDir, it))
    }

    private val fontHeights  = List<Float>(TEXT_SIZE_COUNT) {
        textures[0].height * SCALE_FACTORS[it]
    }

    private val fontRects = ArrayList<ArrayList<Rectangle>>(TEXT_SIZE_COUNT)
    private val fontRectIndices = ArrayList<IntArray>(TEXT_SIZE_COUNT)

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
        pos: Vector,
        sz: TextSize,
        color: Color,
    ) {
        val p = Vector(pos[0], pos[1] + height(sz) / 2.0f)

        for (ch in s) {
            val r = rect(sz, ch)
            val w = r.width / 2.0f

            p[0] += w
            r.draw(program, p, null, color, null, texture(ch).id)
            p[0] += w
        }
    }

    private fun fontFilePath(fontDir: String, index: Int): String =
        fontDir + File.pathSeparator + "ascii_" + index + ".png"

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

        fontRects[sz] = rectArr
        fontRectIndices[sz] = idxArr
    }

    private fun width(sz: Int, charIdx: Int): Int =
        (textures[charIdx].width * SCALE_FACTORS[sz]).toInt()
}