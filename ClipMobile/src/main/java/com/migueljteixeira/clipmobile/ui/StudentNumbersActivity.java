package com.migueljteixeira.clipmobile.ui;

import android.os.Bundle;

//import com.crashlytics.android.Crashlytics;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.migueljteixeira.clipmobile.R;

public class StudentNumbersActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlepane);

        setupActionBar();

//        Crashlytics.log("StudentNumbersActivity - onCreate");

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.content_frame);
        if (fragment == null) {
            fragment = new StudentNumbersFragment();
            fm.beginTransaction().add(R.id.content_frame, fragment).commit();
        }
    }

}
