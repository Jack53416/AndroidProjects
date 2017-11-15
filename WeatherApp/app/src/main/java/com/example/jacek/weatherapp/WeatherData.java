package com.example.jacek.weatherapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Debug;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import database.Condition;
import database.ConditionCursorWrapper;
import database.Forecast;
import database.ForecastCursorWrapper;
import database.WeatherBaseHelper;

import static database.WeatherDbSchema.*;

public class WeatherData {

    private static WeatherData mWeatherData;
    public static final int CONDITION_LIMIT = 30;
    public List<Condition> mConditionList;
    private SQLiteDatabase mDatabase;
    private Context mContext;


    public static WeatherData getInstance(Context context)
    {
        if(mWeatherData == null)
            mWeatherData = new WeatherData(context);
        return mWeatherData;
    }

    private WeatherData(Context context) {
        mConditionList = new ArrayList<>();
        mContext = context.getApplicationContext();
        mDatabase = new WeatherBaseHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getContentValues(Condition condition){
        ContentValues values = new ContentValues();

        values.put(ConditionTable.Cols.WOEID, condition.woeid);
        values.put(ConditionTable.Cols.NAME, condition.cityName);
        values.put(ConditionTable.Cols.LATITUDE, condition.latitude);
        values.put(ConditionTable.Cols.LONGITUDE, condition.longitude);
        values.put(ConditionTable.Cols.CONDITION_CODE, condition.code);
        values.put(ConditionTable.Cols.CONDITION_DATE, condition.date.getTime());
        values.put(ConditionTable.Cols.CONDITION_TEMP, condition.temperature);
        values.put(ConditionTable.Cols.CONDITION_TEXT, condition.text);
        values.put(ConditionTable.Cols.WIND_CHILL, condition.windChill);
        values.put(ConditionTable.Cols.WIND_DIRECTION, condition.windDirection);
        values.put(ConditionTable.Cols.WIND_SPEED, condition.windSpeed);
        values.put(ConditionTable.Cols.ATM_HUMIDITY, condition.humidity);
        values.put(ConditionTable.Cols.ATM_PRESSURE, condition.pressure);
        values.put(ConditionTable.Cols.ATM_VISIBILITY, condition.visibility);
        return values;
    }

    private static ContentValues getContentValues(Forecast forecast, int woeid){
        ContentValues values = new ContentValues();

        values.put(ForecastTable.Cols.WOEID, woeid);
        values.put(ForecastTable.Cols.CODE, forecast.code);
        values.put(ForecastTable.Cols.DATE, forecast.date.getTime());
        values.put(ForecastTable.Cols.DAY, forecast.day);
        values.put(ForecastTable.Cols.HIGH, forecast.high);
        values.put(ForecastTable.Cols.LOW, forecast.low);
        values.put(ForecastTable.Cols.TEXT, forecast.text);

        return values;
    }

    public void addCondition(Condition condition){
        ContentValues values = getContentValues(condition);
        try{
            mDatabase.insertOrThrow(ConditionTable.NAME, null, values);
            for(Forecast forecast : condition.forecasts){
                addForecast(forecast, condition.woeid);
            }
        }catch (SQLException ex){
            Log.e("SQL_EXCEPTION", "Could not add Condition: " + ex.getMessage());
        }
    }

    public void addForecast(Forecast forecast, int woeeid){
        ContentValues values = getContentValues(forecast, woeeid);
        mDatabase.insert(ForecastTable.NAME, null, values);
    }

    public void updateCondition(Condition condition){
        ContentValues values = getContentValues(condition);
        mDatabase.update(ConditionTable.NAME, values,
                         ConditionTable.Cols.WOEID + " = ?", new String[]{String.valueOf(condition.woeid)});

        mDatabase.delete(ForecastTable.NAME,
                         ForecastTable.Cols.WOEID + "= ?", new String[]{String.valueOf(condition.woeid)});

        for(Forecast forecast : condition.forecasts){
            addForecast(forecast, condition.woeid);
        }
    }

    private ConditionCursorWrapper queryConditions(String whereClause, String[] args){

        Cursor cursor = mDatabase.query(
                ConditionTable.NAME,
                null,
                whereClause,
                args,
                null,
                null,
                null
        );

        return new ConditionCursorWrapper(cursor);
    }

    private ForecastCursorWrapper queryForecasts(String whereClause, String[] args){

        Cursor cursor = mDatabase.query(
                ForecastView.NAME,
                null,
                whereClause,
                args,
                null,
                null,
                null
        );

        return new ForecastCursorWrapper(cursor);
    }


    public List<Forecast> getForecasts(int woeid){
        List<Forecast> forecasts = new ArrayList<>();

        ForecastCursorWrapper cursor = queryForecasts(ForecastTable.Cols.WOEID + " = ?", new String[]{String.valueOf(woeid)});

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                forecasts.add(cursor.getForecast());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }

        return forecasts;
    }

    public List<Condition> getConditions(){
        List<Condition> conditions = new ArrayList<>();
        Condition currentCondition;

        try (ConditionCursorWrapper cursor = queryConditions(null, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                currentCondition = cursor.getCondition();
                currentCondition.forecasts = getForecasts(currentCondition.woeid);
                conditions.add(currentCondition);
                cursor.moveToNext();
            }

        }

        return conditions;
    }
}
