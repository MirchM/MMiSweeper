package com.mmisoft.mmisweeper.model

import android.media.SoundPool
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    // Game
    private var _numOfColumns = 0
    val numOfColumns: Int
        get() = _numOfColumns

    private var _numberOfBombs = 0
    val numberOfBombs: Int
        get() = _numberOfBombs

    private var _numOfRows = 0
    val numOfRows: Int
        get() = _numOfRows

    fun initialiseGameBoard(bombs: Int, columns: Int, rows: Int){
        _numberOfBombs = bombs
        _numOfColumns = columns
        _numOfRows = rows
    }

    private var _winCondition = 0
    val winCondition: Int
        get() = _winCondition

    fun setWinCondition(newWinCondition: Int){
        _winCondition = newWinCondition
    }

    // Booleans
    private var _gameOver = false
    val gameOver: Boolean
        get() = _gameOver

    fun setGameOver(newGameOver: Boolean){
        _gameOver = newGameOver
    }

    private var _firstClick = true
    val firstClick: Boolean
        get() = _firstClick

    fun setFirstCick(newFirstClick: Boolean){
        _firstClick = newFirstClick
    }

    private var _toggleFlag = false
    val toggleFlag: Boolean
        get() = _toggleFlag

    fun setToggleFlag(newToggleFlag: Boolean){
        _toggleFlag = newToggleFlag
    }

    private lateinit var _cTimer: CountDownTimer
    val cTimer: CountDownTimer
        get() = _cTimer

    //MediaPlayer for button sound
    private lateinit var _soundPool: SoundPool
    val soundPool: SoundPool
        get() = _soundPool


    private var _soundDefault = 0
    val soundDefault: Int
        get() = _soundDefault

    private var _soundFlag = 0
    val soundFlag: Int
        get() = _soundFlag

    private var _soundRemoveFlag = 0
    val soundRemoveFlag: Int
        get() = _soundRemoveFlag

    private var _soundDeath = 0
    val soundDeath: Int
        get() = _soundDeath

    private var _winDialog = false
    val winDialog: Boolean
        get() = _winDialog

    private var _loseDialog = false
    val loseDialog: Boolean
        get() = _loseDialog

    fun setWinDialog(newWinDialog: Boolean){
        _winDialog = newWinDialog
    }

    fun setLoseDialog(newLoseDialog: Boolean){
        _loseDialog = newLoseDialog
    }

}
