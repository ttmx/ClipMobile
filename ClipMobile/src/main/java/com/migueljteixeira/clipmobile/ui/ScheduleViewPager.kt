package com.migueljteixeira.clipmobile.ui

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.astuetz.PagerSlidingTabStrip
import com.migueljteixeira.clipmobile.R
import com.migueljteixeira.clipmobile.adapters.ScheduleViewPagerAdapter
import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.util.tasks.BaseTask
import com.migueljteixeira.clipmobile.util.tasks.GetStudentScheduleTask
import java.time.LocalDate
import java.util.*

class ScheduleViewPager : BaseViewPager(), BaseTask.OnTaskFinishedListener<Student?> {
    private var mTask: GetStudentScheduleTask? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = super.onCreateView(inflater, container, savedInstanceState)!!

        // Start AsyncTask
        mTask = GetStudentScheduleTask(requireActivity(), this@ScheduleViewPager)
        mTask!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        return rootView
    }

    override fun onTaskFinished(result: Student?) {
        if (!isAdded) return
        showProgressSpinnerOnly(false)

        // Server is unavailable right now
        if (result == null) return

        // Initialize the ViewPager and set the adapter
        mViewPager.adapter = ScheduleViewPagerAdapter(
            childFragmentManager,
            resources.getStringArray(R.array.schedule_tab_array), result
        )

        val day = LocalDate.now().dayOfWeek.ordinal
        mViewPager.currentItem = if(day in 0..4)day else 0
        mViewPager.setPageTransformer(true, DepthPageTransformer())

        // Bind the tabs to the ViewPager
        val tabs: PagerSlidingTabStrip = rootView.findViewById(R.id.tabs)
        tabs.setViewPager(mViewPager)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTasks(mTask)
    }
}