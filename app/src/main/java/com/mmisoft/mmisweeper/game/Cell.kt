package com.mmisoft.mmisweeper.game

import java.io.Serializable

class Cell(val id: Int) : Serializable {
    var isFlagged = false
        private set
    @JvmField
    var isRevealed = false
    @JvmField
    var value: Int = BLANK

    fun toggleFlagged() {
        isFlagged = !isFlagged
    }

    companion object {
        const val BOMB = -1
        const val BLANK = 0
    }
}
