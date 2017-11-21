package com.example.jacek.weatherapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import database.Condition;

public class WeatherFragment extends Fragment {

    public static final String ARG_CONDITION_WOEID = "Arg_woeid";
    private Condition mCondition;
    private ForecastListFragment mForecastListFragment;

    private TextView mCityName;
    private TextView mCityDescription;
    private TextView mTemperature;
    private TextView mWeatherDescription;
    private ImageView mWeatherPicture;

    private TextView mWindDirection;
    private ImageView mWindDirectionArrow;
    private TextView mWindSpeed;
    private TextView mWindChill;

    private TextView mPressure;
    private TextView mHumidity;
    private TextView mVisibility;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        int conditionWoeid = getArguments().getInt(ARG_CONDITION_WOEID);
        WeatherData weatherData = WeatherData.getInstance(getActivity());

        mCondition = weatherData.findConditionByWoeid(conditionWoeid);

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_weather_overview, container, false);
        wireControls(rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FragmentManager fm = getChildFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.weather_fragment_forecast_container);

        if(fragment == null){
            fragment =  ForecastListFragment.newInstance(mCondition.getCity().getWoeid());
            fm.beginTransaction()
                    .replace(R.id.weather_fragment_forecast_container, fragment)
                    .commit();
        }

        mForecastListFragment = (ForecastListFragment) fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUI();
    }

    public static WeatherFragment newInstance(final int conditionWoeid){

        WeatherFragment weatherFragment = new WeatherFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_CONDITION_WOEID ,conditionWoeid);
        weatherFragment.setArguments(args);

        return weatherFragment;
    }

    private void wireControls(View v){
        mCityName = v.findViewById(R.id.weather_fragment_overview_city_name);
        mCityDescription = v.findViewById(R.id.weather_fragment_overview_city_description);
        mTemperature = v.findViewById(R.id.weather_fragment_overview_temperature);
        mWeatherDescription = v.findViewById(R.id.weather_fragment_overview_weather_description);
        mWeatherPicture = v.findViewById(R.id.weather_fragment_overview_weather_picture);

        mWindDirection = v.findViewById(R.id.weather_fragment_wind_direction_value);
        mWindDirectionArrow = v.findViewById(R.id.weather_fragment_wind_direction_arrow_image);
        mWindChill = v.findViewById(R.id.weather_fagment_wind_chill_value);
        mWindSpeed = v.findViewById(R.id.weather_fagment_wind_speed_value);

        mPressure = v.findViewById(R.id.weather_fagment_pressure_value);
        mHumidity = v.findViewById(R.id.weather_fagment_humidity_value);
        mVisibility = v.findViewById(R.id.weather_fagment_visibility_value);

        refreshUI();


    }

    public void refreshUI(){
        mCondition = WeatherData.getInstance(getActivity()).
                        findConditionByWoeid(mCondition.getCity().getWoeid());

        if(mCondition == null)
            return;
        mCityName.setText(mCondition.getCity().getName());
        mCityDescription.setText(mCondition.getCity().getLocationDescription());
        mTemperature.setText(String.valueOf(mCondition.getTemperature() + "°C"));
        mWeatherDescription.setText(mCondition.getText());
        mWeatherPicture.setImageResource(Condition.getWeatherResource(mCondition.getCode()));

        mWindDirection.setText(String.valueOf(mCondition.getWindDirection()));
        mWindSpeed.setText(String.valueOf(mCondition.getWindSpeed()));
        mWindChill.setText(String.valueOf(mCondition.getWindChill()));
        mWindDirectionArrow.setRotation(mCondition.getWindDirection());

        mPressure.setText(String.valueOf(mCondition.getPressure()));
        mHumidity.setText(String.valueOf(mCondition.getHumidity()));
        mVisibility.setText(String.valueOf(mCondition.getVisibility()));

        if(mForecastListFragment != null)
            mForecastListFragment.updateUI();
    }
}
