package database;

import android.content.ContentValues;

import java.util.Date;

public class Forecast {
    private Date mDate = new Date();
    private int mCode;
    private String mDay;
    private double mHigh;
    private double mLow;
    private String mText;


    public ContentValues getContentValues(final int woeid) {
        ContentValues values = new ContentValues();

        values.put(WeatherDbSchema.ForecastTable.Cols.WOEID, woeid);
        values.put(WeatherDbSchema.ForecastTable.Cols.CODE, mCode);
        values.put(WeatherDbSchema.ForecastTable.Cols.DATE, mDate.getTime());
        values.put(WeatherDbSchema.ForecastTable.Cols.DAY, mDay);
        values.put(WeatherDbSchema.ForecastTable.Cols.HIGH, mHigh);
        values.put(WeatherDbSchema.ForecastTable.Cols.LOW, mLow);
        values.put(WeatherDbSchema.ForecastTable.Cols.TEXT, mText);

        return values;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int code) {
        mCode = code;
    }

    public String getDay() {
        return mDay;
    }

    public void setDay(String day) {
        mDay = day;
    }

    public double getHigh() {
        return mHigh;
    }

    public void setHigh(double high) {
        mHigh = high;
    }

    public double getLow() {
        return mLow;
    }

    public void setLow(double low) {
        mLow = low;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }
}