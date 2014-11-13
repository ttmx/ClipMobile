package com.migueljteixeira.clipmobile.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.migueljteixeira.clipmobile.entities.Student;
import com.migueljteixeira.clipmobile.entities.StudentClass;
import com.migueljteixeira.clipmobile.entities.StudentScheduleClass;
import com.migueljteixeira.clipmobile.entities.StudentYearSemester;
import com.migueljteixeira.clipmobile.entities.User;
import com.migueljteixeira.clipmobile.provider.ClipMobileContract;

import java.util.List;
import java.util.Map;

public class DBUtils {

    /*
     * ////////////////////////////// USERS //////////////////////////////
     */

    public static long getUserId(Context mContext, String username) {

        final Cursor user_cursor = mContext.getContentResolver().query(
                ClipMobileContract.Users.CONTENT_URI,
                new String[] { ClipMobileContract.Users._ID },
                ClipMobileContract.Users.USERNAME + "=?", new String[] { username }, null);

        if(user_cursor.getCount() == 0) {
            user_cursor.close();

            return -1;
        }

        user_cursor.moveToFirst();
        long userId = user_cursor.getInt(0);
        user_cursor.close();

        return userId;
    }

    public static long createUser(Context mContext, String username) {
        ContentValues values = new ContentValues();
        values.put(ClipMobileContract.Users.USERNAME, username);

        Uri uri = mContext.getContentResolver().insert(ClipMobileContract.Users.CONTENT_URI, values);
        System.out.println("user inserted! " + uri.getPath());

        return ContentUris.parseId(uri);
    }

    /*
     * ////////////////////////////// STUDENTS //////////////////////////////
     */

    public static User getStudents(Context mContext, long userId) {

        final Cursor students_cursor = mContext.getContentResolver().query(
                ClipMobileContract.Students.CONTENT_URI, null,
                ClipMobileContract.Users.REF_USERS_ID + "=?", new String[] { String.valueOf(userId) }, null);

        User user = new User();
        while(students_cursor.moveToNext()) {
                String id = students_cursor.getString(0);
                String number_id = students_cursor.getString(2);
                String number = students_cursor.getString(3);

            Student student = new Student();
            student.setId(id);
            student.setNumberId(number_id);
            student.setNumber(number);

            user.addStudent(student);
        }
        students_cursor.close();

        return user;
    }

    public static void insertStudentsNumbers(Context mContext, long userId, User user) {

        for(Student student : user.getStudents()) {
            ContentValues values = new ContentValues();
            values.put(ClipMobileContract.Users.REF_USERS_ID, userId);
            values.put(ClipMobileContract.Students.NUMBER_ID, student.getNumberId());
            values.put(ClipMobileContract.Students.NUMBER, student.getNumber());

            Uri uri = mContext.getContentResolver().insert(ClipMobileContract.Students.CONTENT_URI, values);
            System.out.println("student inserted! " + uri.getPath());

            String newId = String.valueOf( ContentUris.parseId(uri) );
            student.setId(newId);
        }

    }

    /*
     * ////////////////////////////// STUDENTS YEARS //////////////////////////////
     */

    public static Student getStudentYears(Context mContext, String student_id) {

        // Get student years (for the 1st semester)
        final Cursor studentYears_cursor = mContext.getContentResolver().query(
                ClipMobileContract.StudentsYearSemester.CONTENT_URI, null,
                ClipMobileContract.Students.REF_STUDENTS_ID + "=? AND " +
                ClipMobileContract.StudentsYearSemester.SEMESTER + "=?", new String[] { student_id, "1" }, null);

        Student student = new Student();
        while(studentYears_cursor.moveToNext()) {
            String id = studentYears_cursor.getString(0);
            String year = studentYears_cursor.getString(2);

            StudentYearSemester student_year = new StudentYearSemester();
            //student_year.setId(id);
            student_year.setYear(year);

            student.addYear(student_year);
        }
        studentYears_cursor.close();

        return student;
    }

    public static void insertStudentYears(Context mContext, String studentId, Student student) {

        // For every year, lets add the 2 semesters already
        for(StudentYearSemester year : student.getYears()) {

            for(int semester=1; semester<=2; semester++) {
                ContentValues values = new ContentValues();
                values.put(ClipMobileContract.Students.REF_STUDENTS_ID, studentId);
                values.put(ClipMobileContract.StudentsYearSemester.YEAR, year.getYear());
                values.put(ClipMobileContract.StudentsYearSemester.SEMESTER, semester);

                Uri uri = mContext.getContentResolver().insert(ClipMobileContract.StudentsYearSemester.CONTENT_URI, values);
                System.out.println("student year semester inserted! " + uri.getPath());

                String newId = String.valueOf( ContentUris.parseId(uri) );
                year.setId(newId);
            }
        }

    }

    /*
     * ////////////////////////////// UPDATE STUDENT INFO //////////////////////////////
     */

    public static void deleteStudentsNumbers(Context mContext, long userId) {

        // Delete Student Numbers
        mContext.getContentResolver().delete(ClipMobileContract.Students.CONTENT_URI,
                ClipMobileContract.Users.REF_USERS_ID + "=?",
                new String[] { String.valueOf(userId) });
    }

    /*
     * ////////////////////////////// STUDENT SCHEDULE  //////////////////////////////
     */

    public static String getYearSemesterId(Context mContext, String studentId, String year, String semester) {

        // First, we get the yearSemester ID
        final Cursor studentYearSemester_cursor = mContext.getContentResolver().query(
                ClipMobileContract.StudentsYearSemester.CONTENT_URI,
                new String[] { ClipMobileContract.StudentsYearSemester._ID },
                ClipMobileContract.Students.REF_STUDENTS_ID + "=? AND " +
                        ClipMobileContract.StudentsYearSemester.YEAR + "=? AND " +
                        ClipMobileContract.StudentsYearSemester.SEMESTER + "=?",
                new String[] { studentId, year, semester }, null);

        if(studentYearSemester_cursor.getCount() == 0) {
            studentYearSemester_cursor.close();

            System.out.println("WHHHHHHHHHHHHHHHHHHHAT?!?!?!?!?");

            return null;
        }

        studentYearSemester_cursor.moveToFirst();
        String yearSemesterId = studentYearSemester_cursor.getString(0);
        System.out.println("--> yearSemesterId: " + yearSemesterId);
        studentYearSemester_cursor.close();

        return yearSemesterId;
    }

    public static Student getStudentSchedule(Context mContext, String yearSemesterId) {

        // Then, we get the schedule days
        final Cursor studentScheduleDays_cursor = mContext.getContentResolver().query(
                ClipMobileContract.ScheduleDays.CONTENT_URI,
                    new String[] { ClipMobileContract.ScheduleDays._ID, ClipMobileContract.ScheduleDays.DAY },
                ClipMobileContract.StudentsYearSemester.REF_STUDENTS_YEAR_SEMESTER_ID + "=?",
                    new String[] { yearSemesterId }, null);

        if(studentScheduleDays_cursor.getCount() == 0) {
            studentScheduleDays_cursor.close();

            return null;
        }

        Student student = new Student();
        while(studentScheduleDays_cursor.moveToNext()) {
            String scheduleDayId = studentScheduleDays_cursor.getString(0);
            int scheduleDay = studentScheduleDays_cursor.getInt(1);
            System.out.println("--> scheduleDay: " + scheduleDay);

            // Finally, we get the schedule classes
            final Cursor studentScheduleClasses_cursor = mContext.getContentResolver().query(
                    ClipMobileContract.ScheduleClasses.CONTENT_URI, null,
                    ClipMobileContract.ScheduleDays.REF_SCHEDULE_DAYS_ID + "=?",
                        new String[] { scheduleDayId }, null);

            while(studentScheduleClasses_cursor.moveToNext()) {
                String name = studentScheduleClasses_cursor.getString(2);
                String nameAbbreviation = studentScheduleClasses_cursor.getString(3);
                String type = studentScheduleClasses_cursor.getString(4);
                String hourStart = studentScheduleClasses_cursor.getString(5);
                String hourEnd = studentScheduleClasses_cursor.getString(6);
                String room = studentScheduleClasses_cursor.getString(7);

                StudentScheduleClass scheduleClass = new StudentScheduleClass();
                scheduleClass.setName(name);
                scheduleClass.setNameMin(nameAbbreviation);
                scheduleClass.setType(type);
                scheduleClass.setHourStart(hourStart);
                scheduleClass.setHourEnd(hourEnd);
                scheduleClass.setRoom(room);

                student.addScheduleClass(scheduleDay, scheduleClass);
            }
            studentScheduleClasses_cursor.close();

        }
        studentScheduleDays_cursor.close();

        return student;
    }

    public static void insertStudentSchedule(Context mContext, String yearSemesterId, Student student) {

        Map<Integer, List<StudentScheduleClass>> schedule = student.getScheduleClasses();

        System.out.println("yearSemesterId !!!-> " + yearSemesterId);
        System.out.println("schedulesize -> " + schedule.size());

        // From monday(2) to friday(6)
        for(int day=2; day<=6; day++) {
            System.out.println("dia: " + day);

            // If we don't have classes today, continue
            if(schedule.get(day) == null) {
                System.out.println("UPS! dia: " + day);
                continue;
            }

            ContentValues values = new ContentValues();
            values.put(ClipMobileContract.StudentsYearSemester.REF_STUDENTS_YEAR_SEMESTER_ID, yearSemesterId);
            values.put(ClipMobileContract.ScheduleDays.DAY, day);

            Uri uri = mContext.getContentResolver().insert(ClipMobileContract.ScheduleDays.CONTENT_URI, values);
            System.out.println("schedule day inserted! " + uri.getPath());

            String dayId = String.valueOf(ContentUris.parseId(uri));

            for(StudentScheduleClass classes : schedule.get(day)) {

                System.out.println("SCHEDULE class!!!  dayID:" + dayId + " , name:" + classes.getName()
                        + ", type:" + classes.getType() + ", hour:" + classes.getHourStart() + " , " + classes.getHourEnd()
                        + ", room:" + classes.getRoom());

                values = new ContentValues();
                values.put(ClipMobileContract.ScheduleDays.REF_SCHEDULE_DAYS_ID, dayId);
                values.put(ClipMobileContract.ScheduleClasses.NAME, classes.getName());
                values.put(ClipMobileContract.ScheduleClasses.NAME_ABBREVIATION, classes.getNameMin());
                values.put(ClipMobileContract.ScheduleClasses.TYPE, classes.getType());
                values.put(ClipMobileContract.ScheduleClasses.HOUR_START, classes.getHourStart());
                values.put(ClipMobileContract.ScheduleClasses.HOUR_END, classes.getHourEnd());
                values.put(ClipMobileContract.ScheduleClasses.ROOM, classes.getRoom());

                uri = mContext.getContentResolver().insert(ClipMobileContract.ScheduleClasses.CONTENT_URI, values);
                System.out.println("schedule class inserted! " + uri.getPath());

            }

        }

    }

    /*
     * ////////////////////////////// STUDENT CLASSES  //////////////////////////////
     */

    public static Student getStudentClasses(Context mContext, String yearSemesterId) {

        // Get the student classes
        final Cursor studentClasses_cursor = mContext.getContentResolver().query(
                ClipMobileContract.StudentClasses.CONTENT_URI, null,
                ClipMobileContract.StudentsYearSemester.REF_STUDENTS_YEAR_SEMESTER_ID + "=?",
                new String[] { yearSemesterId }, null);

        if(studentClasses_cursor.getCount() == 0) {
            studentClasses_cursor.close();

            return null;
        }

        Student student = new Student();
        while(studentClasses_cursor.moveToNext()) {
            String className = studentClasses_cursor.getString(2);
            String classNumber = studentClasses_cursor.getString(3);
            int classSemester = studentClasses_cursor.getInt(4);

            StudentClass studentClass = new StudentClass();
            studentClass.setName(className);
            studentClass.setNumber(classNumber);
            studentClass.setSemester(classSemester);

            student.addStudentClass(classSemester, studentClass);
        }
        studentClasses_cursor.close();

        return student;
    }

    public static void insertStudentClasses(Context mContext, String yearSemesterId, Student student) {
        Map<Integer, List<StudentClass>> classes = student.getClasses();

        System.out.println("yearSemesterId !!!-> " + yearSemesterId);
        System.out.println("classes size -> " + classes.size());

        // For two semesters
        for (int semester = 1; semester <= 2; semester++) {
            List<StudentClass> studentClass = classes.get(semester);

            // we don't have classes in this semester, yet
            if(studentClass == null)
                continue;

            for(StudentClass cl : studentClass) {
                ContentValues values = new ContentValues();
                values.put(ClipMobileContract.StudentsYearSemester.REF_STUDENTS_YEAR_SEMESTER_ID, yearSemesterId);
                values.put(ClipMobileContract.StudentClasses.NAME, cl.getName());
                values.put(ClipMobileContract.StudentClasses.NUMBER, cl.getNumber());
                values.put(ClipMobileContract.StudentClasses.SEMESTER, cl.getSemester());

                Uri uri = mContext.getContentResolver().insert(ClipMobileContract.StudentClasses.CONTENT_URI, values);
                System.out.println("class inserted! " + uri.getPath());
            }

        }

    }


}
