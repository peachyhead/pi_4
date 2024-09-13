package cooktop.interfaces;

import cooktop.EnvironmentProcessor;
import cooktop.HeatStatus;
import cooktop.ReactiveProperty;
import cooktop.StableHeatProvider;
import lombok.Getter;
import lombok.Setter;

import java.beans.PropertyChangeListener;

public abstract class HeatProvider {

    @Setter @Getter private String name;
    @Getter private HeatStatus currentStatus; // Текущий статус нагрева
    protected final double density; // Плотность вещества (кг/м^3)

    protected double currentHeatTransfer;
    private double temporaryHeatTransfer;

    @Getter protected double volume; // Объем вещества (м^3)
    @Getter private final double heatCapacity; // Удельная теплоемкость (Дж/кг·К)
    @Getter private final double boilingPoint; // Температура кипения (°C)
    @Getter private final double latentHeat; // Латентная теплота испарения (Дж/кг)
    @Getter protected double currentTemperature;

    @Getter protected ReactiveProperty<HeatProvider> content = new ReactiveProperty<>();

    private boolean providing;
    private Thread heatUpdaterThread;

    public HeatProvider(String name, double volume,
                        double boilingPoint,
                        double latentHeat,
                        double density, double heatCapacity) {
        this.name = name;
        this.volume = volume;
        this.density = density;
        this.heatCapacity = heatCapacity;
        this.boilingPoint = boilingPoint;
        this.latentHeat = latentHeat;
        this.currentStatus = HeatStatus.heating; // Изначально статус - нагрев
        this.currentTemperature = 0.0; // Начальная температура

        setName(name);
        content.set(null);
    }

    public void initialize() {
        System.out.println("Initializing " + getClass().getSimpleName() + "...");
        providing = true;
        if (heatUpdaterThread != null && heatUpdaterThread.isAlive()) {
            heatUpdaterThread.interrupt();
        }
        heatUpdaterThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted() && providing) {
                handleHeatAbsorb();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        heatUpdaterThread.start();
    }

    protected void handleHeatAbsorb() {
        if (currentStatus == HeatStatus.drying) return;
        // Плавное обновление текущей передачи тепла
        currentHeatTransfer = temporaryHeatTransfer;
        System.out.println("Applied heat transfer to " + getName() + ": " + currentHeatTransfer);

        double currentTemp = currentTemperature;
        double mass = getMass();  // Масса вещества

        // Количество тепла, необходимое для достижения точки кипения
        double heatRequiredToBoil = (boilingPoint - currentTemp) * getHeatCapacity() * mass;
        
        if (currentHeatTransfer > heatRequiredToBoil) {
            currentTemperature = boilingPoint;
            currentStatus = HeatStatus.boiling;
            double heatForEvaporation = currentHeatTransfer - heatRequiredToBoil;
            handleBoilingAndEvaporation(heatForEvaporation);
        } else {
            // Нагреваем объект пропорционально его теплоемкости
            double deltaTemperature = currentHeatTransfer / (getHeatCapacity() * mass);

            // Ограничиваем изменение температуры до точки кипения
            currentTemperature = Math.min(currentTemp + deltaTemperature, boilingPoint);
        }

        temporaryHeatTransfer = 0f;  // Сбрасываем временное тепло
    }
    
    private synchronized void handleBoilingAndEvaporation(double absorbedHeat) {
        if (volume > 0) {
            double maxEvaporationHeat = latentHeat * getMass() / 1000;
            double heatForEvaporation = Math.min(absorbedHeat, maxEvaporationHeat);
            decreaseVolume(heatForEvaporation);
        }
    }

    private synchronized void decreaseVolume(double absorbedHeat) {
        double evaporatedMass = absorbedHeat / latentHeat;
        double evaporatedVolume = evaporatedMass / density;

        volume -= evaporatedVolume;

        if (volume <= 0) {
            volume = 0;
            currentTemperature = 0f;
            currentStatus = HeatStatus.drying;
        }
    }

    public double calculateHeatTransferToProvider(HeatProvider provider) {
        double tempDifference = currentTemperature - provider.getCurrentTemperature();
        double heatTransfer = getHeatTransferCoefficient() * getCollisionSurface() * tempDifference;

        // Ограничиваем, чтобы температура воды не могла быть выше температуры кастрюли
        if (provider instanceof StableHeatProvider && 
                provider.getCurrentTemperature() > currentTemperature) {
            return 0;
        }

        if (tempDifference > 0) {
            return heatTransfer;
        } else {
            return 0;
        }
    }
    
    public synchronized void update(double absorbedHeat) {
        temporaryHeatTransfer += absorbedHeat;
        System.out.println("Updated temporary heat transfer by " + absorbedHeat + ". Current value: " + temporaryHeatTransfer);
    }

    // Масса вещества
    public double getMass() {
        return volume * density;  // Масса = объем * плотность (кг)
    }

    public abstract double getCollisionSurface(); // Площадь контакта
    public abstract double getHeatTransferCoefficient(); // Коэффициент теплопередачи

    public void addHeatProvider(HeatProvider heatProvider) {
        content.set(heatProvider);
        heatProvider.initialize();
    }

    public void removeHeatProvider() {
        EnvironmentProcessor.unbind(content.get());
        content.get().dispose();
        content.set(null);
    }

    public void subscribeOnContentChange(PropertyChangeListener listener) {
        content.subscribe(listener);
    }

    public void dispose() {
        providing = false;
        if (heatUpdaterThread != null) {
            heatUpdaterThread.interrupt();
        }
        if (content.get() != null) {
            removeHeatProvider();
        }
    }
}