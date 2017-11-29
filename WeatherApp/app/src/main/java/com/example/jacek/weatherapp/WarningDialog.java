package com.example.jacek.weatherapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class WarningDialog extends DialogFragment {

    private static final String ARG_MESSAGE = "Arg_message";
    private static final String ARG_TITLE = "Arg_title";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        String message = getArguments().getString(ARG_MESSAGE);
        String title = getArguments().getString(ARG_TITLE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_warning, null);
        TextView alertText = v.findViewById(R.id.warning_dialog_message);
        TextView alertTitle = v.findViewById(R.id.warning_dialog_title);
        alertText.setText(message);
        alertTitle.setText(title);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                }).create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static WarningDialog newInstance(String title, String message){
        WarningDialog dialog = new WarningDialog();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_TITLE, title);
        dialog.setArguments(args);
        return dialog;
    }
}
