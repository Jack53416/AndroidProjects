package database;

import android.database.Cursor;

public class WeatherDbSchema {
    public static final class ConditionTable{
        public static final String NAME = "CONDITIONS";

        public static final class Cols{
            public static final String CITY_WOEID = "CITY_WOEID";
            public static final String CITY_NAME = "CITY_NAME";
            public static final String CITY_LATITUDE = "CITY_LATITUDE";
            public static final String CITY_LONGITUDE = "CITY_LONGITUDE";
            public static final String CITY_COUNTRY = "CITY_COUNTRY";
            public static final String CITY_TIME_ZONE = "CITY_TIME_ZONE";
            public static final String CONDITION_CODE = "CONDITION_CODE";
            public static final String CONDITION_DATE = "CONDITION_DATE";
            public static final String CONDITION_TEMP = "CONDITION_TEMP";
            public static final String CONDITION_TEXT = "CONDITION_TEXT";
            public static final String WIND_CHILL = "WIND_CHILL";
            public static final String WIND_DIRECTION = "WIND_DIRECTION";
            public static final String WIND_SPEED = "WIND_SPEED";
            public static final String ATM_PRESSURE = "ATM_PRESSURE";
            public static final String ATM_HUMIDITY = "ATM_HUMIDITY";
            public static final String ATM_VISIBILITY = "ATM_VISIBILITY";
        }

    }

    public static final class ForecastTable{
        public static final String NAME = "FORECASTS";
        public static final class Cols{
            public static final String WOEID = "CITY_WOEID";
            public static final String DATE = "DATE";
            public static final String CODE = "CODE";
            public static final String DAY = "DAY";
            public static final String HIGH = "HIGH";
            public static final String LOW = "LOW";
            public static final String TEXT = "TEXT";
        }
    }

    public static final class SettingsTable{
        public static final String NAME = "SETTINGS";
        public static final class Cols{
            public static final String ID = "ID";
            public static final String MES_UNIT = "MES_UNIT";
            public static final String REFRESH_DELAY = "REFRESH_DELAY";
            public static final String HOME_CITY = "HOME_CITY";
        }
    }

    public static final class ForecastView{
        public static final String NAME = "FORECAST_VIEW";
    }

}
