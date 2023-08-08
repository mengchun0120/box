package com.mcdane.box

import android.content.res.AssetManager
import android.graphics.Rect
import android.speech.tts.TextToSpeech
import java.io.File
import kotlin.math.floor

enum class TextSize(val index: Int) {
    BIG(0),
    MEDIUM(1),
    SMALL(2),
    TINY(3);
}

const val TEXT_SIZE_COUNT = 4
private val SCALE_FACTORS = floatArrayOf(1.0f, 0.75f, 0.5f, 0.36f)

class TextSystem(mgr: AssetManager, fontDir: String) {
    val minCharCode: Int = 32
    val maxCharCode: Int = 126
    val charCount: Int = maxCharCode - minCharCode + 1
    private val textures: List<Texture>
    private val fontHeights: List<Float>
    private val fontRects = ArrayList<ArrayList<Rectangle>>(TEXT_SIZE_COUNT)
    private val fontRectIndices = ArrayList<IntArray>(TEXT_SIZE_COUNT)

    init {
        textures = List<Texture>(charCount) {
            Texture(mgr, fontFilePath(fontDir, it))
        }

        fontHeights = List<Float>(TEXT_SIZE_COUNT) {
            textures[0].height * SCALE_FACTORS[it]
        }

        initFontRectIndices()
    }


    private fun fontFilePath(fontDir: String, index: Int): String =
        fontDir + File.pathSeparator + "ascii_" + index + ".png"

    private fun initFontRectIndices() {
        for (sz in 0 until TEXT_SIZE_COUNT) {
            val (r, indices) = initRectWidthForTextSize(sz)
            fontRects[sz] = r
            fontRectIndices[sz] = indices
        }
    }

    private fun initRectWidthForTextSize(sz: Int): Pair<ArrayList<Rectangle>, IntArray> {
        val height = fontHeights[sz]
        val widthRectMap = HashMap<Int, Int>()
        val (count, widths) = getRectWidthForTextSize(sz)
        val rectArr = ArrayList<Rectangle>(count)
        val rectIndices = IntArray(charCount)
        var idx = 0

        for (i in 0 until charCount) {
            val w = widths[i]
            val r = widthRectMap[w]
            if (r == null) {
                widthRectMap[w] = idx
                rectIndices[i] = idx
                rectArr[idx] = Rectangle(w.toFloat(), height, true)
                ++idx
            } else {
                rectIndices[i] = r
            }
        }

        return Pair(rectArr, rectIndices)
    }

    private fun getRectWidthForTextSize(sz: Int): Pair<Int, List<Int>> {
        val widths = List<Int>(charCount) {
            floor(textures[it].width * SCALE_FACTORS[sz]).toInt()
        }

        return Pair(widths.distinct().size, widths)
    }


}