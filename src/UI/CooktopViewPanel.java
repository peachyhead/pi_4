package UI;

import cooktop.CookZoneModel;
import cooktop.CooktopModel;
import cooktop.CooktopSignal;
import signal.SignalBus;
import signal.SignalListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class CooktopViewPanel extends JPanel {

    private final List<CookZoneView> cookZoneViews = new ArrayList<>();
    
    private final Action zoneSelectionAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };

    public CooktopViewPanel() {
        var layout = new GridLayout();
        layout.setColumns(2);
        layout.setRows(2);
        setLayout(layout);
    }

    public void initialize(CooktopModel cooktopModel) {
        SignalBus.subscribe(new SignalListener<String>(CooktopSignal.cookZoneSelection, signal -> {
            var index = (Integer.parseInt(signal));
            zoneSelectionAction.putValue("", cookZoneViews.get(index).getModel());
        }));
        
        for (CookZoneModel zone : cooktopModel.getCookZones()) {
            var view = new CookZoneView(zone);
            cookZoneViews.add(view);
            view.initialize();
            add(view);
        }

        revalidate();
    }

    public void onSelectionChange(PropertyChangeListener listener){
        zoneSelectionAction.addPropertyChangeListener(listener);
    } 
}
