package database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Condition {
    public int woeid;
    public String cityName;
    public double longitude;
    public double latitude;
    public int code;
    public Date date = new Date();
    public double temperature;
    public String text;
    public int windChill;
    public int windDirection;
    public double windSpeed;

    public List<Forecast> forecasts = new ArrayList<>();

}
