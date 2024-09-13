package UI;

import cooktop.*;
import cooktop.interfaces.ZoneHeatMode;
import signal.SignalBus;

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
            SignalBus.fire(CooktopSignal.cookZoneChangeHeatMode, String.valueOf(cookZoneModel.getId()));
        });
        add(heatModeButton, c);

        c.gridy += 1;
        c.weighty = 0.3f;
        potToggle = new JToggleButton("Place pot with water");
        potToggle.addActionListener(e -> {
            if (cookZoneModel == null) return;
            if (cookZoneModel.getContent().get() != null)
                cookZoneModel.removeHeatProvider();
            else {
                var pot = new PotModel(
                        8000f, // Коэффициент теплопередачи (Вт/м^2·К)
                        cookZoneModel.getRadius() / 6, // Радиус кастрюли в см (0.125 м)
                        "Pot",
                        0.005f, // Объем кастрюли в м^3
                        1500f, // Температура кипения стали (°C)
                        600000f, // Латентная теплота испарения стали (Дж/кг)
                        8000f, // Плотность стали (кг/м^3)
                        500f // Удельная теплоемкость стали (Дж/кг·К)
                );
                
                var water = new StableHeatProvider(
                        500f, // Коэффициент теплопередачи (Вт/м^2·К)
                        "Water",
                        0.001f, // Объем воды в м^3
                        100f, // Температура кипения воды (°C)
                        2260000f, // Латентная теплота испарения воды (Дж/кг)
                        1000f, // Плотность воды (кг/м^3)
                        4184f // Удельная теплоемкость воды (Дж/кг·К)
                );
                EnvironmentProcessor.bind(pot);
                EnvironmentProcessor.bind(pot, water);
                EnvironmentProcessor.bind(water, pot);
                pot.addHeatProvider(water);
                cookZoneModel.addHeatProvider(pot);
                SignalBus.fire(CooktopSignal.cookZoneTogglePot, String.valueOf(cookZoneModel.getId()));
            }
            setToggleInfo(cookZoneModel);
        });
        add(potToggle, c);
        c.gridy += 1;
        
        potLidToggle = new JToggleButton("Toggle pot lid");
        potLidToggle.addActionListener(e -> {
            if (cookZoneModel == null) return;
            var potModel = (PotModel)cookZoneModel.getContent().get();
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
        potToggle.setSelected(cookZoneModel.getContent().get() != null);
        setToggleInfo(cookZoneModel);
        var pot = (PotModel)cookZoneModel.getContent().get();
        if (pot != null) 
            potLidToggle.setSelected(pot.isLidOpened());
    }

    private void setToggleInfo(CookZoneModel cookZoneModel) {
        var infoText = cookZoneModel.getContent().get() != null
                ? "Remove pot with water"
                : "Place pot with water";
        potToggle.setText(infoText);
    }
}
