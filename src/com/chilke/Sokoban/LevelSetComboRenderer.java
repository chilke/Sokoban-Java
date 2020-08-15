package com.chilke.Sokoban;

import javax.swing.*;
import java.awt.*;

public class LevelSetComboRenderer extends DefaultListCellRenderer {
    private ScalingIcon solvedIcon = null;
    private ScalingIcon icon = null;
    private int lastHeight = 0;

    public LevelSetComboRenderer() {
        solvedIcon = new ScalingIcon(getClass().getResource("/images/SolvedLevelSet.png"));
        icon = new ScalingIcon(getClass().getResource("/images/LevelSet.png"));
    }

    @Override
    public void paint(Graphics g) {
        if (lastHeight != getHeight()) {
            int size = (int)((double)getHeight()*.9);
            solvedIcon.setSize(size, size);
            icon.setSize(size, size);
            lastHeight = getHeight();
            revalidate();
        }
        super.paint(g);
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
