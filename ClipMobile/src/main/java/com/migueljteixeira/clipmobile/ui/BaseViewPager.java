package com.migueljteixeira.clipmobile.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.migueljteixeira.clipmobile.R;
import com.migueljteixeira.clipmobile.databinding.FragmentViewpagerBinding;

public class BaseViewPager extends Fragment {

    FrameLayout mProgressSpinner;
    ViewPager mViewPager;
    protected View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentViewpagerBinding binding = FragmentViewpagerBinding.inflate(inflater);
        mViewPager = binding.viewPager;
        mProgressSpinner = binding.getRoot().findViewById(R.id.progress_spinner);

        // Show progress spinner
        showProgressSpinnerOnly(true);

        return binding.getRoot();
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

//        ButterKnife.unbind(this);

    }

    protected void cancelTasks(AsyncTask mTask) {
        if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED)
            mTask.cancel(true);
    }

}
