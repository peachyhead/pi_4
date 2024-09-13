package cooktop;

import cooktop.interfaces.HeatProvider;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PotModel extends HeatProvider {
    
    @Setter private boolean lidOpened;
    private final double htc;
    private final int radius;
    
    private static final double closedLidHTC = 0.00009f;
    private static final double openedLidHTC = 0.000042f;

    public PotModel(double htc, int radius, String name, 
                    double volume, double boilingPoint, 
                    double latentHeat, double density, double heatCapacity) {
        super(name, volume, boilingPoint, latentHeat, density, heatCapacity);
        this.htc = htc;
        this.radius = radius;
        setLidOpened(false);
        setName(name);
    }
    
    @Override
    public double getCollisionSurface() {
        return Math.PI * radius * radius;
    }

    @Override
    public double getHeatTransferCoefficient() {
        return lidOpened 
                ? htc * openedLidHTC 
                : htc * closedLidHTC;
    }
}
