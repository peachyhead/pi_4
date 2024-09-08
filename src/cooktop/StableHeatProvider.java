package cooktop;

import cooktop.interfaces.HeatProvider;
import lombok.Getter;

public class StableHeatProvider extends HeatProvider {

    private final double htc;  // Коэффициент теплопередачи

    @Getter private HeatStatus currentStatus; // Текущий статус нагрева жидкости

    private static final double BOILING_POINT = 100.0;             // Точка кипения воды (100 °C)
    private static final double LATENT_HEAT_OF_EVAPORATION = 2260; // Удельная теплота парообразования воды (Дж/г)

    public StableHeatProvider(String name, double htc, double volume, 
                              double density, double heatCapacity) {
        super(volume, density, heatCapacity);
        this.htc = htc;
        this.currentStatus = HeatStatus.heating; // Изначально статус - нагрев
        setName(name);
    }

    @Override
    protected double getCollisionSurface() {
        return 150; // Площадь контакта зависит от объема жидкости
    }

    @Override
    public double getHeatTransferCoefficient() {
        return htc;
    }

    @Override
    public void update(double externalTemperature) {
        // Текущая температура жидкости
        double currentTemp = getCurrentTemperature().get();

        // Рассчитываем поглощенное тепло
        double deltaTemperature = externalTemperature - currentTemp;
        double absorbedHeat = deltaTemperature * getHeatTransferCoefficient();
        
        if (currentTemp < BOILING_POINT) {
            // Рассчитываем новую температуру жидкости без учета испарения
            double heatCapacity = getHeatCapacity();
            double mass = getMass();
            double newTemperature = currentTemp + absorbedHeat / (heatCapacity * mass);

            // Обновляем температуру жидкости
            getCurrentTemperature().set(newTemperature);

            // Проверяем, если температура достигла точки кипения
            if (newTemperature >= BOILING_POINT) {
                // Переходим к состоянию кипения
                currentStatus = HeatStatus.boiling;
                handleBoilingAndEvaporation(absorbedHeat * getCollisionSurface());
            }
        } else {
            // Если уже кипит, обрабатываем испарение
            handleBoilingAndEvaporation(absorbedHeat * getCollisionSurface());
        }
    }

    private void handleBoilingAndEvaporation(double absorbedHeat) {
        if (volume > 0) {
            double mass = getMass();
            // Рассчитываем количество тепла, необходимое для испарения текущего объема
            double heatRequiredForCurrentVolumeEvaporation = LATENT_HEAT_OF_EVAPORATION * mass;
            if (absorbedHeat >= heatRequiredForCurrentVolumeEvaporation) {
                decreaseVolume(absorbedHeat);
            } else {
                calculateHeatFromSteam(absorbedHeat);
            }
        }
    }

    private void calculateHeatFromSteam(double absorbedHeat) {
        // Если тепла недостаточно для полного испарения, обновляем температуру
        double temperatureIncrease = absorbedHeat / (getHeatCapacity() * getMass());
        double newTemperature = getCurrentTemperature().get() + temperatureIncrease;
        // Устанавливаем температуру на 100°C, если еще есть жидкость
        if (newTemperature > BOILING_POINT) {
            getCurrentTemperature().set(BOILING_POINT);
            currentStatus = HeatStatus.boiling; // Обновляем статус на кипение
        } else {
            getCurrentTemperature().set(newTemperature);
            currentStatus = HeatStatus.heating; // Если температура ниже точки кипения
        }
    }

    private void decreaseVolume(double absorbedHeat) {
        // Рассчитываем объем испаренной жидкости
        double evaporatedVolume = absorbedHeat / LATENT_HEAT_OF_EVAPORATION;
        volume -= evaporatedVolume / 1000;

        // Если весь объем испарился, сбрасываем температуру и устанавливаем статус высыхания
        if (volume <= 0) {
            volume = 0;  // Объем не может быть отрицательным
            getCurrentTemperature().set(0.0); // Температура сбрасывается после полного испарения
            currentStatus = HeatStatus.drying; // Изменяем статус на высыхание
        } else {
            // Обновляем температуру до точки кипения, так как жидкость кипит
            getCurrentTemperature().set(BOILING_POINT);
        }
    }
}