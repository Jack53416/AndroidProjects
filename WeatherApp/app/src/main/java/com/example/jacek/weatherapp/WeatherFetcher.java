package com.example.jacek.weatherapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import java.util.ListIterator;
import java.util.Locale;

import database.Condition;
import database.Forecast;

public class WeatherFetcher {
    private static final String TAG = "WEATHER_FETCHER";
    private static final String API_URL = "http://query.yahooapis.com/v1/public/yql";
    private static final String QUERY_TEMPLATE = "select wind, item.lat, item.long, item.condition, item.forecast from " +
                                    "weather.forecast(" + String.valueOf(WeatherData.CONDITION_LIMIT) +
                                    ") where u=@unit";

    public byte[] getUrlBytes(String urlSpec) throws IOException{
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

            int bytesRead = 0;
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

    public String getUrlString(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }

    public List<Condition> fetchWeather(List<Condition> conditions){

        try {
            String url = Uri.parse(API_URL)
                    .buildUpon()
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("unit", "\"c\"")
                    .appendQueryParameter("crossProduct","optimized")
                    .appendQueryParameter("q",QUERY_TEMPLATE + " and woeid in " + getWoeidList(conditions, ','))
                    .build()
                    .toString();
            String jsonString = getUrlString(url);
            Log.i("FETCHER", jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseConditions(conditions, jsonBody);
        }
        catch (IOException ioEx){
            Log.e("FETCHER", "Could not retrieve: ", ioEx);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return conditions;
    }

    @NonNull
    private String getWoeidList(List<Condition> conditions, char delimeter){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        for(int i = 0, size = conditions.size() - 1; i < size; i++){
            stringBuilder.append(conditions.get(i).woeid);
            stringBuilder.append(delimeter);
        }
        stringBuilder.append(conditions.get(conditions.size() - 1).woeid);
        stringBuilder.append(')');
        return stringBuilder.toString();
    }

    private void parseConditions(List<Condition> conditions, JSONObject jsonBody) throws
        IOException, JSONException
    {
        Object jsonChannelObject = jsonBody.getJSONObject("query").getJSONObject("results").get("channel");
        JSONArray jsonConditions;
        ListIterator<Condition> iterator = conditions.listIterator();
        iterator.next();

        if(jsonChannelObject instanceof JSONArray) {
            jsonConditions = (JSONArray) jsonChannelObject;
            if (conditions.size() != jsonConditions.length()) {
                Log.e(TAG, "Could not update, received less conditions data than requested");
                return;
            }
            for(int i = 0, size = jsonConditions.length(); i < size; i++){
                Condition conditionItem = parseCondition(jsonConditions.getJSONObject(i),
                                                         conditions.get(i).woeid,
                                                         conditions.get(i).cityName);
                iterator.set(conditionItem);
                if(iterator.hasNext())
                    iterator.next();
            }
        }
        else{
            Condition parsedCondition = parseCondition((JSONObject) jsonChannelObject,
                                                        conditions.get(0).woeid,
                                                        conditions.get(0).cityName);
            iterator.set(parsedCondition);
        }
    }

    private Condition parseCondition(JSONObject jsonChannel, int woeid, String cityName)
    throws JSONException, IOException
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm a", Locale.US);
        JSONObject jsonWind = jsonChannel.getJSONObject("wind");
        JSONObject jsonItem = jsonChannel.getJSONObject("item");
        JSONObject jsonCondition = jsonItem.getJSONObject("condition");
        JSONArray jsonForecasts = jsonItem.getJSONArray("forecast");

        Condition conditionItem = new Condition(woeid);
        conditionItem.cityName = cityName;

        conditionItem.latitude = jsonItem.getDouble("lat");
        conditionItem.longitude = jsonItem.getDouble("long");
        conditionItem.code = jsonCondition.getInt("code");
        try {
            conditionItem.date = dateFormat.parse(jsonCondition.getString("date"));
        } catch (ParseException ex) {
            Log.e(TAG, "Could not parse forecast date: " + ex.getMessage());
        }
        conditionItem.temperature = jsonCondition.getDouble("temp");
        conditionItem.text = jsonCondition.getString("text");
        conditionItem.windChill = jsonWind.getInt("chill");
        conditionItem.windDirection = jsonWind.getInt("direction");
        conditionItem.windSpeed = jsonWind.getDouble("speed");

        parseForecasts(conditionItem.forecasts, jsonForecasts);
        return conditionItem;
    }

    private void parseForecasts(List<Forecast> forecasts, JSONArray jsonForecasts) throws
        IOException, JSONException
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        JSONObject jsonForecast;

        for(int i = 0, size = jsonForecasts.length(); i < size; i++){
            jsonForecast = jsonForecasts.getJSONObject(i);
            Forecast forecastItem = new Forecast();

            forecastItem.code = jsonForecast.getInt("code");
            try {
                forecastItem.date = dateFormat.parse(jsonForecast.getString("date"));
            }catch (ParseException ex){
                Log.e(TAG, "Could not parse forecast date: " + ex.getMessage());
            }

            forecastItem.day = jsonForecast.getString("day");
            forecastItem.high = jsonForecast.getDouble("high");
            forecastItem.low  = jsonForecast.getDouble("low");
            forecastItem.text = jsonForecast.getString("text");

            forecasts.add(forecastItem);
        }
    }
}
