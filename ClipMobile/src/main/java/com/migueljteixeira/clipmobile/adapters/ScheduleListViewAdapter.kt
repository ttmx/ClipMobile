package com.migueljteixeira.clipmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.migueljteixeira.clipmobile.R
import com.migueljteixeira.clipmobile.ui.ScheduleFragment

class ScheduleListViewAdapter(private val mContext: Context) : ArrayAdapter<Any?>(
    mContext, 0
) {
    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is ScheduleFragment.ListViewItem) VIEW_TYPE_ITEM else VIEW_TYPE_ITEM_EMPTY
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (getItemViewType(position) == VIEW_TYPE_ITEM_EMPTY) {
            convertView =
                LayoutInflater.from(context).inflate(R.layout.adapter_schedule_empty, parent, false)
            convertView.setOnClickListener(null)
            return convertView
        }
        if (convertView == null) {
            convertView =
                LayoutInflater.from(mContext).inflate(R.layout.adapter_schedule, parent, false)
            viewHolder = ViewHolder()
            viewHolder.name = convertView.findViewById(R.id.class_name)
            viewHolder.hour_start = convertView.findViewById(R.id.class_hour_start)
            viewHolder.hour_end = convertView.findViewById(R.id.class_hour_end)
            viewHolder.room = convertView.findViewById(R.id.class_room)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        val item = getItem(position) as ScheduleFragment.ListViewItem?
        viewHolder.name.text = "${item!!.name} (${item.type})"
        viewHolder.hour_start.text = item.hour_start
        viewHolder.hour_end.text = item.hour_end
        if (item.room == null) viewHolder.room.text = " - " else viewHolder.room.text =
            item.room
        return convertView!!
    }

    private class ViewHolder {
        lateinit var name: TextView
        lateinit var hour_start: TextView
        lateinit var hour_end: TextView
        lateinit var room: TextView
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_ITEM_EMPTY = 1
    }
}