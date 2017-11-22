package com.example.jacek.weatherapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.concurrent.ExecutionException;

import database.City;
import database.Condition;
import settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQ_DATA_SET_CHANGED = 0;
    private WeatherData mWeatherData;

    private ViewPager mViewPager;
    private WeatherPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWeatherData = WeatherData.getInstance(getBaseContext());
        mWeatherData.mConditionList = mWeatherData.loadConditionsFromDatabase();
        mWeatherData.loadSettingsFromDatabase();
        if(mWeatherData.mConditionList.size() == 0){
            try{
                Condition condition =  new FetchCityTask().execute("Lodz").get();
                mWeatherData.insertCondition(condition);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            new FetchWeatherTask().execute();
        }

        wireControls();
    }

    public void waitForDebugger(){
        if(android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();
    }

    private void wireControls(){
        mViewPager = (ViewPager) findViewById(R.id.weather_pager_view_pager);
        FragmentManager fm  = getSupportFragmentManager();
        mPagerAdapter = new WeatherPagerAdapter(fm);
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        boolean itemsChanged = data.getBooleanExtra(SettingsActivity.EXTRA_ITEM_LIST_CHANGED, false);
        int deletedItemsCount = data.getIntExtra(SettingsActivity.EXTRA_DELETED_ITEMS, 0);
        if(itemsChanged) {
            mPagerAdapter.notifyChangeInPosition(deletedItemsCount);
            mViewPager.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, REQ_DATA_SET_CHANGED);
                return true;
            case R.id.menu_item_refresh:
                new FetchWeatherTask().execute();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        for(Fragment fragment:getSupportFragmentManager().getFragments()){
            if(fragment instanceof WeatherFragment)
                getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
    }

    private class FetchWeatherTask extends AsyncTask<Void, Void, List<Condition>>{

        @Override
        protected List<Condition> doInBackground(Void... voids) {
            waitForDebugger();
            return new WeatherFetcher().fetchWeather(Condition.getCityList(mWeatherData.mConditionList));
        }

        @Override
        protected void onPostExecute(List<Condition> items){
            if(items != null) {
                mWeatherData.mConditionList = items;
                mPagerAdapter.updateFragmentsUI();

            }
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

    private class WeatherPagerAdapter extends FragmentPagerAdapter{
        SparseArrayCompat<Fragment> mWeatherFragments = new SparseArrayCompat<>();
        private long baseId = 0;

        WeatherPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Condition condition = mWeatherData.mConditionList.get(position);
            return WeatherFragment.newInstance(condition.getCity().getWoeid());
        }
        @Override
        public int getCount() {
            return mWeatherData.mConditionList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            mWeatherFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
                mWeatherFragments.remove(position);
                //getSupportFragmentManager().beginTransaction().remove((Fragment) object).commitAllowingStateLoss();
                super.destroyItem(container, position, object);
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        public void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }

        public void updateFragmentsUI(){
            for(int i = 0, size = mWeatherFragments.size(); i < size; i++){
                int key = mWeatherFragments.keyAt(i);

                WeatherFragment fragment = (WeatherFragment) mWeatherFragments.get(key);
                if(fragment != null)
                    fragment.refreshUI();
            }
        }

    }


}
