package settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jacek.weatherapp.R;
import com.example.jacek.weatherapp.WeatherData;


public class AddCityDialog extends android.support.v4.app.DialogFragment {

    public static final String EXTRA_CITY_NAME = "EXTRA_CITY_NAME";

    private EditText mCityNameField;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_add_city, null);
        wireControls(v);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(v)
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
                        sendResult(Activity.RESULT_OK, mCityNameField.getText().toString());
                        dialog.dismiss();
                    }
                });
            }
        });
        return dialog;

    }

    private void wireControls(View v) {
        mCityNameField = v.findViewById(R.id.dialog_add_city_edit_city_text);
    }

    private void sendResult(int resultCode, String cityName){
        if(getTargetFragment() == null)
            return;
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CITY_NAME, cityName);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}
