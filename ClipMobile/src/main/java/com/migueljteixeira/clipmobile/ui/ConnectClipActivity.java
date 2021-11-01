package com.migueljteixeira.clipmobile.ui;

import android.content.Intent;
import android.os.Bundle;

//import com.crashlytics.android.Crashlytics;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.migueljteixeira.clipmobile.R;
import com.migueljteixeira.clipmobile.settings.ClipSettings;

//import io.fabric.sdk.android.Fabric;

public class ConnectClipActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlepane);

        setupActionBar();

        // Crash system reporting
//        Fabric.with(this, new Crashlytics());

//        Crashlytics.log("ConnectClipActivity - onCreate");

        // If the user has already login, start the StudentNumbersActivity instead
        if( ClipSettings.isUserLoggedIn(this) ) {
//            Crashlytics.log("ConnectClipActivity - user has already login");
            
            Intent intent = new Intent(this, StudentNumbersActivity.class);
            startActivity(intent);

            finish();
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.content_frame);
        if (fragment == null) {
            fragment = new ConnectClipFragment();
            fm.beginTransaction().add(R.id.content_frame, fragment).commit();
        }
    }

}
