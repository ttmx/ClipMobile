package com.migueljteixeira.clipmobile.ui

import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.migueljteixeira.clipmobile.R
import android.os.AsyncTask
import android.view.View
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {
    var mProgressSpinner: FrameLayout? = null
    var mMainView: CardView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)!!
        mProgressSpinner = root.findViewById(R.id.progress_spinner)
        mMainView = root.findViewById(R.id.main_view)
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retain this fragment across configuration changes.
        retainInstance = true
    }

    fun bindHelperViews(view: View?) {
        var view = view
        if (view == null) view = getView()
        assert(view != null)
        mProgressSpinner = view!!.findViewById(R.id.progress_spinner)
        mMainView = view.findViewById(R.id.main_view)
    }

    /**
     * Shows the progress spinner and hides the login form.
     */
    protected fun showProgressSpinner(show: Boolean) {
        mProgressSpinner?.visibility = if (show) View.VISIBLE else View.GONE
        mMainView?.visibility = if (show) View.GONE else View.VISIBLE
    }

    /**
     * Shows the progress spinner
     */
    protected fun showProgressSpinnerOnly(show: Boolean) {
        mProgressSpinner?.visibility = if (show) View.VISIBLE else View.GONE
    }

    protected fun cancelTasks(mTask: AsyncTask<*, *, *>?) {
        if (mTask != null && mTask.status != AsyncTask.Status.FINISHED) mTask.cancel(true)
    }
}