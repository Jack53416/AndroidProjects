package settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jacek.weatherapp.R;

public class SettingsFragment extends Fragment {

    private LinearLayout mUnitSettings;
    private LinearLayout mRefreshSettings;
    private TextView mUnitValue;
    private TextView mRefreshDelayValue;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        wireControls(rootView);
        return rootView;
    }

    private void wireControls(View v) {
        mUnitSettings = v.findViewById(R.id.unit_settings);
        mRefreshSettings = v.findViewById(R.id.refresh_delay_settings);
        mUnitValue = v.findViewById(R.id.unit_settings_value);
        mRefreshDelayValue = v.findViewById(R.id.refresh_delay_settings_value);
    }
}
