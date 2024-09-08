package cooktop;

import cooktop.interfaces.HeatProvider;
import cooktop.interfaces.ZoneHeatMode;
import lombok.Getter;
import lombok.Setter;

public class CookZoneModel extends HeatProvider {
    
    @Getter @Setter private ZoneHeatMode mode = ZoneHeatMode.Chill;
    
    @Getter private final int id;
    @Getter private boolean enabled;
    @Getter private final int radius;
    @Getter private float temperature;
    
    private final double baseTemperature;
    private Thread heatTransferStream;
    
    public CookZoneModel(int id, int radius, double baseTemperature, 
                         double heatCapacity) {
        super(1f, 1f, heatCapacity);
        this.id = id;
        this.radius = radius;
        this.baseTemperature = baseTemperature;
    }
    
    public void enable() {
        heatTransferStream = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                var increment = Math.max(getMode().ordinal(), 1) * 
                        Math.signum(getTargetTemperature() - temperature);
                temperature = Math.max(temperature + increment * 0.1f, 0f);
                
                if (getMode() == ZoneHeatMode.Chill && temperature <= 0f)
                    disable();
                if (content != null) 
                    content.update(temperature);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        heatTransferStream.start();
        enabled = true;
    }

    public void setHeatMode(int index){
        var currentMode = mode;
        mode = ZoneHeatMode.values()[index];
        if (currentMode == ZoneHeatMode.Chill && mode != ZoneHeatMode.Chill)
            enable();
    }
    
    public void disable() {
        if (heatTransferStream != null)
            heatTransferStream.interrupt();
        enabled = false;
    }

    private int getTargetTemperature() {
        return ((int) baseTemperature * mode.ordinal());
    };
    
    @Override
    protected double getCollisionSurface() {
        return Math.PI * radius * radius;
    }

    @Override
    public double getHeatTransferCoefficient() {
        return 0;
    }
}
