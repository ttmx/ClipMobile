package com.migueljteixeira.clipmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.migueljteixeira.clipmobile.R;
import com.migueljteixeira.clipmobile.ui.ScheduleFragment;

public class ScheduleListViewAdapter extends ArrayAdapter<Object> {
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_ITEM_EMPTY = 1;
    private final Context mContext;

    public ScheduleListViewAdapter(Context context) {
        super(context, 0);
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(getItem(position) instanceof ScheduleFragment.ListViewItem)
            return VIEW_TYPE_ITEM;

        return VIEW_TYPE_ITEM_EMPTY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(getItemViewType(position) == VIEW_TYPE_ITEM_EMPTY) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_schedule_empty, parent, false);
            convertView.setOnClickListener(null);
            return convertView;
        }

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_schedule, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.class_name);
            viewHolder.hour_start = (TextView) convertView.findViewById(R.id.class_hour_start);
            viewHolder.hour_end = (TextView) convertView.findViewById(R.id.class_hour_end);
            viewHolder.room = (TextView) convertView.findViewById(R.id.class_room);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ScheduleFragment.ListViewItem item = (ScheduleFragment.ListViewItem) getItem(position);
        viewHolder.name.setText(item.name + " (" + item.type + ")");
        viewHolder.hour_start.setText(item.hour_start);
        viewHolder.hour_end.setText(item.hour_end);
        
        if(item.room == null)
            viewHolder.room.setText(" - ");
        else
            viewHolder.room.setText(item.room);
        
        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView hour_start, hour_end;
        TextView room;
    }

}