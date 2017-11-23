package com.example.jacek.weatherapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.util.List;

import database.City;
import database.Condition;
import settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String EXTRA_BASE_ID = "extra_base_id";

    private static final int REQ_DATA_SET_CHANGED = 0;
    private WeatherData mWeatherData;
    private DataUpdater<MainActivity> mDataUpdater;

    private ViewPager mViewPager;
    private WeatherPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long adapterBaseId = 0;

        if(savedInstanceState != null){
            adapterBaseId = savedInstanceState.getLong(EXTRA_BASE_ID, 0);
        }

        setContentView(R.layout.activity_main);
        mWeatherData = WeatherData.getInstance(getBaseContext());
        mWeatherData.mConditionList = mWeatherData.loadConditionsFromDatabase();
        mWeatherData.loadSettingsFromDatabase();
        if(!isNetworkAvailable()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage("No internet connection available, presented data may be outdated")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

            builder.create().show();
        }

        Handler responseHandler = new Handler();
        mDataUpdater = new DataUpdater<>(responseHandler);
        mDataUpdater.setDataUpdaterListener(new DataUpdater.DataUpdaterListener<MainActivity>() {
            @Override
            public void onDataUpdate(MainActivity target, List<Condition> updatedItems) {
                for(Condition condition : updatedItems)
                    mWeatherData.updateCondition(condition);
                mPagerAdapter.updateFragmentsUI();
                mDataUpdater.queueDataRefreshDelayed(MainActivity.this,
                        Condition.getCityList(mWeatherData.mConditionList),
                        mWeatherData.getAppSettings().getRefreshDelay());
            }

            @Override
            public void onDataUpdateManual(MainActivity target, List<Condition> updatedItems) {
                for(Condition condition : updatedItems)
                    mWeatherData.updateCondition(condition);
                mPagerAdapter.updateFragmentsUI();
            }
        });
        mDataUpdater.start();
        mDataUpdater.getLooper();
        Log.i(TAG, "Start of the background rhread");

        mViewPager = (ViewPager) findViewById(R.id.weather_pager_view_pager);
        FragmentManager fm  = getSupportFragmentManager();
        mPagerAdapter = new WeatherPagerAdapter(fm, adapterBaseId);
        mViewPager.setAdapter(mPagerAdapter);
    }

    public void waitForDebugger(){
        if(android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();
    }

    @Override
    protected void onStart() {
        super.onStart();
        List<City> cities = Condition.getCityList(mWeatherData.mConditionList);
        mDataUpdater.queueDataRefreshDelayed(this,
                cities,
                mWeatherData.getAppSettings().getRefreshDelay());

        mDataUpdater.queueDataRefresh(this, cities);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        boolean itemsChanged = data.getBooleanExtra(SettingsActivity.EXTRA_ITEM_LIST_CHANGED, false);
        int deletedItemsCount = data.getIntExtra(SettingsActivity.EXTRA_DELETED_ITEMS, 0);
        boolean refreshDelayChanged = data.getBooleanExtra(SettingsActivity.EXTRA_REFRESH_DELAY_CHANGED, false);

        if(itemsChanged) {
            mPagerAdapter.notifyChangeInPosition(deletedItemsCount);
            mPagerAdapter.notifyDataSetChanged();
        }
        if(refreshDelayChanged){
            mDataUpdater.clearQueue();
            mDataUpdater.queueDataRefreshDelayed(this,
                    Condition.getCityList(mWeatherData.mConditionList),
                    mWeatherData.getAppSettings().getRefreshDelay());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, REQ_DATA_SET_CHANGED);
                return true;
            case R.id.menu_item_refresh:
                mDataUpdater.queueDataRefresh(this, Condition.getCityList(mWeatherData.mConditionList));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(EXTRA_BASE_ID, mPagerAdapter.getBaseId());
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDataUpdater.quit();
        Log.i(TAG, "Background thread destroyed");
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    private class WeatherPagerAdapter extends FragmentStatePagerAdapter{
        SparseArrayCompat<Fragment> mWeatherFragments = new SparseArrayCompat<>();
        private long mBaseId = 0;

        WeatherPagerAdapter(FragmentManager fm, long baseId) {
            super(fm);
            mBaseId = baseId;
        }

        @Override
        public Fragment getItem(int position) {
            Condition condition = mWeatherData.mConditionList.get(position);
            return WeatherFragment.newInstance(condition.getCity().getWoeid());
        }
        @Override
        public int getCount() {
            return mWeatherData.mConditionList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            mWeatherFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
                mWeatherFragments.remove(position);
                //getSupportFragmentManager().beginTransaction().remove((Fragment) object).commitAllowingStateLoss();
                super.destroyItem(container, position, object);
        }


        /*@Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return mBaseId + position;
        }*/

        long getBaseId() {
            return mBaseId;
        }

        void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            mBaseId += getCount() + n;
        }

        void updateFragmentsUI(){
            for(int i = 0, size = mWeatherFragments.size(); i < size; i++){
                int key = mWeatherFragments.keyAt(i);

                WeatherFragment fragment = (WeatherFragment) mWeatherFragments.get(key);
                if(fragment != null)
                    fragment.refreshUI();
            }
        }

    }


}
