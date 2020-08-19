package com.chilke.Sokoban;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class LevelSetSelector {
    public static Path getLevelsPath() {
        String home = System.getProperty("user.home");

        Path path = Paths.get(home);
        path = path.resolve("Documents");
        path = path.resolve("Sokoban 3");
        path = path.resolve("Levels");
        return path;
    }

    private final ArrayList<LevelSet> levelSets = new ArrayList<>();

    public LevelSetSelector() {
        Path p = getLevelsPath();

        File levelsDir = new File(p.toString());
        if (levelsDir.exists() && levelsDir.isDirectory()) {
            FilenameFilter filter = (dir, name) -> name.endsWith(".slc");

            String[] fileNames = levelsDir.list(filter);
            for (String fileName : fileNames) {
                LevelSet ls = new LevelSet(Paths.get(levelsDir.toString(), fileName));

                ls.unloadLevels();

                levelSets.add(ls);
            }
        }
    }

    public Collection<LevelSet> getLevelSets() {
        return levelSets;
    }

    public LevelSet getLevelSet(String title) {
        for (LevelSet ls : levelSets) {
            if (ls.getTitle().equals(title)) {
                return ls;
            }
        }
        return null;
    }

    public LevelSet getLevelSetByFileId(int id) {
        for (LevelSet ls : levelSets) {
            if (id == ls.getFileId()) {
                return ls;
            }
        }
        return null;
    }

    public LevelSet getLevelSet(int i) {
        if (i < levelSets.size()) {
            return levelSets.get(i);
        }
        return null;
    }
}
