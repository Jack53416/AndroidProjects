package com.example.jacek.weatherapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import database.Condition;
import database.Forecast;
import database.WeatherBaseHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private WeatherData mWeatherData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWeatherData = WeatherData.getInstance(getBaseContext());
        mWeatherData.mConditionList = mWeatherData.getConditions();
        Condition newCity = null;
        try {
          newCity = new FetchWoeidTask().execute("Gdansk").get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (newCity != null)
            mWeatherData.addCondition(newCity);
        new FetchWeatherTask().execute();
    }

    public void waitForDebugger(){
        if(android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();
    }

    private class FetchWeatherTask extends AsyncTask<Void, Void, List<Condition>>{

        @Override
        protected List<Condition> doInBackground(Void... voids) {
            waitForDebugger();
            return new WeatherFetcher().fetchWeather(mWeatherData.mConditionList);
        }

        @Override
        protected void onPostExecute(List<Condition> items){
            mWeatherData.mConditionList = items;
        }
    }

    private class FetchWoeidTask extends AsyncTask<String, Void, Condition>{

        @Override
        protected Condition doInBackground(String... strings) {
            waitForDebugger();
            String cityName = strings[0];
            return new WeatherFetcher().fetchWoeid(cityName);
        }

        @Override
        protected  void onPostExecute(Condition conditionItem){

        }
    }
}
