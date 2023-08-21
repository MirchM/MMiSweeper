package com.mmisoft.mmisweeper.Game

interface OnCellClickListener {
    fun onCellClick(position: Int)
    fun onLongCellClick(position: Int)
}
