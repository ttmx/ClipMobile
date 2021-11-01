package com.migueljteixeira.clipmobile.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.migueljteixeira.clipmobile.R;
import com.migueljteixeira.clipmobile.databinding.FragmentMapBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

public class InfoMapFragment extends BaseFragment {

    ImageView mImageView;
    private PhotoViewAttacher mAttacher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentMapBinding binding = FragmentMapBinding.inflate(inflater);
        View view = binding.getRoot();
//        ButterKnife.bind(this, view);
        mImageView = binding.map;
        super.bindHelperViews(binding.getRoot());

        showProgressSpinnerOnly(true);

        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Picasso.get()
                .load(R.drawable.map_campus)
                .into(mImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        showProgressSpinnerOnly(false);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mAttacher.cleanup();
    }
}