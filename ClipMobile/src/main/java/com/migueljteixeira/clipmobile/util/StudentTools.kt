package com.migueljteixeira.clipmobile.util

import android.content.ContentValues
import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.entities.User
import com.migueljteixeira.clipmobile.enums.Result
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException
import com.migueljteixeira.clipmobile.network.*
import com.migueljteixeira.clipmobile.settings.ClipSettings
import com.migueljteixeira.clipmobile.util.DBUtils.createUser
import com.migueljteixeira.clipmobile.util.DBUtils.deleteStudentsInfo
import com.migueljteixeira.clipmobile.util.DBUtils.deleteStudentsNumbers
import com.migueljteixeira.clipmobile.util.DBUtils.getStudentCalendar
import com.migueljteixeira.clipmobile.util.DBUtils.getStudentClasses
import com.migueljteixeira.clipmobile.util.DBUtils.getStudentClassesDocs
import com.migueljteixeira.clipmobile.util.DBUtils.getStudentSchedule
import com.migueljteixeira.clipmobile.util.DBUtils.getStudentYears
import com.migueljteixeira.clipmobile.util.DBUtils.getUserId
import com.migueljteixeira.clipmobile.util.DBUtils.getYearSemesterId
import com.migueljteixeira.clipmobile.util.DBUtils.insertStudentCalendar
import com.migueljteixeira.clipmobile.util.DBUtils.insertStudentClasses
import com.migueljteixeira.clipmobile.util.DBUtils.insertStudentClassesDocs
import com.migueljteixeira.clipmobile.util.DBUtils.insertStudentSchedule
import com.migueljteixeira.clipmobile.util.DBUtils.insertStudentYears
import com.migueljteixeira.clipmobile.util.DBUtils.insertStudentsNumbers
import com.migueljteixeira.clipmobile.util.tasks.BaseTask
import com.migueljteixeira.clipmobile.util.tasks.GetStudentCalendarTask
import java.util.*


object StudentTools {
    fun isNetworkConnected(mContext: Context): Boolean {
        val conMan = mContext.applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return conMan.activeNetworkInfo!!.isConnected
    }

    @JvmStatic
    @Throws(ServerUnavailableException::class)
    fun signIn(mContext: Context, username: String, password: String): Result {

        // Check for connectivity
        if (!isNetworkConnected(mContext)) return Result.OFFLINE

        // Sign in the user, and returns Students available
        val user = StudentRequest.signIn(mContext, username, password)

        // Invalid credentials
        if (!user.hasStudents()) return Result.ERROR
        var userId = getUserId(mContext, username!!)

        // If the user doesn't exist, create a new one
        if (userId == -1L) {
            userId = createUser(mContext, username)

            // Insert Students
            insertStudentsNumbers(mContext, userId, user)
        }

        // User is now logged in
        ClipSettings.setLoggedInUser(mContext, userId, username, password)
        return Result.SUCCESS
    }

    /*
     * ////////////////////////////// STUDENT, STUDENTYEARS, STUDENTNUMBERS  //////////////////////////////
     */
    @JvmStatic
    fun getStudents(mContext: Context?, userId: Long): User {
        return DBUtils.getStudents(mContext!!, userId)
    }

    @JvmStatic
    @Throws(ServerUnavailableException::class)
    fun getStudentsYears(
        mContext: Context,
        studentId: String,
        studentNumberId: String
    ): Student? {
        var student = getStudentYears(mContext, studentId)
        println("has " + student.hasStudentYears())
        if (student.hasStudentYears()) return student
        println("net " + !isNetworkConnected(mContext))

        // Check for connectivity
        if (!isNetworkConnected(mContext)) return null

        // Get student years from the server
        student = StudentRequest.getStudentsYears(mContext, studentNumberId)

        // Insert Students
        insertStudentYears(mContext, studentId, student)
        return student
    }

    @JvmStatic
    @Throws(ServerUnavailableException::class)
    fun updateStudentNumbersAndYears(mContext: Context, userId: Long): User {
        println("request!")

        // Get (new) studentsNumbers from the server
        val user = StudentRequest.getStudentsNumbers(mContext)
        println("deleting!")

        // Delete studentsNumbers and studentsYears
        deleteStudentsNumbers(mContext!!, userId)
        println("inserting!")

        // Insert Students
        insertStudentsNumbers(mContext, userId, user)
        return user
    }

    @JvmStatic
    @Throws(ServerUnavailableException::class)
    fun updateStudentPage(
        mContext: Context, studentId: String, studentNumberId: String,
        studentYearSemesterId: String
    ): Student {
        println("request!")

        // Get (new) student info from the server
        val student = StudentRequest.getStudentsYears(mContext, studentNumberId)
        println("deleting!")

        // Delete students info
        deleteStudentsInfo(mContext, studentYearSemesterId)
        println("inserting!")

        // Insert students info
        insertStudentYears(mContext, studentId, student)
        return student
    }

    /*
     * ////////////////////////////// STUDENT SCHEDULE  //////////////////////////////
     */
    @JvmStatic
    @Throws(ServerUnavailableException::class)
    fun getStudentSchedule(
        mContext: Context, studentId: String, year: String, yearFormatted: String,
        semester: Int, studentNumberId: String
    ): Student? {

        // First, we get the yearSemesterId
        val yearSemesterId = getYearSemesterId(mContext, studentId, year, semester)
        var student = getStudentSchedule(mContext, yearSemesterId!!)
        println("has " + (student != null))
        if (student != null) return student
        println("net " + !isNetworkConnected(mContext))

        // Check for connectivity
        if (!isNetworkConnected(mContext)) return null

        // Get student schedule from the server
        student =
            StudentScheduleRequest.getSchedule(mContext, studentNumberId, yearFormatted, semester)
        println("schedule request done!")

        // Insert schedule on database
        insertStudentSchedule(mContext, yearSemesterId, student)
        println("schedule inserted!")
        return student
    }

    /*
     * ////////////////////////////// STUDENT CLASSES  //////////////////////////////
     */
    @JvmStatic
    @Throws(ServerUnavailableException::class)
    fun getStudentClasses(
        mContext: Context, studentId: String, year: String, yearFormatted: String,
        semester: Int, studentNumberId: String
    ): Student? {

        // First, we get the yearSemesterId
        val yearSemesterId = getYearSemesterId(mContext, studentId, year, semester)
        var student = getStudentClasses(mContext, yearSemesterId!!)
        println("has " + (student != null))
        if (student != null) return student
        println("net " + !isNetworkConnected(mContext))

        // Check for connectivity
        if (!isNetworkConnected(mContext)) return null

        // Get student classes from the server
        student = StudentClassesRequest.getClasses(mContext, studentNumberId, yearFormatted)
        println("classes request done!")

        // Insert classes on database
        insertStudentClasses(mContext, yearSemesterId, student)
        println("classes inserted!")
        return student
    }

    @JvmStatic
    @Throws(ServerUnavailableException::class)
    fun getStudentClassesDocs(
        mContext: Context, studentClassId: String, yearFormatted: String,
        semester: Int, studentNumberId: String, studentClassSelected: String,
        docType: String
    ): Student? {
        var student = getStudentClassesDocs(mContext, studentClassId!!, docType!!)
        Log.d("StudentTools", "has " + (student != null))
        if (student != null) return student
        Log.d("StudentTools", "net " + !isNetworkConnected(mContext))

        // Check for connectivity
        if (!isNetworkConnected(mContext)) return null

        // Get student classes docs from the server
        student = StudentClassesDocsRequest.getClassesDocs(
            mContext, studentNumberId,
            yearFormatted, semester, studentClassSelected, docType
        )
        Log.d("StudentTools", "classes docs request done!")

        // Insert classes docs on database
        insertStudentClassesDocs(mContext, studentClassId, student)
        Log.d("StudentTools", "classes docs inserted!")
        return student
    }

    /*
     * ////////////////////////////// STUDENT CALENDAR  //////////////////////////////
     */
    @JvmStatic
    @Throws(ServerUnavailableException::class)
    fun getStudentCalendar(
        mContext: Context, studentId: String, year: String, yearFormatted: String,
        semester: Int, studentNumberId: String
    ): Student? {

        // First, we get the yearSemesterId
        val yearSemesterId = getYearSemesterId(mContext, studentId!!, year!!, semester)
        var student = getStudentCalendar(mContext, yearSemesterId!!)
        println("has " + (student != null))
        if (student != null) return student
        println("net " + !isNetworkConnected(mContext))

        // Check for connectivity
        if (!isNetworkConnected(mContext)) return null

        // ---- EXAM CALENDAR ----

        // Get student exam calendar from the server
        student = Student()
        StudentCalendarRequest.getExamCalendar(
            mContext,
            student,
            studentNumberId,
            yearFormatted,
            semester
        )

        // ---- TEST CALENDAR ----

        // Get student test calendar from the server
        StudentCalendarRequest.getTestCalendar(
            mContext,
            student,
            studentNumberId,
            yearFormatted,
            semester
        )
        println("calendar request done!")

        // Insert calendar on database
        insertStudentCalendar(mContext, yearSemesterId, student)
        println("calendar inserted!")
        return student
    }

    @JvmStatic
    fun confirmExportCalendar(mContext: Context): Map<Long, String> {
        val EVENT_PROJECTION = arrayOf(
            CalendarContract.Calendars._ID,  // 0
            CalendarContract.Calendars.ACCOUNT_NAME,  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,  // 2
            CalendarContract.Calendars.OWNER_ACCOUNT
        )

        // The indices for the projection array above.
        val PROJECTION_ID_INDEX = 0
        val PROJECTION_ACCOUNT_NAME_INDEX = 1
        val PROJECTION_DISPLAY_NAME_INDEX = 2
        val PROJECTION_OWNER_ACCOUNT_INDEX = 3

        // Run query
        val cr = mContext.contentResolver
        val uri = CalendarContract.Calendars.CONTENT_URI
        val selection = "((" + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))"
        val selectionArgs = arrayOf("com.google")

        // Submit the query and get a Cursor object back.
        val cursor = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null)
        val calendarsNames: MutableMap<Long, String> = HashMap()
        while (cursor!!.moveToNext()) {

            // Get the field values
            val calID: Long = cursor.getLong(PROJECTION_ID_INDEX)
            val displayName: String = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX)
            val accountName: String = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX)
            val ownerName: String = cursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX)
            if (displayName.equals(accountName, ignoreCase = true) &&
                accountName.equals(ownerName, ignoreCase = true)
            ) {
                calendarsNames[calID] = ownerName
            }
        }
        cursor.close()
        return calendarsNames
    }

    @JvmStatic
    fun exportCalendar(mContext: Context, calendarId: Long) {
        val mTask =
            GetStudentCalendarTask(mContext, object : BaseTask.OnTaskFinishedListener<Student> {
                override fun onTaskFinished(result: Student?) {
                    val calendar = result!!.getStudentCalendar()
                    for ((isExam, calendarEvent) in calendar) {
                        for (e in calendarEvent) {
                            val name: String = e.name!!
                            val date: String = e.date!!
                            val hour: String = e.hour!!
                            insertEvent(mContext, calendarId, isExam, name, date, hour)
                        }
                    }
                    Toast.makeText(
                        mContext, "Calendário exportado com sucesso!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        //        AndroidUtils.executeOnPool(mTask);
    }

    private fun insertEvent(
        mContext: Context, calendarId: Long, isExam: Boolean,
        name: String, date: String, hour: String
    ) {
        var hour = hour
        var title = "[TESTE] "
        if (isExam) {
            title = "[EXAME] "
            hour = hour.split("-").toTypedArray()[0]
        }

        // Title
        title += name

        // Date
        val year = date.split("-").toTypedArray()[0].toInt()
        val month = date.split("-").toTypedArray()[1].toInt() - 1
        val day = date.split("-").toTypedArray()[2].toInt()

        // Hour
        val h = hour.split(":").toTypedArray()
        val beginHour = h[0].toInt()
        val beginMinutes: Int = if (isExam) h[1].substring(0, h.size).toInt() else h[1].toInt()
        val endHour = beginHour + 2
        val beginTime = Calendar.getInstance()
        beginTime.clear()
        beginTime[year, month, day, beginHour] = beginMinutes
        val endTime = Calendar.getInstance()
        endTime.clear()
        endTime[year, month, day, endHour] = beginMinutes
        val projection = arrayOf(
            CalendarContract.Instances._ID,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.EVENT_ID
        )
        val cursor = CalendarContract.Instances.query(
            mContext.contentResolver,
            projection, beginTime.timeInMillis, endTime.timeInMillis
        )
        if (cursor.count > 0) {
            // Conflict!
            return
        }
        val cr = mContext.contentResolver
        val values = ContentValues()
        values.put(CalendarContract.Events.DTSTART, beginTime.timeInMillis)
        values.put(CalendarContract.Events.DTEND, endTime.timeInMillis)
        values.put(CalendarContract.Events.TITLE, title)
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId)
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Lisbon")
        cr.insert(CalendarContract.Events.CONTENT_URI, values)
    }
}