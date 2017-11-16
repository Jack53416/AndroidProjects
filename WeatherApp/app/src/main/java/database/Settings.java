package database;

public class Settings {
    private char mMeasurementUnit;
    private String mRefreshDelay;

    public char getMeasurementUnit() {
        return mMeasurementUnit;
    }

    public void setMeasurementUnit(char measurementUnit) {
        mMeasurementUnit = measurementUnit;
    }

    public String getRefreshDelay() {
        return mRefreshDelay;
    }

    public void setRefreshDelay(String refreshDelay) {
        mRefreshDelay = refreshDelay;
    }
}
