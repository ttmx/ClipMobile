package com.migueljteixeira.clipmobile.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Hacky fix for the following issue
 * https://code.google.com/p/android/issues/detail?id=60464
 */
public class DrawerLayout extends androidx.drawerlayout.widget.DrawerLayout {
    
    public DrawerLayout(Context context) {
        super(context);
    }

    public DrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }
}
