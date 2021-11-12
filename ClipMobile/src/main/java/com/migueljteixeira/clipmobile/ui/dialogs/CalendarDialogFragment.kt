package com.migueljteixeira.clipmobile.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.migueljteixeira.clipmobile.R
import com.migueljteixeira.clipmobile.entities.StudentCalendar
import com.migueljteixeira.clipmobile.ui.CalendarFragment

class CalendarDialogFragment : DialogFragment() {
    private var appointment: StudentCalendar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide title
        setStyle(STYLE_NO_TITLE, 0)
        assert(arguments != null)
        appointment = requireArguments().getParcelable(CalendarFragment.APPOINTMENT_TAG)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_calendar, container, false)

        // Set appointment name
        (view.findViewById<View>(R.id.name) as TextView).text = appointment!!.name

        // Set appointment date
        (view.findViewById<View>(R.id.date) as TextView).text =
            "$APPOINTMENT_DATE${appointment!!.date}"

        // Set appointment hour
        (view.findViewById<View>(R.id.hour) as TextView).text =
            "$APPOINTMENT_HOUR${appointment!!.hour}"
        return view
    }

    companion object {
        private const val APPOINTMENT_DATE = "Data: "
        private const val APPOINTMENT_HOUR = "Hora: "
    }
}