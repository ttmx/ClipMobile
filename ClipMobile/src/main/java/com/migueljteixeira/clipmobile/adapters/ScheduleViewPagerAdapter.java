package com.migueljteixeira.clipmobile.adapters;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.migueljteixeira.clipmobile.entities.Student;
import com.migueljteixeira.clipmobile.entities.StudentScheduleClass;
import com.migueljteixeira.clipmobile.ui.ScheduleFragment;

import java.util.ArrayList;
import java.util.List;

public class ScheduleViewPagerAdapter extends FragmentPagerAdapter {
    public static final String SCHEDULE_CLASSES_TAG = "schedule_classes_tag";
    private final String[] tabNames;
    private final Student student;

    public ScheduleViewPagerAdapter(FragmentManager fm, String[] tabNames, Student student) {
        super(fm);
        this.tabNames = tabNames;
        this.student = student;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames[position];
    }

    @Override
    public Fragment getItem(int position) {
        List<StudentScheduleClass> classes = student.getScheduleClasses().get(position + 2);

        Fragment fragment = new ScheduleFragment();
        fragment.setArguments(getBundle(classes));

        return fragment;
    }

    @Override
    public int getCount() {
        return tabNames.length;
    }

    private Bundle getBundle(List<StudentScheduleClass> classes) {
        Bundle bundle = new Bundle();

        if (classes != null) {
            // LinkedList to ArrayList 'conversion'
            ArrayList<StudentScheduleClass> list = new ArrayList<StudentScheduleClass>();
            list.addAll(classes);

            bundle.putParcelableArrayList(SCHEDULE_CLASSES_TAG, new ArrayList<Parcelable>(list));
        }

        return bundle;
    }
}
