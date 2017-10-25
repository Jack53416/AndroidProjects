package com.example.jacek.astroweather;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import java.text.DecimalFormat;
import java.util.Locale;

import static com.example.jacek.astroweather.WeatherFragment.FragmentType.MOON;
import static com.example.jacek.astroweather.WeatherFragment.FragmentType.SUN;

public class WeatherFragment extends Fragment {

    enum FragmentType{
        MOON,
        SUN
    }

    public static final String ARG_TYPE = "fragment_type";
    public static final String BUNDLE_TYPE = "com.example.jacek.astroweather.bundle_Type";

    private FragmentType mCurrentType;
    private DecimalFormat mDecimalPercentageFormat = new DecimalFormat("###.##%");
    private DecimalFormat mDecimalDaysFormat = new DecimalFormat("## days");
    private TextView mTwilightMorningValue;
    private TextView mTwilightEveningValue;
    private TextView mSunsetValue;
    private TextView mSunriseValue;

    private TextView mMoonRiseValue;
    private TextView mMoonsetValue;
    private TextView mMoonIlluminationValue;
    private TextView mMoonAgeValue;

    private void wireControls(View view){

        if(mCurrentType == SUN) {
            mTwilightMorningValue = (TextView) view.findViewById(R.id.twilightM_value);
            mTwilightEveningValue = (TextView) view.findViewById(R.id.twilightE_value);
            mSunsetValue = (TextView) view.findViewById(R.id.sunset_value);
            mSunriseValue = (TextView) view.findViewById(R.id.sunrise_value);
        }
        else if(mCurrentType == MOON){
            mMoonRiseValue = (TextView) view.findViewById(R.id.moonrise_value);
            mMoonsetValue = (TextView) view.findViewById(R.id.moonset_value);
            mMoonIlluminationValue = (TextView) view.findViewById(R.id.moonPhase_value);
            mMoonAgeValue = (TextView) view.findViewById(R.id.moonAge_value);
        }
    }

    private String astroDateToHourString(AstroDateTime astroDateTime){
        String result;

        if(astroDateTime != null)
            result = String.format(Locale.US, "%02d:%02d:%02d", astroDateTime.getHour(),
                                                            astroDateTime.getMinute(),
                                                            astroDateTime.getSecond());
        else
            result = "Undefined";
        return result;

    }
    public void updateAstronomicInformation(AstroCalculator astroCalculator){

        AstroCalculator.SunInfo sunInfo = astroCalculator.getSunInfo();
        AstroCalculator.MoonInfo moonInfo = astroCalculator.getMoonInfo();

        if(mCurrentType == SUN) {
            mTwilightMorningValue.setText(astroDateToHourString(sunInfo.getTwilightMorning()));
            mTwilightEveningValue.setText(astroDateToHourString(sunInfo.getTwilightEvening()));
            mSunriseValue.setText(astroDateToHourString(sunInfo.getSunrise()));
            mSunsetValue.setText(astroDateToHourString(sunInfo.getSunset()));
        }
        else if(mCurrentType == MOON)
        {
            mMoonRiseValue.setText(astroDateToHourString(moonInfo.getMoonrise()));
            mMoonsetValue.setText(astroDateToHourString(moonInfo.getMoonset()));
            mMoonIlluminationValue.setText(mDecimalPercentageFormat.format(moonInfo.getIllumination()));
            mMoonAgeValue.setText(mDecimalDaysFormat.format(moonInfo.getAge()));
        }
    }

    public static WeatherFragment newInstance(FragmentType fragmentType){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TYPE, fragmentType);
        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mCurrentType = (FragmentType) getArguments().getSerializable(ARG_TYPE);

        if(savedInstanceState != null)
        {
            mCurrentType = (FragmentType) savedInstanceState.getSerializable(BUNDLE_TYPE);
        }


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = null;
        if(mCurrentType == SUN)
            v = inflater.inflate(R.layout.fragment_sun, container, false);
        else if(mCurrentType == MOON)
            v = inflater.inflate(R.layout.fragment_moon, container, false);

        wireControls(v);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(BUNDLE_TYPE, mCurrentType);

    }
}
