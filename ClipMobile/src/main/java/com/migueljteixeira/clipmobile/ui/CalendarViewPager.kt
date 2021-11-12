package com.migueljteixeira.clipmobile.ui

import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.util.tasks.GetStudentCalendarTask
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.os.AsyncTask
import android.view.View
import com.migueljteixeira.clipmobile.adapters.CalendarViewPagerAdapter
import com.migueljteixeira.clipmobile.R
import com.astuetz.PagerSlidingTabStrip
import com.migueljteixeira.clipmobile.util.tasks.BaseTask

class CalendarViewPager : BaseViewPager(), BaseTask.OnTaskFinishedListener<Student?> {
    private var mTask: GetStudentCalendarTask? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = super.onCreateView(inflater, container, savedInstanceState)!!

        // Start AsyncTask
        mTask = GetStudentCalendarTask(requireActivity(), this@CalendarViewPager)
        mTask!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        //        AndroidUtils.executeOnPool(mTask);
        return rootView
    }

    override fun onTaskFinished(result: Student?) {
        if (!isAdded) return
        showProgressSpinnerOnly(false)

        // Server is unavailable right now
        if (result == null) return

        // Initialize the ViewPager and set an adapter
        mViewPager!!.adapter = CalendarViewPagerAdapter(
            childFragmentManager,
            resources.getStringArray(R.array.exams_tests_tab_array), result
        )
        mViewPager!!.setPageTransformer(true, DepthPageTransformer())

        // Bind the tabs to the ViewPager
        val tabs: PagerSlidingTabStrip = rootView!!.findViewById(R.id.tabs)
        tabs.setViewPager(mViewPager)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTasks(mTask)
    }
}