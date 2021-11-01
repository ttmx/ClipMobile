package com.migueljteixeira.clipmobile.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.migueljteixeira.clipmobile.R;
import com.migueljteixeira.clipmobile.databinding.FragmentActivityLoginBinding;
import com.migueljteixeira.clipmobile.enums.Result;
import com.migueljteixeira.clipmobile.util.tasks.ConnectClipTask;

public class ConnectClipFragment extends BaseFragment
        implements ConnectClipTask.OnTaskFinishedListener<Result> {

    EditText mUsername;
    EditText mPassword;
    Button mLogInButton;

    private ConnectClipTask mTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivityLoginBinding binding = FragmentActivityLoginBinding.inflate(inflater);
        View root = binding.getRoot();
        mUsername = binding.username;
        mPassword = binding.password;
        mLogInButton = binding.logInButton;
        super.bindHelperViews(root);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Unfinished task around?
        if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED)
            showProgressSpinner(true);

        mLogInButton.setOnClickListener(v -> {
            View mFocusView = null;

            // Get username and password text
            Editable editableUsername = mUsername.getText();
            String username = editableUsername != null ?
                    editableUsername.toString().trim() : null;
            Editable editablePassword = mPassword.getText();
            String password = editablePassword != null ?
                    editablePassword.toString().trim() : null;

            // Check if the username field is not empty
            if (TextUtils.isEmpty(username)) {
                mUsername.setError(getString(R.string.error_fields_required));
                mFocusView = mUsername;
            }

            // Check if the password field is not empty
            else if (TextUtils.isEmpty(password)) {
                mPassword.setError(getString(R.string.error_fields_required));
                mFocusView = mPassword;
            }

            if (mFocusView != null) {
                // Focus the first form field with an error.
                mFocusView.requestFocus();
            } else {
                showProgressSpinner(true);

                // Start AsyncTask
                mTask = new ConnectClipTask(getActivity(), ConnectClipFragment.this);
                mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, username, password);
//                    AndroidUtils.executeOnPool(mTask, username, password);
            }

        });
    }

    @Override
    public void onTaskFinished(Result result) {
        if (!isAdded())
            return;

        showProgressSpinner(false);

        // If there was no errors, lets go to StudentNumbersActivity
        if (result == Result.SUCCESS) {
            Intent intent = new Intent(getActivity(), StudentNumbersActivity.class);
            startActivity(intent);

            getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            getActivity().finish();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelTasks(mTask);
    }
}