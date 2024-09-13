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

    private final double baseHeatTransfer;
    private Thread heatTransferStream;
    
    @Getter private double currentHeatTransfer;

    public CookZoneModel(int id, int radius, double baseHeatTransfer, 
                         String name, double volume, 
                         double boilingPoint, double latentHeat, 
                         double density, double heatCapacity) {
        super(name, volume, boilingPoint, latentHeat, density, heatCapacity);
        this.id = id;
        this.radius = radius;
        this.baseHeatTransfer = baseHeatTransfer;
    }
    
    public void enable() {
        heatTransferStream = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (getMode() == ZoneHeatMode.Chill && currentHeatTransfer <= 0f) {
                    disable();
                }

                var increment = Math.max(getMode().ordinal(), 1) *
                        Math.signum(getTargetHeatTransfer() - currentHeatTransfer);
                currentHeatTransfer = Math.max(currentHeatTransfer + increment * 10, 0f);

                if (currentHeatTransfer > getTargetHeatTransfer())
                    currentHeatTransfer = getTargetHeatTransfer();

                if (content.get() != null) {
                    content.get().update(currentHeatTransfer);
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        heatTransferStream.start();
        enabled = true;
    }

    public void setHeatMode(int index) {
        var currentMode = mode;
        mode = ZoneHeatMode.values()[index];
        if (currentMode == ZoneHeatMode.Chill && mode != ZoneHeatMode.Chill)
            enable();
    }

    public void disable() {
        if (heatTransferStream != null) {
            heatTransferStream.interrupt();
        }
        enabled = false;
    }

    public double getTargetHeatTransfer() {
        int heatModeLevel = mode.ordinal(); // Уровень режима нагрева (минимум 1)
        return baseHeatTransfer * heatModeLevel; // Количество тепла пропорционально режиму
    }
    
    @Override
    public double getCollisionSurface() {
        // Учитываем площадь поверхности конфорки
        return Math.PI * radius * radius;
    }

    @Override
    public double getHeatTransferCoefficient() {
        // Можно задать коэффициент теплопередачи для конфорки
        return 1; // Используем единичный коэффициент для упрощения
    }
}