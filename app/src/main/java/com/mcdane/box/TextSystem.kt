package com.mcdane.box

import android.content.res.AssetManager
import java.io.File

enum class TextSize(val index: Int) {
    BIG(0),
    MEDIUM(1),
    SMALL(2),
    TINY(3);
}

const val TEXT_SIZE_COUNT = 4

class TextSystem(mgr: AssetManager, fontDir: String) {
    val minCharCode: Int = 32
    val maxCharCode: Int = 126
    val charCount: Int = maxCharCode - minCharCode + 1
    private val textures: List<Texture>
    private val fontHeights: List<Float>

    init {
        textures = List<Texture>(charCount) {
            Texture(mgr, fontFilePath(fontDir, it))
        }


    }


    private fun fontFilePath(fontDir: String, index: Int): String =
        fontDir + File.pathSeparator + "ascii_" + index + ".png"
}