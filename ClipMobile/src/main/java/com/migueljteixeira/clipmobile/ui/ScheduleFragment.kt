package com.migueljteixeira.clipmobile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.migueljteixeira.clipmobile.R
import com.migueljteixeira.clipmobile.adapters.ScheduleListViewAdapter
import com.migueljteixeira.clipmobile.adapters.ScheduleViewPagerAdapter
import com.migueljteixeira.clipmobile.entities.StudentScheduleClass

class ScheduleFragment : Fragment() {
    private var classes: List<StudentScheduleClass>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        classes = requireArguments().getParcelableArrayList(ScheduleViewPagerAdapter.SCHEDULE_CLASSES_TAG)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.list_view, container, false)
        val listView = view.findViewById<ListView>(R.id.list_view)
        val adapter = ScheduleListViewAdapter(requireContext())
        if (classes == null) adapter.add(ListViewItemEmpty()) else {
            for (c in classes!!) adapter.add(
                ListViewItem(
                    c.name, c.type, c.hourStart,
                    c.hourEnd, c.room
                )
            )
        }
        listView.adapter = adapter
        return view
    }

    class ListViewItemEmpty
    class ListViewItem(
        var name: String,
        var type: String,
        var hour_start: String,
        var hour_end: String,
        var room: String?
    )
}