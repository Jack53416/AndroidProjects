package astroweather;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;
import com.example.jacek.weatherapp.R;

import java.text.DecimalFormat;
import java.util.Locale;

import static astroweather.WeatherFragment.FragmentType.*;

public class WeatherFragment extends Fragment {

    enum FragmentType{
        MOON,
        SUN
    }

    private enum astroDateConversionType{
        HOUR,
        DATE
    }

    public static final String ARG_TYPE = "fragment_type";
    public static final String BUNDLE_TYPE = "com.example.jacek.astroweather.bundle_Type";

    private FragmentType mCurrentType;
    private DecimalFormat mDecimalPercentageFormat = new DecimalFormat("###.##%");
    private DecimalFormat mDecimalDaysFormat = new DecimalFormat("## days");
    private DecimalFormat mDecimalAngleFormat = new DecimalFormat("###.##°");
    private TextView mTwilightMorningValue;
    private TextView mTwilightEveningValue;
    private TextView mSunsetValue;
    private TextView mSunriseValue;
    private TextView mSunriseAzimuthValue;
    private TextView mSunsetAzimutValue;

    private TextView mMoonRiseValue;
    private TextView mMoonsetValue;
    private TextView mMoonIlluminationValue;
    private TextView mMoonAgeValue;
    private TextView mFullMoonValue;
    private TextView mNewMoonValue;

    private void wireControls(View view){

        if(mCurrentType == SUN) {
            mTwilightMorningValue = (TextView) view.findViewById(R.id.twilightM_value);
            mTwilightEveningValue = (TextView) view.findViewById(R.id.twilightE_value);
            mSunsetValue = (TextView) view.findViewById(R.id.sunset_value);
            mSunriseValue = (TextView) view.findViewById(R.id.sunrise_value);
            mSunriseAzimuthValue = (TextView) view.findViewById(R.id.azimuthSunrise_value);
            mSunsetAzimutValue = (TextView) view.findViewById(R.id.azimuthSunset_value);
        }
        else if(mCurrentType == MOON){
            mMoonRiseValue = (TextView) view.findViewById(R.id.moonrise_value);
            mMoonsetValue = (TextView) view.findViewById(R.id.moonset_value);
            mMoonIlluminationValue = (TextView) view.findViewById(R.id.moonPhase_value);
            mMoonAgeValue = (TextView) view.findViewById(R.id.moonAge_value);
            mFullMoonValue = (TextView) view.findViewById(R.id.fullmoon_value);
            mNewMoonValue = (TextView) view.findViewById(R.id.newmoon_value);
        }
    }


    private String astroDateToString(AstroDateTime astroDateTime, astroDateConversionType conversionType){
        String result;
        if(astroDateTime == null)
            return "Undefined";
        switch (conversionType){
            case DATE:
                result = String.format(Locale.US, "%02d.%02d.%04d",astroDateTime.getDay(),
                        astroDateTime.getMonth(),
                        astroDateTime.getYear());
                break;
            case HOUR:
                result = String.format(Locale.US, "%02d:%02d:%02d", astroDateTime.getHour(),
                        astroDateTime.getMinute(),
                        astroDateTime.getSecond());
                break;
            default:
                result = "Undefined";

        }
        return result;
    }

    public void updateAstronomicInformation(AstroCalculator astroCalculator){

        AstroCalculator.SunInfo sunInfo = astroCalculator.getSunInfo();
        AstroCalculator.MoonInfo moonInfo = astroCalculator.getMoonInfo();

        if(mCurrentType == SUN) {
            mTwilightMorningValue.setText(astroDateToString(sunInfo.getTwilightMorning(), astroDateConversionType.HOUR));
            mTwilightEveningValue.setText(astroDateToString(sunInfo.getTwilightEvening(), astroDateConversionType.HOUR));
            mSunriseValue.setText(astroDateToString(sunInfo.getSunrise(), astroDateConversionType.HOUR));
            mSunsetValue.setText(astroDateToString(sunInfo.getSunset(), astroDateConversionType.HOUR));
            mSunriseAzimuthValue.setText(mDecimalAngleFormat.format(sunInfo.getAzimuthRise()));
            mSunsetAzimutValue.setText(mDecimalAngleFormat.format(sunInfo.getAzimuthSet()));

        }
        else if(mCurrentType == MOON)
        {
            mMoonRiseValue.setText(astroDateToString(moonInfo.getMoonrise(), astroDateConversionType.HOUR));
            mMoonsetValue.setText(astroDateToString(moonInfo.getMoonset(), astroDateConversionType.HOUR));
            mMoonIlluminationValue.setText(mDecimalPercentageFormat.format(moonInfo.getIllumination()));
            mMoonAgeValue.setText(mDecimalDaysFormat.format(moonInfo.getAge()));
            mFullMoonValue.setText(astroDateToString(moonInfo.getNextFullMoon(), astroDateConversionType.DATE));
            mNewMoonValue.setText(astroDateToString(moonInfo.getNextNewMoon(), astroDateConversionType.DATE));
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
