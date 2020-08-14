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
            FilenameFilter filter = (dir, name) -> name.endsWith(".slc");

            String[] fileNames = levelsDir.list(filter);
            for (String fileName : fileNames) {
                System.out.println(fileName);
                FileData file = SokoData.getInstance().getFile(fileName);
                LevelSet ls = new LevelSet(Paths.get(levelsDir.toString(), fileName).toString());
                if (file == null) {
                    file = SokoData.getInstance().insertFile(fileName, ls.getTitle());
                }

                for (int i = 0; i < ls.count(); i++) {
                    Level l = ls.getLevel(i);
                    String hash = l.getExactHash();


                }

                levelSets.put(ls.getTitle(), ls);
                levelSetTitles.add(ls.getTitle());
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
