package cooktop;

import cooktop.interfaces.HeatProvider;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

public class EnvironmentProcessor {

    @Getter @Setter
    private static double targetTemperature; // Текущая температура окружающей среды

    private static final HashMap<HeatProvider, HeatProvider> exchangeMap = new HashMap<>();
    private static final ArrayList<HeatProvider> heatProviders = new ArrayList<>();

    private static Thread updateStream;

    // Инициализация процесса обновления теплового обмена
    public static void initialize() {
        updateStream = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                double totalHeatTransferredToEnvironment = 0.0;

                for (HeatProvider provider : heatProviders) {
                    double deltaTemperature = targetTemperature - provider.getCurrentTemperature();

                    double heatTransfer = provider.getHeatTransferCoefficient() *
                            provider.getCollisionSurface() * deltaTemperature;

                    if (heatTransfer > 0) {
                        provider.update(heatTransfer);
                    }

                    totalHeatTransferredToEnvironment += heatTransfer;
                }

                for (HeatProvider provider : exchangeMap.keySet()) {
                    var content = exchangeMap.get(provider);
                    var heatTransfer = provider.calculateHeatTransferToProvider(content);
                    content.update(heatTransfer);
                }

                double environmentHeatCapacity = calculateHeatCapacityOfEnvironment();
                double deltaEnvironmentTemperature = totalHeatTransferredToEnvironment / environmentHeatCapacity;

                targetTemperature += deltaEnvironmentTemperature;

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        updateStream.start();
    }

    // Привязка объекта к среде для учета теплового обмена
    public static void bind(HeatProvider heatProvider) {
        heatProviders.add(heatProvider);
    }

    public static void bind(HeatProvider heatProvider, HeatProvider content) {
        exchangeMap.put(heatProvider, content);
    }

    // Отключение объекта от среды
    public static void unbind(HeatProvider heatProvider) {
        heatProviders.remove(heatProvider);
        exchangeMap.remove(heatProvider);
    }
    
    private static double calculateHeatCapacityOfEnvironment() {
        return 100000;
    }
}