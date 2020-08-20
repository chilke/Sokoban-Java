package com.chilke.Sokoban;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

public class SettingsDialog extends JDialog {

    private JSlider animationSpeedSlider;
    private JCheckBox fullScreenCheck;

    public SettingsDialog(JFrame frame) {
        super(frame, "Sokoban Settings", true);

        Config config = Config.getConfig();
        Point loc = frame.getLocation();
        setLocation(loc.x+80, loc.y+80);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        JPanel layout = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel label = new JLabel("Animation Speed:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 0, 0);
        layout.add(label, constraints);

        animationSpeedSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, config.getAnimationSpeed());
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        labels.put(0, new JLabel("Slow"));
        labels.put(100, new JLabel("Fast"));
        animationSpeedSlider.setLabelTable(labels);
        animationSpeedSlider.setPaintLabels(true);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 0, 0, 10);
        layout.add(animationSpeedSlider, constraints);

        fullScreenCheck = new JCheckBox("Full Screen");
        fullScreenCheck.setSelected(config.isFullScreen());
        constraints.gridx = 0;
        constraints.gridy = 1;
        layout.add(fullScreenCheck, constraints);

        content.add(layout);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.LINE_AXIS));

        buttons.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttons.add(Box.createHorizontalGlue());

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttons.add(cancel);

        buttons.add(Box.createRigidArea(new Dimension(10, 0)));

        JButton save = new JButton("Save");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Config c = Config.getConfig();
                c.setAnimationSpeed(animationSpeedSlider.getValue());
                c.setFullScreen(fullScreenCheck.isSelected());
                dispose();
            }
        });
        buttons.add(save);

        content.add(buttons);

        getContentPane().add(content);
        pack();
    }
}
