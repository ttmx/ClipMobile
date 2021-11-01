package com.migueljteixeira.clipmobile.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import com.migueljteixeira.clipmobile.entities.Student;
import com.migueljteixeira.clipmobile.entities.StudentCalendar;
import com.migueljteixeira.clipmobile.entities.User;
import com.migueljteixeira.clipmobile.enums.Result;
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException;
import com.migueljteixeira.clipmobile.network.StudentCalendarRequest;
import com.migueljteixeira.clipmobile.network.StudentClassesDocsRequest;
import com.migueljteixeira.clipmobile.network.StudentClassesRequest;
import com.migueljteixeira.clipmobile.network.StudentRequest;
import com.migueljteixeira.clipmobile.network.StudentScheduleRequest;
import com.migueljteixeira.clipmobile.settings.ClipSettings;
import com.migueljteixeira.clipmobile.util.tasks.BaseTask;
import com.migueljteixeira.clipmobile.util.tasks.GetStudentCalendarTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentTools {

    public static boolean isNetworkConnected(Context mContext) {
        ConnectivityManager conMan = (ConnectivityManager) mContext.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        return conMan != null && conMan.getActiveNetworkInfo().isConnected();
    }

    public static Result signIn(Context mContext, String username, String password)
            throws ServerUnavailableException {

        // Check for connectivity
        if (!isNetworkConnected(mContext))
            return Result.OFFLINE;

        // Sign in the user, and returns Students available
        User user = StudentRequest.signIn(mContext, username, password);

        // Invalid credentials
        if (!user.hasStudents())
            return Result.ERROR;

        long userId = DBUtils.getUserId(mContext, username);

        // If the user doesn't exist, create a new one
        if (userId == -1) {
            userId = DBUtils.createUser(mContext, username);

            // Insert Students
            DBUtils.insertStudentsNumbers(mContext, userId, user);
        }

        // User is now logged in
        ClipSettings.setLoggedInUser(mContext, userId, username, password);

        return Result.SUCCESS;
    }

    /*
     * ////////////////////////////// STUDENT, STUDENTYEARS, STUDENTNUMBERS  //////////////////////////////
     */

    public static User getStudents(Context mContext, long userId) {

        return DBUtils.getStudents(mContext, userId);
    }

    public static Student getStudentsYears(Context mContext, String studentId, String studentNumberId)
            throws ServerUnavailableException {

        Student student = DBUtils.getStudentYears(mContext, studentId);

        System.out.println("has " + student.hasStudentYears());

        if (student.hasStudentYears())
            return student;

        System.out.println("net " + !isNetworkConnected(mContext));

        // Check for connectivity
        if (!isNetworkConnected(mContext))
            return null;

        // Get student years from the server
        student = StudentRequest.getStudentsYears(mContext, studentNumberId);

        // Insert Students
        DBUtils.insertStudentYears(mContext, studentId, student);

        return student;
    }

    public static User updateStudentNumbersAndYears(Context mContext, long userId)
            throws ServerUnavailableException {

        System.out.println("request!");

        // Get (new) studentsNumbers from the server
        User user = StudentRequest.getStudentsNumbers(mContext);

        System.out.println("deleting!");

        // Delete studentsNumbers and studentsYears
        DBUtils.deleteStudentsNumbers(mContext, userId);

        System.out.println("inserting!");

        // Insert Students
        DBUtils.insertStudentsNumbers(mContext, userId, user);

        return user;
    }

    public static Student updateStudentPage(Context mContext, String studentId, String studentNumberId,
                                            String studentYearSemesterId)
            throws ServerUnavailableException {

        System.out.println("request!");

        // Get (new) student info from the server
        Student student = StudentRequest.getStudentsYears(mContext, studentNumberId);

        System.out.println("deleting!");

        // Delete students info
        DBUtils.deleteStudentsInfo(mContext, studentYearSemesterId);

        System.out.println("inserting!");

        // Insert students info
        DBUtils.insertStudentYears(mContext, studentId, student);

        return student;
    }

    /*
     * ////////////////////////////// STUDENT SCHEDULE  //////////////////////////////
     */


    public static Student getStudentSchedule(Context mContext, String studentId, String year, String yearFormatted,
                                             int semester, String studentNumberId)
            throws ServerUnavailableException {

        // First, we get the yearSemesterId
        String yearSemesterId = DBUtils.getYearSemesterId(mContext, studentId, year, semester);

        Student student = DBUtils.getStudentSchedule(mContext, yearSemesterId);

        System.out.println("has " + (student != null));

        if (student != null)
            return student;

        System.out.println("net " + !isNetworkConnected(mContext));

        // Check for connectivity
        if (!isNetworkConnected(mContext))
            return null;

        // Get student schedule from the server
        student = StudentScheduleRequest.getSchedule(mContext, studentNumberId, yearFormatted, semester);

        System.out.println("schedule request done!");

        // Insert schedule on database
        DBUtils.insertStudentSchedule(mContext, yearSemesterId, student);

        System.out.println("schedule inserted!");

        return student;
    }

    /*
     * ////////////////////////////// STUDENT CLASSES  //////////////////////////////
     */


    public static Student getStudentClasses(Context mContext, String studentId, String year, String yearFormatted,
                                            int semester, String studentNumberId)
            throws ServerUnavailableException {

        // First, we get the yearSemesterId
        String yearSemesterId = DBUtils.getYearSemesterId(mContext, studentId, year, semester);

        Student student = DBUtils.getStudentClasses(mContext, yearSemesterId);

        System.out.println("has " + (student != null));

        if (student != null)
            return student;

        System.out.println("net " + !isNetworkConnected(mContext));

        // Check for connectivity
        if (!isNetworkConnected(mContext))
            return null;

        // Get student classes from the server
        student = StudentClassesRequest.getClasses(mContext, studentNumberId, yearFormatted);

        System.out.println("classes request done!");

        // Insert classes on database
        DBUtils.insertStudentClasses(mContext, yearSemesterId, student);

        System.out.println("classes inserted!");

        return student;
    }

    public static Student getStudentClassesDocs(Context mContext, String studentClassId, String yearFormatted,
                                                int semester, String studentNumberId, String studentClassSelected,
                                                String docType)
            throws ServerUnavailableException {

        Student student = DBUtils.getStudentClassesDocs(mContext, studentClassId, docType);

        Log.d("StudentTools","has " + (student != null));

        if (student != null)
            return student;

        Log.d("StudentTools","net " + !isNetworkConnected(mContext));

        // Check for connectivity
        if (!isNetworkConnected(mContext))
            return null;

        // Get student classes docs from the server
        student = StudentClassesDocsRequest.getClassesDocs(mContext, studentNumberId,
                yearFormatted, semester, studentClassSelected, docType);

        Log.d("StudentTools","classes docs request done!");

        // Insert classes docs on database
        DBUtils.insertStudentClassesDocs(mContext, studentClassId, student);

        Log.d("StudentTools","classes docs inserted!");

        return student;
    }

    /*
     * ////////////////////////////// STUDENT CALENDAR  //////////////////////////////
     */

    public static Student getStudentCalendar(Context mContext, String studentId, String year, String yearFormatted,
                                             int semester, String studentNumberId)
            throws ServerUnavailableException {

        // First, we get the yearSemesterId
        String yearSemesterId = DBUtils.getYearSemesterId(mContext, studentId, year, semester);

        Student student = DBUtils.getStudentCalendar(mContext, yearSemesterId);

        System.out.println("has " + (student != null));

        if (student != null)
            return student;

        System.out.println("net " + !isNetworkConnected(mContext));

        // Check for connectivity
        if (!isNetworkConnected(mContext))
            return null;

        // ---- EXAM CALENDAR ----

        // Get student exam calendar from the server
        student = new Student();
        StudentCalendarRequest.getExamCalendar(mContext, student, studentNumberId, yearFormatted, semester);

        // ---- TEST CALENDAR ----

        // Get student test calendar from the server
        StudentCalendarRequest.getTestCalendar(mContext, student, studentNumberId, yearFormatted, semester);

        System.out.println("calendar request done!");

        // Insert calendar on database
        DBUtils.insertStudentCalendar(mContext, yearSemesterId, student);

        System.out.println("calendar inserted!");

        return student;
    }

    public static Map<Long, String> confirmExportCalendar(Context mContext) {

        final String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                CalendarContract.Calendars.OWNER_ACCOUNT,                 // 3
        };

        // The indices for the projection array above.
        final int PROJECTION_ID_INDEX = 0;
        final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
        final int PROJECTION_DISPLAY_NAME_INDEX = 2;
        final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

        // Run query
        ContentResolver cr = mContext.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";
        String[] selectionArgs = new String[]{"com.google"};

        // Submit the query and get a Cursor object back.
        Cursor cursor = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        Map<Long, String> calendars_names = new HashMap<Long, String>();
        while (cursor.moveToNext()) {
            long calID;
            String displayName;
            String accountName;
            String ownerName;

            // Get the field values
            calID = cursor.getLong(PROJECTION_ID_INDEX);
            displayName = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            if (displayName.equalsIgnoreCase(accountName) &&
                    accountName.equalsIgnoreCase(ownerName)) {

                calendars_names.put(calID, ownerName);
            }
        }

        cursor.close();

        return calendars_names;
    }

    public static void exportCalendar(final Context mContext, final long calendarId) {

        GetStudentCalendarTask mTask = new GetStudentCalendarTask(mContext, new BaseTask.OnTaskFinishedListener<Student>() {
            @Override
            public void onTaskFinished(Student result) {

                Map<Boolean, List<StudentCalendar>> calendar = result.getStudentCalendar();

                for (Map.Entry<Boolean, List<StudentCalendar>> event : calendar.entrySet()) {
                    boolean isExam = event.getKey();
                    List<StudentCalendar> calendarEvent = event.getValue();

                    for (StudentCalendar e : calendarEvent) {
                        String name = e.getName();
                        String date = e.getDate();
                        String hour = e.getHour();

                        insertEvent(mContext, calendarId, isExam, name, date, hour);
                    }
                }

                Toast.makeText(mContext, "Calendário exportado com sucesso!",
                        Toast.LENGTH_LONG).show();
            }
        });
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        AndroidUtils.executeOnPool(mTask);

    }

    private static void insertEvent(Context mContext, long calendarId, boolean isExam,
                                    String name, String date, String hour) {
        String title = "[TESTE] ";
        if (isExam) {
            title = "[EXAME] ";

            hour = hour.split("-")[0];
        }

        // Title
        title += name;

        // Date
        int year = Integer.parseInt(date.split("-")[0]);
        int month = Integer.parseInt(date.split("-")[1]) - 1;
        int day = Integer.parseInt(date.split("-")[2]);

        // Hour
        String[] h = hour.split(":");
        int begin_hour = Integer.parseInt(h[0]);

        int begin_minutes;
        if (isExam)
            begin_minutes = Integer.parseInt(h[1].substring(0, h.length));
        else
            begin_minutes = Integer.parseInt(h[1]);

        int end_hour = begin_hour + 2;
        int end_minutes = begin_minutes;

        Calendar beginTime = Calendar.getInstance();
        beginTime.clear();
        beginTime.set(year, month, day, begin_hour, begin_minutes);
        Calendar endTime = Calendar.getInstance();
        endTime.clear();
        endTime.set(year, month, day, end_hour, end_minutes);

        String[] projection = new String[]{
                CalendarContract.Instances._ID,
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.END,
                CalendarContract.Instances.EVENT_ID};
        Cursor cursor = CalendarContract.Instances.query(mContext.getContentResolver(),
                projection, beginTime.getTimeInMillis(), endTime.getTimeInMillis());

        if (cursor.getCount() > 0) {
            // Conflict!
            return;
        }

        ContentResolver cr = mContext.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Lisbon");

        cr.insert(CalendarContract.Events.CONTENT_URI, values);
    }

}
