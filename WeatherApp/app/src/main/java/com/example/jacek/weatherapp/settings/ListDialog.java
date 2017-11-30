package com.example.jacek.weatherapp.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.jacek.weatherapp.database.Settings;


public class ListDialog extends DialogFragment {

    enum DialogType{
        MEASUREMENT_UNITS,
        REFRESH_DELAYS
    }

    public static final String ARG_DIALOG_TYPE = "Arg_dialog_type";
    public static final String EXTRA_STRING_RESULT = "Extra_Result";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        DialogType dialogType = (DialogType) getArguments().getSerializable(ARG_DIALOG_TYPE);
        final String[] listItems;
        if(dialogType == DialogType.MEASUREMENT_UNITS)
            listItems = Settings.Units.getValues();
        else
            listItems = Settings.RefreshDelayOptions.getValues();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setItems(listItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendResult(Activity.RESULT_OK, listItems[i]);
                        dismiss();
                    }
                });
        return builder.create();
    }

    public static DialogFragment newInstance(DialogType dialogType){
        ListDialog listDialog = new ListDialog();

        Bundle args = new Bundle();
        args.putSerializable(ARG_DIALOG_TYPE, dialogType);
        listDialog.setArguments(args);

        return listDialog;
    }

    private void sendResult(int resultCode, String chosenOption){
        if(getTargetFragment() == null)
            return;
        Intent intent = new Intent();
        intent.putExtra(EXTRA_STRING_RESULT, chosenOption);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
