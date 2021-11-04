package com.migueljteixeira.clipmobile.settings

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.migueljteixeira.clipmobile.settings.ClipSettings
import java.util.*
import java.util.concurrent.TimeUnit

object ClipSettings {
    private const val COOKIE_NAME = "com.migueljteixeira.clipmobile.cookie"
    private const val LOGIN_TIME = "com.migueljteixeira.clipmobile.loggedInTime"
    private const val LOGGED_IN_USER_ID = "com.migueljteixeira.clipmobile.loggedInUserId"
    private const val LOGGED_IN_USER_NAME = "com.migueljteixeira.clipmobile.loggedInUserName"
    private const val LOGGED_IN_USER_PW = "com.migueljteixeira.clipmobile.loggedInUserPw"
    private const val STUDENT_ID_SELECTED = "com.migueljteixeira.clipmobile.studentIdSelected"
    private const val YEAR_SELECTED = "com.migueljteixeira.clipmobile.yearSelected"
    private const val SEMESTER_SELECTED = "com.migueljteixeira.clipmobile.semesterSelected"
    private const val STUDENT_NUMBERID_SELECTED =
        "com.migueljteixeira.clipmobile.studentNumberIdSelected"
    private const val STUDENT_YEARSEMESTER_ID_SELECTED =
        "com.migueljteixeira.clipmobile.studentYearSemesterIdSelected"
    private const val STUDENT_CLASS_ID_SELECTED =
        "com.migueljteixeira.clipmobile.studentClassIdSelected"
    private const val STUDENT_CLASS_SELECTED = "com.migueljteixeira.clipmobile.studentClassSelected"
    private operator fun get(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    private fun edit(context: Context): SharedPreferences.Editor {
        return PreferenceManager.getDefaultSharedPreferences(context).edit()
    }

    fun getCookie(context: Context): String? {
        return ClipSettings[context].getString(COOKIE_NAME, null)
    }

    fun saveCookie(context: Context, cookie: String?) {
        edit(context).putString(COOKIE_NAME, cookie).commit()
    }

    fun saveLoginTime(context: Context) {
        edit(context).putLong(LOGIN_TIME, Date().time).commit()
    }

    fun isTimeForANewCookie(context: Context): Boolean {
        val currentTime = Date().time
        val loginTime = ClipSettings[context].getLong(LOGIN_TIME, -1)
        val elapsedTime = currentTime - loginTime
        //        Crashlytics.log("ClipSettings - newCookie? - loginTime:" + loginTime);
//        Crashlytics.log("ClipSettings - newCookie? - currentTime:" + currentTime);
        val elapsedTimeInMinutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime)
            .toInt()
        //        Crashlytics.log("ClipSettings - newCookie? - elapsedTime:" + elapsedTimeInMinutes);
        println("ClipSettings - newCookie? - elapsedTime:$elapsedTimeInMinutes")

        // If the elapsedTime >= 50min, we need to request a new cookie from the server
        return elapsedTimeInMinutes >= 50
    }

    @JvmStatic
    fun isUserLoggedIn(context: Context): Boolean {
        return ClipSettings[context].getLong(LOGGED_IN_USER_ID, -1) != -1L
    }

    @JvmStatic
    fun getLoggedInUserId(context: Context): Long {
        return ClipSettings[context].getLong(LOGGED_IN_USER_ID, -1)
    }

    fun getLoggedInUserName(context: Context): String? {
        return ClipSettings[context].getString(LOGGED_IN_USER_NAME, null)
    }

    fun getLoggedInUserPw(context: Context): String? {
        return ClipSettings[context].getString(LOGGED_IN_USER_PW, null)
    }

    fun setLoggedInUser(context: Context, id: Long, username: String?, password: String?) {
        edit(context).putLong(LOGGED_IN_USER_ID, id).commit()

        // Save credentials
        edit(context).putString(LOGGED_IN_USER_NAME, username).commit()
        edit(context).putString(LOGGED_IN_USER_PW, password).commit()
    }

    @JvmStatic
    fun logoutUser(context: Context) {

        // Clear user personal data
        edit(context).clear().commit()
    }

    @JvmStatic
    fun getYearSelected(context: Context): String? {
        return ClipSettings[context].getString(YEAR_SELECTED, null)
    }

    @JvmStatic
    fun getYearSelectedFormatted(context: Context): String {
        val year = ClipSettings[context].getString(YEAR_SELECTED, null)
        val split = year!!.split("/").toTypedArray() // [ "2014", "15" ]
        val chars = split[1] // [ "15" ]
        var newString = split[0].substring(0, chars.length) // [ "20" ]
        newString += chars // [ "2015" ]
        return newString
    }

    @JvmStatic
    fun saveYearSelected(context: Context, yearSelected: String?) {
        edit(context).putString(YEAR_SELECTED, yearSelected).commit()
    }

    //  March <= month <= September
    val currentSemester: Int
        get() {
            val calendar = Calendar.getInstance()
            val month = calendar[Calendar.MONTH]
            return if (month in 2..7) 2 else 1
        }

    @JvmStatic
    fun getSemesterSelected(context: Context): Int {
        return ClipSettings[context].getInt(SEMESTER_SELECTED, 1)
    }

    @JvmStatic
    fun saveSemesterSelected(context: Context, semesterSelected: Int) {
        edit(context).putInt(SEMESTER_SELECTED, semesterSelected).commit()
    }

    @JvmStatic
    fun getStudentNumberidSelected(context: Context): String? {
        return ClipSettings[context].getString(STUDENT_NUMBERID_SELECTED, null)
    }

    @JvmStatic
    fun saveStudentNumberId(context: Context, numberId: String?) {
        edit(context).putString(STUDENT_NUMBERID_SELECTED, numberId).commit()
    }

    @JvmStatic
    fun getStudentYearSemesterIdSelected(context: Context): String? {
        return ClipSettings[context].getString(STUDENT_YEARSEMESTER_ID_SELECTED, null)
    }

    @JvmStatic
    fun saveStudentYearSemesterIdSelected(context: Context, studentYearSemesterId: String?) {
        edit(context).putString(STUDENT_YEARSEMESTER_ID_SELECTED, studentYearSemesterId).commit()
    }

    @JvmStatic
    fun getStudentIdSelected(context: Context): String? {
        return ClipSettings[context].getString(STUDENT_ID_SELECTED, null)
    }

    @JvmStatic
    fun saveStudentIdSelected(context: Context, studentId: String?) {
        edit(context).putString(STUDENT_ID_SELECTED, studentId).commit()
    }

    @JvmStatic
    fun getStudentClassIdSelected(context: Context): String? {
        return ClipSettings[context].getString(STUDENT_CLASS_ID_SELECTED, null)
    }

    @JvmStatic
    fun saveStudentClassIdSelected(context: Context, classId: String?) {
        edit(context).putString(STUDENT_CLASS_ID_SELECTED, classId).commit()
    }

    @JvmStatic
    fun getStudentClassSelected(context: Context): String? {
        return ClipSettings[context].getString(STUDENT_CLASS_SELECTED, null)
    }

    @JvmStatic
    fun saveStudentClassSelected(context: Context, classNumber: String?) {
        edit(context).putString(STUDENT_CLASS_SELECTED, classNumber).commit()
    }

    @JvmStatic
    fun getSemesterStartDate(context: Context): Date {
        val year = getYearSelectedFormatted(context).toInt()
        val semester = getSemesterSelected(context)
        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_MONTH] = 1
        if (semester == 1) {
            calendar[Calendar.MONTH] = Calendar.SEPTEMBER
            calendar[Calendar.YEAR] = year - 1
        } else {
            calendar[Calendar.MONTH] = Calendar.MARCH
            calendar[Calendar.YEAR] = year
        }
        return calendar.time
    }

    @JvmStatic
    fun getSemesterEndDate(context: Context): Date {
        val year = getYearSelectedFormatted(context).toInt()
        val semester = getSemesterSelected(context)
        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_MONTH] = 1
        if (semester == 1) {
            calendar[Calendar.MONTH] = Calendar.APRIL
            calendar[Calendar.YEAR] = year
        } else {
            calendar[Calendar.MONTH] = Calendar.OCTOBER
            calendar[Calendar.YEAR] = year
        }
        return calendar.time
    }
}