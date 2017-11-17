package database;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static database.WeatherDbSchema.*;

public class Condition {
    public final City mCity;
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
