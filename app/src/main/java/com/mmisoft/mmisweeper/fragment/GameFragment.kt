package com.mmisoft.mmisweeper.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mmisoft.mmisweeper.R
import com.mmisoft.mmisweeper.game.Cell
import com.mmisoft.mmisweeper.game.MyRecyclerViewAdapter
import com.mmisoft.mmisweeper.model.GameViewModel
import java.text.DecimalFormat
import java.util.Random

class GameFragment : Fragment(), MyRecyclerViewAdapter.ItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        numberOfBombs = requireArguments().getInt("bombs")
        numOfColumns = requireArguments().getInt("columns")
        numOfRows = requireArguments().getInt("rows")
    }

    private val viewModel : GameViewModel by viewModels()
    private var adapter: MyRecyclerViewAdapter? = null
    private var numOfColumns = 0
    private var cells: ArrayList<Cell>? = null
    private var numberOfBombs = 0
    private var numOfRows = 0
    private var winCondition = 0

    //Booleans
    private var gameOver = false
    private var firstClick = true
    private var toggleFlag = false
    private var cTimer: CountDownTimer? = null
    private var timeTV: TextView? = null
    private var bombsTV: TextView? = null

    //dialogBooleans for screen orientation changes
    private var loseDialog = false
    private var winDialog = false

    //MediaPlayer for button sound
    private var soundPool: SoundPool? = null
    private var soundDefault = 0
    private var soundFlag = 0
    private var soundRemoveFlag = 0
    private var soundDeath = 0


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_game, container, false)
        bombsTV = v.findViewById(R.id.bombsTextView)
        timeTV = v.findViewById(R.id.timeTextView)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_GAME)
            .build()
        soundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttributes)
            .build()
        val sh = requireContext().getSharedPreferences("Theme", Context.MODE_PRIVATE)
        soundPool?.let { soundPool ->
            when (sh.getString("theme", "default")) {
                "minecraft wood", "minecraft iron", "minecraft gold", "minecraft diamond" -> {
                    soundDefault = soundPool.load(context, R.raw.minecraft_default_sound, 1)
                    soundFlag = soundPool.load(context, R.raw.minecraft_flag_sound, 1)
                    soundRemoveFlag = soundPool.load(context, R.raw.minecraft_flag_remove_sound, 1)
                    soundDeath = soundPool.load(context, R.raw.minecraft_death_sound, 1)
                }

                "default" -> {
                    soundDefault = soundPool.load(context, R.raw.default_sound, 1)
                    soundFlag = soundPool.load(context, R.raw.default_sound, 1)
                    soundRemoveFlag = soundPool.load(context, R.raw.default_sound, 1)
                    soundDeath = soundPool.load(context, R.raw.default_sound, 1)
                }
            }
        }
        val resetButton = v.findViewById<ImageButton>(R.id.resetBtn)
        resetButton.setOnClickListener {
            resetBoard()
            gameOver = false
            adapter!!.notifyDataSetChanged()
        }
        val flagToggleBtn = v.findViewById<ImageButton>(R.id.flagButton)
        flagToggleBtn.setOnClickListener {
            toggleFlag = !toggleFlag
            if (toggleFlag) {
                flagToggleBtn.setImageResource(R.drawable.flagged_selected)
            } else {
                flagToggleBtn.setImageResource(R.drawable.flagged)
            }
        }
        cells = ArrayList()
        if (savedInstanceState != null) {
            saved = true
            gameOver = savedInstanceState.getBoolean("gameOver")
            firstClick = savedInstanceState.getBoolean("firstClick")
            toggleFlag = savedInstanceState.getBoolean("toggleFlag")
            winCondition = savedInstanceState.getInt("winCondition")
            loseDialog = savedInstanceState.getBoolean("loseDialog")
            winDialog = savedInstanceState.getBoolean("winDialog")
            bombsTV?.text = customFormat(savedInstanceState.getInt("bombs").toLong())
            if (!firstClick && !winDialog && !loseDialog) {
                startTimer(savedInstanceState.getInt("currentTime"))
            } else {
                timeTV?.text = (customFormat(savedInstanceState.getInt("currentTime").toLong()))
            }
            if (winDialog) showWinDialog()
            if (loseDialog) showLostDialog()
            cells?.clear()
            cells?.addAll((savedInstanceState.getSerializable("cells") as ArrayList<Cell>?)!!)
        } else {
            bombsTV?.text = (customFormat(numberOfBombs.toLong()))
            saved = false
            resetBoard()
        }

        // set up the RecyclerView
        val recyclerView = v.findViewById<RecyclerView>(R.id.rvNumbers)
        recyclerView.layoutManager = GridLayoutManager(context, numOfColumns)
        adapter = cells?.let {
            MyRecyclerViewAdapter(
                requireContext(),
                it,
                numOfColumns,
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            )
        }

        adapter!!.setClickListener(this)
        recyclerView.adapter = adapter

        //return the view
        return v
    }

    private fun resetBoard() {
        if (saved) {
            saved = false
        }
        winCondition = numOfColumns * numOfRows - numberOfBombs
        if (cells!!.size > 0) {
            cells!!.clear()
            firstClick = true
            cancelTimer()
            timeTV!!.text = customFormat(0)
            bombsTV!!.text = customFormat(numberOfBombs.toLong())
        }
        for (i in 0 until numOfRows * numOfColumns) {
            val cell = Cell(i)
            cell.value = 0
            cells!!.add(cell)
        }
    }

    private fun flagCell(position: Int) {
        if (!firstClick) {
            if (!cells!![position].isRevealed) {
                cells!![position].toggleFlagged()
                adapter!!.notifyItemChanged(position)
                if (cells!![position].isFlagged) {
                    soundPool!!.play(soundRemoveFlag, 0.44f, 0.44f, 1, 0, 1f)
                    bombsTV!!.text = customFormat((bombsTV!!.text.toString().toInt() - 1).toLong())
                } else {
                    soundPool!!.play(soundFlag, 0.44f, 0.44f, 1, 0, 1f)
                    bombsTV!!.text = customFormat((bombsTV!!.text.toString().toInt() + 1).toLong())
                }
            }
        }
    }

    private fun placeBombs(cell: Cell) {
        val bombs: MutableList<Cell> = ArrayList()
        var bombsPlaced = 0
        while (bombsPlaced < numberOfBombs) {
            val x = Random().nextInt(numOfColumns)
            val y = Random().nextInt(numOfColumns)
            if (cells!![x + y * numOfColumns].value == Cell.BLANK && cells!![x + y * numOfColumns].id != cell.id) {
                cells!![x + y * numOfColumns].value = Cell.BOMB
                bombsPlaced++
                bombs.add(cells!![x + y * numOfColumns])
            }
        }
        for (cell2 in bombs) {
            for (cell1 in getNeighbours(cell2)) {
                if (cell1.value != Cell.BOMB) cell1.value = cell1.value + 1
            }
        }
    }

    private fun revealAllBombs() {
        for (cell in cells!!) {
            if (cell.value == Cell.BOMB) {
                cell.isRevealed = true
            }
        }
    }

    private fun getNeighbours(cell: Cell): List<Cell> {
        val neighbours: MutableList<Cell> = ArrayList()
        //getting the cell's y coordinate
        val y = cell.id / numOfColumns
        //getting the cell's x coordinate
        val x = cell.id % numOfColumns
        if (x != 0) {
            if (checkOutOfBounds(x - 1 + (y - 1) * numOfColumns)) neighbours.add(cells!![x - 1 + (y - 1) * numOfColumns])
            if (checkOutOfBounds(x - 1 + y * numOfColumns)) neighbours.add(cells!![x - 1 + y * numOfColumns])
            if (checkOutOfBounds(x - 1 + (y + 1) * numOfColumns)) neighbours.add(cells!![x - 1 + (y + 1) * numOfColumns])
        }
        if (x != numOfColumns - 1) {
            if (checkOutOfBounds(x + 1 + (y - 1) * numOfColumns)) neighbours.add(cells!![x + 1 + (y - 1) * numOfColumns])
            if (checkOutOfBounds(x + 1 + y * numOfColumns)) neighbours.add(cells!![x + 1 + y * numOfColumns])
            if (checkOutOfBounds(x + 1 + (y + 1) * numOfColumns)) neighbours.add(cells!![x + 1 + (y + 1) * numOfColumns])
        }
        if (checkOutOfBounds(x + (y - 1) * numOfColumns)) neighbours.add(cells!![x + (y - 1) * numOfColumns])
        if (checkOutOfBounds(x + (y + 1) * numOfColumns)) neighbours.add(cells!![x + (y + 1) * numOfColumns])
        return neighbours
    }

    private fun getEmptyNeighbourCells(cell: Cell): List<Cell> {
        for (cell2 in getNeighbours(cell)) {
            if (cell2.value != Cell.BLANK && !cell2.isRevealed) {
                cell2.isRevealed = true
                winCondition--
                if (cell2.isFlagged) {
                    bombsTV!!.text = customFormat((bombsTV!!.text.toString().toInt() + 1).toLong())
                }
            }
        }
        cell.isRevealed = true
        if (cell.isFlagged) {
            bombsTV!!.text = customFormat((bombsTV!!.text.toString().toInt() + 1).toLong())
        }
        winCondition--
        val emptyCellList: MutableList<Cell> = ArrayList()
        for (cell1 in getNeighbours(cell)) {
            if (cell1.value == Cell.BLANK && !cell1.isRevealed) {
                emptyCellList.addAll(getEmptyNeighbourCells(cell1))
            }
        }
        return emptyCellList
    }

    private fun checkOutOfBounds(pos: Int): Boolean {
        return pos < numOfColumns * numOfRows && pos >= 0
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onItemClick(position: Int) {
        val cell = cells!![position]
        if (firstClick) {
            soundPool?.play(soundDefault, 0.44f, 0.44f, 1, 0, 1f)
            placeBombs(cell)
            firstClick = false
            startTimer(0)
        }
        if (!gameOver) {
            if (toggleFlag) {
                flagCell(position)
            } else if (!cell.isFlagged) {
                when (cell.value) {
                    Cell.BOMB -> {
                        soundPool?.play(soundDeath, 0.44f, 0.44f, 1, 0, 1f)
                        cells?.let { it[it.indexOf(cell)].isRevealed = true }
                        cell.value = -2
                        showLostDialog()
                        revealAllBombs()
                        gameOver = true
                        cancelTimer()
                    }

                    Cell.BLANK -> {
                        soundPool?.play(soundDefault, 0.44f, 0.44f, 1, 0, 1f)
                        if (!cell.isRevealed) {
                            getEmptyNeighbourCells(cell)
                        }
                    }

                    else -> {
                        soundPool?.play(soundDefault, 0.44f, 0.44f, 1, 0, 1f)
                        if (!cell.isRevealed) {
                            winCondition--
                        }
                        cells?.let { it[it.indexOf(cell)].isRevealed = true }
                    }
                }
            }
            adapter?.notifyDataSetChanged()
            if (winCondition == 0) {
                showWinDialog()
                gameOver = true
                cancelTimer()
            }
        }
    }

    override fun onLongItemClick(position: Int) {
        flagCell(position)
    }

    private fun startTimer(currentMillis: Int) {
        cTimer = object : CountDownTimer(10000000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeTV?.text =
                    customFormat((10000000 - (millisUntilFinished - currentMillis * 1000L)) / 1000)
            }

            override fun onFinish() {}
        }
        cTimer?.start()
    }

    private fun cancelTimer() {
        cTimer?.cancel()
    }

    override fun onDestroy() {
        cancelTimer()
        super.onDestroy()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showWinDialog() {
        winDialog = true
        val dialog = Dialog(requireContext())
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        //disabling the default title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //cancel the dialog by pressing anywhere outside of it
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.win_dialog)
        //BUTTONS
        val dismissBtn = dialog.findViewById<Button>(R.id.dismissWinDialogButton)
        dismissBtn.setOnClickListener {
            dialog.dismiss()
            winDialog = false
        }
        val restartBtn = dialog.findViewById<Button>(R.id.resetWinDialogButton)
        restartBtn.setOnClickListener {
            dialog.dismiss()
            resetBoard()
            gameOver = false
            adapter!!.notifyDataSetChanged()
            winDialog = false
        }
        //TEXT VIEW
        val timeView = dialog.findViewById<TextView>(R.id.winTimeTV)
        timeView.text = timeTV!!.text
        dialog.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showLostDialog() {
        loseDialog = true
        val dialog = Dialog(requireContext())
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        //disabling the default title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //cancel the dialog by pressing anywhere outside of it
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.lose_dialog)
        //BUTTONS
        val dismissBtn = dialog.findViewById<Button>(R.id.dismissLoseDialogButton)
        dismissBtn.setOnClickListener {
            dialog.dismiss()
            loseDialog = false
        }
        val restartBtn = dialog.findViewById<Button>(R.id.resetLoseDialogButton)
        restartBtn.setOnClickListener {
            dialog.dismiss()
            resetBoard()
            gameOver = false
            loseDialog = false
            adapter!!.notifyDataSetChanged()
        }
        //TEXT VIEW
        val timeView = dialog.findViewById<TextView>(R.id.loseTimeTV)
        timeView.text = timeTV!!.text
        val bombsView = dialog.findViewById<TextView>(R.id.bombsDiscoveredTV)
        bombsView.text = bombsTV!!.text.toString()
        dialog.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("cells", cells)
        outState.putBoolean("gameOver", gameOver)
        outState.putBoolean("firstClick", firstClick)
        outState.putBoolean("toggleFlag", toggleFlag)
        outState.putInt("winCondition", winCondition)
        outState.putInt("bombs", bombsTV!!.text.toString().toInt())
        outState.putInt("currentTime", timeTV!!.text.toString().toInt())
        outState.putBoolean("winDialog", winDialog)
        outState.putBoolean("loseDialog", loseDialog)
    }

    companion object {
        @JvmStatic
        fun newInstance(numOfColumns: Int, numOfRows: Int, numberOfBombs: Int): GameFragment {
            val fragment = GameFragment()
            val args = Bundle()
            args.putInt("columns", numOfColumns)
            args.putInt("rows", numOfRows)
            args.putInt("bombs", numberOfBombs)
            fragment.arguments = args
            return fragment
        }

        var saved = false
        fun customFormat(value: Long): String {
            val myFormatter = DecimalFormat("000")
            return myFormatter.format(value)
        }
    }
}