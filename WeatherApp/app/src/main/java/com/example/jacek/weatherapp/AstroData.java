package com.example.jacek.weatherapp;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class AstroData {

    private enum astroDateConversionType{
        HOUR,
        DATE
    }

    public static final AstroCalculator.Location DEFAULT_LOCATION = new AstroCalculator.Location(0, 0);
    public static final String DEFAULT_TIME_ZONE = "Europe/Warsaw";

    private static final DecimalFormat mDecimalAngleFormat = new DecimalFormat("###.##°");
    private static final DecimalFormat mDecimalPercentageFormat = new DecimalFormat("###.##%");

    private String mSunrise;
    private String mSunset;
    private String mSunriseAzimuth;
    private String mSunsetAzimuth;
    private String mTwilightEvening;
    private String mTwilightMorning;
    private String mMoonrise;
    private String mMoonset;
    private String mNewMoonDate;
    private String mFullMoonDate;
    private String mMoonIllumination;



    public void updateAstronomicInformation(AstroCalculator astroCalculator){
        if(astroCalculator == null)
            return;

        AstroCalculator.SunInfo sunInfo = astroCalculator.getSunInfo();
        AstroCalculator.MoonInfo moonInfo = astroCalculator.getMoonInfo();

        mSunrise = astroDateToString(sunInfo.getSunrise(), astroDateConversionType.HOUR);
        mSunset = astroDateToString(sunInfo.getSunset(), astroDateConversionType.HOUR);
        mSunriseAzimuth = mDecimalAngleFormat.format(sunInfo.getAzimuthRise());
        mSunsetAzimuth = mDecimalAngleFormat.format(sunInfo.getAzimuthSet());
        mTwilightEvening = astroDateToString(sunInfo.getTwilightEvening(), astroDateConversionType.HOUR);
        mTwilightMorning = astroDateToString(sunInfo.getTwilightMorning(), astroDateConversionType.HOUR);

        mMoonrise = astroDateToString(moonInfo.getMoonrise(), astroDateConversionType.HOUR);
        mMoonset = astroDateToString(moonInfo.getMoonset(), astroDateConversionType.HOUR);
        mFullMoonDate = astroDateToString(moonInfo.getNextFullMoon(), astroDateConversionType.DATE);
        mNewMoonDate = astroDateToString(moonInfo.getNextNewMoon(), astroDateConversionType.DATE);
        mMoonIllumination = mDecimalPercentageFormat.format(moonInfo.getIllumination());
    }


    public static AstroDateTime getCurrentAstroDateTime(String timeZoneId){
        Calendar currentTime = Calendar.getInstance();

        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);

        int timeZoneOffset =  (int) TimeUnit.HOURS
                                        .convert(timeZone.getOffset(new Date().getTime()), TimeUnit.MILLISECONDS);

        return new AstroDateTime(currentTime.get(Calendar.YEAR),
                currentTime.get(Calendar.MONTH) + 1,
                currentTime.get(Calendar.DAY_OF_MONTH),
                currentTime.get(Calendar.HOUR_OF_DAY),
                currentTime.get(Calendar.MINUTE),
                currentTime.get(Calendar.SECOND),
                timeZoneOffset,
                false);
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

    public String getSunrise() {
        return mSunrise;
    }

    public String getSunset() {
        return mSunset;
    }

    public String getSunriseAzimuth() {
        return mSunriseAzimuth;
    }

    public String getSunsetAzimuth() {
        return mSunsetAzimuth;
    }

    public String getTwilightEvening() {
        return mTwilightEvening;
    }

    public String getTwilightMorning() {
        return mTwilightMorning;
    }

    public String getMoonrise() {
        return mMoonrise;
    }

    public String getMoonset() {
        return mMoonset;
    }

    public String getNewMoonDate() {
        return mNewMoonDate;
    }

    public String getFullMoonDate() {
        return mFullMoonDate;
    }

    public String getMoonIllumination() {
        return mMoonIllumination;
    }
}
