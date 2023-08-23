package com.mcdane.box

import org.junit.Test
import org.junit.Assert.*

class BoardTest {
    val configs = listOf(
        BoxConfig(
            listOf(0x0027, 0x0027, 0x0027, 0x0027),
            Color(255, 0, 0, 255),
            1,
            1
        )
    )

    @Test
    fun constructorWithGoodContentWorks() {
        val goodContent = listOf(
            listOf<Int?>(null, 0, 0, null, null, null, null, null),
            listOf<Int?>(null, null, 0, 0, null, null, null, null),
            listOf<Int?>(0, 0, 0, null, null, null, null, null),
            listOf<Int?>(null, null, 0, 0, null, null, null, null),
            listOf<Int?>(null, 0, null, null, null, null, null, null),
            listOf<Int?>(null, null, null, null, null, null, null, null),
            listOf<Int?>(null, null, null, null, null, null, null, null),
            listOf<Int?>(null, null, null, null, null, null, null, null),
        )


        val board = Board(goodContent, configs)
        assertTrue(board.rowCount == 8 && board.colCount == 8)
        assertTrue(
            board[0, 0] == null &&
            board[1, 2] == configs[0].color &&
            board[2, 4] == null &&
            board[5, 5] == null
        )
    }

    @Test
    fun constructorWithBadContentFails() {
        val badContent1 = listOf(
            listOf<Int?>(0, null, 0),
            listOf<Int?>(null, 0, null),
            listOf<Int?>(0, null, 0, null),
            listOf<Int?>(0, null, 0, null),
        )

        var exceptionHappened1 = false
        try {
            Board(badContent1, configs)
        } catch (e: IllegalArgumentException) {
            exceptionHappened1 = true
        }
        assertTrue(exceptionHappened1)

        val badContent2 = listOf(
            listOf<Int?>(0, null, 0, null),
            listOf<Int?>(0, 0, 0, 0),
        )

        var exceptionHappened2 = false
        try {
            Board(badContent2,  configs)
        } catch (e: IllegalArgumentException) {
            exceptionHappened2 = true
        }
        assertTrue(exceptionHappened2)
    }
}