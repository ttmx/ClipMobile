package com.migueljteixeira.clipmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.migueljteixeira.clipmobile.R
import com.migueljteixeira.clipmobile.entities.StudentClassDoc

class StudentClassesDocsAdapter(
    private val mContext: Context,
    private val categories: Array<String>,
    private val classDocs: List<StudentClassDoc>
) : BaseExpandableListAdapter() {
    override fun getGroupCount(): Int {
        return categories.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return classDocs.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return categories[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return classDocs[groupPosition]
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
        convertView: View,
        parent: ViewGroup
    ): View {
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
        viewHolder.name!!.text = categories[groupPosition]
        return convertView
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_child_student_classes_docs, parent, false)
            viewHolder = ViewHolder()
            viewHolder.name = convertView.findViewById(R.id.name)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        // Set row name
        viewHolder.name!!.text = classDocs[childPosition].name
        return convertView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    private class ViewHolder {
        var name: TextView? = null
    }
}