package com.mmisoft.mmisweeper.Game;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mmisoft.mmisweeper.R;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    Context context;
    boolean isLandscape;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int numOfCollumns;
    private int numOfRows;
    private ArrayList<Cell> cells;
    private OnCellClickListener listener;

    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, ArrayList<Cell> cells, int numOfCollumns, int numOfRows, boolean isLandscape) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.numOfCollumns = numOfCollumns;
        this.numOfRows = numOfRows;
        this.cells = cells;
        this.isLandscape = isLandscape;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (cells.get(position).isRevealed()) {
            switch (cells.get(position).getValue()) {
                case -2:
                    holder.myTextView.setBackgroundResource(R.drawable.bomb_exploded);
                    break;
                case Cell.BOMB:
                    holder.myTextView.setBackgroundResource(R.drawable.bomb);
                    break;
                case Cell.BLANK:
                    holder.myTextView.setBackgroundResource(R.drawable.zero);
                    break;
                case 1:
                    holder.myTextView.setBackgroundResource(R.drawable.one);
                    break;
                case 2:
                    holder.myTextView.setBackgroundResource(R.drawable.two);
                    break;
                case 3:
                    holder.myTextView.setBackgroundResource(R.drawable.three);
                    break;
                case 4:
                    holder.myTextView.setBackgroundResource(R.drawable.four);
                    break;
                case 5:
                    holder.myTextView.setBackgroundResource(R.drawable.five);
                    break;
                case 6:
                    holder.myTextView.setBackgroundResource(R.drawable.six);
                    break;
                case 7:
                    holder.myTextView.setBackgroundResource(R.drawable.seven);
                    break;
                case 8:
                    holder.myTextView.setBackgroundResource(R.drawable.eight);
            }
        }else {
            if(cells.get(position).isFlagged()){
                holder.myTextView.setBackgroundResource(R.drawable.flagged);
            }else {
                holder.myTextView.setBackgroundResource(R.drawable.facing_down);
            }
        }
//            } else {
//                holder.myTextView.setBackgroundResource(R.drawable.facing_down);
//            }

//            holder.myTextView.setBackground(R.drawable);
//            holder.myTextView.setText(String.valueOf(cells.get(position).getValue()));
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return cells.size();
    }



    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView myTextView;


        ViewHolder(View itemView) {
            super(itemView);


            myTextView = itemView.findViewById(R.id.info_text);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            //if you need three fix imageview in width
            if(isLandscape){
                int deviceheight = displaymetrics.heightPixels / numOfCollumns;

                myTextView.getLayoutParams().width = deviceheight;

                //if you need same height as width you can set devicewidth in holder.image_view.getLayoutParams().height
                myTextView.getLayoutParams().height = deviceheight;
            }else {
                int devicewidth = displaymetrics.widthPixels / numOfCollumns;

                myTextView.getLayoutParams().width = devicewidth;

                //if you need same height as width you can set devicewidth in holder.image_view.getLayoutParams().height
                myTextView.getLayoutParams().height = devicewidth;
            }


            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(getAdapterPosition());
        }


        @Override
        public boolean onLongClick(View view) {
            if (mClickListener != null) mClickListener.onLongItemClick(getAdapterPosition());
            return true;
        }
    }

    // convenience method for getting data at click position
    Cell getItem(int id) {
        return cells.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(int position);
        void onLongItemClick(int position);
    }
}
