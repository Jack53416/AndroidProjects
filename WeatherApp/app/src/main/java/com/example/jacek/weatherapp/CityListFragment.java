package com.example.jacek.weatherapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;

import database.Condition;

public class CityListFragment extends Fragment {

    private RecyclerView mCityRecyclerView;
    private CityAdapter mCityAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_city_list, container, false);

        mCityRecyclerView = view.findViewById(R.id.city_recycler_view);

        mCityRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.activity_city_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.list_menu_item_add:

                return true;
            case R.id.list_menu_item_delete:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI() {
        WeatherData weatherData = WeatherData.getInstance(getContext());

        if(mCityAdapter == null) {
            mCityAdapter = new CityAdapter(weatherData.mConditionList);
            mCityRecyclerView.setAdapter(mCityAdapter);
        }
        else{
            mCityAdapter.notifyDataSetChanged();
        }
    }


    private class CityHolder extends RecyclerView.ViewHolder{

        private Condition mCity;

        public TextView mCityNameTextView;
        public CheckBox mCityCheckBox;
        public TextView mCityDescription;

        public CityHolder(View itemView) {
            super(itemView);

            mCityNameTextView = itemView.findViewById(R.id.list_item_city_name);
            mCityDescription = itemView.findViewById(R.id.list_item_city_description);
            mCityCheckBox = itemView.findViewById(R.id.list_item_city_mark);
        }

        public void bindCity(Condition city){
            mCity = city;
            mCityNameTextView.setText(mCity.cityName);
            mCityDescription.setText(String.format(Locale.US,"%f %f", mCity.longitude, mCity.latitude));
        }
    }

    private class CityAdapter extends RecyclerView.Adapter<CityHolder>{

        private List<Condition> mCities;

        public CityAdapter(List<Condition> cities){
            mCities = cities;
        }

        @Override
        public CityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_city, parent, false);

            return new CityHolder(view);
        }

        @Override
        public void onBindViewHolder(CityHolder holder, int position) {
            Condition condition = mCities.get(position);
            holder.bindCity(condition);
        }

        @Override
        public int getItemCount() {
            return mCities.size();
        }
    }
}
