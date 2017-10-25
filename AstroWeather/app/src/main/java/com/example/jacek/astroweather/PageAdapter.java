package com.example.jacek.astroweather;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

class PageAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList;

    PageAdapter(FragmentManager fm, List<Fragment> fragments){
        super(fm);
        this.mFragmentList = fragments;

    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
