package com.migueljteixeira.clipmobile.ui.dialogs;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.migueljteixeira.clipmobile.R;
import com.migueljteixeira.clipmobile.entities.StudentCalendar;
import com.migueljteixeira.clipmobile.ui.CalendarFragment;

public class CalendarDialogFragment extends DialogFragment {
    private static final String APPOINTMENT_DATE = "Data: ";
    private static final String APPOINTMENT_HOUR = "Hora: ";
    private StudentCalendar appointment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title
        setStyle(STYLE_NO_TITLE, 0);

        appointment = getArguments().getParcelable(CalendarFragment.APPOINTMENT_TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_calendar, container, false);

        // Set appointment name
        ((TextView) view.findViewById(R.id.name)).setText(appointment.getName());

        // Set appointment date
        ((TextView) view.findViewById(R.id.date)).setText(APPOINTMENT_DATE + appointment.getDate());

        // Set appointment hour
        ((TextView) view.findViewById(R.id.hour)).setText(APPOINTMENT_HOUR + appointment.getHour());

        return view;
    }

}