package database;

import android.database.Cursor;
import android.database.CursorWrapper;

import static database.WeatherDbSchema.*;


public class SettingsCursorWrapper extends CursorWrapper {
    public SettingsCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Settings getSettings(){
        Settings settings = new Settings();
        Settings.Units unit = Settings.Units.
                getUnit(getString(getColumnIndex(SettingsTable.Cols.MES_UNIT)));
        settings.setMeasurementUnit(unit);

        Settings.RefreshDelayOptions delay = Settings.RefreshDelayOptions
                .getRefreshDelay(getString(getColumnIndex(SettingsTable.Cols.REFRESH_DELAY)));

        settings.setRefreshDelay(delay);

        return settings;
    }
}
