package com.example.jacek.weatherapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.astrocalculator.AstroCalculator;

import java.util.ArrayList;
import java.util.List;

import database.Condition;
import database.CursorWrappers.*;
import database.Forecast;
import database.Settings;
import database.WeatherBaseHelper;

import static database.WeatherDbSchema.*;

public class WeatherData {

    private static WeatherData mWeatherData;
    public static final int CONDITION_LIMIT = 10;
    public List<Condition> mConditionList;
    private Settings mAppSettings;
    private SQLiteDatabase mDatabase;
    private AstroCalculator mAstroCalculator;

    public static WeatherData getInstance(Context context)
    {
        if(mWeatherData == null)
            mWeatherData = new WeatherData(context);
        return mWeatherData;
    }

    private WeatherData(Context context) {
        mConditionList = new ArrayList<>();
        mAppSettings = new Settings();
        Context appContext = context.getApplicationContext();
        mDatabase = new WeatherBaseHelper(appContext).getWritableDatabase();

        mAstroCalculator = new AstroCalculator(AstroData.getCurrentAstroDateTime(AstroData.DEFAULT_TIME_ZONE), AstroData.DEFAULT_LOCATION);
    }

    public Settings getAppSettings() {
        return mAppSettings;
    }

    AstroCalculator getAstroCalculator() {
        return mAstroCalculator;
    }

    public void insertCondition(Condition condition){
        if(mConditionList.size() >= CONDITION_LIMIT)
            return;

        ContentValues values = condition.getContentValues();
        try{
            mDatabase.insertOrThrow(ConditionTable.NAME, null, values);
            for(Forecast forecast : condition.getForecasts()){
                insertForecast(forecast, condition.getCity().getWoeid());
            }
            mConditionList.add(condition);
        }catch (SQLException ex){
            Log.e("SQL_EXCEPTION", "Could not add Condition: " + ex.getMessage());
        }
    }

    private void insertForecast(Forecast forecast, int woeeid){
        ContentValues values = forecast.getContentValues(woeeid);
        mDatabase.insert(ForecastTable.NAME, null, values);
    }

    void updateCondition(Condition condition){
        ContentValues values = condition.getContentValues();

        int idx = mConditionList.indexOf(findConditionByWoeid(condition.getCity().getWoeid()));

        if(idx < 0)
            return;

        mConditionList.set(idx, condition);

        mDatabase.update(ConditionTable.NAME, values,
                         ConditionTable.Cols.CITY_WOEID + " = ?", new String[]{String.valueOf(condition.getCity().getWoeid())});

        mDatabase.delete(ForecastTable.NAME,
                         ForecastTable.Cols.WOEID + "= ?", new String[]{String.valueOf(condition.getCity().getWoeid())});

        for(Forecast forecast : condition.getForecasts()){
            insertForecast(forecast, condition.getCity().getWoeid());
        }
    }

    public void deleteCondition(final int cityWoeid){
        mDatabase.delete(ForecastTable.NAME,
                ForecastTable.Cols.WOEID + "= ?", new String[]{String.valueOf(cityWoeid)});
        mDatabase.delete(ConditionTable.NAME,
                ConditionTable.Cols.CITY_WOEID + "= ?", new String[]{String.valueOf(cityWoeid)});
        Condition condtionToRemove = findConditionByWoeid(cityWoeid);
        if(condtionToRemove != null)
            mConditionList.remove(condtionToRemove);
    }

    Condition findConditionByWoeid(final int cityWoeid){
        for(Condition condition : mConditionList){
            if(condition.getCity().getWoeid() == cityWoeid)
                return condition;
        }
        return null;
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

    private List<Forecast> loadForecastsFromDatabase(int woeid){
        List<Forecast> forecasts = new ArrayList<>();

        try(ForecastCursorWrapper cursor = queryForecasts(ForecastTable.Cols.WOEID + " = ?", new String[]{String.valueOf(woeid)})) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                forecasts.add(cursor.getForecast());
                cursor.moveToNext();
            }
        }
        return forecasts;
    }

    List<Condition> loadConditionsFromDatabase(){
        List<Condition> conditions = new ArrayList<>();
        Condition currentCondition;

        try (ConditionCursorWrapper cursor = queryConditions(null, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                currentCondition = cursor.getCondition();
                currentCondition.setForecasts(loadForecastsFromDatabase(currentCondition.getCity().getWoeid()));
                conditions.add(currentCondition);
                cursor.moveToNext();
            }

        }
        return conditions;
    }

     void loadSettingsFromDatabase(){
        Cursor cursorTemplate = mDatabase.query(
                SettingsTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        try(SettingsCursorWrapper cursor = new SettingsCursorWrapper(cursorTemplate)){
            cursor.moveToFirst();
            if(!cursor.isAfterLast()){
                mAppSettings = cursor.getSettings();
            }
        }
    }

    public void updateSettings(){
        int settingsCount;
        try(Cursor cursor = mDatabase.rawQuery("SELECT COUNT(*) FROM " + SettingsTable.NAME, null)){
            cursor.moveToFirst();
            settingsCount = cursor.getInt(0);
        }
        if(settingsCount > 0){
            mDatabase.update(SettingsTable.NAME,
                    mAppSettings.getContentValues(),
                    SettingsTable.Cols.ID + "= ?",
                    new String[]{String.valueOf(Settings.ID)}
                    );
        }
        else{
            try {
                mDatabase.insertOrThrow(SettingsTable.NAME, null, mAppSettings.getContentValues());
            }catch (SQLException ex){
                Log.e("SQL","Could not insert Settings! " + ex.getMessage());
            }
        }
    }
}
