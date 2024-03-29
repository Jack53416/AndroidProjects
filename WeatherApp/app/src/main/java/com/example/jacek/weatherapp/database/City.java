package com.example.jacek.weatherapp.database;

import java.util.List;
import java.util.Locale;

public class City {
    private int woeid;
    private String mName;
    private String mCountry;
    private double mLatitude;
    private double mLongitude;
    private String mTimeZone;
    private boolean mIsSelected = false;

    public static String getWoeidList(List<City> cities, char delimiter){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        for(int i = 0, size = cities.size() - 1; i < size; i++){
            stringBuilder.append(cities.get(i).woeid);
            stringBuilder.append(delimiter);
        }
        stringBuilder.append(cities.get(cities.size() - 1).woeid);
        stringBuilder.append(')');
        return stringBuilder.toString();
    }

    public String getLocationDescription(){
        char latitudeDirection = mLatitude > 0 ? 'N' : 'S';
        char longitudeDirection = mLongitude > 0 ? 'E' : 'W';

        return String.format(Locale.US, "%s (%.3f°%c %.3f°%c)",
                mCountry,
                Math.abs(mLatitude),
                latitudeDirection,
                Math.abs(mLongitude),
                longitudeDirection);
    }

    public int getWoeid() {
        return woeid;
    }

    public void setWoeid(int woeid) {
        this.woeid = woeid;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }
}
