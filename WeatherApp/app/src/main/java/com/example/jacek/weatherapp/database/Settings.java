package com.example.jacek.weatherapp.database;

import android.content.ContentValues;
import static com.example.jacek.weatherapp.database.WeatherDbSchema.*;

public class Settings {

    public enum Units{
        UNIT_C("°C", "km", "hPa", "km/h"),
        UNIT_F("°F" , "mi", "in", "mph");

        private static final double PRESSURE_CONST = 0.0295301;
        private static final double DISTANCE_CONST = 1.609344;
        private static final double TEMPERATURE_CONST = 5.0/9.0;

        private String mUnitName;
        private String mDistanceUnit;
        private String mPressureUnit;
        private String mSpeedUnit;

        Units(String unitName, String distanceUnit, String pressureUnit, String speedUnit){
            this.mUnitName = unitName;
            this.mDistanceUnit = distanceUnit;
            this.mSpeedUnit = speedUnit;
            this.mPressureUnit = pressureUnit;
        }

        public static double fahrenheitsToCelsius(double fahrenheits){
            return (fahrenheits - 32) * TEMPERATURE_CONST;
        }

        public static double milesToKilometers(double miles){
            return miles * DISTANCE_CONST;
        }

        public static double hPaToInchens(double hpa){
            return hpa * PRESSURE_CONST;
        }

        public String getUnitName() {
            return mUnitName;
        }

        public String getDistanceUnit() {
            return mDistanceUnit;
        }

        public String getPressureUnit() {
            return mPressureUnit;
        }

        public String getSpeedUnit() {
            return mSpeedUnit;
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
        REFRESH_1_MINUTE("1 minute", 60),
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
    public static final int ID = 1;

    private Units mMeasurementUnit;
    private RefreshDelayOptions mRefreshDelay;

    public Settings(){
        mMeasurementUnit = DEFAULT_UNIT;
        mRefreshDelay = DEFAULT_REFRESH_DELAY;
    }

    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();

        values.put(SettingsTable.Cols.ID, ID);
        values.put(SettingsTable.Cols.MES_UNIT, mMeasurementUnit.getUnitName());
        values.put(SettingsTable.Cols.REFRESH_DELAY, mRefreshDelay.getOptionName());

        return values;
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
