package com.migueljteixeira.clipmobile.ui

import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.migueljteixeira.clipmobile.R
import android.os.AsyncTask
import android.view.View
import androidx.fragment.app.Fragment
import com.migueljteixeira.clipmobile.databinding.FragmentViewpagerBinding

open class BaseViewPager : Fragment() {
    var mProgressSpinner: FrameLayout? = null
    lateinit var mViewPager: ViewPager
    protected lateinit var rootView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retain this fragment across configuration changes.
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentViewpagerBinding.inflate(inflater)
        mViewPager = binding.viewPager
        mProgressSpinner = binding.root.findViewById(R.id.progress_spinner)

        // Show progress spinner
        showProgressSpinnerOnly(true)
        return binding.root
    }

    /**
     * Shows the progress spinner
     */
    protected fun showProgressSpinnerOnly(show: Boolean) {
        mProgressSpinner!!.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()

//        ButterKnife.unbind(this);
    }

    protected fun cancelTasks(mTask: AsyncTask<*, *, *>?) {
        if (mTask != null && mTask.status != AsyncTask.Status.FINISHED) mTask.cancel(true)
    }
}