package com.chilke.Sokoban;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class SkinSelector {
    public static Path getSkinsPath() {
        String home = System.getProperty("user.home");

        Path path = Paths.get(home);
        path = path.resolve("Documents");
        path = path.resolve("Sokoban 3");
        path = path.resolve("Skins");
        return path;
    }

    private final HashMap<String, Skin> skinsMap = new HashMap();
    private final ArrayList<String> skinTitles = new ArrayList();

    public SkinSelector() {
        Path p = getSkinsPath();

        File skinsDir = new File(p.toString());

        if (skinsDir.exists() && skinsDir.isDirectory()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) { return name.endsWith(".skn"); }
            };

            File[] skinsFiles = skinsDir.listFiles(filter);

            for (File skinFile : skinsFiles) {
                if (skinFile.isFile()) {
                    System.out.println(skinFile.getName());
                    Skin skin = new Skin(skinFile);

                    skinsMap.put(skin.getTitle(), skin);
                    skinTitles.add(skin.getTitle());
                }
            }
            Collections.sort(skinTitles);
        }
    }

    public Collection<String> getSkinTitles() {
        return skinTitles;
    }

    public Skin getSkin(String title) {
        return skinsMap.get(title);
    }

    public Skin getSkin(int i) {
        return skinsMap.get(skinTitles.get(i));
    }
}
