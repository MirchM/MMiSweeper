package com.mmisoft.mmisweeper.game

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mmisoft.mmisweeper.R

class MyRecyclerViewAdapter(
    var context: Context,
    private val cells: ArrayList<Cell>,
    private val numOfColumns: Int,
    var isLandscape: Boolean
) : RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mClickListener: ItemClickListener? = null

    // inflates the cell layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.recyclerview_item, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each cell
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sh = context.getSharedPreferences("Theme", Context.MODE_PRIVATE)
        if (cells[position].isRevealed) {
            when (sh.getString("theme", "default")) {
                "default" -> when (cells[position].value) {
                    -2 -> holder.myTextView.setBackgroundResource(R.drawable.bomb_exploded)
                    Cell.BOMB -> holder.myTextView.setBackgroundResource(R.drawable.bomb)
                    Cell.BLANK -> holder.myTextView.setBackgroundResource(R.drawable.zero)
                    1 -> holder.myTextView.setBackgroundResource(R.drawable.one)
                    2 -> holder.myTextView.setBackgroundResource(R.drawable.two)
                    3 -> holder.myTextView.setBackgroundResource(R.drawable.three)
                    4 -> holder.myTextView.setBackgroundResource(R.drawable.four)
                    5 -> holder.myTextView.setBackgroundResource(R.drawable.five)
                    6 -> holder.myTextView.setBackgroundResource(R.drawable.six)
                    7 -> holder.myTextView.setBackgroundResource(R.drawable.seven)
                    8 -> {
                        holder.myTextView.setBackgroundResource(R.drawable.eight)
                        holder.myTextView.setBackgroundResource(R.drawable.facing_down)
                    }

                    else -> holder.myTextView.setBackgroundResource(R.drawable.facing_down)
                }

                "minecraft iron" -> when (cells[position].value) {
                    -2 -> holder.myTextView.setBackgroundResource(R.drawable.iron_minecraft_rbomb_exploed)
                    Cell.BOMB -> holder.myTextView.setBackgroundResource(R.drawable.iron_minecraft_bomb)
                    Cell.BLANK -> holder.myTextView.setBackgroundResource(R.drawable.iron_minecraft_revealed)
                    1 -> holder.myTextView.setBackgroundResource(R.drawable.iron_minecraft_1)
                    2 -> holder.myTextView.setBackgroundResource(R.drawable.iron_minecraft_2)
                    3 -> holder.myTextView.setBackgroundResource(R.drawable.iron_minecraft_3)
                    4 -> holder.myTextView.setBackgroundResource(R.drawable.iron_minecraft_4)
                    5 -> holder.myTextView.setBackgroundResource(R.drawable.iron_minecraft_5)
                    6 -> holder.myTextView.setBackgroundResource(R.drawable.iron_minecraft_6)
                    7 -> holder.myTextView.setBackgroundResource(R.drawable.iron_minecraft_7)
                    8 -> {
                        holder.myTextView.setBackgroundResource(R.drawable.iron_minecraft_8)
                        holder.myTextView.setBackgroundResource(R.drawable.iron_minecraft)
                    }

                    else -> holder.myTextView.setBackgroundResource(R.drawable.iron_minecraft)
                }

                "minecraft gold" -> when (cells[position].value) {
                    -2 -> holder.myTextView.setBackgroundResource(R.drawable.gold_minecraft_bomb_exploded)
                    Cell.BOMB -> holder.myTextView.setBackgroundResource(R.drawable.gold_minecraft_bomb)
                    Cell.BLANK -> holder.myTextView.setBackgroundResource(R.drawable.gold_minecraft_revealed)
                    1 -> holder.myTextView.setBackgroundResource(R.drawable.gold_minecraft_1)
                    2 -> holder.myTextView.setBackgroundResource(R.drawable.gold_minecraft_2)
                    3 -> holder.myTextView.setBackgroundResource(R.drawable.gold_minecraft_3)
                    4 -> holder.myTextView.setBackgroundResource(R.drawable.gold_minecraft_4)
                    5 -> holder.myTextView.setBackgroundResource(R.drawable.gold_minecraft_5)
                    6 -> holder.myTextView.setBackgroundResource(R.drawable.gold_minecraft_6)
                    7 -> holder.myTextView.setBackgroundResource(R.drawable.gold_minecraft_7)
                    8 -> {
                        holder.myTextView.setBackgroundResource(R.drawable.gold_minecraft_8)
                        holder.myTextView.setBackgroundResource(R.drawable.gold_minecraft)
                    }

                    else -> holder.myTextView.setBackgroundResource(R.drawable.gold_minecraft)
                }

                "minecraft diamond" -> when (cells[position].value) {
                    -2 -> holder.myTextView.setBackgroundResource(R.drawable.diamond_minecraft_bomb_exploded)
                    Cell.BOMB -> holder.myTextView.setBackgroundResource(R.drawable.diamond_minecraft_bomb)
                    Cell.BLANK -> holder.myTextView.setBackgroundResource(R.drawable.diamond_minecraft_revealed)
                    1 -> holder.myTextView.setBackgroundResource(R.drawable.diamond_minecraft_1)
                    2 -> holder.myTextView.setBackgroundResource(R.drawable.diamond_minecraft_2)
                    3 -> holder.myTextView.setBackgroundResource(R.drawable.diamond_minecraft_3)
                    4 -> holder.myTextView.setBackgroundResource(R.drawable.diamond_minecraft_4)
                    5 -> holder.myTextView.setBackgroundResource(R.drawable.diamond_minecraft_5)
                    6 -> holder.myTextView.setBackgroundResource(R.drawable.diamond_minecraft_6)
                    7 -> holder.myTextView.setBackgroundResource(R.drawable.diamond_minecraft_7)
                    8 -> {
                        holder.myTextView.setBackgroundResource(R.drawable.diamond_minecraft_8)
                        holder.myTextView.setBackgroundResource(R.drawable.diamond_minecraft)
                    }

                    else -> holder.myTextView.setBackgroundResource(R.drawable.diamond_minecraft)
                }
            }
        } else {
            if (cells[position].isFlagged) {
                when (sh.getString("theme", "default")) {
                    "default" -> holder.myTextView.setBackgroundResource(R.drawable.flagged)
                    "minecraft iron" -> holder.myTextView.setBackgroundResource(R.drawable.iron_minecraft_flagged)
                    "minecraft gold" -> holder.myTextView.setBackgroundResource(R.drawable.gold_minecraft_flag)
                    "minecraft diamond" -> holder.myTextView.setBackgroundResource(R.drawable.diamond_minecraft_flag)
                }
            } else {
                when (sh.getString("theme", "default")) {
                    "default" -> holder.myTextView.setBackgroundResource(R.drawable.facing_down)
                    "minecraft iron" -> holder.myTextView.setBackgroundResource(R.drawable.iron_minecraft)
                    "minecraft gold" -> holder.myTextView.setBackgroundResource(R.drawable.gold_minecraft)
                    "minecraft diamond" -> holder.myTextView.setBackgroundResource(R.drawable.diamond_minecraft)
                }
            }
        }
    }

    // total number of cells
    override fun getItemCount(): Int {
        return cells.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, OnLongClickListener {
        var myTextView: TextView

        init {
            myTextView = itemView.findViewById(R.id.info_text)
            val displayMetrics = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
            //if you need three fix imageview in width
            if (isLandscape) {
                val deviceHeight = displayMetrics.heightPixels / numOfColumns
                myTextView.layoutParams.width = deviceHeight

                //if you need same height as width you can set deviceWidth in holder.image_view.getLayoutParams().height
                myTextView.layoutParams.height = deviceHeight
            } else {
                val deviceWidth = displayMetrics.widthPixels / numOfColumns
                myTextView.layoutParams.width = deviceWidth

                //if you need same height as width you can set deviceWidth in holder.image_view.getLayoutParams().height
                myTextView.layoutParams.height = deviceWidth
            }
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(view: View) {
            if (mClickListener != null) mClickListener!!.onItemClick(adapterPosition)
        }

        override fun onLongClick(view: View): Boolean {
            if (mClickListener != null) mClickListener!!.onLongItemClick(adapterPosition)
            return true
        }
    }

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(position: Int)
        fun onLongItemClick(position: Int)
    }
}
