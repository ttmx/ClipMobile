package com.migueljteixeira.clipmobile.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.migueljteixeira.clipmobile.R;
import com.migueljteixeira.clipmobile.databinding.ProgressSpinnerBinding;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BaseFragment extends Fragment {

    FrameLayout mProgressSpinner;
    CardView mMainView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        mProgressSpinner = root.findViewById(R.id.progress_spinner);
        mMainView = root.findViewById(R.id.main_view);
        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mProgressSpinner = view.findViewById(R.id.progress_spinner);
        mMainView = view.findViewById(R.id.main_view);
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Shows the progress spinner and hides the login form.
     */
    protected void showProgressSpinner(final boolean show) {
        mProgressSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
        mMainView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    /**
     * Shows the progress spinner
     */
    protected void showProgressSpinnerOnly(final boolean show) {
        mProgressSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
    }

    protected void cancelTasks(AsyncTask mTask) {
        if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED)
            mTask.cancel(true);
    }

}
