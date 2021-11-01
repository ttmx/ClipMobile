package com.migueljteixeira.clipmobile.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.migueljteixeira.clipmobile.R;
import com.migueljteixeira.clipmobile.adapters.ScheduleViewPagerAdapter;
import com.migueljteixeira.clipmobile.entities.Student;
import com.migueljteixeira.clipmobile.util.tasks.GetStudentScheduleTask;
//import com.uwetrottmann.androidutils.AndroidUtils;

public class ScheduleViewPager extends BaseViewPager
        implements GetStudentScheduleTask.OnTaskFinishedListener<Student> {
    
    private GetStudentScheduleTask mTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);

        // Start AsyncTask
        mTask = new GetStudentScheduleTask(getActivity(), ScheduleViewPager.this);
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

        // Initialize the ViewPager and set the adapter
        mViewPager.setAdapter(new ScheduleViewPagerAdapter(getChildFragmentManager(),
                getResources().getStringArray(R.array.schedule_tab_array), result));
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
