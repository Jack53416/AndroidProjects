package com.example.jacek.weatherapp;

import android.support.v4.app.Fragment;

public class SettingsActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CityListFragment();
    }
}
