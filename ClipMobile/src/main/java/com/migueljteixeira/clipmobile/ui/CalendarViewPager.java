package com.migueljteixeira.clipmobile.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.astuetz.PagerSlidingTabStrip;
import com.migueljteixeira.clipmobile.R;
import com.migueljteixeira.clipmobile.adapters.CalendarViewPagerAdapter;
import com.migueljteixeira.clipmobile.entities.Student;
import com.migueljteixeira.clipmobile.util.tasks.GetStudentCalendarTask;
//import com.uwetrottmann.androidutils.AndroidUtils;

public class CalendarViewPager extends BaseViewPager
        implements GetStudentCalendarTask.OnTaskFinishedListener<Student> {
    private GetStudentCalendarTask mTask;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);

        // Start AsyncTask
        mTask = new GetStudentCalendarTask(getActivity(), CalendarViewPager.this);
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        AndroidUtils.executeOnPool(mTask);

        return view;
    }

    @Override
    public void onTaskFinished(Student result) {
        if(!isAdded())
            return;

        showProgressSpinnerOnly(false);

        // Server is unavailable right now
        if(result == null) return;

        // Initialize the ViewPager and set an adapter
        mViewPager.setAdapter(new CalendarViewPagerAdapter(getChildFragmentManager(),
                getResources().getStringArray(R.array.exams_tests_tab_array), result));
        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = view.findViewById(R.id.tabs);
        tabs.setViewPager(mViewPager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelTasks(mTask);
    }

}
