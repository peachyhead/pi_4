package UI;

import cooktop.CookZoneModel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Ooga booga");
        setSize(1200, 800);

        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setLayout(new GridBagLayout());
        
        var timer = new Timer(10, e -> repaint());
        timer.start();
    }

    public void setupPanels(CooktopViewPanel viewPanel) {
        var contentPane = getContentPane();
        
        var constraints = new GridBagConstraints();
        viewPanel.setBackground(Color.BLACK);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.weightx = 0.8f;
        constraints.weighty = 1;
        constraints.insets = new Insets(4, 4, 4, 2);
        contentPane.add(viewPanel, constraints);
        viewPanel.setPreferredSize(new Dimension(800, 800));
        
        var infoPanel = new InfoPanel();
        constraints.gridx = 1;
        constraints.weightx = 0.2f;
        constraints.insets = new Insets(4, 2, 4, 4);
        infoPanel.setBackground(Color.GRAY);
        infoPanel.setPreferredSize(new Dimension(400, 800));
        contentPane.add(infoPanel, constraints);
        contentPane.revalidate();

        viewPanel.onSelectionChange(evt -> {
            var value = (CookZoneModel) evt.getNewValue();
            infoPanel.set(value);
        });
    }
}
