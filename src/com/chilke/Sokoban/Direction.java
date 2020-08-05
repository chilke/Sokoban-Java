package com.chilke.Sokoban;

enum Direction {
    NONE(-1),
    UP(0),
    LEFT(1),
    DOWN(2),
    RIGHT(3);

    public final int value;

    Direction(int value) {
        this.value = value;
    }
}
