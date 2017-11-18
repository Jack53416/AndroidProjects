package database;

import android.content.ContentValues;

import com.example.jacek.weatherapp.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static database.WeatherDbSchema.*;

public class Condition {
    private final City mCity;
    private int mCode;
    private Date mDate = new Date();
    private double mTemperature;
    private String mText;
    private int mWindChill;
    private int mWindDirection;
    private double mWindSpeed;
    private int mHumidity;
    private double mPressure;
    private double mVisibility;

    private List<Forecast> mForecasts = new ArrayList<>();

    public Condition(City city){
        mCity = city;
        mCode = -1;
        mText = "Unknown";
    }


    public static List<City> getCityList(List<Condition> conditions){
        List<City> cities = new ArrayList<>();
        for(Condition condition : conditions){
            cities.add(condition.getCity());
        }
        return cities;
    }

    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();

        values.put(ConditionTable.Cols.CITY_WOEID, mCity.getWoeid());
        values.put(ConditionTable.Cols.CITY_NAME, mCity.getName());
        values.put(ConditionTable.Cols.CITY_LATITUDE, mCity.getLatitude());
        values.put(ConditionTable.Cols.CITY_LONGITUDE, mCity.getLongitude());
        values.put(ConditionTable.Cols.CITY_COUNTRY, mCity.getCountry());
        values.put(ConditionTable.Cols.CONDITION_CODE, mCode);
        values.put(ConditionTable.Cols.CONDITION_DATE, mDate.getTime());
        values.put(ConditionTable.Cols.CONDITION_TEMP, mTemperature);
        values.put(ConditionTable.Cols.CONDITION_TEXT, mText);
        values.put(ConditionTable.Cols.WIND_CHILL, mWindChill);
        values.put(ConditionTable.Cols.WIND_DIRECTION, mWindDirection);
        values.put(ConditionTable.Cols.WIND_SPEED, mWindSpeed);
        values.put(ConditionTable.Cols.ATM_HUMIDITY, mHumidity);
        values.put(ConditionTable.Cols.ATM_PRESSURE, mPressure);
        values.put(ConditionTable.Cols.ATM_VISIBILITY, mVisibility);
        return values;
    }

    public int getWeatherResource(){
        switch (mCode){
            case 0:
            case 2:
            case 23:
            case 24:
                return R.drawable.wind;
            case 1:
            case 3:
            case 4:
            case 37:
            case 47:
                return R.drawable.thunder;
            case 5:
                return R.drawable.rain_and_snow;
            case 6:
            case 35:
                return R.drawable.rain_and_sleet;
            case 7:
                return R.drawable.snow_and_sleet;
            case 8:
            case 9:
                return R.drawable.freezing_drizzle;
            case 10:
                return R.drawable.freezing_rain;
            case 11:
            case 12:
                return R.drawable.few_showers;
            case 13:
                return R.drawable.snow_flurries;
            case 14:
            case 16:
            case 41:
            case 43:
                return R.drawable.snow;
            case 15:
                return R.drawable.blowing_snow;
            case 17:
            case 18:
                return R.drawable.sleet;
            case 19:
                return R.drawable.dust;
            case 20:
            case 21:
                return R.drawable.fog;
            case 22:
                return R.drawable.dust;
            case 25:
                return R.drawable.cold;
            case 26:
                return R.drawable.cloudy;
            case 27:
                return R.drawable.mostly_cloudy_night;
            case 28:
            case 44:
                return R.drawable.mostly_cloudy_day;
            case 29:
                return R.drawable.partly_cloudy_night;
            case 30:
                return R.drawable.partly_cloudy_day;
            case 31:
            case 33:
                return R.drawable.clear_night;
            case 32:
            case 34:
                return R.drawable.sunny;
            case 36:
                return R.drawable.hot;
            case 38:
            case 39:
                return R.drawable.scattered_thunder;
            case 40:
                return R.drawable.scattered_showers;
            case 42:
            case 46:
                return R.drawable.snow_showers;
            case 45:
                return R.drawable.thundershower;
            default:
                return R.drawable.unknown;
        }
    }

    public City getCity() {
        return mCity;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public double getTemperature() {
        return mTemperature;
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public int getWindChill() {
        return mWindChill;
    }

    public void setWindChill(int windChill) {
        mWindChill = windChill;
    }

    public int getWindDirection() {
        return mWindDirection;
    }

    public void setWindDirection(int windDirection) {
        mWindDirection = windDirection;
    }

    public double getWindSpeed() {
        return mWindSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        mWindSpeed = windSpeed;
    }

    public int getHumidity() {
        return mHumidity;
    }

    public void setHumidity(int humidity) {
        mHumidity = humidity;
    }

    public double getPressure() {
        return mPressure;
    }

    public void setPressure(double pressure) {
        mPressure = pressure;
    }

    public double getVisibility() {
        return mVisibility;
    }

    public void setVisibility(double visibility) {
        mVisibility = visibility;
    }

    public List<Forecast> getForecasts() {
        return mForecasts;
    }

    public void setForecasts(List<Forecast> forecasts) {
        mForecasts = forecasts;
    }
}
