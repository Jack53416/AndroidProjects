package com.example.jacek.astroweather;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AstroWeatherActivity extends AppCompatActivity implements SettingsDialog.OnSettingsConfirmedListener {
    private static final String DIALOG_SETTINGS = "DialogSettings";
    private List<Fragment> mFragmentList = new ArrayList<>();

    private TextView mCurrentCoordinates;
    private AppSettings mAppSettings;
    private Toast mToast;

    private Handler mTimerHandler = new Handler();
    private Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run() {
            mAppSettings.mAstroCalculator.setDateTime(mAppSettings.getCurrentAstroDateTime());

            for(Fragment fragment : mFragmentList) {
                WeatherFragment weatherFragment = (WeatherFragment) fragment;
                weatherFragment.updateAstronomicInformation(mAppSettings.mAstroCalculator);
            }
            mTimerHandler.postDelayed(this, mAppSettings.getRefreshDelay_ms());
        }
    };


    private void instantiateFragments(){
        Fragment sunFragment = WeatherFragment.newInstance(WeatherFragment.FragmentType.SUN);
        Fragment moonFragment = WeatherFragment.newInstance(WeatherFragment.FragmentType.MOON);
        if(isScreenXLarge()){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.sunFragmentContainer, sunFragment)
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.moonFragmentContainer, moonFragment)
                    .commit();
        }
        mFragmentList.add(sunFragment);
        mFragmentList.add(moonFragment);
    }

    private boolean isScreenXLarge(){
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_astro_weather);
        FragmentManager fm = getSupportFragmentManager();

        mCurrentCoordinates = (TextView) findViewById(R.id.textViewCoordinates);

        if(savedInstanceState != null){
            mFragmentList = getSupportFragmentManager().getFragments();
        }
        else{
            instantiateFragments();
        }

        if(!isScreenXLarge()) {
            PageAdapter pageAdapter = new PageAdapter(fm, mFragmentList);
            ViewPager pager = (ViewPager) findViewById(R.id.fragmentContainer);
            pager.setAdapter(pageAdapter);
        }
        mAppSettings = AppSettings.get();
        mCurrentCoordinates.setText(mAppSettings.getCurrentCoordinates());
        mTimerHandler.postDelayed(mTimerRunnable, mAppSettings.mInitialDelay_ms);


       /* F
       ragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if(fragment == null){
            fragment = WeatherFragment.newInstance(WeatherFragment.FragmentType.MOON);
            fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }*/

    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

       /* for(int i = 0, size = mPageAdapter.getCount(); i < size; i++)
            getSupportFragmentManager().putFragment(savedInstanceState, Integer.toString(i), mPageAdapter.getItem(i));
*/

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mTimerHandler.removeCallbacks(mTimerRunnable);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_astro_weather, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_settings:
                android.app.FragmentManager manager = getFragmentManager();
                SettingsDialog dialog = new SettingsDialog();
                dialog.show(manager, DIALOG_SETTINGS);
                return true;
            case R.id.menu_item_refresh:
                if(mToast == null)
                    mToast = Toast.makeText(this, getResources().getString(R.string.refreshToast),Toast.LENGTH_SHORT);
                if(!mToast.getView().isShown())
                {
                    mTimerRunnable.run();
                    mToast.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onSettingsConfirmed() {
        mCurrentCoordinates.setText(mAppSettings.getCurrentCoordinates());
        mTimerHandler.removeCallbacks(mTimerRunnable);
        mTimerHandler.post(mTimerRunnable);
    }
}
