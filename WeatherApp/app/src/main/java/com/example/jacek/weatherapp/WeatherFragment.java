package com.example.jacek.weatherapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import database.Condition;

public class WeatherFragment extends Fragment {

    public static final String ARG_CONDITION_WOEID = "Arg_woeid";
    private Condition mCondition;

    private TextView mCityName;
    private TextView mCityDescription;
    private TextView mTemperature;
    private TextView mWeatherDescription;
    private ImageView mWeatherPicture;

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

        refreshUI();


    }

    public void refreshUI(){
        mCondition = WeatherData.getInstance(getActivity()).
                        findConditionByWoeid(mCondition.getCity().getWoeid());

        mCityName.setText(mCondition.getCity().getName());
        mCityDescription.setText(mCondition.getCity().getLocationDescription());
        mTemperature.setText(String.valueOf(mCondition.getTemperature() + "°C"));
        mWeatherDescription.setText(mCondition.getText());
    }
}
