package com.example.jacek.weatherapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import com.example.jacek.weatherapp.database.Condition;
import com.example.jacek.weatherapp.database.Forecast;
import com.example.jacek.weatherapp.database.Settings;
import com.example.jacek.weatherapp.services.WeatherData;

public class ForecastListFragment extends Fragment {

    private final static String ARG_WOEID = "Arg_woeid";

    private RecyclerView mForecastRecyclerView;
    private ForecastAdapter mForecastAdapter;
    private int mConditionWoeid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConditionWoeid = getArguments().getInt(ARG_WOEID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_forcast_list, container, false);

        mForecastRecyclerView = rootView.findViewById(R.id.fragment_forecast_list_recycler_view);

        mForecastRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL,
                false));
        updateUI(mConditionWoeid);

        return rootView;
    }

    public static ForecastListFragment newInstance(int conditionWoeid){

        ForecastListFragment forecastListFragment = new ForecastListFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_WOEID, conditionWoeid);

        forecastListFragment.setArguments(args);
        return forecastListFragment;
    }


    public void updateUI(int conditionWoeid)
    {
        Condition condition = WeatherData.getInstance(getActivity()).findConditionByWoeid(conditionWoeid);

        if(condition == null)
            return;
        if(mForecastAdapter == null){
            mForecastAdapter = new ForecastAdapter(condition.getForecasts());
            mForecastRecyclerView.setAdapter(mForecastAdapter);
        }
        else{
            mForecastAdapter.setForecastList(condition.getForecasts());
            mForecastAdapter.notifyDataSetChanged();
        }

    }


    private class ForecastHolder extends RecyclerView.ViewHolder {

        private Forecast mForecast;
        private SimpleDateFormat mDateFormat = new SimpleDateFormat("dd.MMM", Locale.US);
        private TextView mDayValue;
        private TextView mDateValue;
        private TextView mTemperatureValue;
        private ImageView mWeatherPicture;

        ForecastHolder(View itemView) {
            super(itemView);
            mDateValue = itemView.findViewById(R.id.list_item_forecast_date_value);
            mDayValue = itemView.findViewById(R.id.list_item_forecast_day_value);
            mTemperatureValue = itemView.findViewById(R.id.list_item_forecast_temperature_value);
            mWeatherPicture = itemView.findViewById(R.id.list_item_forecast_weather_picture);
        }

        void bindForecast(Forecast forecast){
            Settings.Units unit = WeatherData.getInstance(getActivity()).getAppSettings().getMeasurementUnit();

            mForecast = forecast;
            mDayValue.setText(mForecast.getDay());
            mDateValue.setText(mDateFormat.format(mForecast.getDate()));
            mTemperatureValue.setText(String.format(Locale.US,
                    "%.0f/%.0f%s", mForecast.getHigh(unit), mForecast.getLow(unit), unit.getUnitName()));
            mWeatherPicture.setImageResource(Condition.getWeatherResource(mForecast.getCode()));
        }
    }

    private class ForecastAdapter extends RecyclerView.Adapter<ForecastHolder>{

        private List<Forecast> mForecastList;

        ForecastAdapter(List<Forecast> forecasts){
            mForecastList = forecasts;
        }

        @Override
        public ForecastHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_forecast, parent, false);
            return new ForecastHolder(view);
        }

        @Override
        public void onBindViewHolder(ForecastHolder holder, int position) {
            holder.bindForecast(mForecastList.get(position));
        }

        @Override
        public int getItemCount() {
            return mForecastList.size();
        }

        void setForecastList(List<Forecast> forecastList) {
            mForecastList = forecastList;
        }
    }
}
