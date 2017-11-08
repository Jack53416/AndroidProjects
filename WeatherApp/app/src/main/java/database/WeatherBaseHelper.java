package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static database.WeatherDbSchema.*;

public class WeatherBaseHelper extends SQLiteOpenHelper {
    private static int VERSION = 1;
    private static final String DATABASE_NAME = "weatherBase.db";

    public WeatherBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery = "CREATE TABLE " + ConditionTable.NAME + "(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ConditionTable.Cols.WOEID + " INTEGER NOT NULL, " +
                ConditionTable.Cols.NAME + ", " +
                ConditionTable.Cols.CONDITION_CODE + ", " +
                ConditionTable.Cols.CONDITION_DATE + ", " +
                ConditionTable.Cols.CONDITION_TEMP + ", " +
                ConditionTable.Cols.CONDITION_TEXT + ", " +
                ConditionTable.Cols.LATITUDE + ", " +
                ConditionTable.Cols.LONGITUDE + ", " +
                ConditionTable.Cols.WIND_CHILL + ", " +
                ConditionTable.Cols.WIND_DIRECTION + ", " +
                ConditionTable.Cols.WIND_SPEED + ", " +
                "CONSTRAINT " + ConditionTable.Cols.WOEID + "_UNIQUE UNIQUE(" +
                ConditionTable.Cols.WOEID + ") " +
                ")";
        db.execSQL(sqlQuery);

        sqlQuery = "CREATE TABLE " + ForecastTable.NAME + "(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ForecastTable.Cols.WOEID + " INTEGER NOT NULL, " +
                ForecastTable.Cols.DATE + ", " +
                ForecastTable.Cols.CODE + ", " +
                ForecastTable.Cols.DAY + ", " +
                ForecastTable.Cols.HIGH + ", " +
                ForecastTable.Cols.LOW + ", " +
                ForecastTable.Cols.TEXT + ", " +
                "FOREIGN KEY (WOEID) REFERENCES " + ConditionTable.NAME +
                " (" + ConditionTable.Cols.WOEID + " ) ON DELETE CASCADE " +
                ")";

        db.execSQL(sqlQuery);

        sqlQuery = "CREATE TABLE " + SettingsTable.NAME + "(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SettingsTable.Cols.MES_UNIT + ", " +
                SettingsTable.Cols.REFRESH_DELAY + ", " +
                SettingsTable.Cols.HOME_CITY +
                ")";

        db.execSQL(sqlQuery);

        sqlQuery = "CREATE VIEW " + ForecastView.NAME + " AS " +
                "SELECT F." + ForecastTable.Cols.WOEID + ", " +
                "F." + ForecastTable.Cols.DATE + ", " +
                "F." + ForecastTable.Cols.CODE + ", " +
                "F." + ForecastTable.Cols.DAY + ", " +
                "F." + ForecastTable.Cols.HIGH + ", " +
                "F." + ForecastTable.Cols.LOW + ", " +
                "F." + ForecastTable.Cols.TEXT + " FROM " + ForecastTable.NAME +
                " AS F INNER JOIN " + ConditionTable.NAME + " AS C ON " +
                "F." + ForecastTable.Cols.WOEID + " = " + "C." + ConditionTable.Cols.WOEID;
                    db.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
