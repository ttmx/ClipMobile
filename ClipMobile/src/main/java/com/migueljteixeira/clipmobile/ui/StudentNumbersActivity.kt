package com.migueljteixeira.clipmobile.ui

import com.migueljteixeira.clipmobile.ui.BaseActivity
import android.os.Bundle
import com.migueljteixeira.clipmobile.R
import com.migueljteixeira.clipmobile.ui.StudentNumbersFragment

class StudentNumbersActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singlepane)
        setupActionBar()

//        Crashlytics.log("StudentNumbersActivity - onCreate");
        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.content_frame)
        if (fragment == null) {
            fragment = StudentNumbersFragment()
            fm.beginTransaction().add(R.id.content_frame, fragment).commit()
        }
    }
}