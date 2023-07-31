package com.mcdane.box

import android.content.res.Resources
import android.util.Log
import java.io.InputStream
import java.io.InputStreamReader

fun readText(stream: InputStream): String = InputStreamReader(stream).readText()

fun readText(res: Resources, resId: Int): String = readText(res.openRawResource(resId))