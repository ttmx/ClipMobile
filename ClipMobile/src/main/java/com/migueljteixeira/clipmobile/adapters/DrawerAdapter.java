package com.migueljteixeira.clipmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.migueljteixeira.clipmobile.R;
import com.migueljteixeira.clipmobile.ui.NavDrawerActivity.DrawerDivider;
import com.migueljteixeira.clipmobile.ui.NavDrawerActivity.DrawerItem;
import com.migueljteixeira.clipmobile.ui.NavDrawerActivity.DrawerTitle;

public class DrawerAdapter extends ArrayAdapter<Object> {
    private static final int VIEW_TYPE_TITLE = 0;
    private static final int VIEW_TYPE_DIVIDER = 1;
    private static final int VIEW_TYPE_ITEM = 2;

    private final Context mContext;

    public DrawerAdapter(Context context) {
        super(context, 0);
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(getItem(position) instanceof DrawerTitle)
            return VIEW_TYPE_TITLE;

        else if(getItem(position) instanceof DrawerDivider)
            return VIEW_TYPE_DIVIDER;

        return VIEW_TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(getItemViewType(position) == VIEW_TYPE_DIVIDER) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_divider, parent, false);
            convertView.setOnClickListener(null);
            return convertView;
        }

        if(convertView == null) {
            viewHolder = new ViewHolder();
            
            if(getItemViewType(position) == VIEW_TYPE_TITLE) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_title, parent, false);
                convertView.setOnClickListener(null);
            }
            else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.drawer_item, parent, false);
                viewHolder.icon = convertView.findViewById(R.id.icon);
            }
            
            viewHolder.name = convertView.findViewById(R.id.name);
            
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DrawerItem item = (DrawerItem) getItem(position);
        viewHolder.name.setText(item.mTitle);

        if(getItemViewType(position) == VIEW_TYPE_ITEM)
            viewHolder.icon.setImageResource(item.mIconRes);

        return convertView;
    }

    static class ViewHolder {

        TextView name;
        ImageView icon;
    }

}