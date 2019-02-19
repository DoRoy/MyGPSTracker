package mygpstracker.android.mygpstracker.Battery;

import java.util.Map;

public class BatteryInfoWrapper {

    public static final String KEY_CHARGING_TYPE = "charging_type";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_SCALE = "scale";
    public static final String KEY_PERCENT = "percent";
    public static final String KEY_TEMPERATURE = "temperature";
    public static final String KEY_VOLTAGE = "voltage";
    public static final String KEY_HEALTH = "health";
    public static final String KEY_CAPACITY = "capacity";
    public static final String KEY_TECHNOLOGY = "technology";


    private String level;
    private String scale;
    private String percent;
    private String temperature ;
    private String voltage ;
    private String capacity ;
    private String technology;
    private String chargingType;
    private String health ;

    public BatteryInfoWrapper() {
    }

    public BatteryInfoWrapper(Map<String,String> data){
        for (Map.Entry<String,String> iterate: data.entrySet()) {
            String parameter = iterate.getKey();
            String value = iterate.getValue();
            switch (parameter){
                case KEY_CHARGING_TYPE:
                    setChargingType(value);
                    break;
                case KEY_LEVEL:
                    setLevel(value);
                    break;
                case KEY_SCALE:
                    setScale(value);
                    break;
                case KEY_PERCENT:
                    setPercent(value);
                    break;
                case KEY_TEMPERATURE:
                    setTemperature(value);
                    break;
                case KEY_VOLTAGE:
                    setVoltage(value);
                    break;
                case KEY_HEALTH:
                    setHealth(value);
                    break;
                case KEY_CAPACITY:
                    setCapacity(value);
                    break;
                case KEY_TECHNOLOGY:
                    setTechnology(value);
                    break;
            }
        }

    }

    public String getAttribute(String attribute){
        switch (attribute){
            case KEY_CHARGING_TYPE:
                return getChargingType();

            case KEY_LEVEL:
                return getLevel();

            case KEY_SCALE:
                return getScale();

            case KEY_PERCENT:
                return getPercent();

            case KEY_TEMPERATURE:
                return getTemperature();

            case KEY_VOLTAGE:
                return getVoltage();

            case KEY_HEALTH:
                return getHealth();

            case KEY_CAPACITY:
                return getCapacity();

            case KEY_TECHNOLOGY:
                return getTechnology();

        }
        return null;
    }


    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public String getChargingType() {
        return chargingType;
    }

    public void setChargingType(String chargingType) {
        this.chargingType = chargingType;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }
}
