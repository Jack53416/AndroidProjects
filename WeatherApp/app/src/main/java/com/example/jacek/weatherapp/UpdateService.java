package com.example.jacek.weatherapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import database.City;
import database.Condition;

public class UpdateService extends IntentService {

    private static final String TAG = "UPDATE_SERVICE";
    private static Handler mResponseHandler;
    private static UpdateListener mUpdateListener;

    interface UpdateListener{
        void onDataUpdate(List<Condition> updatedItems);
        void onNoConnection();
    }

    public static void setUpdateListener(UpdateListener updateListener) {
        mUpdateListener = updateListener;
    }

    public static Intent newIntent(Context context){
        return new Intent(context, UpdateService.class);
    }

    public static void setmResponseHandler(Handler mResponseHandler) {
        UpdateService.mResponseHandler = mResponseHandler;
    }

    public UpdateService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        android.os.Debug.waitForDebugger();
        if(!isNetworkAvaliable()){
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    mUpdateListener.onNoConnection();
                }
            });
            return;
        }
        Log.i(TAG, "Received an intent! " + intent);

        final List<Condition> updatedData;
        List<City> citiesToupdate = Condition.getCityList(WeatherData.getInstance(getApplicationContext()).mConditionList);

        updatedData = new WeatherFetcher().fetchWeather(citiesToupdate);

        if(updatedData == null)
            return;

        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                mUpdateListener.onDataUpdate(updatedData);
            }
        });
    }

    public static void setServiceAlarm(Context context, int interval_ms, boolean isActive){
        Intent intent = UpdateService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if(isActive){
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,0, interval_ms, pendingIntent);
        }
        else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }

    }

    private boolean isNetworkAvaliable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
}
