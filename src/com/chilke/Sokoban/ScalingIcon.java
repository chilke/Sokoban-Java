package com.chilke.Sokoban;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class ScalingIcon extends ImageIcon {
    private BufferedImage originalImage;
    private int currentSize = 5;
    public ScalingIcon(URL location) {
        try {
            originalImage = ImageIO.read(location);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setSize(1, 1);
    }

    public void setSize(int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaledImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        this.setImage(scaledImage);
    }
}
