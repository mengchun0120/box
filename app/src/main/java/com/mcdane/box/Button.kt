package com.mcdane.box

import android.util.Log
import kotlin.math.abs

typealias ButtonAction = ()->Unit

private val buttonFocusColor = Color(255, 0, 0, 255)

class Button(
    val texture: Texture,
    val activeTexture: Texture,
    x: Float = 0f,
    y: Float = 0f,
    _width: Float = 0f,
    _height: Float = 0f,
    var action: ButtonAction? = null
) {
    val pos = Vector(x, y)
    val rect = Rectangle(
        if (_width > 0f) _width else texture.width,
        if (_height > 0f) _height else texture.height,
        true
    )

    private val halfWidth: Float = rect.width / 2.0f
    private val halfHeight: Float = rect.height / 2.0f
    private var hasFocus = false

    fun contains(x: Float, y: Float): Boolean =
        abs(x - pos[0]) <= halfWidth &&
        abs(y - pos[1]) <= halfHeight

    fun draw(program:SimpleProgram) {
        val tex = if (hasFocus) activeTexture else texture
        rect.draw(
            program,
            pos,
            null,
            null,
            null,
            tex.id,
        )
    }

    fun onPointerDown(x: Float, y: Float): Boolean {
        return if (contains(x, y)) {
            hasFocus = true
            action?.invoke()
            true
        } else {
            false
        }
    }

    fun onPointerOut() {
        hasFocus = false
    }
}