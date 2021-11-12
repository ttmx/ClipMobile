package com.migueljteixeira.clipmobile.ui

import android.graphics.Color
import android.os.AsyncTask
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.migueljteixeira.clipmobile.R

open class BaseActivity : AppCompatActivity() {
    protected lateinit var mToolbar: Toolbar
    protected fun setupActionBar() {
        mToolbar = findViewById(R.id.toolbar)
        mToolbar.setTitleTextColor(Color.WHITE)
        mToolbar.setTitleTextAppearance(this, R.style.Toolbar)
        setSupportActionBar(mToolbar)
    }

    protected fun setActionBarShadow() {
        findViewById<View>(R.id.toolbar_shadow).visibility = View.VISIBLE
    }

    protected fun hideActionBarShadow() {
        findViewById<View>(R.id.toolbar_shadow).visibility = View.GONE
    }

    protected fun cancelTasks(mTask: AsyncTask<*, *, *>?) {
        if (mTask != null && mTask.status != AsyncTask.Status.FINISHED) mTask.cancel(true)
    }
}