package com.migueljteixeira.clipmobile.ui

import android.content.Intent
import android.os.Bundle
import com.migueljteixeira.clipmobile.R
import com.migueljteixeira.clipmobile.settings.ClipSettings.getYearSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.isUserLoggedIn
import com.migueljteixeira.clipmobile.ui.NavDrawerActivity

class ConnectClipActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singlepane)
        setupActionBar()

        // Crash system reporting
//        Fabric.with(this, new Crashlytics());

//        Crashlytics.log("ConnectClipActivity - onCreate");

        // If the user has already login, start the StudentNumbersActivity instead
        if (isUserLoggedIn(this)) {
//            Crashlytics.log("ConnectClipActivity - user has already login");
            val intent: Intent = if (getYearSelected(applicationContext) == null) {
                Intent(this, StudentNumbersActivity::class.java)
            } else {
                Intent(this, NavDrawerActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.content_frame)
        if (fragment == null) {
            fragment = ConnectClipFragment()
            fm.beginTransaction().add(R.id.content_frame, fragment).commit()
        }
    }
}