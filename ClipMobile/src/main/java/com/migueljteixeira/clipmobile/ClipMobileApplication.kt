package com.migueljteixeira.clipmobile

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump

class ClipMobileApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("Roboto-Regular.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )

        // Set provider authority
        CONTENT_AUTHORITY = getString(R.string.provider_authority)

        // Enable StrictMode
        enableStrictMode()
    }

    private fun enableStrictMode() {
        if (!BuildConfig.DEBUG) return

        // Enable StrictMode
        StrictMode.setThreadPolicy(
            ThreadPolicy.Builder()
                .detectAll().penaltyLog().build()
        )

        // Policy applied to all threads in the virtual machine's process
        val vmPolicyBuilder = VmPolicy.Builder()
        vmPolicyBuilder.detectAll()
        vmPolicyBuilder.penaltyLog()
        vmPolicyBuilder.detectLeakedRegistrationObjects()
        StrictMode.setVmPolicy(vmPolicyBuilder.build())
    }

    companion object {
        @JvmField
        var CONTENT_AUTHORITY: String? = null
    }
}