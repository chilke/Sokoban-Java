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

    private final HashMap<String, LevelSet> levelSets = new HashMap();
    private final ArrayList<String> levelSetTitles = new ArrayList();

    public LevelSetSelector() {
        Path p = getLevelsPath();

        File levelsDir = new File(p.toString());
        if (levelsDir.exists() && levelsDir.isDirectory()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".slc");
                }
            };
            File[] files = levelsDir.listFiles(filter);
            for (File levelsFile : files) {
                if (levelsFile.isFile()) {
                    System.out.println(levelsFile.getName());
                    LevelSet ls = new LevelSet(levelsFile);
                    levelSets.put(ls.getTitle(), ls);
                    levelSetTitles.add(ls.getTitle());
                }
            }
        }
    }

    public Collection<String> getLevelSetTitles() {
        return levelSetTitles;
    }

    public LevelSet getLevelSet(String title) {
        return levelSets.get(title);
    }
}
