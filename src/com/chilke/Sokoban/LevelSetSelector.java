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
                FileData file = SokoData.getInstance().getFile(fileName);
                LevelSet ls = new LevelSet(Paths.get(levelsDir.toString(), fileName).toString());
                if (file == null) {
                    file = SokoData.getInstance().insertFile(fileName, ls.getTitle());
                }

                int allSolved = 0xFF;

                ArrayList<LevelData> levelData = SokoData.getInstance().getLevels(file.getId());

                for (int i = 0; i < ls.count(); i++) {
                    Level l = ls.getLevel(i);
                    String hash = l.getExactHash();

                    LevelData ld = null;

                    if (i < levelData.size()) {
                        ld = levelData.get(i);
                    }

                    if (ld == null || !hash.equals(ld.getHash())) {
                        if (ld != null) {
                            System.out.format("Hash mismatch, deleting level %s, %d\n", fileName, i);
                            SokoData.getInstance().deleteLevel(file.getId(), i);
                        }
                        System.out.format("Creating level %s, %d\n", fileName, i);
                        ld = SokoData.getInstance().createLevel(file.getId(), i, hash);
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
                        allSolved &= ld.getSolvedMask();
                    }
                }

                ls.setAllSolved(allSolved);

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

    public LevelSet getLevelSet(int i) {
        if (i < levelSets.size()) {
            return levelSets.get(i);
        }
        return null;
    }
}
