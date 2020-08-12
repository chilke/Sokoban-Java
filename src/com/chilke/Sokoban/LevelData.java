package com.chilke.Sokoban;

public class LevelData {
    private FileData file;
    private int id;
    private String hash;
    private int number;
    private int solvedMask;

    public LevelData(int id, FileData file, int number, String hash, int solvedMask) {
        this.id = id;
        this.file = file;
        this.number = number;
        this.hash = hash;
        this.solvedMask = solvedMask;
    }
}
