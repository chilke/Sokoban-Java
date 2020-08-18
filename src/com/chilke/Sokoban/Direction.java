package com.chilke.Sokoban;

enum Direction {
    NONE(-1, 'N'),
    UP(0, 'U'),
    LEFT(1, 'L'),
    DOWN(2, 'D'),
    RIGHT(3, 'R');

    public final int value;
    private final char lurd;

    Direction(int value, char lurd) {
        this.value = value;
        this.lurd = lurd;
    }

    public char getLurd() {
        return lurd;
    }
}
