package astroweather;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.jacek.weatherapp.R;

import java.util.ArrayList;
import java.util.List;

public class AstroWeatherActivity extends AppCompatActivity {
    private List<Fragment> mFragmentList = new ArrayList<>();

    private TextView mCurrentCoordinates;
    private AppSettings mAppSettings;

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
            ViewPager pager = (ViewPager) findViewById(R.id.astroweather_fragment_container);
            pager.setAdapter(pageAdapter);
        }
        mAppSettings = AppSettings.get();
        mCurrentCoordinates.setText(mAppSettings.getCurrentCoordinates());
        mTimerHandler.postDelayed(mTimerRunnable, mAppSettings.mInitialDelay_ms);

    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mTimerHandler.removeCallbacks(mTimerRunnable);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
    }
}
