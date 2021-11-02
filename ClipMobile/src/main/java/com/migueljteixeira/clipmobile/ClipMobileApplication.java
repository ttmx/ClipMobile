package com.migueljteixeira.clipmobile;

import android.app.Application;
import android.os.StrictMode;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class ClipMobileApplication extends Application {

    public static String CONTENT_AUTHORITY;

    @Override
    public void onCreate() {
        super.onCreate();

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()

                                .setDefaultFontPath("Roboto-Regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("Roboto-Regular.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build());

        // Set provider authority
        CONTENT_AUTHORITY = getString(R.string.provider_authority);

        // Enable StrictMode
        enableStrictMode();
    }

    //    @SuppressLint("NewApi")
    private void enableStrictMode() {
        if (!BuildConfig.DEBUG)
            return;

        // Enable StrictMode
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll().penaltyLog().build());

        // Policy applied to all threads in the virtual machine's process
        final StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder();
        vmPolicyBuilder.detectAll();
        vmPolicyBuilder.penaltyLog();
//        if (AndroidUtils.isJellyBeanOrHigher())
        vmPolicyBuilder.detectLeakedRegistrationObjects();

        StrictMode.setVmPolicy(vmPolicyBuilder.build());
    }

}
