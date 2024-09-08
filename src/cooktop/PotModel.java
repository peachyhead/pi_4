package cooktop;

import cooktop.interfaces.HeatProvider;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PotModel extends HeatProvider {
    
    @Setter private boolean lidOpened;
    private final int radius;
    
    private static final double closedLidHTC = 0.00009f;
    private static final double openedLidHTC = 0.000042f;

    public PotModel(int radius, double volume, 
                    double density, double heatCapacity) {
        super(volume, density, heatCapacity);
        setName("Pot");
        this.radius = radius;
    }

    @Override
    protected double getCollisionSurface() {
        return Math.PI * radius * radius;
    }

    @Override
    public double getHeatTransferCoefficient() {
        return lidOpened ? openedLidHTC : closedLidHTC;
    }
}
