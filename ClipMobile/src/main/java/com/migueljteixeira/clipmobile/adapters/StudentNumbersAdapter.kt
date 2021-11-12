package com.migueljteixeira.clipmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.migueljteixeira.clipmobile.R;
import com.migueljteixeira.clipmobile.entities.Student;

import java.util.List;

public class StudentNumbersAdapter extends BaseExpandableListAdapter {

    private final List<Student> students;
    private final Context mContext;

    public StudentNumbersAdapter(Context context, List<Student> students) {
        this.mContext = context;
        this.students = students;
    }

    @Override
    public int getGroupCount() {
        return students.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return students.get(groupPosition).getYears().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return students.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return students.get(groupPosition).getYears().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_group_student_numbers, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Set row name
        viewHolder.name.setText(students.get(groupPosition).getNumber());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_child_student_numbers, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Set row name
        viewHolder.name.setText(students.get(groupPosition).getYears().get(childPosition).getYear());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class ViewHolder {

        TextView name;
    }
}
