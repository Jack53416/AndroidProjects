package com.example.jacek.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import com.example.jacek.weatherapp.database.Condition;
import com.example.jacek.weatherapp.services.UpdateService;
import com.example.jacek.weatherapp.services.WeatherData;
import com.example.jacek.weatherapp.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {


    private static final int REQ_DATA_SET_CHANGED = 0;
    private static final String EXTRA_UPDATE_SERVICE_RUNNING = "Extra_update_service_running";
    private static final String WARNING_DIALOG_TAG = "WARNING_DIALOG";

    private WeatherData mWeatherData;
    private boolean isUpdateServiceRunning = false;
    private Toast mToast;

    private WeatherPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToast = new Toast(this);

        if(savedInstanceState != null)
            isUpdateServiceRunning = savedInstanceState.getBoolean(EXTRA_UPDATE_SERVICE_RUNNING, false);

        setContentView(R.layout.activity_main);
        mWeatherData = WeatherData.getInstance(getBaseContext());

        Handler responseHandler = new Handler();

        UpdateService.setmResponseHandler(responseHandler);
        UpdateService.setUpdateListener(new UpdateService.UpdateListener() {
            @Override
            public void onDataUpdate(List<Condition> updatedItems) {
                for(Condition condition : updatedItems)
                    mWeatherData.updateCondition(condition);
                mPagerAdapter.notifyDataSetChanged();
                showToastMessage("Refreshed!");
            }

            @Override
            public void onNoConnection() {
                showToastMessage("Update Failed!");
            }
        });

        if(!isUpdateServiceRunning) {
            startUpdateService();
            if(!isNetworkAvailable()) {
                String message = "No internet connection available, presented data may be outdated";
                String title = "Connection Problem";
                WarningDialog.newInstance(title, message).show(getSupportFragmentManager(), WARNING_DIALOG_TAG);
            }
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.weather_pager_view_pager);
        FragmentManager fm  = getSupportFragmentManager();
        mPagerAdapter = new WeatherPagerAdapter(fm);
        viewPager.setAdapter(mPagerAdapter);
    }

    private void showToastMessage(String message){
        mToast.cancel();
        mToast = Toast.makeText(this ,message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void startUpdateService(){
        int refreshDelay_s = mWeatherData.getAppSettings().getRefreshDelay().getOptionLength_s();
        UpdateService.setServiceAlarm(this, refreshDelay_s * 1000, true);
        isUpdateServiceRunning = true;
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
                showToastMessage("Refreshing ...");
                startService(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_UPDATE_SERVICE_RUNNING, isUpdateServiceRunning);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mPagerAdapter.notifyDataSetChanged();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    private class WeatherPagerAdapter extends FragmentStatePagerAdapter{

        WeatherPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Condition condition = mWeatherData.getConditionList().get(position);
            return WeatherFragment.newInstance(condition.getCity().getWoeid());
        }
        @Override
        public int getCount() {
            return mWeatherData.getConditionList().size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


    }


}
