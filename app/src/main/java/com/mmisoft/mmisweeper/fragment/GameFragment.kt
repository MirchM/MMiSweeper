package com.mmisoft.mmisweeper.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
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
        viewModel.initialiseGameBoard(
            requireArguments().getInt("bombs"),
            requireArguments().getInt("columns"),
            requireArguments().getInt("rows")
        )
    }

    private val viewModel: GameViewModel by viewModels()
    private var adapter: MyRecyclerViewAdapter? = null

    //Booleans
    private var timeTV: TextView? = null
    private var bombsTV: TextView? = null

    //dialogBooleans for screen orientation changes

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
            viewModel.setGameOver(false)
            adapter!!.notifyDataSetChanged()
        }
        val flagToggleBtn = v.findViewById<ImageButton>(R.id.flagButton)
        flagToggleBtn.setOnClickListener {
            viewModel.setToggleFlag(!viewModel.toggleFlag)
            if (viewModel.toggleFlag) {
                flagToggleBtn.setImageResource(R.drawable.flagged_selected)
            } else {
                flagToggleBtn.setImageResource(R.drawable.flagged)
            }
        }
        viewModel.countValue.observe(viewLifecycleOwner) { newCountValue ->
            timeTV?.text = customFormat(newCountValue)
        }

        if (savedInstanceState != null) {
            saved = true
            bombsTV?.text = (customFormat(viewModel.numberOfBombs.toLong()))
            startTimer()
            if (viewModel.winDialog) showWinDialog()
            if (viewModel.loseDialog) showLostDialog()
        } else {
            bombsTV?.text = (customFormat(viewModel.numberOfBombs.toLong()))
            saved = false
            resetBoard()
        }

        // set up the RecyclerView
        val recyclerView = v.findViewById<RecyclerView>(R.id.rvNumbers)
        recyclerView.layoutManager = GridLayoutManager(context, viewModel.numOfColumns)
        adapter = MyRecyclerViewAdapter(
            requireContext(),
            viewModel.cells,
            viewModel.numOfColumns,
            resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        )

        adapter!!.setClickListener(this)
        recyclerView.adapter = adapter

        //return the view
        return v
    }

    private fun resetBoard() {
        if (saved) {
            saved = false
        }
        viewModel.setWinCondition(viewModel.numOfColumns * viewModel.numOfRows - viewModel.numberOfBombs)
        if (viewModel.cells.size > 0) {
            viewModel.cells.clear()
            viewModel.setFirstCick(true)
            cancelTimer()
            timeTV!!.text = customFormat(0)
            bombsTV!!.text = customFormat(viewModel.numberOfBombs.toLong())
        }
        for (i in 0 until viewModel.numOfRows * viewModel.numOfColumns) {
            val cell = Cell(i)
            cell.value = 0
            viewModel.cells.add(cell)
        }
    }

    private fun flagCell(position: Int) {
        if (!viewModel.firstClick) {
            if (!viewModel.cells[position].isRevealed) {
                viewModel.cells[position].toggleFlagged()
                adapter!!.notifyItemChanged(position)
                if (viewModel.cells[position].isFlagged) {
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
        while (bombsPlaced < viewModel.numberOfBombs) {
            val x = Random().nextInt(viewModel.numOfColumns)
            val y = Random().nextInt(viewModel.numOfColumns)
            if (viewModel.cells[x + y * viewModel.numOfColumns].value == Cell.BLANK && viewModel.cells[x + y * viewModel.numOfColumns].id != cell.id) {
                viewModel.cells[x + y * viewModel.numOfColumns].value = Cell.BOMB
                bombsPlaced++
                bombs.add(viewModel.cells[x + y * viewModel.numOfColumns])
            }
        }
        for (cell2 in bombs) {
            for (cell1 in getNeighbours(cell2)) {
                if (cell1.value != Cell.BOMB) cell1.value = cell1.value + 1
            }
        }
    }

    private fun revealAllBombs() {
        for (cell in viewModel.cells) {
            if (cell.value == Cell.BOMB) {
                cell.isRevealed = true
            }
        }
    }

    private fun getNeighbours(cell: Cell): List<Cell> {
        val neighbours: MutableList<Cell> = ArrayList()
        //getting the cell's y coordinate
        val y = cell.id / viewModel.numOfColumns
        //getting the cell's x coordinate
        val x = cell.id % viewModel.numOfColumns
        if (x != 0) {
            if (checkOutOfBounds(x - 1 + (y - 1) * viewModel.numOfColumns)) neighbours.add(viewModel.cells[x - 1 + (y - 1) * viewModel.numOfColumns])
            if (checkOutOfBounds(x - 1 + y * viewModel.numOfColumns)) neighbours.add(viewModel.cells[x - 1 + y * viewModel.numOfColumns])
            if (checkOutOfBounds(x - 1 + (y + 1) * viewModel.numOfColumns)) neighbours.add(viewModel.cells[x - 1 + (y + 1) * viewModel.numOfColumns])
        }
        if (x != viewModel.numOfColumns - 1) {
            if (checkOutOfBounds(x + 1 + (y - 1) * viewModel.numOfColumns)) neighbours.add(viewModel.cells[x + 1 + (y - 1) * viewModel.numOfColumns])
            if (checkOutOfBounds(x + 1 + y * viewModel.numOfColumns)) neighbours.add(viewModel.cells[x + 1 + y * viewModel.numOfColumns])
            if (checkOutOfBounds(x + 1 + (y + 1) * viewModel.numOfColumns)) neighbours.add(viewModel.cells[x + 1 + (y + 1) * viewModel.numOfColumns])
        }
        if (checkOutOfBounds(x + (y - 1) * viewModel.numOfColumns)) neighbours.add(viewModel.cells[x + (y - 1) * viewModel.numOfColumns])
        if (checkOutOfBounds(x + (y + 1) * viewModel.numOfColumns)) neighbours.add(viewModel.cells[x + (y + 1) * viewModel.numOfColumns])
        return neighbours
    }

    private fun getEmptyNeighbourCells(cell: Cell): List<Cell> {
        for (cell2 in getNeighbours(cell)) {
            if (cell2.value != Cell.BLANK && !cell2.isRevealed) {
                cell2.isRevealed = true
                viewModel.setWinCondition(viewModel.winCondition - 1)
                if (cell2.isFlagged) {
                    bombsTV!!.text = customFormat((bombsTV!!.text.toString().toInt() + 1).toLong())
                }
            }
        }
        cell.isRevealed = true
        if (cell.isFlagged) {
            bombsTV!!.text = customFormat((bombsTV!!.text.toString().toInt() + 1).toLong())
        }
        viewModel.setWinCondition(viewModel.winCondition - 1)
        val emptyCellList: MutableList<Cell> = ArrayList()
        for (cell1 in getNeighbours(cell)) {
            if (cell1.value == Cell.BLANK && !cell1.isRevealed) {
                emptyCellList.addAll(getEmptyNeighbourCells(cell1))
            }
        }
        return emptyCellList
    }

    private fun checkOutOfBounds(pos: Int): Boolean {
        return pos < viewModel.numOfColumns * viewModel.numOfRows && pos >= 0
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onItemClick(position: Int) {
        val cell = viewModel.cells[position]
        if (viewModel.firstClick) {
            soundPool?.play(soundDefault, 0.44f, 0.44f, 1, 0, 1f)
            placeBombs(cell)
            viewModel.setFirstCick(false)
            startTimer()
        }
        if (!viewModel.gameOver) {
            if (viewModel.toggleFlag) {
                flagCell(position)
            } else if (!cell.isFlagged) {
                when (cell.value) {
                    Cell.BOMB -> {
                        soundPool?.play(soundDeath, 0.44f, 0.44f, 1, 0, 1f)
                        viewModel.cells.let { it[it.indexOf(cell)].isRevealed = true }
                        cell.value = -2
                        showLostDialog()
                        revealAllBombs()
                        viewModel.setGameOver(true)
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
                            viewModel.setWinCondition(viewModel.winCondition - 1)
                        }
                        viewModel.cells.let { it[it.indexOf(cell)].isRevealed = true }
                    }
                }
            }
            adapter?.notifyDataSetChanged()
            if (viewModel.winCondition == 0) {
                showWinDialog()
                viewModel.setGameOver(true)
                cancelTimer()
            }
        }
    }

    override fun onLongItemClick(position: Int) {
        flagCell(position)
    }

    private fun startTimer() {
        viewModel.countValue.value?.toInt()?.let { viewModel.startCountdownTimer(it) }
    }

    private fun cancelTimer() {
        viewModel.cTimer?.cancel()
    }

    override fun onDestroy() {
        cancelTimer()
        super.onDestroy()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showWinDialog() {
        viewModel.setWinDialog(true)
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
            viewModel.setWinDialog(false)
        }
        val restartBtn = dialog.findViewById<Button>(R.id.resetWinDialogButton)
        restartBtn.setOnClickListener {
            dialog.dismiss()
            resetBoard()
            viewModel.setGameOver(false)
            adapter!!.notifyDataSetChanged()
            viewModel.setWinDialog(false)
        }
        //TEXT VIEW
        val timeView = dialog.findViewById<TextView>(R.id.winTimeTV)
        timeView.text = timeTV?.text
        dialog.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showLostDialog() {
        viewModel.setLoseDialog(true)
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
            viewModel.setLoseDialog(false)
        }
        val restartBtn = dialog.findViewById<Button>(R.id.resetLoseDialogButton)
        restartBtn.setOnClickListener {
            dialog.dismiss()
            resetBoard()
            viewModel.setGameOver(false)
            viewModel.setLoseDialog(false)
            adapter!!.notifyDataSetChanged()
        }
        //TEXT VIEW
        val timeView = dialog.findViewById<TextView>(R.id.loseTimeTV)
        timeView.text = timeTV!!.text
        val bombsView = dialog.findViewById<TextView>(R.id.bombsDiscoveredTV)
        bombsView.text = bombsTV!!.text.toString()
        dialog.show()
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