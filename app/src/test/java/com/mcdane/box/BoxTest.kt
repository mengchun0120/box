package com.mcdane.box

import org.junit.Test
import org.junit.Assert.*

class BoxConfigTest {
    @Test
    fun colTestMaskIsCorrect() {
        assertTrue(Box.colTestMask == 0x1111)
    }

    @Test
    fun toBitmapWorks() {
        val frame1 = listOf(
            "1000",
            "0100",
            "0011",
            "0001"
        )
        assertTrue(Box.toBitmap(frame1) == 0x12c8)

        val frame2 = listOf(
            "0011",
            "1100",
            "1111",
            "0111"
        )
        assertTrue(Box.toBitmap(frame2) == 0xc3fe)
    }

    @Test
    fun toBitmapFails() {
        var exceptionHappened1 = false
        try {
            val frame1 = listOf(
                "1021"
            )
            Box.toBitmap(frame1)
        } catch (e: IllegalArgumentException) {
            exceptionHappened1 = true
        }
        assertTrue(exceptionHappened1)

        var exceptionHappened2 = false
        try {
            val frame2 = listOf(
                "102",
                "1111",
                "0000",
                "0000"
            )
            Box.toBitmap(frame2)
        } catch (e: IllegalArgumentException) {
            exceptionHappened2 = true
        }
        assertTrue(exceptionHappened2)
    }

    @Test
    fun firstRowWorks() {
        assertTrue(Box.firstRow(0x0111) == 0)
        assertTrue(Box.firstRow(0x0420) == 1)
        assertTrue(Box.firstRow(0x0600) == 2)
        assertTrue(Box.firstRow(0x8000) == 3)
        assertTrue(Box.firstRow(0) == -1)
    }

    @Test
    fun lastRowWorks() {
        assertTrue(Box.lastRow(0x0001) == 0)
        assertTrue(Box.lastRow(0x0081) == 1)
        assertTrue(Box.lastRow(0x0420) == 2)
        assertTrue(Box.lastRow(0x3001) == 3)
        assertTrue(Box.lastRow(0) == -1)
    }

    @Test
    fun firstColWorks() {
        assertTrue(Box.firstCol(0x0241) == 0)
        assertTrue(Box.firstCol(0x2600) == 1)
        assertTrue(Box.firstCol(0x4008) == 2)
        assertTrue(Box.firstCol(0x0008) == 3)
        assertTrue(Box.firstCol(0) == -1)
    }

    @Test
    fun lastColWorks() {
        assertTrue(Box.lastCol(0x0110) == 0)
        assertTrue(Box.lastCol(0x2030) == 1)
        assertTrue(Box.lastCol(0x7777) == 2)
        assertTrue(Box.lastCol(0x8007) == 3)
        assertTrue(Box.lastCol(0) == -1)
    }
}