package com.chilke.Sokoban;

import javax.swing.*;
import java.awt.*;

public class LevelSetComboRenderer extends DefaultListCellRenderer {
    private final ImageIcon solvedIcon;
    private final ImageIcon icon;

    public LevelSetComboRenderer() {
        solvedIcon = new ImageIcon(getClass().getResource("/images/SolvedLevelSet.png"));
        icon = new ImageIcon(getClass().getResource("/images/LevelSet.png"));
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
