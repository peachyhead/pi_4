package UI;

import cooktop.CookZoneModel;
import cooktop.StableHeatProvider;

import javax.swing.*;
import java.awt.*;

public class HeatProviderDataPanel extends JPanel {
    
    private final JTextField providerName;
    private final JTextField providerTemperature;
    private final JTextField providerHTC;
    
    private final JTextField contentHTC;
    private final JTextField contentName;
    private final JTextField contentTemperature;
    private final JTextField contentVolume;
    private final JTextField contentStatus;

    private Thread dataUpdateStream;
    
    public HeatProviderDataPanel(){
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5f;
        c.weighty = 1f;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 10, 5, 5);
        providerName = getField("Provider name", c);
        
        c.gridy -= 1;
        c.gridx = 1;
        c.insets = new Insets(5, 5, 5, 10);
        providerTemperature = getField("Provider temperature", c);

        c.gridy += 1;
        c.gridx = 0;
        c.insets = new Insets(5, 10, 5, 5);
        providerHTC = getField("Provider HTC", c);

        c.gridy -= 1;
        c.gridx = 1;
        c.insets = new Insets(5, 5, 5, 10);
        contentHTC = getField("Content HTC", c);
        
        c.gridy += 1;
        c.gridx = 0;
        c.insets = new Insets(0, 10, 2, 5);
        contentName = getField("Content name", c);
        
        c.gridy -= 1;
        c.gridx = 1;
        c.insets = new Insets(0, 5, 2, 10);
        contentTemperature = getField("Content temperature", c);

        c.gridy += 1;
        c.gridx = 0;
        c.insets = new Insets(0, 10, 2, 5);
        contentVolume = getField("Content volume", c);
        
        c.gridy -= 1;
        c.gridx = 1;
        c.insets = new Insets(0, 5, 2, 10);
        contentStatus = getField("Content status", c);
        revalidate();
    }
    
    public void set(CookZoneModel cookZoneModel){
        if (cookZoneModel == null)
            return;
        if (dataUpdateStream != null) 
            dataUpdateStream.interrupt();
        
        cookZoneModel.subscribeOnContentChange(evt -> ApplyData(cookZoneModel));
        dataUpdateStream = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                ApplyData(cookZoneModel);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        
        dataUpdateStream.start();
    }

    private void ApplyData(CookZoneModel cookZoneModel) {
        if (cookZoneModel == null || cookZoneModel.getContent().get() == null) {
            setBlank();
            return;
        }
        
        var content = cookZoneModel.getContent().get();
        providerName.setText(content.getName());
        providerTemperature.setText(String.valueOf(content.getCurrentTemperature()));
        providerHTC.setText(String.valueOf(content.getHeatTransferCoefficient()));
        var providerContent = (StableHeatProvider) content.getContent().get();
        if (providerContent == null) {
            contentName.setText("");
            contentTemperature.setText("");
            contentHTC.setText("");
            contentVolume.setText("");
            contentStatus.setText("");
        }
        else {
            contentName.setText(providerContent.getName());
            contentTemperature.setText(String.valueOf(providerContent.getCurrentTemperature()));
            contentHTC.setText(String.valueOf(providerContent.getHeatTransferCoefficient()));
            contentStatus.setText(providerContent.getCurrentStatus().name());
            contentVolume.setText(String.valueOf(providerContent.getVolume() * 1000f));
        }
    }
    
    private void setBlank(){
        providerName.setText("");
        providerTemperature.setText("");
        providerHTC.setText("");
        
        contentHTC.setText("");
        contentName.setText("");
        contentTemperature.setText("");
        contentVolume.setText("");
        contentStatus.setText("");
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
