package database;


import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;

public class CursorWrappers {
    /*
    * Condition Cursor Wrapper class
    * */

    public static class ConditionCursorWrapper extends CursorWrapper {
        public ConditionCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Condition getCondition(){
            City city = new City();
            city.setName(getString(getColumnIndex(WeatherDbSchema.ConditionTable.Cols.CITY_NAME)));
            city.setWoeid(getInt(getColumnIndex(WeatherDbSchema.ConditionTable.Cols.CITY_WOEID)));
            city.setLatitude(getDouble(getColumnIndex(WeatherDbSchema.ConditionTable.Cols.CITY_LATITUDE)));
            city.setLongitude(getDouble(getColumnIndex(WeatherDbSchema.ConditionTable.Cols.CITY_LONGITUDE)));
            city.setCountry(getString(getColumnIndex(WeatherDbSchema.ConditionTable.Cols.CITY_COUNTRY)));

            Condition condition = new Condition(city);
            condition.setCode(getInt(getColumnIndex(WeatherDbSchema.ConditionTable.Cols.CONDITION_CODE)));
            condition.setDate(new Date(getLong(getColumnIndex(WeatherDbSchema.ConditionTable.Cols.CONDITION_DATE))));
            condition.setTemperature(getDouble(getColumnIndex(WeatherDbSchema.ConditionTable.Cols.CONDITION_TEMP)));
            condition.setText(getString(getColumnIndex(WeatherDbSchema.ConditionTable.Cols.CONDITION_TEXT)));
            condition.setWindChill(getInt(getColumnIndex(WeatherDbSchema.ConditionTable.Cols.WIND_CHILL)));
            condition.setWindDirection(getInt(getColumnIndex(WeatherDbSchema.ConditionTable.Cols.WIND_DIRECTION)));
            condition.setWindSpeed( getDouble(getColumnIndex(WeatherDbSchema.ConditionTable.Cols.WIND_SPEED)));
            condition.setPressure(getDouble(getColumnIndex(WeatherDbSchema.ConditionTable.Cols.ATM_PRESSURE)));
            condition.setHumidity(getInt(getColumnIndex(WeatherDbSchema.ConditionTable.Cols.ATM_HUMIDITY)));
            condition.setVisibility(getDouble(getColumnIndex(WeatherDbSchema.ConditionTable.Cols.ATM_VISIBILITY)));

            return condition;
        }
    }

    /**
     *  Forecast Cursor Wrapper class
     */

    public static class ForecastCursorWrapper extends CursorWrapper {
        public ForecastCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Forecast getForecast(){
            Forecast forecast = new Forecast();

            forecast.setCode(getInt(getColumnIndex(WeatherDbSchema.ForecastTable.Cols.CODE)));
            forecast.setDate(new Date(getLong(getColumnIndex(WeatherDbSchema.ForecastTable.Cols.DATE))));
            forecast.setDay(getString(getColumnIndex(WeatherDbSchema.ForecastTable.Cols.DAY)));
            forecast.setHigh(getDouble(getColumnIndex(WeatherDbSchema.ForecastTable.Cols.HIGH)));
            forecast.setLow(getDouble(getColumnIndex(WeatherDbSchema.ForecastTable.Cols.LOW)));
            forecast.setText(getString(getColumnIndex(WeatherDbSchema.ForecastTable.Cols.TEXT)));

            return forecast;
        }
    }

    /*
    * Settings Cursor Wrapper class
    * */

    public static class SettingsCursorWrapper extends CursorWrapper {
        public SettingsCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Settings getSettings(){
            Settings settings = new Settings();
            Settings.Units unit = Settings.Units.
                    getUnit(getString(getColumnIndex(WeatherDbSchema.SettingsTable.Cols.MES_UNIT)));
            settings.setMeasurementUnit(unit);

            Settings.RefreshDelayOptions delay = Settings.RefreshDelayOptions
                    .getRefreshDelay(getString(getColumnIndex(WeatherDbSchema.SettingsTable.Cols.REFRESH_DELAY)));

            settings.setRefreshDelay(delay);

            return settings;
        }
    }
}
