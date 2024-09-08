package UI;

import cooktop.CookZoneModel;

import javax.swing.*;
import java.awt.*;

public class InfoPanel extends JPanel {

    private final CookZoneDataPanel cookZoneDataPanel;
    private final CookZoneControlPanel cookZoneControlPanel; 
    private final HeatProviderDataPanel heatProviderDataPanel;

    public InfoPanel() {
        setLayout(new GridBagLayout());
        cookZoneDataPanel = new CookZoneDataPanel();
        var c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.weighty = 0.5f;
        c.weightx = 1f;
        c.gridx = 0;
        c.gridy = 0;
        add(cookZoneDataPanel, c);
        
        cookZoneControlPanel = new CookZoneControlPanel();
        c.weighty = 1f;
        c.gridy += 1;
        add(cookZoneControlPanel, c);
        
        heatProviderDataPanel = new HeatProviderDataPanel();
        c.weighty = 0.4f;
        c.gridy += 1;
        add(heatProviderDataPanel, c);
        revalidate();
    }
    
    public void set(CookZoneModel cookZoneModel){
        cookZoneDataPanel.set(cookZoneModel);
        cookZoneControlPanel.set(cookZoneModel);
        heatProviderDataPanel.set(cookZoneModel);
    }
}
