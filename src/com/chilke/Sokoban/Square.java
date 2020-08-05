package com.chilke.Sokoban;

public class Square {
    public static final int OUTSIDE = 0;
    public static final int FLOOR = 1;
    public static final int WALL = 2;
    public static final int PACK = 4;
    public static final int GOAL = 8;
    public static final int MAN = 16;

    protected int col;
    protected int row;
    protected Level level;

    public Square(int col, int row, Level level) {
        this.col = col;
        this.row = row;
        this.level = level;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setRow(int row) {
        this.row = row;
    }
}
