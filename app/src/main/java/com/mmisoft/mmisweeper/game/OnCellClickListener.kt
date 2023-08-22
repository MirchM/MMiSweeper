package com.mmisoft.mmisweeper.game

interface OnCellClickListener {
    fun onCellClick(position: Int)
    fun onLongCellClick(position: Int)
}
