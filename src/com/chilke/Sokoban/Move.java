package com.chilke.Sokoban;

public class Move {
    private Direction dir;
    private Pack pushedPack;

    public Move(Direction d, Pack p) {
        dir = d;
        pushedPack = p;
    }

    public Move(Direction d) {
        this(d, null);
    }

    public Move() {
        this(Direction.NONE);
    }

    public Direction getDir() {
        return dir;
    }

    public Pack getPushedPack() {
        return pushedPack;
    }

    public boolean isPush() {
        return pushedPack != null;
    }
}
