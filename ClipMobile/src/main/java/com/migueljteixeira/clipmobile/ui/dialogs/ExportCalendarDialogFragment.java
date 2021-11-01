package com.migueljteixeira.clipmobile.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.migueljteixeira.clipmobile.util.StudentTools;

public class ExportCalendarDialogFragment extends DialogFragment {
    public static final String CALENDAR_ID = "calendar_id";
    public static final String CALENDAR_NAME = "calendar_name";
    private long[] calendarIds;
    private String[] calendarNames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        calendarIds = getArguments().getLongArray(CALENDAR_ID);
        calendarNames = getArguments().getStringArray(CALENDAR_NAME);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
                .setTitle("Exportar para")
                .setItems(calendarNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        StudentTools.exportCalendar(getActivity(), calendarIds[which]);
                    }
                }).create();
    }

}