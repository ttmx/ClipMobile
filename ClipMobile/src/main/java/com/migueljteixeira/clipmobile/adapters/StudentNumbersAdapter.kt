package com.migueljteixeira.clipmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.migueljteixeira.clipmobile.R
import com.migueljteixeira.clipmobile.entities.Student

class StudentNumbersAdapter(private val mContext: Context, private val students: List<Student>) :
    BaseExpandableListAdapter() {
    override fun getGroupCount(): Int {
        return students.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return students[groupPosition].getYears().size
    }

    override fun getGroup(groupPosition: Int): Any {
        return students[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return students[groupPosition].getYears()[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_group_student_numbers, parent, false)
            viewHolder = ViewHolder()
            viewHolder.name = convertView.findViewById(R.id.name)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        // Set row name
        viewHolder.name!!.text = students[groupPosition].number
        return convertView
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_child_student_numbers, parent, false)
            viewHolder = ViewHolder()
            viewHolder.name = convertView.findViewById(R.id.name)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        // Set row name
        viewHolder.name!!.text = students[groupPosition].getYears()[childPosition].year
        return convertView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    internal class ViewHolder {
        var name: TextView? = null
    }
}