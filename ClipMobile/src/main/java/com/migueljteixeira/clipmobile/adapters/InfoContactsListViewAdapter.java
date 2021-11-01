package com.migueljteixeira.clipmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.migueljteixeira.clipmobile.R;
import com.migueljteixeira.clipmobile.ui.InfoContactsFragment;

public class InfoContactsListViewAdapter extends ArrayAdapter<Object> {
    private static final int VIEW_TYPE_ITEM_CONTACT_TITLE = 0;
    private static final int VIEW_TYPE_ITEM_CONTACT_INTERNAL = 1;
    private static final int VIEW_TYPE_ITEM_CONTACT_EXTERNAL = 2;
    private final Context mContext;

    public InfoContactsListViewAdapter(Context context) {
        super(context, 0);
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(getItem(position) instanceof InfoContactsFragment.ContactInternal)
            return VIEW_TYPE_ITEM_CONTACT_INTERNAL;

        else if(getItem(position) instanceof InfoContactsFragment.ContactExternal)
            return VIEW_TYPE_ITEM_CONTACT_EXTERNAL;

        return VIEW_TYPE_ITEM_CONTACT_TITLE;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();

            if(getItemViewType(position) == VIEW_TYPE_ITEM_CONTACT_INTERNAL) {
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.adapter_info_internal_contact, parent, false);

                viewHolder.name = (TextView) convertView.findViewById(R.id.contact_name);
                viewHolder.phone = (TextView) convertView.findViewById(R.id.contact_phone);
                viewHolder.schedule = (TextView) convertView.findViewById(R.id.contact_schedule);
            }

            else if(getItemViewType(position) == VIEW_TYPE_ITEM_CONTACT_EXTERNAL) {
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.adapter_info_external_contact, parent, false);

                viewHolder.name = (TextView) convertView.findViewById(R.id.contact_name);
                viewHolder.phone = (TextView) convertView.findViewById(R.id.contact_phone);
            }

            else {
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.adapter_info_contact_title, parent, false);

                viewHolder.title = (TextView) convertView.findViewById(R.id.contact_title);
            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(getItemViewType(position) == VIEW_TYPE_ITEM_CONTACT_INTERNAL) {
            InfoContactsFragment.ContactInternal item = (InfoContactsFragment.ContactInternal) getItem(position);
            viewHolder.name.setText(item.name);
            viewHolder.phone.setText(item.phone);
            viewHolder.schedule.setText(item.schedule);
        }

        else if(getItemViewType(position) == VIEW_TYPE_ITEM_CONTACT_EXTERNAL) {
            InfoContactsFragment.ContactExternal item = (InfoContactsFragment.ContactExternal) getItem(position);
            viewHolder.name.setText(item.name);
            viewHolder.phone.setText(item.phone);
        }

        else {
            InfoContactsFragment.ContactTitle item = (InfoContactsFragment.ContactTitle) getItem(position);
            viewHolder.title.setText(item.name);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView title, name, phone, schedule;
    }

}