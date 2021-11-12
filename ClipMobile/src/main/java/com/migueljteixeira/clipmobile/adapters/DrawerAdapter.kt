package com.migueljteixeira.clipmobile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.migueljteixeira.clipmobile.R
import com.migueljteixeira.clipmobile.ui.NavDrawerActivity.*

class DrawerAdapter(private val mContext: Context) : ArrayAdapter<Any?>(
    mContext, 0
) {
    override fun getItemViewType(position: Int): Int {
        if (getItem(position) is DrawerTitle) return VIEW_TYPE_TITLE else if (getItem(position) is DrawerDivider) return VIEW_TYPE_DIVIDER
        return VIEW_TYPE_ITEM
    }

    override fun getViewTypeCount(): Int {
        return 3
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (getItemViewType(position) == VIEW_TYPE_DIVIDER) {
            convertView =
                LayoutInflater.from(context).inflate(R.layout.drawer_divider, parent, false)
            convertView.setOnClickListener(null)
            return convertView
        }
        if (convertView == null) {
            viewHolder = ViewHolder()
            if (getItemViewType(position) == VIEW_TYPE_TITLE) {
                convertView =
                    LayoutInflater.from(context).inflate(R.layout.drawer_title, parent, false)
                convertView.setOnClickListener(null)
            } else {
                convertView =
                    LayoutInflater.from(mContext).inflate(R.layout.drawer_item, parent, false)
                viewHolder.icon = convertView.findViewById(R.id.icon)
            }
            viewHolder.name = convertView.findViewById(R.id.name)
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        val item = getItem(position) as DrawerItem?
        viewHolder.name!!.text = item!!.mTitle
        if (getItemViewType(position) == VIEW_TYPE_ITEM) viewHolder.icon!!.setImageResource(
            item.mIconRes
        )
        return convertView!!
    }

    internal class ViewHolder {
        var name: TextView? = null
        var icon: ImageView? = null
    }

    companion object {
        private const val VIEW_TYPE_TITLE = 0
        private const val VIEW_TYPE_DIVIDER = 1
        private const val VIEW_TYPE_ITEM = 2
    }
}