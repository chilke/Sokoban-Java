package com.chilke.Sokoban;

public class LevelData {
    private int file;
    private int id;
    private String hash;
    private int number;
    private int solvedMask;
    private int scoresSolvedMask;

    public LevelData(int id, int file, int number, String hash, int solvedMask, int scoresSolvedMask) {
        this.id = id;
        this.file = file;
        this.number = number;
        this.hash = hash;
        this.solvedMask = solvedMask;
        this.scoresSolvedMask = scoresSolvedMask;
    }

    public void setSolvedMask(int solvedMask) {
        this.solvedMask = solvedMask;
    }

    public int getFile() {
        return file;
    }

    public int getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    public int getNumber() {
        return number;
    }

    public int getSolvedMask() {
        return solvedMask;
    }

    public int getScoresSolvedMask() {
        return scoresSolvedMask;
    }
}
