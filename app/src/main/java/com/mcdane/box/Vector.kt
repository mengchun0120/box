package com.mcdane.box

import kotlin.math.sqrt
import kotlin.math.abs

open class Vector(val dim: Int) {
    val data = FloatArray(dim)

    val norm: Float
        get() {
            var sum = 0.0f
            data.forEach {
                sum += it * it
            }
            return sqrt(sum)
        }

    constructor(vararg a: Float): this(a.size) {
        populate(a.withIndex())
    }

    constructor(a: Collection<Float>): this(a.size) {
        populate(a.withIndex())
    }

    fun <T: Iterable<IndexedValue<Float>>> populate(i: T): Vector =
        this.also {
            for ((idx, e) in i) {
                data[idx] = e
            }
        }

    operator fun get(idx: Int): Float = data[idx]

    operator fun set(idx: Int, v: Float) {
        data[idx] = v
    }

    operator fun component1() = data[0]

    operator fun component2() = data[1]

    operator fun component3() = data[2]

    operator fun component4() = data[3]

    operator fun component5() = data[4]

    operator fun plus(o: Vector): Vector {
        if (dim != o.dim) throw IllegalArgumentException("Dimension doesn't match")

        return Vector(dim).also {
            for (idx in 0 until dim) {
                it[idx] = this[idx] + o[idx]
            }
        }
    }

    operator fun minus(o: Vector): Vector {
        if (dim != o.dim) throw IllegalArgumentException("Dimension doesn't match")

        return Vector(dim).also {
            for (idx in 0 until dim) {
                it[idx] = this[idx] - o[idx]
            }
        }
    }

    operator fun times(f: Float): Vector =
        Vector(dim).also {
            for (idx in 0 until dim) {
                it[idx] = this[idx] * f
            }
        }

    operator fun times(o: Vector): Vector {
        if (dim != o.dim) throw IllegalArgumentException("Dimension doesn't match")

        return Vector(dim).also {
            for (idx in 0 until dim) {
                it[idx] = this[idx] * o[idx]
            }
        }
    }

    operator fun div(f: Float): Vector = this * (1.0f / f)

    operator fun div(o: Vector): Vector {
        if (dim != o.dim) throw IllegalArgumentException("Dimension doesn't match")

        return Vector(dim).also {
            for (idx in 0 until dim) {
                it[idx] = this[idx] / o[idx]
            }
        }
    }

    operator fun unaryMinus(): Vector =
        Vector(dim).also {
            for (idx in 0 until dim) {
                it[idx] = -this[idx]
            }
        }

    fun assign(vararg args: Float): Vector =
        if (dim == args.size) {
            populate(args.withIndex())
        } else {
            throw IllegalArgumentException("Dimension doesn't match")
        }

    fun assign(o: Vector): Vector {
        if (dim != o.dim) throw IllegalArgumentException("Dimension doesn't match")
        return populate(o.data.withIndex())
    }

    fun assign(a: Collection<Float>): Vector {
        if (dim != a.size) throw java.lang.IllegalArgumentException("Dimension doesn't match")
        return populate(a.withIndex())
    }

    fun copy(): Vector =
        Vector(dim).also { data.copyInto(it.data) }

    override fun equals(other: Any?): Boolean {
        val o: Vector = other as? Vector ?: return false

        if (dim != o.dim) return false

        for (idx in 0 until dim) {
            if (this[idx] != o[idx]) return false
        }

        return true
    }

    fun fuzzyEqual(o: Vector, threshold: Float = 1e-5f): Boolean {
        if (dim != o.dim) throw IllegalArgumentException("Dimension doesn't match")

        for ((idx, e) in data.withIndex()) {
            if (!fuzzyEqual(e, o[idx], threshold)) return false
        }

        return true
    }

    fun negate(): Vector {
        for (idx in 0 until dim) {
            this[idx] = -this[idx]
        }
        return this
    }

    fun normalize(): Vector {
        val n = norm
        return this.also {
            for (idx in 0 until dim) {
                data[idx] /= n
            }
        }
    }

    fun abs(): Vector =
        Vector(dim).also {
            for (idx in 0 until dim) {
                it[idx] = abs(this[idx])
            }
        }

    fun dist(o: Vector): Float {
        if (dim != o.dim) throw IllegalArgumentException("Dimension doesn't match")

        var sum = 0.0f
        var d = 0.0f

        for (idx in 0 until dim) {
            d = this[idx] - o[idx]
            sum += d * d
        }

        return sqrt(sum)
    }

    override fun toString(): String = data.joinToString(prefix="[", postfix="]")
}

val Iterable<Vector>.numFloats: Int
    get() {
        var c = 0
        this.forEach{ c += it.data.size }
        return c
    }

fun Iterable<Vector>.toFloatArray(): FloatArray =
    FloatArray(this.numFloats).also {
        var idx = 0
        for (v in this) {
            for (f in v.data) {
                it[idx++] = f
            }
        }
    }