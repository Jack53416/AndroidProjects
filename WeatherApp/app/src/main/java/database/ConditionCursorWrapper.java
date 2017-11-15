package database;

import android.database.Cursor;
import android.database.CursorWrapper;


import java.util.Date;

import database.WeatherDbSchema.ConditionTable;

public class ConditionCursorWrapper extends CursorWrapper {
    public ConditionCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Condition getCondition(){
        City city = new City();
        city.setName(getString(getColumnIndex(ConditionTable.Cols.CITY_NAME)));
        city.setWoeid(getInt(getColumnIndex(ConditionTable.Cols.CITY_WOEID)));
        city.setLatitude(getDouble(getColumnIndex(ConditionTable.Cols.CITY_LATITUDE)));
        city.setLongitude(getDouble(getColumnIndex(ConditionTable.Cols.CITY_LONGITUDE)));
        city.setCountry(getString(getColumnIndex(ConditionTable.Cols.CITY_COUNTRY)));

        Condition condition = new Condition(city);
        condition.setCode(getInt(getColumnIndex(ConditionTable.Cols.CONDITION_CODE)));
        condition.setDate(new Date(getLong(getColumnIndex(ConditionTable.Cols.CONDITION_DATE))));
        condition.setTemperature(getDouble(getColumnIndex(ConditionTable.Cols.CONDITION_TEMP)));
        condition.setText(getString(getColumnIndex(ConditionTable.Cols.CONDITION_TEXT)));
        condition.setWindChill(getInt(getColumnIndex(ConditionTable.Cols.WIND_CHILL)));
        condition.setWindDirection(getInt(getColumnIndex(ConditionTable.Cols.WIND_DIRECTION)));
        condition.setWindSpeed( getDouble(getColumnIndex(ConditionTable.Cols.WIND_SPEED)));
        condition.setPressure(getDouble(getColumnIndex(ConditionTable.Cols.ATM_PRESSURE)));
        condition.setHumidity(getInt(getColumnIndex(ConditionTable.Cols.ATM_HUMIDITY)));
        condition.setVisibility(getDouble(getColumnIndex(ConditionTable.Cols.ATM_VISIBILITY)));

        return condition;
    }
}
