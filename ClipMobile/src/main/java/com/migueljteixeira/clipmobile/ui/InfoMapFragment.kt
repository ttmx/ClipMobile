package com.migueljteixeira.clipmobile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.migueljteixeira.clipmobile.R
import com.migueljteixeira.clipmobile.databinding.FragmentMapBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import uk.co.senab.photoview.PhotoViewAttacher

class InfoMapFragment : BaseFragment() {
    private lateinit var mImageView: ImageView
    private lateinit var mAttacher: PhotoViewAttacher
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMapBinding.inflate(inflater)
        val view: View = binding.root
        //        ButterKnife.bind(this, view);
        mImageView = binding.map
        super.bindHelperViews(binding.root)
        showProgressSpinnerOnly(true)
        mAttacher = PhotoViewAttacher(mImageView)
        mAttacher.scaleType = ImageView.ScaleType.FIT_CENTER
        Picasso.get()
            .load(R.drawable.map_campus)
            .into(mImageView, object : Callback {
                override fun onSuccess() {
                    showProgressSpinnerOnly(false)
                }

                override fun onError(e: Exception) {}
            })
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        mAttacher.cleanup()
    }
}