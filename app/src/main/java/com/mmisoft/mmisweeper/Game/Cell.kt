package com.mmisoft.mmisweeper.Game;

import java.io.Serializable;

public class Cell implements Serializable {
    public static final int BOMB = -1;
    public static final int BLANK = 0;
    private boolean isFlagged, isRevealed;
    private int value;
    private int id;

    public Cell(int id){
        this.id = id;
        isFlagged = false;
        isRevealed = false;
        value = Cell.BLANK;
    }

    public int getId() {
        return id;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void toggleFlagged() {
        isFlagged = !isFlagged;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public void setRevealed(boolean clicked) {
        isRevealed = clicked;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
