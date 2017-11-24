package com.example.jacek.weatherapp;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import database.City;
import database.Condition;
import database.Settings;

class DataUpdater<T> extends HandlerThread {

    private static final String TAG = "DataUpdater";

    private static final int MESSAGE_DOWNLOAD = 0;
    private static final int MESSAGE_DOWNLOAD_MANUAL = 1;
    private static Handler mRequestHandler;
    private Handler mResponseHandler;
    private DataUpdaterListener<T> mDataUpdaterListener;
    private ConcurrentMap<T,List<City>> mRequestMap = new ConcurrentHashMap<>();

    DataUpdater(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    interface DataUpdaterListener<T>{
        void onDataUpdate(T target , List<Condition> updatedItems);
        void onDataUpdateManual(T target, List<Condition> updatedItems);
      //  void onBackgroundThreadStart();
    }

    void setDataUpdaterListener(DataUpdaterListener<T> dataUpdaterListener) {
        mDataUpdaterListener = dataUpdaterListener;
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MESSAGE_DOWNLOAD || msg.what == MESSAGE_DOWNLOAD_MANUAL){
                    @SuppressWarnings("unchecked")
                    T target = (T) msg.obj;
                    handleRequest(target, msg.what);
                }
            }
        };
    }

    private void handleRequest(final T target, final int messageType) {
        final List<City> cities = mRequestMap.get(target);
        if(cities == null)
            return;
        final List<Condition> conditions = new WeatherFetcher().fetchWeather(cities);

        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mRequestMap.get(target) != cities)
                    return;
                //mRequestMap.remove(target);
                if(messageType == MESSAGE_DOWNLOAD)
                    mDataUpdaterListener.onDataUpdate(target, conditions);
                else if(messageType == MESSAGE_DOWNLOAD_MANUAL)
                    mDataUpdaterListener.onDataUpdateManual(target, conditions);
            }
        });
    }

    void queueDataRefresh(T target, List<City> cityList){
        if(cityList == null){
            mRequestMap.remove(target);
        }
        else {
            mRequestMap.put(target, cityList);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD_MANUAL, target)
                    .sendToTarget();
        }
    }

    void queueDataRefreshDelayed(T target, List<City> cityList, Settings.RefreshDelayOptions delay){
        if(cityList == null){
            mRequestMap.remove(target);
        }
        else {
            mRequestMap.put(target, cityList);
            Message message = mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target);
            mRequestHandler.sendMessageDelayed(message, delay.getOptionLength_s()*1000);
        }
    }

    void clearQueue(){
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }
}
