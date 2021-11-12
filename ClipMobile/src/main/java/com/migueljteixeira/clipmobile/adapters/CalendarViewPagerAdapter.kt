package com.migueljteixeira.clipmobile.adapters

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.entities.StudentCalendar
import com.migueljteixeira.clipmobile.ui.CalendarFragment
import java.util.*

class CalendarViewPagerAdapter(
    fm: FragmentManager?,
    private val tabNames: Array<String>,
    private val student: Student
) : FragmentPagerAdapter(
    fm!!
) {
    override fun getPageTitle(position: Int): CharSequence? {
        return tabNames[position]
    }

    override fun getItem(position: Int): Fragment {
        val calendar: List<StudentCalendar> = student.getStudentCalendar()[position == 1]!!
        val fragment: Fragment = CalendarFragment()
        fragment.arguments = getBundle(calendar)
        return fragment
    }

    override fun getCount(): Int {
        return tabNames.size
    }

    private fun getBundle(calendar: List<StudentCalendar>?): Bundle {
        val bundle = Bundle()
        if (calendar != null) {
            // LinkedList to ArrayList 'conversion'
            val list = ArrayList(calendar)
            bundle.putParcelableArrayList(CALENDAR_TAG, ArrayList<Parcelable>(list))
        }
        return bundle
    }

    companion object {
        const val CALENDAR_TAG = "calendar_tag"
    }
}