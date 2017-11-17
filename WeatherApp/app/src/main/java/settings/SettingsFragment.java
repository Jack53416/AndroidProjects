package settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jacek.weatherapp.R;
import com.example.jacek.weatherapp.WeatherData;

import database.Settings;

public class SettingsFragment extends Fragment {

    private static final String DIALOG_SET_UNIT = "Dialog_set_unit";
    private static final String DIALOG_SET_DELAY = "Dialog_set_delay";
    private static final int REQUEST_UNIT = 0;
    private static final int REQUEST_DELAY = 1;

    private TextView mUnitValue;
    private TextView mRefreshDelayValue;
    private Settings mAppSettings;
    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        mAppSettings = WeatherData.getInstance(getActivity()).getAppSettings();
        wireControls(rootView);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String dialogResult = data.getStringExtra(ListDialog.EXTRA_STRING_RESULT);

        switch (requestCode){
            case REQUEST_UNIT:
                mAppSettings.setMeasurementUnit(Settings.Units.getUnit(dialogResult));
                break;
            case REQUEST_DELAY:
                mAppSettings.setRefreshDelay(Settings.RefreshDelayOptions.getRefreshDelay(dialogResult));
                break;
        }
        refreshUI();
        WeatherData.getInstance(getContext()).updateSettings();
    }

    private void refreshUI(){
        mUnitValue.setText(mAppSettings.getMeasurementUnit().getUnitName());
        mRefreshDelayValue.setText(mAppSettings.getRefreshDelay().getOptionName());
    }

    private void wireControls(View v) {
        LinearLayout unitSettings = v.findViewById(R.id.unit_settings);
        LinearLayout refreshSettings = v.findViewById(R.id.refresh_delay_settings);
        mUnitValue = v.findViewById(R.id.unit_settings_value);
        mRefreshDelayValue = v.findViewById(R.id.refresh_delay_settings_value);

        unitSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = ListDialog.newInstance(ListDialog.DialogType.MEASUREMENT_UNITS);
                FragmentManager fm = getFragmentManager();
                dialog.setTargetFragment(SettingsFragment.this, REQUEST_UNIT);
                dialog.show(fm, DIALOG_SET_UNIT);
            }
        });

        refreshSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = ListDialog.newInstance(ListDialog.DialogType.REFRESH_DELAYS);
                FragmentManager fm = getFragmentManager();
                dialog.setTargetFragment(SettingsFragment.this, REQUEST_DELAY);
                dialog.show(fm, DIALOG_SET_DELAY);
            }
        });

        refreshUI();
    }
}
