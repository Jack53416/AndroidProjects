package com.example.jacek.weatherapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import database.City;
import database.Condition;
import settings.AddCityDialog;


public class CityListFragment extends Fragment {

    private static final String DIALOG_ADD_CITY = "Dialog_add_city";
    private static final Integer REQUEST_CITY = 0;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CITY){
            Condition newCity = null;
            String cityName = data.getStringExtra(AddCityDialog.EXTRA_CITY_NAME);
            try {
                newCity = new FetchCityTask().execute(cityName).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            if(newCity != null){
                if(!Objects.equals(newCity.getCity().getName(), cityName))
                    Toast.makeText(getActivity(), "Error, Invalid City Name !", Toast.LENGTH_SHORT).show();
                else{
                    WeatherData.getInstance(getActivity()).insertCondition(newCity);
                    updateUI();
                }
            }

        }
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
                FragmentManager manager = getActivity().getSupportFragmentManager();
                AddCityDialog dialog = new AddCityDialog();
                dialog.setTargetFragment(CityListFragment.this, REQUEST_CITY);
                dialog.show(manager, DIALOG_ADD_CITY);
                return true;

            case R.id.list_menu_item_delete:
                List<City> cityList = mCityAdapter.getCities();
                WeatherData weatherData = WeatherData.getInstance(getActivity());
                int itemsDeleted = 0;
                for(City city : cityList){
                    if(city.isSelected()){
                        weatherData.deleteCondition(city.getWoeid());
                        itemsDeleted++;
                    }
                }
                if(itemsDeleted == 0){
                    Toast.makeText(getActivity(), "No items selected !", Toast.LENGTH_SHORT).show();
                }
                else {
                    updateUI();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI() {
        WeatherData weatherData = WeatherData.getInstance(getContext());

        if(mCityAdapter == null) {
            mCityAdapter = new CityAdapter(Condition.getCityList(weatherData.mConditionList));
            mCityRecyclerView.setAdapter(mCityAdapter);
        }
        else{
            mCityAdapter.setCities(Condition.getCityList(weatherData.mConditionList));
            mCityAdapter.notifyDataSetChanged();
        }
    }


    private class CityHolder extends RecyclerView.ViewHolder{

        private City mCity;

        public TextView mCityNameTextView;
        public CheckBox mCityCheckBox;
        public TextView mCityDescription;

        public CityHolder(View itemView) {
            super(itemView);

            mCityNameTextView = itemView.findViewById(R.id.list_item_city_name);
            mCityDescription = itemView.findViewById(R.id.list_item_city_description);
            mCityCheckBox = itemView.findViewById(R.id.list_item_city_mark);

            mCityCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    mCity.setSelected(isChecked);
                }
            });
        }

        public void bindCity(City city){
            mCity = city;
            mCityNameTextView.setText(mCity.getName());
            mCityDescription.setText(String.format(Locale.US,"%s (%.3f %.3f)", mCity.getCountry(),
                    mCity.getLongitude(), mCity.getLatitude()));
            mCityCheckBox.setChecked(mCity.isSelected());
        }
    }

    private class CityAdapter extends RecyclerView.Adapter<CityHolder>{

        private List<City> mCities;

        public CityAdapter(List<City> cities){
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
            City city = mCities.get(position);
            holder.bindCity(city);
        }

        @Override
        public int getItemCount() {
            return mCities.size();
        }

        public void setCities(List<City> cities) {
            mCities = cities;
        }

        public List<City> getCities() {
            return mCities;
        }
    }

    private class FetchCityTask extends AsyncTask<String, Void, Condition> {

        @Override
        protected Condition doInBackground(String... strings) {
            String cityName = strings[0];
            return new WeatherFetcher().fetchCity(cityName);
        }

        @Override
        protected  void onPostExecute(Condition conditionItem){

        }
    }
}
