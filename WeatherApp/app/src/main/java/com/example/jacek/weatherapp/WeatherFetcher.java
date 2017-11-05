package com.example.jacek.weatherapp;

import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFetcher {
    public static String API_URL = "http://query.yahooapis.com/v1/public/yql";

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

    public void fetchWeather(){
        try {
            String url = Uri.parse(API_URL)
                    .buildUpon()
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("woeid", "505120")
                    .appendQueryParameter("crossProduct","optimized")
                    .appendQueryParameter("q","select wind,item.lat, item.long, item.condition, item.forecast from \n" +
                            "weather.forecast where woeid=@woeid and u=\"c\"")
                    .build()
                    .toString();
            String jsonString = getUrlString(url);
            Log.i("FETCHER", jsonString);
        }
        catch (IOException ioEx){
            Log.e("FETCHER", "Could not retrieve: ", ioEx);
        }
    }
}
