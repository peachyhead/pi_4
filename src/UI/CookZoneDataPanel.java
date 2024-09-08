package UI;

import cooktop.CookZoneModel;

import javax.swing.*;
import java.awt.*;

public class CookZoneDataPanel extends JPanel {
    private final JTextField id;
    private final JTextField temperature;
    private final JTextField heatMode;
    
    private Thread dataUpdateStream;
    
    public CookZoneDataPanel(){
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1f;
        c.weighty = 1f;
        c.gridx = 0;
        c.gridy = 0;
        id = getField("Cook zone ID", c);
        c.weightx = 0.5f;
        c.gridy += 1;
        temperature = getField("Cook zone temperature", c);
        c.gridy -= 1;
        c.gridx = 1;
        heatMode = getField("Cook zone heat mode", c);
        revalidate();
    }
    
    public void set(CookZoneModel cookZoneModel){
        if (cookZoneModel == null)
            return;
        if (dataUpdateStream != null) 
            dataUpdateStream.interrupt();

        id.setText(String.valueOf(cookZoneModel.getId()));
        
        dataUpdateStream = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                ApplyData(cookZoneModel);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        
        dataUpdateStream.start();
    }

    private void ApplyData(CookZoneModel cookZoneModel) {
        temperature.setText(String.valueOf(cookZoneModel.getTemperature()));
        heatMode.setText(String.valueOf(cookZoneModel.getMode()));
    }

    private JTextField getField(String text, GridBagConstraints c){
        var label = new JLabel(text);
        var field = new JTextField();
        field.setToolTipText(text);
        field.setEditable(false);
        add(label, c);
        c.gridy += 1;
        add(field, c);
        return field;
    }
}
