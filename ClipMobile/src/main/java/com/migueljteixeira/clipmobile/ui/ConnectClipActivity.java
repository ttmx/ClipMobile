package com.migueljteixeira.clipmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.migueljteixeira.clipmobile.settings.ClipSettings;

import io.fabric.sdk.android.Fabric;

public class ConnectClipActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());


        // If the user is already logged in, start the StudentNumbersActivity instead
        if( ClipSettings.isUserLoggedIn(this) ) {
            Intent intent = new Intent(getApplicationContext(), StudentNumbersActivity.class);
            startActivity(intent);

            Toast.makeText(this, "you're already logged in, dude!", Toast.LENGTH_SHORT).show();
            finish();
        }

        FragmentManager fm = getSupportFragmentManager();
        ConnectClipFragment fragment = (ConnectClipFragment) fm.findFragmentById(android.R.id.content);

        if (fragment == null) {
            fragment = new ConnectClipFragment();
            fm.beginTransaction().add(android.R.id.content, fragment).commit();
        }
    }

}
