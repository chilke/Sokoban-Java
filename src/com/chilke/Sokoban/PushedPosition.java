package com.chilke.Sokoban;

public class PushedPosition extends Position {
    private Direction dir;

    public PushedPosition(int col, int row, Direction dir) {
        super(col, row);

        this.dir = dir;
    }

    public Direction getDir() {
        return dir;
    }
}
