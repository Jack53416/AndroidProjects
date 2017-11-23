package astroweather;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

class AppSettings {
    private static AppSettings sAppSettings;
    private String mRefreshDelayKey;
    Map<String, Integer> mRefreshDelayOptions_s = new LinkedHashMap<>();
    final int mInitialDelay_ms = 100;
    AstroCalculator mAstroCalculator;
    private DecimalFormat coordinatesFormat = new DecimalFormat("##.####°");

    static AppSettings get(){
        if(sAppSettings == null)
            sAppSettings = new AppSettings( );
        return sAppSettings;
    }

    private AppSettings()
    {
        initDelayOptions();
        AstroCalculator.Location defaultLocation = new AstroCalculator.Location(51.7592, 19.4560);
        mAstroCalculator = new AstroCalculator(getCurrentAstroDateTime(), defaultLocation);
        List<String> refreshKeys = new ArrayList<>(mRefreshDelayOptions_s.keySet());
        mRefreshDelayKey = refreshKeys.get(0);
    }

    void setRefreshDelayKey(String key){
        if(mRefreshDelayOptions_s.containsKey(key))
            mRefreshDelayKey = key;
    }
    String getRefreshDelayKey(){
        return mRefreshDelayKey;
    }

    int getRefreshDelay_ms(){
        return mRefreshDelayOptions_s.get(mRefreshDelayKey) * 1000;
    }

    private void initDelayOptions(){
        mRefreshDelayOptions_s.put("30 seconds", 30);
        mRefreshDelayOptions_s.put("1 minute" , 60);
        mRefreshDelayOptions_s.put("5 minutes", 300);
        mRefreshDelayOptions_s.put("10 minutes", 600);
        mRefreshDelayOptions_s.put("30 minutes", 1800);
        mRefreshDelayOptions_s.put("1 hour", 3600);
        mRefreshDelayOptions_s.put("2 hours", 7200);
    }

    AstroDateTime getCurrentAstroDateTime(){
        Calendar currentTime = Calendar.getInstance();
        TimeZone timeZone = currentTime.getTimeZone();
        int timeZoneOffset =  (int) TimeUnit.HOURS.convert(timeZone.getRawOffset() /*+ timeZone.getDSTSavings()*/, TimeUnit.MILLISECONDS);
        return new AstroDateTime(currentTime.get(Calendar.YEAR),
                currentTime.get(Calendar.MONTH) + 1,
                currentTime.get(Calendar.DAY_OF_MONTH),
                currentTime.get(Calendar.HOUR_OF_DAY),
                currentTime.get(Calendar.MINUTE),
                currentTime.get(Calendar.SECOND),
                timeZoneOffset,
                false);
    }

    String getCurrentCoordinates(){
        AstroCalculator.Location currentLocation = mAstroCalculator.getLocation();
        String longitude = coordinatesFormat.format(Math.abs(currentLocation.getLongitude()));
        String latitude = coordinatesFormat.format(Math.abs(currentLocation.getLatitude()));

        if(currentLocation.getLatitude() > 0){
            latitude += " N, ";
        }
        else
            latitude += " S, ";

        if(currentLocation.getLongitude() > 0)
        {
            longitude += "E";
        }
        else
            longitude += "W";

        return latitude + longitude;
    }
}
