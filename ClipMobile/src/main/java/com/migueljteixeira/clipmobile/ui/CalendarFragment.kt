package com.migueljteixeira.clipmobile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.migueljteixeira.clipmobile.R
import com.migueljteixeira.clipmobile.adapters.CalendarViewPagerAdapter
import com.migueljteixeira.clipmobile.entities.StudentCalendar
import com.migueljteixeira.clipmobile.settings.ClipSettings.getSemesterEndDate
import com.migueljteixeira.clipmobile.settings.ClipSettings.getSemesterStartDate
import com.migueljteixeira.clipmobile.ui.dialogs.CalendarDialogFragment
import com.squareup.timessquare.CalendarPickerView
import com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment(), OnDateSelectedListener {
    private val format = SimpleDateFormat("yyyy-MM-dd")
    private var calendar: List<StudentCalendar>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assert(arguments != null)
        calendar = requireArguments().getParcelableArrayList(CalendarViewPagerAdapter.CALENDAR_TAG)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        val calendar: CalendarPickerView = view.findViewById(R.id.calendar_view)

        // Set calendar background color
        val resources = requireActivity().resources
        calendar.setBackgroundColor(resources.getColor(R.color.main_background_color))

        // Set calendar start/end date and highlight dates
        calendar.init(
            getSemesterStartDate(requireActivity()),
            getSemesterEndDate(requireActivity())
        )
            .withHighlightedDates(datesToHighlight)
        calendar.setOnDateSelectedListener(this)
        return view
    }

    override fun onDateSelected(date: Date) {
        if (calendar == null) return
        for (appointment in calendar!!) {
            try {
                val appointmentDate = format.parse(appointment.date)
                if (appointmentDate == date) {
                    val bundle = Bundle()
                    bundle.putParcelable(APPOINTMENT_TAG, appointment)

                    // Create an instance of the dialog fragment and show it
                    val dialog: DialogFragment = CalendarDialogFragment()
                    dialog.arguments = bundle
                    dialog.show(parentFragmentManager, "CalendarDialogFragment")
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }

    override fun onDateUnselected(date: Date) {}
    private val datesToHighlight: List<Date>
        get() {
            val dates: MutableList<Date> = LinkedList()
            if (calendar == null) return dates
            for (appointment in calendar!!) {
                try {
                    val date = format.parse(appointment.date)
                    dates.add(date)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
            return dates
        }

    companion object {
        const val APPOINTMENT_TAG = "appointment_tag"
    }
}