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
        Condition condition = new Condition(getInt(getColumnIndex(ConditionTable.Cols.WOEID)));

        condition.cityName = getString(getColumnIndex(ConditionTable.Cols.NAME));
        condition.code = getInt(getColumnIndex(ConditionTable.Cols.CONDITION_CODE));
        condition.date = new Date(getLong(getColumnIndex(ConditionTable.Cols.CONDITION_DATE)));
        condition.latitude = getDouble(getColumnIndex(ConditionTable.Cols.LATITUDE));
        condition.longitude = getDouble(getColumnIndex(ConditionTable.Cols.LONGITUDE));
        condition.temperature = getDouble(getColumnIndex(ConditionTable.Cols.CONDITION_TEMP));
        condition.text = getString(getColumnIndex(ConditionTable.Cols.CONDITION_TEXT));
        condition.windChill = getInt(getColumnIndex(ConditionTable.Cols.WIND_CHILL));
        condition.windDirection = getInt(getColumnIndex(ConditionTable.Cols.WIND_DIRECTION));
        condition.windSpeed = getDouble(getColumnIndex(ConditionTable.Cols.WIND_SPEED));

        return condition;
    }
}
