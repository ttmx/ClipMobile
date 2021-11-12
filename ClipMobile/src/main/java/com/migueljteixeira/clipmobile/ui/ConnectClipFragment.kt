package com.migueljteixeira.clipmobile.ui

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.migueljteixeira.clipmobile.R
import com.migueljteixeira.clipmobile.databinding.FragmentActivityLoginBinding
import com.migueljteixeira.clipmobile.enums.Result
import com.migueljteixeira.clipmobile.util.tasks.BaseTask
import com.migueljteixeira.clipmobile.util.tasks.ConnectClipTask

class ConnectClipFragment : BaseFragment(), BaseTask.OnTaskFinishedListener<Result?> {
    var mUsername: EditText? = null
    var mPassword: EditText? = null
    var mLogInButton: Button? = null
    private var mTask: ConnectClipTask? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentActivityLoginBinding.inflate(inflater)
        val root: View = binding.root
        mUsername = binding.username
        mPassword = binding.password
        mLogInButton = binding.logInButton
        super.bindHelperViews(root)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Unfinished task around?
        if (mTask != null && mTask!!.status != AsyncTask.Status.FINISHED) showProgressSpinner(true)
        mLogInButton!!.setOnClickListener { v: View? ->
            var mFocusView: View? = null

            // Get username and password text
            val editableUsername = mUsername!!.text
            val username = editableUsername?.toString()?.trim { it <= ' ' }
            val editablePassword = mPassword!!.text
            val password = editablePassword?.toString()?.trim { it <= ' ' }

            // Check if the username field is not empty
            if (TextUtils.isEmpty(username)) {
                mUsername!!.error = getString(R.string.error_fields_required)
                mFocusView = mUsername
            } else if (TextUtils.isEmpty(password)) {
                mPassword!!.error = getString(R.string.error_fields_required)
                mFocusView = mPassword
            }
            if (mFocusView != null) {
                // Focus the first form field with an error.
                mFocusView.requestFocus()
            } else {
                showProgressSpinner(true)

                // Start AsyncTask
                mTask = ConnectClipTask(activity, this@ConnectClipFragment)
                mTask!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, username, password)
                //                    AndroidUtils.executeOnPool(mTask, username, password);
            }
        }
    }

    override fun onTaskFinished(result: Result?) {
        if (!isAdded) return
        showProgressSpinner(false)

        // If there was no errors, lets go to StudentNumbersActivity
        if (result === Result.SUCCESS) {
            val intent = Intent(activity, StudentNumbersActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out)
            requireActivity().finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTasks(mTask)
    }
}