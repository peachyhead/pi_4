package cooktop;

import cooktop.interfaces.HeatProvider;

public class StableHeatProvider extends HeatProvider {

    private final double htc;  // Коэффициент теплопередачи (Вт/м^2·К)

    public StableHeatProvider(double htc, String name, double volume, 
                              double boilingPoint, double latentHeat, 
                              double density, double heatCapacity) {
        super(name, volume, boilingPoint, latentHeat, density, heatCapacity);
        this.htc = htc;
        setName(name);
    }

    @Override
    public double getCollisionSurface() {
        double initialSurfaceArea = 0.375f; //Радиус кастрюли
        return initialSurfaceArea * Math.pow(volume, 2.0/3.0);  // Учитываем уменьшение объема
    }

    @Override
    public double getHeatTransferCoefficient() {
        return htc;
    }
}