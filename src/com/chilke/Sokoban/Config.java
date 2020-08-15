package com.chilke.Sokoban;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Config {
    private static Config config = null;

    private int animationSpeed = 50;
    private boolean fullScreen = false;
    private int locationX = 100;
    private int locationY = 100;
    private int width = 640;
    private int height = 480;
    private int skinSize = 30;
    private String skin = "HeavyMetal";
    private String levelSet = "Original & Extra";
    private int levelIndex = 0;
    private int userId = 1;
    private String userName = "Craig";

    private transient Timer saveTimer = new Timer(100, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            save(true);
        }
    });

    public static Config getConfig() {
        if (config == null) {
            loadConfigFile();
        }
        return config;
    }

    private static void loadConfigFile() {
        File f = getConfigPath().toFile();

        if (f.exists()) {
            try {
                Reader r = new FileReader(f);
                config = new Gson().fromJson(r, Config.class);
            } catch (Exception ex) {
                System.out.println("Thought config file existed, but it didn't");
            }
        }

        if (config == null) {
            config = new Config();
        }
    }

    private static Path getConfigPath() {
        String home = System.getProperty("user.home");

        Path path = Paths.get(home);
        path = path.resolve(".sokoban");
        File dir = path.toFile();
        if (!dir.exists()) {
            dir.mkdir();
        }
        path = path.resolve("config.json");

        return path;
    }

    private void save(boolean now) {
        saveTimer.stop();
        if (now) {
            Gson g = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            try {
                FileWriter writer = new FileWriter(getConfigPath().toString());
                g.toJson(this, writer);
                writer.close();
            } catch (Exception ex) {
                System.err.println("Couldn't write file");
                ex.printStackTrace();
            }
        } else {
            saveTimer.start();
        }
    }

    public int getAnimationSpeed() {
        return animationSpeed;
    }

    public void setAnimationSpeed(int animationSpeed) {
        this.animationSpeed = animationSpeed;
        save(false);
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
        save(false);
    }

    public int getLocationX() {
        return locationX;
    }

    public void setLocationX(int locationX) {
        this.locationX = locationX;
        save(false);
    }

    public int getLocationY() {
        return locationY;
    }

    public void setLocationY(int locationY) {
        this.locationY = locationY;
        save(false);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        save(false);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        save(false);
    }

    public int getSkinSize() {
        return skinSize;
    }

    public void setSkinSize(int skinSize) {
        this.skinSize = skinSize;
        save(false);
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
        save(false);
    }

    public String getLevelSet() {
        return levelSet;
    }

    public void setLevelSet(String levelSet) {
        this.levelSet = levelSet;
        save(false);
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    public void setLevelIndex(int levelIndex) {
        this.levelIndex = levelIndex;
        save(false);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
        save(false);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        save(false);
    }
}
