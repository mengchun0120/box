package com.mcdane.box

import org.junit.Test
import org.junit.Assert.*

class BoxConfigTest {
    @Test
    fun colTestMaskIsCorrect() {
        assertTrue(BoxConfig.colTestMask == 0x1111)
    }

    @Test
    fun transformBitmapWorks() {
        val bitmap1 = listOf(
            "1000",
            "0100",
            "0011",
            "0001"
        )
        assertTrue(BoxConfig.transformBitmap(bitmap1) == 0x12c8)

        val bitmap2 = listOf(
            "0011",
            "1100",
            "1111",
            "0111"
        )
        assertTrue(BoxConfig.transformBitmap(bitmap2) == 0xc3fe)
    }

    @Test
    fun firstRowWorks() {
        assertTrue(BoxConfig.firstRow(0x0111) == 0)
        assertTrue(BoxConfig.firstRow(0x0420) == 1)
        assertTrue(BoxConfig.firstRow(0x0600) == 2)
        assertTrue(BoxConfig.firstRow(0x8000) == 3)
        assertTrue(BoxConfig.firstRow(0) == -1)
    }

    @Test
    fun lastRowWorks() {
        assertTrue(BoxConfig.lastRow(0x0001) == 0)
        assertTrue(BoxConfig.lastRow(0x0081) == 1)
        assertTrue(BoxConfig.lastRow(0x0420) == 2)
        assertTrue(BoxConfig.lastRow(0x3001) == 3)
        assertTrue(BoxConfig.lastRow(0) == -1)
    }

    @Test
    fun firstColWorks() {
        assertTrue(BoxConfig.firstCol(0x0241) == 0)
        assertTrue(BoxConfig.firstCol(0x2600) == 1)
        assertTrue(BoxConfig.firstCol(0x4008) == 2)
        assertTrue(BoxConfig.firstCol(0x0008) == 3)
        assertTrue(BoxConfig.firstCol(0) == -1)
    }

    @Test
    fun lastColWorks() {
        assertTrue(BoxConfig.lastCol(0x0110) == 0)
        assertTrue(BoxConfig.lastCol(0x2030) == 1)
        assertTrue(BoxConfig.lastCol(0x7777) == 2)
        assertTrue(BoxConfig.lastCol(0x8007) == 3)
        assertTrue(BoxConfig.lastCol(0) == -1)
    }
}