package com.chilke.Sokoban;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class LevelComboRenderer extends DefaultListCellRenderer {
    private ImageIcon solvedIcon = null;
    private ImageIcon icon = null;
//    private int lastHeight = 0;

    public LevelComboRenderer() {
        solvedIcon = new ImageIcon(getClass().getResource("/images/SolvedLevel.png"));
        icon = new ImageIcon(getClass().getResource("/images/Level.png"));
    }
//
//    @Override
//    public void paint(Graphics g) {
//        if (lastHeight != getHeight()) {
//            int size = (int)((double)getHeight()*.9);
//            solvedIcon.setSize(size, size);
//            icon.setSize(size, size);
//            lastHeight = getHeight();
//        }
//        super.paint(g);
//    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Level l = (Level)value;
        this.setText(l.getName());
        if ((l.getSolved() & Config.getConfig().getUserId()) != 0) {
            this.setIcon(solvedIcon);
        } else {
            this.setIcon(solvedIcon);
        }
        return this;
    }
}
