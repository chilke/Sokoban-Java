package com.chilke.Sokoban;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

public class Skin {
    private enum Section {
        NONE, DESCRIPTION, SKIN, IMAGE, BACKGROUND, END
    }

    private static final int MAX_SIZE = 100;
    private static final int MIN_SIZE = 10;

    private static final int MAX_TRANS_DIFF = 10;

    private static final HashMap<String, Section> sectionNameMap = new HashMap();
    private static final HashMap<String, SkinImage> imageNameMap = new HashMap();

    private final int[] skinImageIds = new int[SkinImage.MAX_VALUE+1];

    private String title = "";
    private String copyright = "";
    private String email = "";
    private String url = "";

    private String transparent = "";

    private int orgWallTopX;
    private int orgWallTopY;

    private int wallTopX;
    private int wallTopY;

    private int originalSize;

    private int currentSkinSize;

    private BufferedImage bgImage = null;

    private BufferedImage[] originalSkinImages;
    private BufferedImage[] scaledSkinImages;

    public Skin(File skinFile) {
        BufferedImage skinImage = null;
        Arrays.fill(skinImageIds, -1);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(skinFile));
            Section section = Section.NONE;
            StringBuilder imageText = new StringBuilder();
            String line = reader.readLine();

            while (line != null) {
                line = line.trim();
                if (!(line.startsWith(";") || line.length() == 0)) {
                    if (line.startsWith("[")) {
                        if (section == Section.IMAGE || section == Section.BACKGROUND) {
                            byte[] data = Base64.getDecoder().decode(imageText.toString());
                            InputStream stream = new ByteArrayInputStream(data);
                            BufferedImage img = ImageIO.read(stream);
                            if (section == Section.IMAGE) {
                                skinImage = img;
                            } else {
                                bgImage = img;
                            }
                            imageText.setLength(0);
                        }
                        String sectionName = line.substring(1, line.length() - 1);
                        section = sectionNameMap.get(sectionName);
                        if (section == null) {
                            System.out.println("Couldn't find section: " + sectionName);
                        }
                    } else if (section == Section.IMAGE || section == Section.BACKGROUND) {
                        imageText.append(line);
                    } else {
                        String[] toks = line.split("=", 2);
                        if (toks.length == 2) {
                            String parmName = toks[0].trim();
                            String parmValue = toks[1].trim();
                            SkinImage img = imageNameMap.get(parmName);
                            if (img != null) {
                                skinImageIds[img.value] = Integer.parseInt(parmValue);
                            } else {
                                handleParm(parmName, parmValue);
                            }
                        } else {
                            System.out.println("Invalid K=V pair: " + line);
                        }
                    }
                }
                line = reader.readLine();
            }
        } catch(Exception e) {
            System.err.print(e.toString());
            e.printStackTrace();
        }

        splitSprites(skinImage);
        fillSkinImageIds();
        createPulseImages();

        wallTopX = orgWallTopX;
        wallTopY = orgWallTopY;
    }

    private void fillSkinImageIds() {
        boolean updated = true;

        if (skinImageIds[SkinImage.OUTSIDE.value] == -1) {
            skinImageIds[SkinImage.OUTSIDE.value] = skinImageIds[SkinImage.FLOOR.value];
        }
        if (skinImageIds[SkinImage.PACK_GOAL.value] == -1) {
            skinImageIds[SkinImage.PACK_GOAL.value] = skinImageIds[SkinImage.PACK.value];
        }
        for (SkinImage wall : SkinImage.wallImages) {
            if (skinImageIds[wall.value] == -1) {
                skinImageIds[wall.value] = skinImageIds[SkinImage.WALL.value];
            }
        }
        for (SkinImage man : SkinImage.manImages) {
            int value = man.value;
            while (skinImageIds[value] == -1 && value != SkinImage.MAN_BIT) {
                if ((value & SkinImage.GOAL_BIT) != 0) {
                    value &= ~SkinImage.GOAL_BIT;
                } else if ((value & SkinImage.PUSH_BIT) != 0) {
                    value &= ~SkinImage.PUSH_BIT;
                    if ((man.value & SkinImage.GOAL_BIT) != 0) {
                        value |= SkinImage.GOAL_BIT;
                    }
                } else if ((value & SkinImage.DIR_BIT) != 0) {
                    value = man.value & ~SkinImage.DIR_BIT_AND_MASK;
                }
            }
            if (title.equals("HeavyMetal")) {
                System.out.println("Setting " + man.toString() + " to " + SkinImage.skinImageForValue(value).toString());
            }
            skinImageIds[man.value] = skinImageIds[value];
        }
    }

    private void handleParm(String parmName, String parmValue) {
        switch (parmName) {
            case "Title" -> title = parmValue;
            case "Copyright" -> copyright = parmValue;
            case "Email" -> email = parmValue;
            case "Url" -> url = parmValue;
            case "Wall_Top_X" -> orgWallTopX = Integer.parseInt(parmValue);
            case "Wall_Top_Y" -> orgWallTopY = Integer.parseInt(parmValue);
            case "Transparent" -> transparent = parmValue;
            default -> System.out.println("Unknown parameter: " + parmName + " = " + parmValue);
        }
    }

    private void splitSprites(BufferedImage skinImage) {
        originalSize = skinImage.getHeight();
        currentSkinSize = originalSize;
        int width = skinImage.getWidth();
        int imageCount = width/originalSize;
        int addOns = 0;
        boolean hadManMarker = true;
        if (skinImageIds[SkinImage.MAN_MARKER.value] == -1) {
            addOns++;
            hadManMarker = false;
        }
        if (skinImageIds[SkinImage.PACK_MARKER.value] == -1) {
            addOns++;
        }
        SkinImage si = SkinImage.PACK_PULSE_0;
        for (int i = 0; i < 16; i++) {
            skinImageIds[si.value] = imageCount+addOns;
            si = SkinImage.skinImageForValue(si.value+1);
            addOns++;
        }

        originalSkinImages = new BufferedImage[imageCount+addOns];
        scaledSkinImages = new BufferedImage[imageCount+addOns];

        for (int i = 0; i < imageCount; i++) {
            BufferedImage img = new BufferedImage(originalSize, originalSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.drawImage(skinImage, -i*originalSize, 0, null);
            g.dispose();

            if (i < transparent.length() && transparent.charAt(i) == '1') {
                int transColor = img.getRGB(0, originalSize-1);
                int transR = (transColor & 0x00ff0000)>>16;
                int transG = (transColor & 0x0000ff00)>>8;
                int transB = transColor & 0x000000ff;
                int replaceColor = transColor & 0xffffff;
                for (int y = 0; y < originalSize; y++) {
                    for (int x = 0; x < originalSize; x++) {
                        int color = img.getRGB(x, y);
                        int cR = (color & 0x00ff0000)>>16;
                        int cG = (color & 0x0000ff00)>>8;
                        int cB = color & 0x000000ff;
                        if (cR > transR-MAX_TRANS_DIFF && cR < transR+MAX_TRANS_DIFF &&
                            cG > transG-MAX_TRANS_DIFF && cG < transG+MAX_TRANS_DIFF &&
                            cB > transB-MAX_TRANS_DIFF && cB < transB+MAX_TRANS_DIFF) {
                            img.setRGB(x, y, replaceColor);
                        }
                    }
                }
            }

            originalSkinImages[i] = img;
            scaledSkinImages[i] = img;
        }
        if (skinImageIds[SkinImage.MAN_MARKER.value] == -1) {
            skinImageIds[SkinImage.MAN_MARKER.value] = imageCount;
            BufferedImage img = new BufferedImage(originalSize, originalSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            int id = skinImageIds[SkinImage.MAN.value];
            if (id == -1) {
                id = skinImageIds[SkinImage.MAN_UP.value];
                if (id == -1) {
                    System.out.println("Uh-oh, missing MAN_MARKER image");
                }
            }
            if (id != -1) {
                g.drawImage(originalSkinImages[id], originalSize/4, originalSize/4, originalSize/2, originalSize/2, null);
            }

            g.dispose();

            originalSkinImages[imageCount] = img;
            scaledSkinImages[imageCount] = img;
        }

        if (skinImageIds[SkinImage.PACK_MARKER.value] == -1) {
            int value;
            if (hadManMarker) {
                value = imageCount;
            } else {
                value = imageCount+1;
            }
            skinImageIds[SkinImage.PACK_MARKER.value] = value;

            BufferedImage img = new BufferedImage(originalSize, originalSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            int id = skinImageIds[SkinImage.PACK.value];
            if (id == -1) {
                System.out.println("Uh-oh, missing MAN_MARKER image");
            } else {
                g.drawImage(originalSkinImages[id], originalSize/4, originalSize/4, originalSize/2, originalSize/2, null);
            }

            g.dispose();

            originalSkinImages[value] = img;
            scaledSkinImages[value] = img;
        }
    }

    private void createPulseImages() {
        SkinImage packPulse = SkinImage.PACK_PULSE_0;
        SkinImage packGoalPulse = SkinImage.PACK_GOAL_PULSE_0;
        double currentScale = 1.0;
        double scaleAmount = .5/8;
        BufferedImage packImage = originalSkinImages[skinImageIds[SkinImage.PACK.value]];
        BufferedImage packGoalImage = originalSkinImages[skinImageIds[SkinImage.PACK_GOAL.value]];
        int index = skinImageIds[SkinImage.PACK_PULSE_0.value];
        for (int i = 0; i < 8; i++) {
            currentScale -= scaleAmount;
            BufferedImage pulseImage = new BufferedImage(originalSize, originalSize, BufferedImage.TYPE_INT_ARGB);
            BufferedImage pulseGoalImage = new BufferedImage(originalSize, originalSize, BufferedImage.TYPE_INT_ARGB);
            int size = (int)((double)originalSize * currentScale);
            int offset = (originalSize-size)/2;

            Graphics2D g = pulseImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(packImage, offset, offset, size, size, null);
            g.dispose();
            g = pulseGoalImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(packGoalImage, offset, offset, size, size, null);
            g.dispose();
            originalSkinImages[index+i] = pulseImage;
            originalSkinImages[index+i+8] = pulseGoalImage;
            scaledSkinImages[index+i] = pulseImage;
            scaledSkinImages[index+i+8] = pulseGoalImage;
        }
    }

    public int getSize() {
        return currentSkinSize;
    }

    public int setSkinSize(int newSize) {
        if (newSize != currentSkinSize && newSize >= MIN_SIZE && newSize <= MAX_SIZE) {
            wallTopX = orgWallTopX * newSize / originalSize;
            wallTopY = orgWallTopY * newSize / originalSize;

            for (int i = 0; i < originalSkinImages.length; i++) {
                BufferedImage orgImage = originalSkinImages[i];
                BufferedImage scaledImage = new BufferedImage(newSize, newSize, BufferedImage.TYPE_INT_ARGB);

                Graphics2D g = scaledImage.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.drawImage(orgImage, 0, 0, newSize, newSize, null);
                g.dispose();

                scaledSkinImages[i] = scaledImage;
            }

            currentSkinSize = newSize;
        }

        return currentSkinSize;
    }

    public int increaseSkinSize() {
        return setSkinSize(currentSkinSize+1);
    }

    public int decreaseSkinSize() {
        return setSkinSize(currentSkinSize-1);
    }

    public boolean hasBackground() {
        return bgImage != null;
    }

    public BufferedImage getSkinImage(SkinImage img) {
        if (skinImageIds[img.value] != -1) {
            return scaledSkinImages[skinImageIds[img.value]];
        } else if (img == SkinImage.BACKGROUND) {
            return bgImage;
        }

        return null;
    }

    public String getTitle() {
        return title;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getEmail() {
        return email;
    }

    public String getUrl() {
        return url;
    }

    public BufferedImage[] getScaledSkinImages() {
        return scaledSkinImages;
    }

    public BufferedImage[] getOriginalSkinImages() {
        return originalSkinImages;
    }

    public int getOriginalSize() {
        return originalSize;
    }

    public int getWallTopX() {
        return wallTopX;
    }

    public int getWallTopY() {
        return wallTopY;
    }

    static {
        sectionNameMap.put("DESCRIPTION", Section.DESCRIPTION);
        sectionNameMap.put("SKIN", Section.SKIN);
        sectionNameMap.put("IMAGE", Section.IMAGE);
        sectionNameMap.put("BACKGROUND", Section.BACKGROUND);
        sectionNameMap.put("END", Section.END);

        imageNameMap.put("Floor", SkinImage.FLOOR);
        imageNameMap.put("Goal", SkinImage.GOAL);
        imageNameMap.put("Pack", SkinImage.PACK);
        imageNameMap.put("Pack_Goal", SkinImage.PACK_GOAL);
        imageNameMap.put("Man", SkinImage.MAN);
        imageNameMap.put("Man_Up", SkinImage.MAN_UP);
        imageNameMap.put("Man_Right", SkinImage.MAN_RIGHT);
        imageNameMap.put("Man_Down", SkinImage.MAN_DOWN);
        imageNameMap.put("Man_Left", SkinImage.MAN_LEFT);
        imageNameMap.put("Man_Goal", SkinImage.MAN_GOAL);
        imageNameMap.put("Man_Up_Goal", SkinImage.MAN_GOAL_UP);
        imageNameMap.put("Man_Right_Goal", SkinImage.MAN_GOAL_RIGHT);
        imageNameMap.put("Man_Down_Goal", SkinImage.MAN_GOAL_DOWN);
        imageNameMap.put("Man_Left_Goal", SkinImage.MAN_GOAL_LEFT);
        imageNameMap.put("Push_Up", SkinImage.MAN_PUSH_UP);
        imageNameMap.put("Push_Right", SkinImage.MAN_PUSH_RIGHT);
        imageNameMap.put("Push_Down", SkinImage.MAN_PUSH_DOWN);
        imageNameMap.put("Push_Left", SkinImage.MAN_PUSH_LEFT);
        imageNameMap.put("Push_Up_Goal", SkinImage.MAN_PUSH_GOAL_UP);
        imageNameMap.put("Push_Right_Goal", SkinImage.MAN_PUSH_GOAL_RIGHT);
        imageNameMap.put("Push_Down_Goal", SkinImage.MAN_PUSH_GOAL_DOWN);
        imageNameMap.put("Push_Left_Goal", SkinImage.MAN_PUSH_GOAL_LEFT);
        imageNameMap.put("Outside", SkinImage.OUTSIDE);
        imageNameMap.put("Wall", SkinImage.WALL);
        imageNameMap.put("Wall_0", SkinImage.WALL_0);
        imageNameMap.put("Wall_1", SkinImage.WALL_1);
        imageNameMap.put("Wall_2", SkinImage.WALL_2);
        imageNameMap.put("Wall_3", SkinImage.WALL_3);
        imageNameMap.put("Wall_4", SkinImage.WALL_4);
        imageNameMap.put("Wall_5", SkinImage.WALL_5);
        imageNameMap.put("Wall_6", SkinImage.WALL_6);
        imageNameMap.put("Wall_7", SkinImage.WALL_7);
        imageNameMap.put("Wall_8", SkinImage.WALL_8);
        imageNameMap.put("Wall_9", SkinImage.WALL_9);
        imageNameMap.put("Wall_A", SkinImage.WALL_A);
        imageNameMap.put("Wall_B", SkinImage.WALL_B);
        imageNameMap.put("Wall_C", SkinImage.WALL_C);
        imageNameMap.put("Wall_D", SkinImage.WALL_D);
        imageNameMap.put("Wall_E", SkinImage.WALL_E);
        imageNameMap.put("Wall_F", SkinImage.WALL_F);
        imageNameMap.put("Wall_Top", SkinImage.WALL_TOP);
        imageNameMap.put("Man_Marker", SkinImage.MAN_MARKER);
        imageNameMap.put("Pack_Marker", SkinImage.PACK_MARKER);
    }
}
