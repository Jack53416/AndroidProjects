package com.example.jacek.weatherapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.Date;

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
        Condition a = new Condition(546);
        Forecast b = new Forecast();
        a.cityName = "Ldz";
        a.text = "Dupa test";
        b.text = "bad weather ahead";
        b.high = 15.2;
        b.low = 13.3;
        b.day = "tue";
        b.code = 23;
        a.forecasts.add(b);
        mWeatherData.updateCondition(a);
        mWeatherData.mConditionList = mWeatherData.getConditions();
        new FetchWeatherTask().execute();
    }


    private class FetchWeatherTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            new WeatherFetcher().fetchWeather();
            return null;
        }
    }
}
