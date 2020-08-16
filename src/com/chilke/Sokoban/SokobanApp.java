package com.chilke.Sokoban;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class SokobanApp {
    private JFrame frame;
    private LevelPanel levelPanel;

    //TODO - Move these initializers into a pre-init loop after a loading windows is displayed
    private final LevelSetSelector levelSetSelector = new LevelSetSelector();
    private final SkinSelector skinSelector = new SkinSelector();

    private LevelSet currentLevelSet = null;
    private Level currentLevel = null;

    private JLabel movesLabel = null;
    private JLabel pushesLabel = null;

    JComboBox<LevelSet> levelSetsCombo = null;
    JComboBox<Level> levelsCombo = null;
    JComboBox skinsCombo = null;

    public void showSettings() {
        SettingsDialog s = new SettingsDialog(frame);
        s.setVisible(true);
        System.out.println("Closed");
    }

    private void makeFullscreen(boolean fullscreen) {
        if (fullscreen) {
            frame.dispose();
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true);
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(frame);
            frame.setVisible(true);
        } else {
            frame.dispose();
            Config config = Config.getConfig();
            frame.setSize(config.getWidth(), config.getHeight());
            frame.setLocation(config.getLocationX(), config.getLocationY());
            frame.setUndecorated(false);
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(null);
            frame.setVisible(true);
        }
    }

    private JMenuBar createMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        JMenuItem item = new JMenuItem("Reset");
        item.setMnemonic(KeyEvent.VK_R);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
        item.addActionListener(e -> {
            currentLevel.reset();
            updateMoves();
            levelPanel.repaint();
        });
        fileMenu.add(item);

        item = new JMenuItem("Settings");
        item.setMnemonic(KeyEvent.VK_S);
        item.addActionListener(e -> showSettings());
        fileMenu.add(item);

        JCheckBoxMenuItem fullScreen = new JCheckBoxMenuItem("Fullscreen");
        fullScreen.setMnemonic(KeyEvent.VK_F);
        fullScreen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.ALT_DOWN_MASK));
        fullScreen.addActionListener(e -> {
            System.out.println("In Action Listener");
            Config.getConfig().setFullScreen(fullScreen.isSelected());
            makeFullscreen(fullScreen.isSelected());
        });
        fullScreen.setSelected(Config.getConfig().isFullScreen());
        fileMenu.add(fullScreen);

        item = new JMenuItem("Exit");
        item.setMnemonic(KeyEvent.VK_E);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(item);

        return menuBar;
    }

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
        if (movesLabel != null) {
            movesLabel.setText(String.valueOf(levelPanel.getLevel().getMoves()));
            pushesLabel.setText(String.valueOf(levelPanel.getLevel().getPushes()));
        }
    }

    public void enableInputs(boolean enabled) {
        levelSetsCombo.setEnabled(enabled);
        levelsCombo.setEnabled(enabled);
        skinsCombo.setEnabled(enabled);
    }

    public void nextLevel() {
        int index = levelsCombo.getSelectedIndex() + 1;
        if (index < levelsCombo.getItemCount()) {
            levelsCombo.setSelectedIndex(index);
        } else {
            index = levelSetsCombo.getSelectedIndex() + 1;
            if (index < levelSetsCombo.getItemCount()) {
                levelSetsCombo.setSelectedIndex(index);
            } else {
                //TODO - Logic to look for first level set with unsolved level
                levelSetsCombo.setSelectedIndex(0);
            }
        }
    }

    public void reloadCombos() {
        levelSetsCombo.setModel(levelSetsCombo.getModel());
        levelsCombo.setModel(levelsCombo.getModel());
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        LevelSet[] levelSets = new LevelSet[levelSetSelector.getLevelSets().size()];
        for (int i = 0; i < levelSets.length; i++) {
            levelSets[i] = levelSetSelector.getLevelSet(i);
        }
        levelSetsCombo = new JComboBox<>(levelSets);
        levelSetsCombo.setRenderer(new LevelSetComboRenderer());
        levelsCombo = new JComboBox<>();
        levelsCombo.setRenderer(new LevelComboRenderer());
        skinsCombo = new JComboBox(skinSelector.getSkinTitles().toArray());

        skinsCombo.addActionListener(e -> {
            levelPanel.setSkin(skinSelector.getSkin((String)skinsCombo.getSelectedItem()));

            levelPanel.requestFocusInWindow();
        });

        levelsCombo.addActionListener(e -> {
            if (currentLevel != null) {
                //TODO - Add logic to finalize old level
            }
            int index = levelsCombo.getSelectedIndex();
            Config.getConfig().setLevelIndex(index);
            currentLevel = currentLevelSet.getLevel(index);
            levelPanel.setLevel(currentLevel);
            updateMoves();
            levelPanel.requestFocusInWindow();
        });

        levelSetsCombo.addActionListener(e -> {
            if (currentLevelSet != null) {
                currentLevelSet.unloadLevels();
            }
            currentLevelSet = (LevelSet)levelSetsCombo.getSelectedItem();
            currentLevelSet.loadLevels();

            Config.getConfig().setLevelSet(currentLevelSet.getTitle());

            DefaultComboBoxModel<Level> model = new DefaultComboBoxModel<>(currentLevelSet.getLevels().toArray(new Level[currentLevelSet.getLevels().size()]));
            levelsCombo.setModel(model);

            if (currentLevel == null) {
                levelsCombo.setSelectedIndex(Config.getConfig().getLevelIndex());
            } else {
                levelsCombo.setSelectedIndex(0);
            }
        });

        skinsCombo.setSelectedItem(Config.getConfig().getSkin());
        LevelSet ls = levelSetSelector.getLevelSet(Config.getConfig().getLevelSet());
        levelSetsCombo.setSelectedItem(ls);

        panel.add(levelSetsCombo);
        panel.add(levelsCombo);
        panel.add(skinsCombo);

        return panel;
    }

    private void initUI() {
        frame = new JFrame("Sokoban");
        Config config = Config.getConfig();
        if (config.isFullScreen()) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true);
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(frame);
        } else {
            frame.setSize(config.getWidth(), config.getHeight());
            frame.setLocation(config.getLocationX(), config.getLocationY());
        }
        frame.setLayout(new BorderLayout());

        levelPanel = new LevelPanel(this);

        frame.add(createHeaderPanel(), BorderLayout.PAGE_START);
        JScrollPane scroller = new JScrollPane(levelPanel);
        scroller.setBorder(null);

        frame.add(scroller, BorderLayout.CENTER);

        frame.add(createFooterPanel(), BorderLayout.PAGE_END);

        frame.setJMenuBar(createMenu());

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                if (frame.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                    Config.getConfig().setWidth(frame.getWidth());
                    Config.getConfig().setHeight(frame.getHeight());
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                super.componentMoved(e);
                if (frame.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                    Config.getConfig().setLocationX(frame.getX());
                    Config.getConfig().setLocationY(frame.getY());
                }
            }
        });
    }

    private void run() {
        initUI();
        frame.setVisible(true);
        levelPanel.requestFocusInWindow();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void printTree(Component cmp, String indent) {
        if (cmp == null) {
            return;
        }

        System.out.println(indent + cmp.getClass().getSimpleName());

        Container cnt = (Container) cmp;
        if (cnt == null) {
            return;
        }

        for (Component sub : cnt.getComponents()) {
            printTree(sub, indent+"   ");
        }
    }

    public static void main(String args[]) {
        SokobanApp app = new SokobanApp();
        app.run();
    }
}
