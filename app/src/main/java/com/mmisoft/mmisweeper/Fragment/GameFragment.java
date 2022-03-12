package com.mmisoft.mmisweeper.Fragment;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.os.ParcelFileDescriptor.MODE_APPEND;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mmisoft.mmisweeper.Game.Cell;
import com.mmisoft.mmisweeper.Game.MyRecyclerViewAdapter;
import com.mmisoft.mmisweeper.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class GameFragment extends Fragment implements MyRecyclerViewAdapter.ItemClickListener {

    public GameFragment() {
    }

    public static GameFragment newInstance(int numOfCollumns, int numOfRows, int numberOfBombs) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putInt("collumns",numOfCollumns);
        args.putInt("rows", numOfRows);
        args.putInt("bombs", numberOfBombs);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            numberOfBombs = getArguments().getInt("bombs");
            numOfCollumns = getArguments().getInt("collumns");
            numOfRows = getArguments().getInt("rows");
        }
    }

    MyRecyclerViewAdapter adapter;
    public int numOfCollumns;
    private ArrayList<Cell> cells;
    private int numberOfBombs;
    private int numOfRows;
    private int winCondition;
    //Booleans
    private boolean gameOver = false;
    private boolean firstClick = true;
    private boolean toggleFlag = false;
    private CountDownTimer cTimer = null;
    private TextView timeTV, bombsTV;
    public static boolean saved;

    //dialogBooleans for screen orientation changes
    private boolean loseDialog = false;
    private boolean winDialog = false;


    //MediaPlayer for button sound
    private SoundPool soundPool;
    private int soundDefault, soundFlag, soundRemoveFlag, soundDeath;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_game, container, false);
        bombsTV = v.findViewById(R.id.bombsTextView);
        timeTV = v.findViewById(R.id.timeTextView);


        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .build();
        SharedPreferences sh = getContext().getSharedPreferences("Theme", Context.MODE_PRIVATE);
        switch (sh.getString("theme", "default")){
            case "minecraft wood":
            case "minecraft iron":
            case "minecraft gold":
            case "minecraft diamond":
                soundDefault = soundPool.load(getContext(), R.raw.minecraft_default_sound, 1);
                soundFlag = soundPool.load(getContext(), R.raw.minecraft_flag_sound, 1);
                soundRemoveFlag = soundPool.load(getContext(), R.raw.minecraft_flag_remove_sound, 1);
                soundDeath = soundPool.load(getContext(), R.raw.minecraft_death_sound, 1);
                break;
            case "default":
                soundDefault = soundPool.load(getContext(), R.raw.default_sound, 1);
                soundFlag = soundPool.load(getContext(), R.raw.default_sound, 1);
                soundRemoveFlag = soundPool.load(getContext(), R.raw.default_sound, 1);
                soundDeath = soundPool.load(getContext(), R.raw.default_sound, 1);
                break;
        }

        ImageButton resetButton = v.findViewById(R.id.resetBtn);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBoard();
                gameOver = false;
                adapter.notifyDataSetChanged();
            }
        });

        ImageButton flagToggleBtn = v.findViewById(R.id.flagButton);

        flagToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFlag = !toggleFlag;
                if (toggleFlag) {
                    flagToggleBtn.setImageResource(R.drawable.flagged_selected);
                } else {
                    flagToggleBtn.setImageResource(R.drawable.flagged);
                }
            }
        });
        cells = new ArrayList<>();

        if(savedInstanceState != null) {
            saved = true;
            gameOver = savedInstanceState.getBoolean("gameOver");
            firstClick = savedInstanceState.getBoolean("firstClick");
            toggleFlag = savedInstanceState.getBoolean("toggleFlag");
            winCondition = savedInstanceState.getInt("winCondition");
            loseDialog = savedInstanceState.getBoolean("loseDialog");
            winDialog = savedInstanceState.getBoolean("winDialog");
            bombsTV.setText(customFormat(savedInstanceState.getInt("bombs")));
            if (!firstClick && !winDialog && !loseDialog) {
                startTimer(savedInstanceState.getInt("currentTime"));
            }else{
                timeTV.setText(customFormat(savedInstanceState.getInt("currentTime")));
            }
            if(winDialog) showWinDialog();
            if(loseDialog) showLostDialog();
            this.cells.clear();
            this.cells.addAll((ArrayList<Cell>) savedInstanceState.getSerializable("cells"));
        }else{
            bombsTV.setText(customFormat(numberOfBombs));
            saved = false;
            resetBoard();
        }

        // set up the RecyclerView
        RecyclerView recyclerView = v.findViewById(R.id.rvNumbers);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numOfCollumns));
        adapter = new MyRecyclerViewAdapter(getContext(), cells, numOfCollumns, numOfRows, getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        //return the view
        return v;
    }

    private void resetBoard() {
        if(saved) {
            saved = false;
        }
        winCondition = numOfCollumns * numOfRows - numberOfBombs;
        if (cells.size() > 0) {
            cells.clear();
            firstClick = true;
            cancelTimer();
            timeTV.setText(customFormat(000));
            bombsTV.setText(customFormat(numberOfBombs));
        }
        for (int i = 0; i < numOfRows * numOfCollumns; i++) {
            Cell cell = new Cell(i);
            cell.setValue(0);
            cells.add(cell);
        }
    }

    private void flagCell(int position) {
        if (!firstClick) {
            if(!cells.get(position).isRevealed()) {
                cells.get(position).toggleFlagged();
                adapter.notifyItemChanged(position);
                if (cells.get(position).isFlagged()) {
                    soundPool.play(soundRemoveFlag, 0.44f, 0.44f, 1, 0, 1f);
                    bombsTV.setText(customFormat(Integer.parseInt(bombsTV.getText().toString()) - 1));
                } else {
                    soundPool.play(soundFlag, 0.44f, 0.44f, 1, 0, 1f);
                    bombsTV.setText(customFormat(Integer.parseInt(bombsTV.getText().toString()) + 1));
                }
            }
        }
    }

    private void placeBombs(Cell cell) {
        List<Cell> bombs = new ArrayList<>();
        int bombsPlaced = 0;
        while (bombsPlaced < numberOfBombs) {
            int x = new Random().nextInt(numOfCollumns);
            int y = new Random().nextInt(numOfCollumns);

            if (cells.get(x + (y * numOfCollumns)).getValue() == Cell.BLANK && cells.get(x + (y * numOfCollumns)).getId() != cell.getId()) {
                cells.get(x + (y * numOfCollumns)).setValue(Cell.BOMB);
                bombsPlaced++;
                bombs.add(cells.get(x + (y * numOfCollumns)));
            }
        }
        for (Cell cell2 : bombs) {
            for (Cell cell1 : getNeighbours(cell2)) {
                if (cell1.getValue() != Cell.BOMB)
                    cell1.setValue(cell1.getValue() + 1);
            }
        }
    }

    private void revealAllBombs() {
        for (Cell cell : cells) {
            if (cell.getValue() == Cell.BOMB) {
                cell.setRevealed(true);
            }
        }
    }

    private List<Cell> getNeighbours(Cell cell) {
        List<Cell> neighbours = new ArrayList<>();
        //getting the cell's y coordinate
        int y = cell.getId() / numOfCollumns;
        //getting the cell's x coordinate
        int x = cell.getId() % numOfCollumns;
        if (x != 0) {
            if (checkOutOfBounds(x - 1 + ((y - 1) * numOfCollumns)))
                neighbours.add(cells.get(x - 1 + ((y - 1) * numOfCollumns)));
            if (checkOutOfBounds(x - 1 + ((y) * numOfCollumns)))
                neighbours.add(cells.get(x - 1 + ((y) * numOfCollumns)));
            if (checkOutOfBounds(x - 1 + ((y + 1) * numOfCollumns)))
                neighbours.add(cells.get(x - 1 + ((y + 1) * numOfCollumns)));
        }
        if (x != numOfCollumns - 1) {
            if (checkOutOfBounds(x + 1 + ((y - 1) * numOfCollumns)))
                neighbours.add(cells.get(x + 1 + ((y - 1) * numOfCollumns)));
            if (checkOutOfBounds(x + 1 + ((y) * numOfCollumns)))
                neighbours.add(cells.get(x + 1 + ((y) * numOfCollumns)));
            if (checkOutOfBounds(x + 1 + ((y + 1) * numOfCollumns)))
                neighbours.add(cells.get(x + 1 + ((y + 1) * numOfCollumns)));
        }
        if (checkOutOfBounds(x + ((y - 1) * numOfCollumns)))
            neighbours.add(cells.get(x + ((y - 1) * numOfCollumns)));
        if (checkOutOfBounds(x + ((y + 1) * numOfCollumns)))
            neighbours.add(cells.get(x + ((y + 1) * numOfCollumns)));
        return neighbours;
    }

    private List<Cell> getEmptyNeighbourCells(Cell cell) {
        for (Cell cell2 : getNeighbours(cell)) {
            if (cell2.getValue() != Cell.BLANK && !cell2.isRevealed()) {
                cell2.setRevealed(true);
                winCondition--;
                if(cell2.isFlagged()){
                    bombsTV.setText(customFormat(Integer.parseInt(bombsTV.getText().toString()) + 1));
                }
            }
        }
        cell.setRevealed(true);
        if(cell.isFlagged()){
            bombsTV.setText(customFormat(Integer.parseInt(bombsTV.getText().toString()) + 1));
        }
        winCondition--;
        List<Cell> emptyCellList = new ArrayList<>();
        for (Cell cell1 : getNeighbours(cell)) {
            if (cell1.getValue() == Cell.BLANK && !cell1.isRevealed()) {
                emptyCellList.addAll(getEmptyNeighbourCells(cell1));
            }
        }
        return emptyCellList;
    }

    private boolean checkOutOfBounds(int pos) {
        return (pos < numOfCollumns * numOfRows) && (pos >= 0);
    }

    @Override
    public void onItemClick(int position) {
        Cell cell = cells.get(position);
        if (firstClick) {
            soundPool.play(soundDefault, 0.44f, 0.44f, 1, 0, 1f);
            placeBombs(cell);
            firstClick = false;
            startTimer(0);
        }
        if (!gameOver) {
            if (toggleFlag) {
                flagCell(position);
            } else if (!cell.isFlagged()) {
                switch (cell.getValue()) {
                    case Cell.BOMB:
                        soundPool.play(soundDeath, 0.44f, 0.44f, 1, 0, 1f);
                        cells.get(cells.indexOf(cell)).setRevealed(true);
                        cell.setValue(-2);
                        showLostDialog();
                        revealAllBombs();
                        gameOver = true;
                        cancelTimer();
                        break;
                    case Cell.BLANK:
                        soundPool.play(soundDefault, 0.44f, 0.44f, 1, 0, 1f);
                        if (!cell.isRevealed()) {
                            getEmptyNeighbourCells(cell);
                        }
                        break;
                    default:
                        soundPool.play(soundDefault, 0.44f, 0.44f, 1, 0, 1f);
                        if (!cell.isRevealed()) {
                            winCondition--;
                        }
                        cells.get(cells.indexOf(cell)).setRevealed(true);
                        break;
                }
            }
            adapter.notifyDataSetChanged();
            if (winCondition == 0) {
                showWinDialog();
                gameOver = true;
                cancelTimer();
            }
        }
    }

    @Override
    public void onLongItemClick(int position) {
        flagCell(position);
    }

    static public String customFormat(long value ) {
        DecimalFormat myFormatter = new DecimalFormat("000");
        String output = myFormatter.format(value);
        return output;
    }

    void startTimer(int currentmills) {
        cTimer = new CountDownTimer(10000000, 1000) {
            public void onTick(long millisUntilFinished) {
                millisUntilFinished -= (currentmills * 1000);
                    timeTV.setText(customFormat((10000000 - millisUntilFinished) / 1000));
            }
            public void onFinish() {

            }
        };
        cTimer.start();
    }

    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    @Override
    public void onDestroy() {
        cancelTimer();
        super.onDestroy();
    }

    private void showWinDialog(){
        winDialog = true;
        final Dialog dialog = new Dialog(getContext());
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //disabling the default title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //cancel the dialog by pressing anywhere outside of it
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.win_dialog);
        //BUTTONS
        Button dismissBtn = dialog.findViewById(R.id.dismissWinDialogButton);
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                winDialog = false;
            }
        });


        Button restartBtn = dialog.findViewById(R.id.resetWinDialogButton);
        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                resetBoard();
                gameOver = false;
                adapter.notifyDataSetChanged();
                winDialog = false;
            }
        });
        //TEXT VIEW
        TextView timeView = dialog.findViewById(R.id.winTimeTV);
        timeView.setText(timeTV.getText());

        dialog.show();
    }

    private void showLostDialog(){
        loseDialog = true;
        final Dialog dialog = new Dialog(getContext());
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //disabling the default title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //cancel the dialog by pressing anywhere outside of it
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.lose_dialog);
        //BUTTONS
        Button dismissBtn = dialog.findViewById(R.id.dismissLoseDialogButton);
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                loseDialog = false;
            }
        });


        Button restartBtn = dialog.findViewById(R.id.resetLoseDialogButton);
        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                resetBoard();
                gameOver = false;
                loseDialog = false;
                adapter.notifyDataSetChanged();
            }
        });
        //TEXT VIEW
        TextView timeView = dialog.findViewById(R.id.loseTimeTV);
        timeView.setText(timeTV.getText());

        TextView bombsView = dialog.findViewById(R.id.bombsDiscoveredTV);
        bombsView.setText(bombsTV.getText().toString());
        dialog.show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("cells", cells);
        outState.putBoolean("gameOver", gameOver);
        outState.putBoolean("firstClick", firstClick);
        outState.putBoolean("toggleFlag", toggleFlag);
        outState.putInt("winCondition", winCondition);
        outState.putInt("bombs", Integer.parseInt(bombsTV.getText().toString()));
        outState.putInt("currentTime", Integer.parseInt(timeTV.getText().toString()));
        outState.putBoolean("winDialog", winDialog);
        outState.putBoolean("loseDialog", loseDialog);
    }

}