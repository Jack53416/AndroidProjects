package database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;

import static database.WeatherDbSchema.*;

public class ForecastCursorWrapper extends CursorWrapper {
    public ForecastCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Forecast getForecast(){
        Forecast forecast = new Forecast();

        forecast.setCode(getInt(getColumnIndex(ForecastTable.Cols.CODE)));
        forecast.setDate(new Date(getLong(getColumnIndex(ForecastTable.Cols.DATE))));
        forecast.setDay(getString(getColumnIndex(ForecastTable.Cols.DAY)));
        forecast.setHigh(getDouble(getColumnIndex(ForecastTable.Cols.HIGH)));
        forecast.setLow(getDouble(getColumnIndex(ForecastTable.Cols.LOW)));
        forecast.setText(getString(getColumnIndex(ForecastTable.Cols.TEXT)));

        return forecast;
    }
}
