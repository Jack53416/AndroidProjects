package settings;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.example.jacek.weatherapp.R;

public class SettingsActivity extends AppCompatActivity
        implements CityListFragment.CityListFragmentListener,
        SettingsFragment.SettingsFragmentListener{

    public final static String EXTRA_DELETED_ITEMS = "extra_deleted_items";
    public final static String EXTRA_ITEM_LIST_CHANGED = "extra_item_list_changed";
    public final static String EXTRA_REFRESH_DELAY_CHANGED = "extra_refresh_delay_changed";
    private int mDeletedItemsCount = 0;
    private boolean mCityListChanged = false;
    private boolean mRefreshDelayChanged = false;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if(savedInstanceState != null){
            mDeletedItemsCount = savedInstanceState.getInt(EXTRA_DELETED_ITEMS, 0);
            mCityListChanged = savedInstanceState.getBoolean(EXTRA_ITEM_LIST_CHANGED, false);
            mRefreshDelayChanged = savedInstanceState.getBoolean(EXTRA_REFRESH_DELAY_CHANGED, false);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_REFRESH_DELAY_CHANGED, mRefreshDelayChanged);
        outState.putInt(EXTRA_DELETED_ITEMS, mDeletedItemsCount);
        outState.putBoolean(EXTRA_ITEM_LIST_CHANGED, mCityListChanged);
    }

    @Override
    public void onChangeCityList(int itemsDeleted) {
        if(itemsDeleted > mDeletedItemsCount)
            mDeletedItemsCount = itemsDeleted;
        mCityListChanged = true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DELETED_ITEMS, mDeletedItemsCount);
        intent.putExtra(EXTRA_ITEM_LIST_CHANGED, mCityListChanged);
        intent.putExtra(EXTRA_REFRESH_DELAY_CHANGED, mRefreshDelayChanged);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRefreshDelayChanged() {
        mRefreshDelayChanged = true;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0:
                    return new CityListFragment();
                case 1:
                    return SettingsFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getApplicationContext().getString(R.string.citySettingsTabTitle);
                case 1:
                    return getApplicationContext().getString(R.string.generalSettingsTabTitle);
            }
            return null;
        }
    }
}
