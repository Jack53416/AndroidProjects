package com.example.jacek.astroweather;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.astrocalculator.AstroCalculator;

import java.util.ArrayList;
import java.util.List;

public class SettingsDialog extends DialogFragment {

    private AppSettings mAppSettings;
    private EditText mLatitudeValue;
    private EditText mLongitudeValue;
    private Spinner mLatitudeDirection;
    private Spinner mLongitudeDirection;
    private Spinner mRefreshDelaySpinner;
    OnSettingsConfirmedListener mListener;

    interface OnSettingsConfirmedListener{
         void onSettingsConfirmed();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            mListener = (OnSettingsConfirmedListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnSettingsConfirmedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_settings, null);

        mLatitudeValue = (EditText) v.findViewById(R.id.preferenceLatitude_value);
        mLongitudeValue = (EditText) v.findViewById(R.id.preferenceLongitude_value);
        mLatitudeDirection = (Spinner) v.findViewById(R.id.preferenceLatitude_direction);
        mLongitudeDirection = (Spinner) v.findViewById(R.id.preferenceLongitude_direction);
        mRefreshDelaySpinner = (Spinner) v.findViewById(R.id.preference_refreshRate_value);
        mAppSettings = AppSettings.get();
        setInitialValues();

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.editSettings)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button okButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(updateSettings())
                        {
                            if(mListener!=null)
                                mListener.onSettingsConfirmed();
                             dialog.dismiss();
                        }
                        else
                            Toast.makeText(getActivity(),"Invalid longitude or latitude", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return dialog;

    }

    private boolean updateSettings(){
        Double latitude = Double.parseDouble(String.valueOf(mLatitudeValue.getText()));
        Double longitude = Double.parseDouble(String.valueOf(mLongitudeValue.getText()));
        String latitudeDirection = (String) mLatitudeDirection.getSelectedItem();
        String longitudeDirection = (String) mLongitudeDirection.getSelectedItem();
        String refreshDelay = (String) mRefreshDelaySpinner.getSelectedItem();

        if(latitude > 90 || longitude > 180)
            return false;
        if(latitudeDirection.equals("S"))
            latitude = - latitude;
        if(longitudeDirection.equals("W"))
            longitude = - longitude;

        mAppSettings.mAstroCalculator.setLocation(new AstroCalculator.Location(latitude, longitude));
        mAppSettings.setRefreshDelayKey(refreshDelay);
        return true;
    }

    private void setInitialValues(){
        double latitude = mAppSettings.mAstroCalculator.getLocation().getLatitude();
        double longitude = mAppSettings.mAstroCalculator.getLocation().getLongitude();

        List<String> refreshDelayValues = new ArrayList<>(mAppSettings.mRefreshDelayOptions_s.keySet());

        mLongitudeValue.setText(String.valueOf(Math.abs(longitude)));
        mLatitudeValue.setText(String.valueOf(Math.abs(latitude)));

        if(latitude < 0){
            mLatitudeDirection.setSelection(1);
        }
        if(longitude < 0)
        {
            mLongitudeDirection.setSelection(1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                                                                R.layout.support_simple_spinner_dropdown_item,
                                                                refreshDelayValues);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mRefreshDelaySpinner.setAdapter(adapter);

        int currentOptionIndex = 0;
        String currentKey = mAppSettings.getRefreshDelayKey();
        for(int i = 0, size = refreshDelayValues.size(); i < size; i++){
            if(refreshDelayValues.get(i).equals(currentKey))
                currentOptionIndex = i;
        }

        mRefreshDelaySpinner.setSelection(currentOptionIndex);


    }
}
