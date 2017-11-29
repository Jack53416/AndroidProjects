package com.example.jacek.weatherapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import database.City;
import database.Condition;
import database.Forecast;

public class WeatherFetcher {
    private static final String TAG = "WEATHER_FETCHER";
    private static final String API_URL = "http://query.yahooapis.com/v1/public/yql";
    private static final String WEATHER_QUERY_TEMPLATE = "select wind, atmosphere, item.lat, item.long, item.condition, item.forecast from " +
                                    "weather.forecast(" + String.valueOf(WeatherData.CONDITION_LIMIT) +
                                    ") where u=@unit";
    private static final String CITY_QUERY_TEMPLATE = "select woeid,name,country,centroid,timezone from geo.places(1) where text=@cityName";


    private byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }

            int bytesRead;
            byte[] buffer = new byte[1024];
            while((bytesRead = in.read(buffer))>0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    private String getUrlString(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }

    List<Condition> fetchWeather(List<City> cities){

        List<Condition> conditions = null;
        if(cities.size() == 0)
            return null;

        try {
            String url = Uri.parse(API_URL)
                    .buildUpon()
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("unit", "f")
                    .appendQueryParameter("crossProduct","optimized")
                    .appendQueryParameter("q", WEATHER_QUERY_TEMPLATE + " and woeid in " + City.getWoeidList(cities, ','))
                    .build()
                    .toString();
            String jsonString = getUrlString(url);
            Log.i("FETCHER", jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            conditions = updateConditions(cities, jsonBody);
        }
        catch (IOException ioEx){
            Log.e("FETCHER", "Could not retrieve: ", ioEx);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return conditions;
    }

    public Condition fetchCity(String cityName){
        Condition conditionItem = null;

        try{
            String url = Uri.parse(API_URL)
                    .buildUpon()
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("cityName", cityName)
                    .appendQueryParameter("q", CITY_QUERY_TEMPLATE)
                    .build()
                    .toString();

            String jsonString = getUrlString(url);
            Log.i("FETCHER", jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            JSONObject jsonQuery = jsonBody.getJSONObject("query");

            if(jsonQuery.getInt("count") == 0)
                return null;

            JSONObject jsonResults = jsonQuery.getJSONObject("results");
            JSONObject jsonPlace = jsonResults.getJSONObject("place");

            City city = new City();

            city.setWoeid(jsonPlace.getInt("woeid"));
            city.setName(jsonPlace.getString("name"));
            city.setCountry(jsonPlace.getJSONObject("country").getString("content"));
            city.setLatitude(jsonPlace.getJSONObject("centroid").getDouble("latitude"));
            city.setLongitude(jsonPlace.getJSONObject("centroid").getDouble("longitude"));
            city.setTimeZone(jsonPlace.getJSONObject("timezone").getString("content"));
            conditionItem = new Condition(city);
        }catch (IOException ioEx){
            Log.e("FETCHER", "Could not retrieve: ", ioEx);
        }
        catch (JSONException ex){
            ex.printStackTrace();
        }

        return conditionItem;
    }

    private List<Condition> updateConditions(List<City> cities, JSONObject jsonBody) throws
        IOException, JSONException
    {
        List<Condition> conditionList = new ArrayList<>();
        Condition conditionItem;

        Object jsonChannelObject = jsonBody.getJSONObject("query").getJSONObject("results").get("channel");
        JSONArray jsonConditions;


        if(jsonChannelObject instanceof JSONArray) {
            jsonConditions = (JSONArray) jsonChannelObject;
            for(int i = 0, size = jsonConditions.length(); i < size; i++){
                conditionItem = parseCondition(jsonConditions.getJSONObject(i),
                                                         cities.get(i));
                conditionList.add(conditionItem);
            }
        }
        else{
            conditionItem = parseCondition((JSONObject) jsonChannelObject,
                                                        cities.get(0));
            conditionList.add(conditionItem);
        }
        return conditionList;
    }

    private Condition parseCondition(JSONObject jsonChannel, City city)
    throws JSONException
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm a", Locale.US);
        JSONObject jsonWind = jsonChannel.getJSONObject("wind");
        JSONObject jsonItem = jsonChannel.getJSONObject("item");
        JSONObject jsonCondition = jsonItem.getJSONObject("condition");
        JSONObject jsonAtmosphere = jsonChannel.getJSONObject("atmosphere");
        JSONArray jsonForecasts = jsonItem.getJSONArray("forecast");

        Condition conditionItem = new Condition(city);

        conditionItem.setCode(jsonCondition.getInt("code"));
        try {
            conditionItem.setDate(dateFormat.parse(jsonCondition.getString("date")));
        } catch (ParseException ex) {
            Log.e(TAG, "Could not parse forecast mDate: " + ex.getMessage());
        }
        conditionItem.setTemperature(jsonCondition.getDouble("temp"));
        conditionItem.setText(jsonCondition.getString("text"));
        conditionItem.setWindChill(jsonWind.getInt("chill"));
        conditionItem.setWindDirection(jsonWind.getInt("direction"));
        conditionItem.setWindSpeed(jsonWind.getDouble("speed"));
        conditionItem.setPressure(jsonAtmosphere.getDouble("pressure"));
        conditionItem.setHumidity(jsonAtmosphere.getInt("humidity"));
        conditionItem.setVisibility(jsonAtmosphere.getDouble("visibility"));

        conditionItem.setForecasts(parseForecasts(jsonForecasts));
        return conditionItem;
    }

    private List<Forecast> parseForecasts(JSONArray jsonForecasts) throws
            JSONException
    {
        List<Forecast> forecasts = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        JSONObject jsonForecast;

        for(int i = 0, size = jsonForecasts.length(); i < size; i++){
            jsonForecast = jsonForecasts.getJSONObject(i);
            Forecast forecastItem = new Forecast();

            forecastItem.setCode(jsonForecast.getInt("code"));
            try {
                forecastItem.setDate(dateFormat.parse(jsonForecast.getString("date")));
            }catch (ParseException ex){
                Log.e(TAG, "Could not parse forecast date: " + ex.getMessage());
            }

            forecastItem.setDay(jsonForecast.getString("day"));
            forecastItem.setHigh(jsonForecast.getDouble("high"));
            forecastItem.setLow(jsonForecast.getDouble("low"));
            forecastItem.setText(jsonForecast.getString("text"));

            forecasts.add(forecastItem);
        }
        return forecasts;
    }
}
