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

        forecast.code = getInt(getColumnIndex(ForecastTable.Cols.CODE));
        forecast.date = new Date(getLong(getColumnIndex(ForecastTable.Cols.DATE)));
        forecast.day = getString(getColumnIndex(ForecastTable.Cols.DAY));
        forecast.high = getDouble(getColumnIndex(ForecastTable.Cols.HIGH));
        forecast.low = getDouble(getColumnIndex(ForecastTable.Cols.LOW));
        forecast.text = getString(getColumnIndex(ForecastTable.Cols.TEXT));

        return forecast;
    }
}
