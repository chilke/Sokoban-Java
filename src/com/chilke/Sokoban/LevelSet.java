package com.chilke.Sokoban;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class LevelSet {

    private String filePath;
    private String title;
    private String description;
    private int maxWidth;
    private int maxHeight;
    private final HashMap<String, Level> levels = new HashMap();
    private final ArrayList<String> levelNames = new ArrayList();

    public LevelSet(File xmlFile) {
        filePath = xmlFile.getPath();

        loadFile(xmlFile, false);
    }

    private void loadFile(File xmlFile, boolean loadLevels) {
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

            if (loadLevels) {
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
                    levels.put(name, level);
                    levelNames.add(level.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    public void loadLevels() {
        File xmlFile = new File(filePath);
        loadFile(xmlFile, true);
    }

    public void unloadLevels() {
        levels.clear();
        levelNames.clear();
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

    public Collection<String> getLevelNames() {
        return levelNames;
    }

    public Level getLevel(String name) {
        return levels.get(name);
    }
}
