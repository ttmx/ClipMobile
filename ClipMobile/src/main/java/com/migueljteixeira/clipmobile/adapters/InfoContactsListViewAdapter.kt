package com.migueljteixeira.clipmobile.adapters

import android.content.Context
import android.widget.ArrayAdapter
import com.migueljteixeira.clipmobile.ui.InfoContactsFragment.ContactInternal
import com.migueljteixeira.clipmobile.adapters.InfoContactsListViewAdapter
import com.migueljteixeira.clipmobile.ui.InfoContactsFragment.ContactExternal
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.migueljteixeira.clipmobile.R
import android.widget.TextView
import com.migueljteixeira.clipmobile.databinding.AdapterInfoContactTitleBinding
import com.migueljteixeira.clipmobile.databinding.AdapterInfoExternalContactBinding
import com.migueljteixeira.clipmobile.databinding.AdapterInfoInternalContactBinding
import com.migueljteixeira.clipmobile.ui.InfoContactsFragment.ContactTitle

class InfoContactsListViewAdapter(private val mContext: Context) : ArrayAdapter<Any?>(
    mContext, 0
) {
    override fun getItemViewType(position: Int): Int {
        if (getItem(position) is ContactInternal) return VIEW_TYPE_ITEM_CONTACT_INTERNAL else if (getItem(
                position
            ) is ContactExternal
        ) return VIEW_TYPE_ITEM_CONTACT_EXTERNAL
        return VIEW_TYPE_ITEM_CONTACT_TITLE
    }

    override fun getViewTypeCount(): Int {
        return 3
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            viewHolder = ViewHolder()
            when(getItemViewType(position)) {
                VIEW_TYPE_ITEM_CONTACT_INTERNAL -> {
                    val binding = AdapterInfoInternalContactBinding.inflate(LayoutInflater.from(mContext))
                    convertView = binding.root
                    viewHolder.name = binding.contactName
                    viewHolder.phone = binding.contactPhone
                    viewHolder.schedule = binding.contactSchedule
                }
                VIEW_TYPE_ITEM_CONTACT_EXTERNAL -> {
                    val binding = AdapterInfoExternalContactBinding.inflate(LayoutInflater.from(mContext))
                    convertView = binding.root
                    viewHolder.name = binding.contactName
                    viewHolder.phone = binding.contactPhone
                }
                else -> {
                    val binding = AdapterInfoContactTitleBinding.inflate(LayoutInflater.from(mContext))
                    convertView = binding.root
                    viewHolder.title = binding.contactTitle
                }
            }
            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        when(getItemViewType(position)) {
            VIEW_TYPE_ITEM_CONTACT_INTERNAL -> {
                val item = getItem(position) as ContactInternal?
                viewHolder.name!!.text = item!!.name
                viewHolder.phone!!.text = item.phone
                viewHolder.schedule!!.text = item.schedule
            }
            VIEW_TYPE_ITEM_CONTACT_EXTERNAL -> {
                val item = getItem(position) as ContactExternal?
                viewHolder.name!!.text = item!!.name
                viewHolder.phone!!.text = item.phone
            }
            else -> {
                val item = getItem(position) as ContactTitle?
                viewHolder.title!!.text = item!!.name
            }
        }
        return convertView
    }

    private class ViewHolder {
        var title: TextView? = null
        var name: TextView? = null
        var phone: TextView? = null
        var schedule: TextView? = null
    }

    companion object {
        private const val VIEW_TYPE_ITEM_CONTACT_TITLE = 0
        private const val VIEW_TYPE_ITEM_CONTACT_INTERNAL = 1
        private const val VIEW_TYPE_ITEM_CONTACT_EXTERNAL = 2
    }
}