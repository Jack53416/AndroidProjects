package com.example.jacek.weatherapp;

import android.app.PendingIntent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.astrocalculator.AstroCalculator;

import java.text.DecimalFormat;
import java.util.Locale;

import database.Condition;
import database.Settings;

public class WeatherFragment extends Fragment {

    public static final String ARG_CONDITION_WOEID = "Arg_woeid";
    private static final String EXTRA_DETAILS_VISIBLE = "extra_details_visible";
    private static final DecimalFormat measurementFormat = new DecimalFormat("####.##");
    private static final DecimalFormat temperatureFormat = new DecimalFormat("##.#");

    private int mDetailsVisibility = View.INVISIBLE;
    private int mConditionWoeid;
    private Condition mCondition;
    private AstroData mAstroData;

    private FrameLayout mForecastListFragmentContainer;
    private ForecastListFragment mForecastListFragment;
    private ImageButton mMoreButton;
    private ScrollView mDetailsScrollView;

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


    private TextView mSunrise;
    private TextView mSunset;
    private TextView mSunriseAzimuth;
    private TextView mSunsetAzimuth;
    private TextView mTwilightEvening;
    private TextView mTwilightMorning;

    private TextView mMoonrise;
    private TextView mMoonset;
    private TextView mFullMoonDate;
    private TextView mNewMoonDate;
    private TextView mMoonIllumination;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        mConditionWoeid = getArguments().getInt(ARG_CONDITION_WOEID);

        WeatherData weatherData = WeatherData.getInstance(getActivity());

        if(savedInstanceState != null)
            mDetailsVisibility = savedInstanceState.getInt(EXTRA_DETAILS_VISIBLE, View.INVISIBLE);

        mCondition = weatherData.findConditionByWoeid(mConditionWoeid);
        mAstroData = new AstroData();

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mDetailsScrollView != null)
            outState.putInt(EXTRA_DETAILS_VISIBLE, mDetailsScrollView.getVisibility());
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
        createForecastListFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUI();
    }

    private void createForecastListFragment(){
        FragmentManager fm = getChildFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.weather_fragment_forecast_container);

        if(fragment == null){
            fragment =  ForecastListFragment.newInstance(mCondition.getCity().getWoeid());
            fm.beginTransaction()
                    .add(R.id.weather_fragment_forecast_container, fragment)
                    .commit();
        }

        mForecastListFragment = (ForecastListFragment) fragment;
    }
    public static WeatherFragment newInstance(final int conditionWoeid){

        WeatherFragment weatherFragment = new WeatherFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_CONDITION_WOEID ,conditionWoeid);
        weatherFragment.setArguments(args);

        return weatherFragment;
    }

    private void wireControls(View v){

        wireCityControls(v);
        wireWindControls(v);
        wireDetailsControls(v);
        wireSunControls(v);
        wireMoonControls(v);

        mDetailsScrollView = v.findViewById(R.id.weather_fragment_details_scroll_view);
        mForecastListFragmentContainer = v.findViewById(R.id.weather_fragment_forecast_container);

        if(mDetailsVisibility == View.VISIBLE)
            mMoreButton.callOnClick();
    }

    private void wireCityControls(View v){
        mCityName = v.findViewById(R.id.weather_fragment_overview_city_name);
        mCityDescription = v.findViewById(R.id.weather_fragment_overview_city_description);
        mTemperature = v.findViewById(R.id.weather_fragment_overview_temperature);
        mWeatherDescription = v.findViewById(R.id.weather_fragment_overview_weather_description);
        mWeatherPicture = v.findViewById(R.id.weather_fragment_overview_weather_picture);
        mMoreButton = v.findViewById(R.id.weather_fragment_more_button);

        mMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDetailsScrollView.getVisibility() == View.INVISIBLE){
                    mDetailsScrollView.setVisibility(View.VISIBLE);
                    mForecastListFragmentContainer.setVisibility(View.VISIBLE);
                    mMoreButton.setImageResource(R.drawable.ic_button_drop_up);
                }

                else {
                    mDetailsScrollView.setVisibility(View.INVISIBLE);
                    mForecastListFragmentContainer.setVisibility(View.INVISIBLE);
                    mMoreButton.setImageResource(R.drawable.ic_button_drop_down);
                }
            }
        });
    }

    private void wireWindControls(View v){
        mWindDirection = v.findViewById(R.id.weather_fragment_wind_direction_value);
        mWindDirectionArrow = v.findViewById(R.id.weather_fragment_wind_direction_arrow_image);
        mWindChill = v.findViewById(R.id.weather_fagment_wind_chill_value);
        mWindSpeed = v.findViewById(R.id.weather_fagment_wind_speed_value);
    }

    private void wireDetailsControls(View v){
        mPressure = v.findViewById(R.id.weather_fagment_pressure_value);
        mHumidity = v.findViewById(R.id.weather_fagment_humidity_value);
        mVisibility = v.findViewById(R.id.weather_fagment_visibility_value);

    }


    private void wireSunControls(View v){
        mSunrise = v.findViewById(R.id.weather_fragment_sun_panel_sunrise_value);
        mSunset = v.findViewById(R.id.weather_fragment_sun_panel_sunset_value);
        mTwilightEvening = v.findViewById(R.id.weather_fragment_sun_panel_twillightE_value);
        mTwilightMorning = v.findViewById(R.id.weather_fragment_sun_panel_twilightM_value);
        mSunriseAzimuth = v.findViewById(R.id.weather_fragment_sun_panel_azimuthE_value);
        mSunsetAzimuth = v.findViewById(R.id.weather_fragment_sun_panel_azimuthM_value);
    }

    private void wireMoonControls(View v){
        mMoonrise = v.findViewById(R.id.weather_fragment_moon_panel_moonrise_value);
        mMoonset = v.findViewById(R.id.weather_fragment_moon_panel_moonset_value);
        mNewMoonDate = v.findViewById(R.id.weather_fragment_moon_panel_new_moon_value);
        mFullMoonDate = v.findViewById(R.id.weather_fragment_moon_panel_full_moon_value);
        mMoonIllumination = v.findViewById(R.id.weather_fragment_moon_panel_ilumination_value);
    }



    public void refreshUI(){
        WeatherData weatherData = WeatherData.getInstance(getActivity());
        Settings.Units unit = weatherData.getAppSettings().getMeasurementUnit();
        mCondition = weatherData.findConditionByWoeid(mConditionWoeid);
        if(mCondition == null)
            return;

        updateCityInformation(unit);
        updateWindInformation(unit);
        updateDetailsInformation(unit);
        updateAstronomicInfo();

        if(mForecastListFragment != null)
            mForecastListFragment.updateUI(mCondition.getCity().getWoeid());
    }


    private void updateCityInformation(Settings.Units unit){
        mCityName.setText(mCondition.getCity().getName());
        mCityDescription.setText(mCondition.getCity().getLocationDescription());
        mTemperature.setText(String.format(Locale.US,
                "%s %s", temperatureFormat.format(mCondition.getTemperature(unit)), unit.getUnitName()));
        mWeatherDescription.setText(mCondition.getText());
        mWeatherPicture.setImageResource(Condition.getWeatherResource(mCondition.getCode()));
    }


    private void updateWindInformation(Settings.Units unit){
        mWindDirection.setText(mCondition.getWindDirectionDescription());
        mWindSpeed.setText(String.format(Locale.US,
                "%s %s", measurementFormat.format(mCondition.getWindSpeed(unit)) , unit.getSpeedUnit()));

        mWindChill.setText(String.format(Locale.US,
                "%s %s", temperatureFormat.format(mCondition.getWindChill(unit)), unit.getUnitName()));
        mWindDirectionArrow.setRotation(mCondition.getWindDirection());

    }

    private void updateDetailsInformation(Settings.Units unit) {
        mPressure.setText(String.format(Locale.US,
                "%s %s", measurementFormat.format(mCondition.getPressure(unit)), unit.getPressureUnit()));

        mHumidity.setText(String.format(Locale.US,
                "%d %%", mCondition.getHumidity()));
        mVisibility.setText(String.format(Locale.US,
                "%s %s", measurementFormat.format(mCondition.getVisibility(unit)), unit.getDistanceUnit()));

    }

    private void updateAstronomicInfo(){
        AstroCalculator astroCalculator = WeatherData.getInstance(getActivity()).getAstroCalculator();
        AstroCalculator.Location location =
                new AstroCalculator.Location(mCondition.getCity().getLatitude(), mCondition.getCity().getLongitude());
        astroCalculator.setLocation(location);
        astroCalculator.setDateTime(AstroData.getCurrentAstroDateTime(mCondition.getCity().getTimeZone()));
        mAstroData.updateAstronomicInformation(astroCalculator);

        mSunrise.setText(mAstroData.getSunrise());
        mSunriseAzimuth.setText(mAstroData.getSunriseAzimuth());
        mSunset.setText(mAstroData.getSunset());
        mSunsetAzimuth.setText(mAstroData.getSunsetAzimuth());
        mTwilightMorning.setText(mAstroData.getTwilightMorning());
        mTwilightEvening.setText(mAstroData.getTwilightEvening());
        mMoonrise.setText(mAstroData.getMoonrise());
        mMoonset.setText(mAstroData.getMoonset());
        mMoonIllumination.setText(mAstroData.getMoonIllumination());
        mNewMoonDate.setText(mAstroData.getNewMoonDate());
        mFullMoonDate.setText(mAstroData.getFullMoonDate());

    }
}
