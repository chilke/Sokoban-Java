package com.chilke.Sokoban;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

public class LevelSet {

    private Path filePath;
    private String title;
    private String description;
    private int maxWidth;
    private int maxHeight;
    private final ArrayList<Level> levels = new ArrayList<>();

    private FileData fileData;

    private int allSolved = 0;

    public LevelSet(Path path) {
        filePath = path;

        loadLevels();
    }

    public void loadLevels() {
        File xmlFile = new File(filePath.toString());
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbFactory.newDocumentBuilder();
            Document doc = db.parse(xmlFile);
            doc.getDocumentElement().normalize();
//            printNode(doc, "");

            Element sokoLevels = doc.getDocumentElement();
            title = sokoLevels.getElementsByTagName("Title").item(0).getTextContent().trim();
            description = sokoLevels.getElementsByTagName("Description").item(0).getTextContent().trim();
            description = description.replace('\n', ' ').replace("  ", " ");
            Element collection = (Element)sokoLevels.getElementsByTagName("LevelCollection").item(0);
            maxWidth = Integer.parseInt(collection.getAttribute("MaxWidth"));
            maxHeight = Integer.parseInt(collection.getAttribute("MaxHeight"));
//            System.out.println("Title: "+title);
//            System.out.println("Description: "+description);
//            System.out.format("Max Size (%d, %d)\n", maxWidth, maxHeight);

            NodeList levelNodes = collection.getElementsByTagName("Level");

            for (int i = 0; i < levelNodes.getLength(); i++) {
                Element levelElement = (Element) levelNodes.item(i);
                String name = levelElement.getAttribute("Id");
                int width = Integer.parseInt(levelElement.getAttribute("Width"));
                int height = Integer.parseInt(levelElement.getAttribute("Height"));
                NodeList lineNodes = levelElement.getElementsByTagName("L");
                String[] lines = new String[lineNodes.getLength()];
                for (int j = 0; j < lineNodes.getLength(); j++) {
                    Element lineElement = (Element) lineNodes.item(j);
                    lines[j] = lineElement.getTextContent();
                }
                Level level = new Level(name, width, height, lines);
                levels.add(level);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }

        fileData = SokoData.getInstance().getFile(filePath.getFileName().toString());

        if (fileData == null) {
            fileData = SokoData.getInstance().insertFile(filePath.getFileName().toString(), title);
        }

        ArrayList<LevelData> levelData = SokoData.getInstance().getLevels(fileData.getId());
        allSolved = 0xFF;

        for (int i = 0; i < levels.size(); i++) {
            Level l = levels.get(i);
            String hash = l.getExactHash();

            LevelData ld = null;

            if (i < levelData.size()) {
                ld = levelData.get(i);
            }

            if (ld == null || !hash.equals(ld.getHash())) {
                if (ld != null) {
                    System.out.format("Hash mismatch, deleting level %s, %d\n", filePath.getFileName(), i);
                    SokoData.getInstance().deleteLevel(fileData.getId(), i);
                }
                System.out.format("Creating level %s, %d\n", filePath.getFileName().toString(), i);
                ld = SokoData.getInstance().createLevel(fileData.getId(), i, hash);
                if (i < levelData.size()) {
                    levelData.set(i, ld);
                } else {
                    while (i > levelData.size()) {
                        levelData.add(null);
                    }
                    levelData.add(ld);
                }
            }

            if (ld != null && ld.getScoresSolvedMask() != ld.getSolvedMask()) {
                SokoData.getInstance().updateLevelSolved(ld.getId(), ld.getScoresSolvedMask());
                ld.setSolvedMask(ld.getScoresSolvedMask());
            }

            if (ld != null) {
                l.setSolved(ld.getSolvedMask());
                allSolved &= ld.getSolvedMask();
            } else {
                allSolved = 0;
            }
        }
    }

    public boolean updateAllSolved() {
        int curAllSolved = allSolved;
        if (!levels.isEmpty()) {
            allSolved = 0xFF;

            for (Level l : levels) {
                allSolved &= l.getSolved();
            }
        } else {
            allSolved = SokoData.getInstance().getFileAllSolved(fileData.getId());
        }

        return (allSolved != curAllSolved);
    }

    public int count() {
        return levels.size();
    }

    public void unloadLevels() {
        levels.clear();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public Level getLevel(int i) {
        if (i < levels.size()) {
            return levels.get(i);
        }
        return null;
    }

    public Collection<Level> getLevels() {
        return levels;
    }

    public int getAllSolved() {
        return allSolved;
    }

    public void setAllSolved(int allSolved) {
        this.allSolved = allSolved;
    }

    public int getFileId() {
        if (fileData != null) {
            return fileData.getId();
        }

        return -1;
    }

    public int getLevelId(Level l) {
        for (int i = 0; i < levels.size(); i++) {
            if (l == levels.get(i)) {
                return i;
            }
        }

        return -1;
    }
}
