package com.migueljteixeira.clipmobile.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.migueljteixeira.clipmobile.R;
import com.migueljteixeira.clipmobile.adapters.ScheduleListViewAdapter;
import com.migueljteixeira.clipmobile.adapters.ScheduleViewPagerAdapter;
import com.migueljteixeira.clipmobile.entities.StudentScheduleClass;

import java.util.List;

public class ScheduleFragment extends Fragment {

    private List<StudentScheduleClass> classes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classes = getArguments().getParcelableArrayList(ScheduleViewPagerAdapter.SCHEDULE_CLASSES_TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view, container, false);
        ListView listView = view.findViewById(R.id.list_view);

        ScheduleListViewAdapter adapter = new ScheduleListViewAdapter(getActivity());

        if (classes == null)
            adapter.add(new ListViewItemEmpty());
        else {
            for (StudentScheduleClass c : classes)
                adapter.add(new ListViewItem(c.getName(), c.getType(), c.getHourStart(),
                        c.getHourEnd(), c.getRoom()));
        }

        listView.setAdapter(adapter);

        return view;
    }

    public static class ListViewItemEmpty {

        public ListViewItemEmpty() {
        }
    }

    public static class ListViewItem {
        public String name, type, hour_start, hour_end, room;

        public ListViewItem(String name, String type, String hour_start, String hour_end, String room) {
            this.name = name;
            this.type = type;
            this.hour_start = hour_start;
            this.hour_end = hour_end;
            this.room = room;
        }
    }
}
