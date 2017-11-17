package database;

import java.util.Set;

public class Settings {

    public enum Units{
        UNIT_C("°C"),
        UNIT_F("°F");

        private String mUnitName;
        Units(String unitName){
            this.mUnitName = unitName;
        }

        public String getUnitName() {
            return mUnitName;
        }

        @Override
        public String toString() {
            return mUnitName;
        }

        public static Units getUnit(String value) {
            for(Units unit : values())
                if(unit.getUnitName().equalsIgnoreCase(value))
                    return unit;
            throw new IllegalArgumentException();
        }

        public static String[] getValues(){
            Units[] values = values();
            String[] result = new String[values.length];
            for(int i = 0, size = values.length; i < size; ++i){
                result[i] = values[i].toString();
            }
            return result;
        }


    }

    public enum RefreshDelayOptions{
        REFRESH_30_MINUTES("30 minutes", 1800),
        REFRESH_1_HOUR("1 hour", 3600),
        REFRESH_6_HOURS("6 hours", 6* 3600),
        REFRESH_12_HOURS("12 hours", 12* 3600),
        REFRESH_24_HOURS("24 hours", 24 * 3600);

        private String mOptionName;
        private int mOptionLength_s;
        RefreshDelayOptions(String optionName, int optionLength_s){
            this.mOptionName = optionName;
            this.mOptionLength_s = optionLength_s;
        }

        @Override
        public String toString() {
            return mOptionName;
        }

        public String getOptionName() {
            return mOptionName;
        }

        public int getOptionLength_s() {
            return mOptionLength_s;
        }

        public static RefreshDelayOptions getRefreshDelay(String value) {
            for(RefreshDelayOptions delay : values())
                if(delay.getOptionName().equalsIgnoreCase(value))
                    return delay;
            throw new IllegalArgumentException();
        }
        public static String[] getValues(){
            RefreshDelayOptions[] values = values();
            String[] result = new String[values.length];
            for(int i = 0, size = values.length; i < size; ++i){
                result[i] = values[i].toString();
            }
            return result;
        }

    }

    private static final Units DEFAULT_UNIT = Units.UNIT_C;
    private static final RefreshDelayOptions DEFAULT_REFRESH_DELAY = RefreshDelayOptions.REFRESH_1_HOUR;

    private Units mMeasurementUnit;
    private RefreshDelayOptions mRefreshDelay;

    public Settings(){
        mMeasurementUnit = DEFAULT_UNIT;
        mRefreshDelay = DEFAULT_REFRESH_DELAY;
    }

    public Units getMeasurementUnit() {
        return mMeasurementUnit;
    }

    public void setMeasurementUnit(Units measurementUnit) {
        mMeasurementUnit = measurementUnit;
    }

    public RefreshDelayOptions getRefreshDelay() {
        return mRefreshDelay;
    }

    public void setRefreshDelay(RefreshDelayOptions refreshDelay) {
        mRefreshDelay = refreshDelay;
    }
}
