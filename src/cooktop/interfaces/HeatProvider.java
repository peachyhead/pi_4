package cooktop.interfaces;

import cooktop.ReactiveProperty;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

public abstract class HeatProvider {

    @Setter @Getter private String name;
    @Getter private final double heatCapacity;
    private final double density;
    
    @Getter protected final ReactiveProperty<Double> currentTemperature = new ReactiveProperty<>();
    @Getter protected HeatProvider content;
    @Getter protected double volume;
    
    private final Action onContentChange = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            
        }
    };

    public HeatProvider(double volume, double density, double heatCapacity) {
        this.volume = volume;
        this.density = density;
        this.heatCapacity = heatCapacity;
        currentTemperature.set(0.0);
    }
    
    public void update(double externalTemperature) {
        double currentTemp = currentTemperature.get();
        double deltaTemperature = externalTemperature - currentTemp;

        // Рассчитываем количество тепла, которое сообщается этому объекту
        double absorbedHeat = getHeatTransferCoefficient() * getCollisionSurface() * deltaTemperature;

        // Рассчитываем прирост температуры жидкости от поглощенного тепла
        double heatCapacity = getHeatCapacity();
        double mass = getMass();
        double newTemperature = currentTemp + absorbedHeat / (heatCapacity * mass);

        // Обновляем температуру жидкости
        currentTemperature.set(newTemperature);

        // Если в кастрюле есть содержимое (например, вода), передаем тепло содержимому
        if (content != null) {
            // Рассчитываем количество тепла, переданное содержимому
            double heatTransfer = getHeatTransferCoefficient() * getCollisionSurface() * 
                    (newTemperature - content.currentTemperature.get());

            // Обновляем температуру содержимого
            double contentHeatCapacity = content.getHeatCapacity();
            double contentMass = content.getMass();
            double transferredTemperature = heatTransfer / (contentHeatCapacity * contentMass);

            content.update(content.currentTemperature.get() + transferredTemperature);
        }
    }

    protected abstract double getCollisionSurface();
    
    public abstract double getHeatTransferCoefficient();

    public void addHeatProvider(HeatProvider heatProvider) {
        this.content = heatProvider;
        onContentChange.putValue("", content);
    }

    public void removeHeatProvider() {
        this.content = null;
        onContentChange.putValue("", null);
    }
    
    public void subscribeOnContentChange(PropertyChangeListener listener) {
        onContentChange.addPropertyChangeListener(listener);
    }
    
    public double getMass() {
        return volume * density;
    }
}
