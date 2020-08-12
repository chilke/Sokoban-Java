package com.chilke.Sokoban;

public class FileData {
    private int id;
    private String fileName;
    private String title;
    private long timestamp;
    private int state;

    public FileData(int id, String fileName, String title, long timestamp, int state) {
        this.id = id;
        this.fileName = fileName;
        this.title = title;
        this.timestamp = timestamp;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getTitle() {
        return title;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getState() {
        return state;
    }
}
