package com.mcdane.box

import kotlin.math.abs

inline fun fuzzyEqual(a: Float, b: Float, threshold: Float = 1e-6f) =
    abs(a - b) <= threshold