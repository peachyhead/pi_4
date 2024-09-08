package UI;

import cooktop.CookZoneModel;
import cooktop.PotModel;
import cooktop.StableHeatProvider;
import cooktop.interfaces.ZoneHeatMode;

import javax.swing.*;
import java.awt.*;

public class CookZoneControlPanel extends JPanel {

    private CookZoneModel cookZoneModel;
    private final JButton heatModeButton;
    private final JToggleButton potToggle;
    private final JToggleButton potLidToggle;
    
    private int currentHeatMode;
    
    public CookZoneControlPanel() {
        setLayout(new GridBagLayout());
        var c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.3f;
        c.weightx = 1f;
        c.gridx = 0;
        c.gridy = 0;
        
        heatModeButton = new JButton("Heat mode handler");
        heatModeButton.addActionListener(e -> {
           if (cookZoneModel == null) return;
           currentHeatMode += 1;
           if (currentHeatMode >= ZoneHeatMode.values().length)
               currentHeatMode = 0;
           cookZoneModel.setHeatMode(currentHeatMode);
           heatModeButton.setText(ZoneHeatMode.values()[currentHeatMode].toString());
        });
        add(heatModeButton, c);

        c.gridy += 1;
        c.weighty = 0.3f;
        potToggle = new JToggleButton("Place pot with water");
        potToggle.addActionListener(e -> {
            if (cookZoneModel == null) return;
            if (cookZoneModel.getContent() != null)
                cookZoneModel.removeHeatProvider();
            else {
                var pot = new PotModel(cookZoneModel.getRadius() / 6, 
                        1f, 2.7f, 0.9f);
                var water = new StableHeatProvider("Water", 1400, 
                        1f, 1f, 4184f);
                pot.addHeatProvider(water);
                cookZoneModel.addHeatProvider(pot);
            }
            setToggleInfo(cookZoneModel);
        });
        add(potToggle, c);
        c.gridy += 1;
        
        potLidToggle = new JToggleButton("Toggle pot lid");
        potLidToggle.addActionListener(e -> {
            if (cookZoneModel == null) return;
            var potModel = (PotModel)cookZoneModel.getContent();
            if (potModel == null) return;
            var current = potModel.isLidOpened();
            potModel.setLidOpened(!current);
        });
        add(potLidToggle, c);
        revalidate();
    }
    
    public void set(CookZoneModel cookZoneModel){
        this.cookZoneModel = cookZoneModel;
        currentHeatMode = cookZoneModel.getMode().ordinal();
        heatModeButton.setText(ZoneHeatMode.values()[currentHeatMode].toString());
        potToggle.setSelected(cookZoneModel.getContent() != null);
        setToggleInfo(cookZoneModel);
        var pot = (PotModel)cookZoneModel.getContent();
        if (pot != null) 
            potLidToggle.setSelected(pot.isLidOpened());
    }

    private void setToggleInfo(CookZoneModel cookZoneModel) {
        var infoText = cookZoneModel.getContent() != null
                ? "Remove pot with water"
                : "Place pot with water";
        potToggle.setText(infoText);
    }
}
