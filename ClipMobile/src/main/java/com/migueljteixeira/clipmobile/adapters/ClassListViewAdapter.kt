package com.migueljteixeira.clipmobile.adapters

import android.content.Context
import android.widget.ArrayAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.migueljteixeira.clipmobile.R
import android.widget.TextView
import com.migueljteixeira.clipmobile.ui.ClassesFragment

class ClassListViewAdapter(private val mContext: Context) : ArrayAdapter<Any?>(
    mContext, 0
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            convertView =
                LayoutInflater.from(mContext).inflate(R.layout.adapter_class, parent, false)
            viewHolder = ViewHolder()
            viewHolder.name = convertView.findViewById(R.id.class_name)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        val item = getItem(position) as ClassesFragment.ListViewItem?
        viewHolder.name!!.text = item!!.name
        return convertView!!
    }

    internal class ViewHolder {
        var name: TextView? = null
    }
}