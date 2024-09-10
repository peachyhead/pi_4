package cooktop;

import cooktop.interfaces.HeatProvider;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class EnvironmentProcessor {
    
    @Getter @Setter
    private static double targetTemperature;
    
    private static final ArrayList<HeatProvider> heatProviders = new ArrayList<>();
    
    private static Thread updateStream;

    public static void initialize() {
        updateStream = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                double totalHeatTransferredToEnvironment = 0.0;

                // Обрабатываем каждый HeatProvider
                for (HeatProvider provider : heatProviders) {
                    synchronized (provider) {
                        // Рассчитываем изменение температуры между средой и объектом
                        double initialTemperature = provider.getCurrentTemperature().get();

                        // Обновляем температуру объекта с учетом внешней температуры
                        provider.update(targetTemperature);

                        // После обновления получаем новое значение температуры объекта
                        double finalTemperature = provider.getCurrentTemperature().get();

                        // Рассчитываем количество тепла, переданное от/к объекту
                        double heatTransferred = (finalTemperature - initialTemperature) * provider.getHeatCapacity() * provider.getMass();

                        // Общее тепло, переданное среде
                        totalHeatTransferredToEnvironment += heatTransferred;
                    }
                }

                // Учитываем тепловой обмен с окружающей средой
                double environmentHeatCapacity = calculateHeatCapacityOfEnvironment();
                double deltaEnvironmentTemperature = -totalHeatTransferredToEnvironment / environmentHeatCapacity;

                // Обновляем температуру окружающей среды
                targetTemperature += deltaEnvironmentTemperature;

                // Устанавливаем интервал между циклами
                try {
                    Thread.sleep(100); // обновление каждую 1/10 секунды
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        updateStream.start();
    }
    public static void bind(HeatProvider heatProvider){
        heatProviders.add(heatProvider);
    }
    
    public static void unbind(HeatProvider heatProvider){
        heatProviders.remove(heatProvider);
    }

    private static double calculateHeatCapacityOfEnvironment() {
        // Примерное значение теплоемкости окружающей среды (может быть уточнено)
        return 100000; // произвольное значение для расчета изменений температуры среды
    }
}
