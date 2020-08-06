package com.chilke.Sokoban;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SokobanApp {
    private final static int MIN_WIDTH = 640;
    private final static int MIN_HEIGHT = 480;

    private int curWidth = MIN_WIDTH;
    private int curHeight = MIN_HEIGHT;

    private int curX = 200;
    private int curY = 100;

    private int curSkinSize = 30;

    private String curSkin = "HeavyMetal";

    private String curLevelSet = "Original & Extra";

    private JFrame frame;
    private LevelPanel levelPanel;

    private String[] LevelSets = { "Levels 1", "Levels 2", "Levels 3", "Long Levels 4" };
    private String[] Levels = { "Level 1", "Level 2", "Level 3", "Level 4" };
    private String[] Skins = { "Skin 1", "Skin 2", "Skin 3", "Skin 4", "Skin 5" };

    //TODO - Move these initializers into a pre-init loop after a loading windows is displayed
    private LevelSetSelector levelSetSelector = new LevelSetSelector();
    private SkinSelector skinSelector = new SkinSelector();

    private LevelSet currentLevelSet = null;
    private Level currentLevel = null;

    private JLabel movesLabel = null;
    private JLabel pushesLabel = null;

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(new JLabel("Moves:"));
        movesLabel = new JLabel("0");
        movesLabel.setPreferredSize(new Dimension(40, 16));
        panel.add(movesLabel);
        panel.add(new JLabel("Pushes:"));
        pushesLabel = new JLabel("0");
        pushesLabel.setPreferredSize(new Dimension(40, 16));
        panel.add(pushesLabel);

        return panel;
    }

    public void updateMoves() {
        movesLabel.setText(String.valueOf(levelPanel.getLevel().getMoves()));
        pushesLabel.setText(String.valueOf(levelPanel.getLevel().getPushes()));
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));


        JComboBox levelSetsCombo = new JComboBox(levelSetSelector.getLevelSetTitles().toArray());
        JComboBox levelsCombo = new JComboBox();
        JComboBox skinsCombo = new JComboBox(skinSelector.getSkinTitles().toArray());

        skinsCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                levelPanel.setSkin(skinSelector.getSkin((String)skinsCombo.getSelectedItem()));

                levelPanel.requestFocusInWindow();
            }
        });

        levelsCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentLevel != null) {
                    //TODO - Add logic to finalize old level
                }

                currentLevel = currentLevelSet.getLevel((String)levelsCombo.getSelectedItem());

                levelPanel.setLevel(currentLevel);

                levelPanel.requestFocusInWindow();
            }
        });

        levelSetsCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentLevelSet != null) {
                    currentLevelSet.unloadLevels();
                }
                currentLevelSet = levelSetSelector.getLevelSet((String)levelSetsCombo.getSelectedItem());
                currentLevelSet.loadLevels();

                DefaultComboBoxModel model = new DefaultComboBoxModel(currentLevelSet.getLevelNames().toArray());
                levelsCombo.setModel(model);
                levelsCombo.setSelectedIndex(0);
            }
        });

        skinsCombo.setSelectedItem(curSkin);
        levelSetsCombo.setSelectedItem(curLevelSet);

        panel.add(levelSetsCombo);
        panel.add(levelsCombo);
        panel.add(skinsCombo);

        JButton plusButton = new JButton("+");
        JButton minusButton = new JButton("-");
        JLabel sizeLabel = new JLabel(String.valueOf(curSkinSize));

        plusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int s = levelPanel.increaseSize();
                sizeLabel.setText(String.valueOf(s));
                levelPanel.requestFocusInWindow();
            }
        });

        minusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int s = levelPanel.decreaseSize();
                sizeLabel.setText(String.valueOf(s));
                levelPanel.requestFocusInWindow();
            }
        });

        panel.add(minusButton);
        panel.add(sizeLabel);
        panel.add(plusButton);

        return panel;
    }

    private void initUI() {
        frame = new JFrame("Sokoban");
        frame.setSize(curWidth, curHeight);
        frame.setLocation(curX, curY);
        frame.setLayout(new BorderLayout());

        levelPanel = new LevelPanel(this);

        frame.add(createHeaderPanel(), BorderLayout.PAGE_START);
        JScrollPane scroller = new JScrollPane(levelPanel);
        scroller.setBorder(null);

        frame.add(scroller, BorderLayout.CENTER);

        frame.add(createFooterPanel(), BorderLayout.PAGE_END);
    }

    private void run() {
        initUI();
        frame.setVisible(true);
        levelPanel.requestFocusInWindow();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String args[]) {
        SokobanApp app = new SokobanApp();
        app.run();
    }
}
