package com.migueljteixeira.clipmobile.adapters

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.entities.StudentScheduleClass
import com.migueljteixeira.clipmobile.ui.ScheduleFragment
import java.util.*

class ScheduleViewPagerAdapter(
    fm: FragmentManager,
    private val tabNames: Array<String>,
    private val student: Student
) : FragmentPagerAdapter(
    fm
) {
    override fun getPageTitle(position: Int): CharSequence {
        return tabNames[position]
    }

    override fun getItem(position: Int): Fragment {
        val classes: List<StudentScheduleClass>? = student.getScheduleClasses()[position + 2]
        val fragment: Fragment = ScheduleFragment()
        fragment.arguments = getBundle(classes)
        return fragment
    }

    override fun getCount(): Int {
        return tabNames.size
    }

    private fun getBundle(classes: List<StudentScheduleClass>?): Bundle {
        val bundle = Bundle()
        if (classes != null) {
            // LinkedList to ArrayList 'conversion'
            val list = ArrayList(classes)
            bundle.putParcelableArrayList(SCHEDULE_CLASSES_TAG, ArrayList<Parcelable>(list))
        }

        return bundle
    }

    companion object {
        const val SCHEDULE_CLASSES_TAG = "schedule_classes_tag"
    }
}