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

import database.Condition;
import settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int REQ_DATA_SET_CHANGED = 0;
    private WeatherData mWeatherData;
    private DataUpdater<MainActivity> mDataUpdater;

    private WeatherPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        UpdateService.setmResponseHandler(responseHandler);
        UpdateService.setUpdateListener(new UpdateService.UpdateListener() {
            @Override
            public void onDataUpdate(List<Condition> updatedItems) {
                for(Condition condition : updatedItems)
                    mWeatherData.updateCondition(condition);
                mPagerAdapter.updateFragmentsUI();
            }

            @Override
            public void onNoConnection() {

            }
        });

        UpdateService.setServiceAlarm(this, mWeatherData.getAppSettings().getRefreshDelay().getOptionLength_s() * 1000, true);


        ViewPager viewPager = (ViewPager) findViewById(R.id.weather_pager_view_pager);
        FragmentManager fm  = getSupportFragmentManager();
        mPagerAdapter = new WeatherPagerAdapter(fm);
        viewPager.setAdapter(mPagerAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        boolean itemsChanged = data.getBooleanExtra(SettingsActivity.EXTRA_ITEM_LIST_CHANGED, false);
        boolean refreshDelayChanged = data.getBooleanExtra(SettingsActivity.EXTRA_REFRESH_DELAY_CHANGED, false);

        if(itemsChanged) {
            mPagerAdapter.notifyDataSetChanged();
        }
        if(refreshDelayChanged){
            UpdateService.setServiceAlarm(this, mWeatherData.getAppSettings().getRefreshDelay().getOptionLength_s() * 1000, true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent;
        switch (item.getItemId()){
            case R.id.menu_item_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, REQ_DATA_SET_CHANGED);
                return true;
            case R.id.menu_item_refresh:
                intent = UpdateService.newIntent(this);
                startService(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

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

        WeatherPagerAdapter(FragmentManager fm) {
            super(fm);
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
