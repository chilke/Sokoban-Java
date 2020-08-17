package com.chilke.Sokoban;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class LevelSetComboRenderer extends DefaultListCellRenderer {
    private ScalingIcon solvedIcon = null;
    private ScalingIcon icon = null;

    private int lastSize = 0;

    public LevelSetComboRenderer() {
        solvedIcon = new ScalingIcon(getClass().getResource("/images/SolvedLevelSet.png"));
        icon = new ScalingIcon(getClass().getResource("/images/LevelSet.png"));

        Font f = getFont();
        resizeForFont(f);
    }

    private void resizeForFont(Font f) {
        if (lastSize != f.getSize() && icon != null) {
            lastSize = f.getSize();
            BufferedImage img = icon.getOriginalImage();
            Graphics2D g = img.createGraphics();
            FontMetrics metrics = g.getFontMetrics(f);
            solvedIcon.setSize(metrics.getHeight(), metrics.getHeight());
            icon.setSize(metrics.getHeight(), metrics.getHeight());

            SwingUtilities.updateComponentTreeUI(this);
        }
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        resizeForFont(font);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        LevelSet ls = (LevelSet)value;
        this.setText(ls.getTitle());
        if ((ls.getAllSolved() & Config.getConfig().getUserId()) != 0) {
            this.setIcon(solvedIcon);
        } else {
            this.setIcon(icon);
        }
        return this;
    }
}
