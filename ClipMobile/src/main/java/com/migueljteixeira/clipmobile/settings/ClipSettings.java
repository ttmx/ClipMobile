package com.migueljteixeira.clipmobile.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

//import com.crashlytics.android.Crashlytics;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ClipSettings {
    private final static String COOKIE_NAME = "com.migueljteixeira.clipmobile.cookie";
    private final static String LOGIN_TIME = "com.migueljteixeira.clipmobile.loggedInTime";

    private final static String LOGGED_IN_USER_ID = "com.migueljteixeira.clipmobile.loggedInUserId";
    private final static String LOGGED_IN_USER_NAME = "com.migueljteixeira.clipmobile.loggedInUserName";
    private final static String LOGGED_IN_USER_PW = "com.migueljteixeira.clipmobile.loggedInUserPw";

    private final static String STUDENT_ID_SELECTED = "com.migueljteixeira.clipmobile.studentIdSelected";
    private final static String YEAR_SELECTED = "com.migueljteixeira.clipmobile.yearSelected";
    private final static String SEMESTER_SELECTED = "com.migueljteixeira.clipmobile.semesterSelected";

    private final static String STUDENT_NUMBERID_SELECTED = "com.migueljteixeira.clipmobile.studentNumberIdSelected";
    private final static String STUDENT_YEARSEMESTER_ID_SELECTED = "com.migueljteixeira.clipmobile.studentYearSemesterIdSelected";

    private final static String STUDENT_CLASS_ID_SELECTED = "com.migueljteixeira.clipmobile.studentClassIdSelected";
    private final static String STUDENT_CLASS_SELECTED = "com.migueljteixeira.clipmobile.studentClassSelected";

    private static SharedPreferences get(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static SharedPreferences.Editor edit(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit();
    }

    public static String getCookie(Context context) {
        return get(context).getString(COOKIE_NAME, null);
    }

    public static void saveCookie(Context context, String cookie) {
        edit(context).putString(COOKIE_NAME, cookie).commit();
    }

    public static void saveLoginTime(Context context) {
        edit(context).putLong(LOGIN_TIME, new Date().getTime()).commit();
    }

    public static boolean isTimeForANewCookie(Context context) {
        long currentTime = new Date().getTime();
        long loginTime = get(context).getLong(LOGIN_TIME, -1);

        long elapsedTime = currentTime - loginTime;
//        Crashlytics.log("ClipSettings - newCookie? - loginTime:" + loginTime);
//        Crashlytics.log("ClipSettings - newCookie? - currentTime:" + currentTime);

        int elapsedTimeInMinutes = (int) TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
//        Crashlytics.log("ClipSettings - newCookie? - elapsedTime:" + elapsedTimeInMinutes);
        
        System.out.println("ClipSettings - newCookie? - elapsedTime:" + elapsedTimeInMinutes);

        // If the elapsedTime >= 50min, we need to request a new cookie from the server
        return elapsedTimeInMinutes >= 50;
    }

    public static boolean isUserLoggedIn(Context context) {
        return get(context).getLong(LOGGED_IN_USER_ID, -1) != -1;
    }

    public static long getLoggedInUserId(Context context) {
        return get(context).getLong(LOGGED_IN_USER_ID, -1);
    }

    public static String getLoggedInUserName(Context context) {
        return get(context).getString(LOGGED_IN_USER_NAME, null);
    }

    public static String getLoggedInUserPw(Context context) {
        return get(context).getString(LOGGED_IN_USER_PW, null);
    }

    public static void setLoggedInUser(Context context, long id, String username, String password) {
        edit(context).putLong(LOGGED_IN_USER_ID, id).commit();

        // Save credentials
        edit(context).putString(LOGGED_IN_USER_NAME, username).commit();
        edit(context).putString(LOGGED_IN_USER_PW, password).commit();
    }

    public static void logoutUser(Context context) {

        // Clear user personal data
        edit(context).clear().commit();
    }

    public static String getYearSelected(Context context) {
        return get(context).getString(YEAR_SELECTED, null);
    }

    public static String getYearSelectedFormatted(Context context) {
        String year = get(context).getString(YEAR_SELECTED, null);
        String[] split = year.split("/"); // [ "2014", "15" ]

        String chars = split[1]; // [ "15" ]

        String newString = split[0].substring(0, chars.length()); // [ "20" ]
        newString = newString.concat(chars); // [ "2015" ]

        return newString;
    }

    public static void saveYearSelected(Context context, String yearSelected) {
        edit(context).putString(YEAR_SELECTED, yearSelected).commit();
    }

    public static int getCurrentSemester() {
        Calendar calendar = Calendar.getInstance();

        int month = calendar.get(Calendar.MONTH);
        if(month >= 2 && month <= 7) //  March <= month <= September
            return 2;

        return 1;
    }

    public static int getSemesterSelected(Context context) {
        return get(context).getInt(SEMESTER_SELECTED, 1);
    }

    public static void saveSemesterSelected(Context context, int semesterSelected) {
        edit(context).putInt(SEMESTER_SELECTED, semesterSelected).commit();
    }

    public static String getStudentNumberidSelected(Context context) {
        return get(context).getString(STUDENT_NUMBERID_SELECTED, null);
    }

    public static void saveStudentNumberId(Context context, String numberId) {
        edit(context).putString(STUDENT_NUMBERID_SELECTED, numberId).commit();
    }

    public static String getStudentYearSemesterIdSelected(Context context) {
        return get(context).getString(STUDENT_YEARSEMESTER_ID_SELECTED, null);
    }

    public static void saveStudentYearSemesterIdSelected(Context context, String studentYearSemesterId) {
        edit(context).putString(STUDENT_YEARSEMESTER_ID_SELECTED, studentYearSemesterId).commit();
    }

    public static String getStudentIdSelected(Context context) {
        return get(context).getString(STUDENT_ID_SELECTED, null);
    }

    public static void saveStudentIdSelected(Context context, String studentId) {
        edit(context).putString(STUDENT_ID_SELECTED, studentId).commit();
    }

    public static String getStudentClassIdSelected(Context context) {
        return get(context).getString(STUDENT_CLASS_ID_SELECTED, null);
    }

    public static void saveStudentClassIdSelected(Context context, String classId) {
        edit(context).putString(STUDENT_CLASS_ID_SELECTED, classId).commit();
    }

    public static String getStudentClassSelected(Context context) {
        return get(context).getString(STUDENT_CLASS_SELECTED, null);
    }

    public static void saveStudentClassSelected(Context context, String classNumber) {
        edit(context).putString(STUDENT_CLASS_SELECTED, classNumber).commit();
    }

    public static Date getSemesterStartDate(Context context) {
        int year = Integer.parseInt(ClipSettings.getYearSelectedFormatted(context));
        int semester = ClipSettings.getSemesterSelected(context);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        
        if(semester == 1) {
            calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
            calendar.set(Calendar.YEAR, year - 1);
        } else {
            calendar.set(Calendar.MONTH, Calendar.MARCH);
            calendar.set(Calendar.YEAR, year);
        }

        return calendar.getTime();
    }

    public static Date getSemesterEndDate(Context context) {
        int year = Integer.parseInt(ClipSettings.getYearSelectedFormatted(context));
        int semester = ClipSettings.getSemesterSelected(context);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        
        if(semester == 1) {
            calendar.set(Calendar.MONTH, Calendar.APRIL);
            calendar.set(Calendar.YEAR, year);
        } else {
            calendar.set(Calendar.MONTH, Calendar.OCTOBER);
            calendar.set(Calendar.YEAR, year);
        }

        return calendar.getTime();
    }
}
