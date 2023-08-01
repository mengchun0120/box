package com.mcdane.box

class Color {
    val data = FloatArray(4){ 0.0f }

    var red: Float
        get() = data[0]
        set(v: Float) {
            validate(v)
            data[0] = v
        }

    var green: Float
        get() = data[1]
        set(v: Float) {
            data[1] = validate(v)
        }

    var blue: Float
        get() = data[2]
        set(v: Float) {
            data[2] = validate(v)
        }

    var alpha: Float
        get() = data[3]
        set(v: Float) {
            data[3] = validate(v)
        }

    constructor(r: Int, g: Int, b: Int, a: Int) {
        assign(r, g, b, a)
    }

    constructor(a: Collection<Int>) {
        assign(a)
    }

    constructor(r: Float, g: Float, b: Float, a: Float) {
        assign(r, g, b, a)
    }

    constructor(a: FloatArray) {
        assign(a)
    }

    fun assign(r: Int, g: Int, b: Int, a: Int): Color =
        this.also {
            red = toFloat(r)
            green = toFloat(g)
            blue = toFloat(b)
            alpha = toFloat(a)
        }

    fun assign(r: Float, g: Float, b: Float, a: Float): Color =
        this.also {
            red = r
            green = g
            blue = b
            alpha = a
        }

    fun assign(a: Collection<Int>): Color {
        if (a.size != 4) {
            throw IllegalArgumentException("collection is of wrong size")
        }

        for ((idx, e) in a.withIndex()) {
            data[idx] = validate(toFloat(e))
        }

        return this
    }

    fun assign(a: FloatArray): Color {
        if (a.size != 4) {
            throw IllegalArgumentException("collection is of wrong size")
        }

        for ((idx, e) in a.withIndex()) {
            data[idx] = validate(e)
        }

        return this
    }

    fun copy(): Color = Color(data)

    override fun toString(): String = data.joinToString(prefix="[", postfix="]")

    override fun equals(other: Any?): Boolean {
        val o: Color = other as? Color ?: return false

        return o.red == red && o.green == green && o.blue == blue && o.alpha == alpha
    }

    private fun validate(f: Float): Float =
        if (f in 0.0f..1.0f) {
            f
        } else {
            throw IllegalArgumentException("Invalid color component $f")
        }

    private fun toFloat(c: Int): Float {
        return c / 255.0f
    }
}