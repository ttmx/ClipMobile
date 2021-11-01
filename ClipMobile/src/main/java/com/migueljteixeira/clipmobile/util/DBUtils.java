package com.migueljteixeira.clipmobile.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.migueljteixeira.clipmobile.entities.Student;
import com.migueljteixeira.clipmobile.entities.StudentCalendar;
import com.migueljteixeira.clipmobile.entities.StudentClass;
import com.migueljteixeira.clipmobile.entities.StudentClassDoc;
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
            student_year.setId(id);
            student_year.setYear(year);

            student.addYear(student_year);
        }
        studentYears_cursor.close();

        return student;
    }

    public static void insertStudentYears(Context mContext, String studentId, Student student) {

        // For every year, lets add the 2 semesters (and the trimester) already
        for(StudentYearSemester year : student.getYears()) {

            for(int semester=1; semester<=3; semester++) {
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

    public static void deleteStudentsInfo(Context mContext, String studentYearSemesterId) {

        // Delete Students Info
        mContext.getContentResolver().delete(ClipMobileContract.StudentsYearSemester.CONTENT_URI, null, null
                /*ClipMobileContract.StudentsYearSemester._ID + "=?",
                new String[] { studentYearSemesterId }*/);

        // TODO: what to do?
    }

    /*
     * ////////////////////////////// STUDENT SCHEDULE  //////////////////////////////
     */

    public static String getYearSemesterId(Context mContext, String studentId, String year, int semester) {

        // First, we get the yearSemester ID
        final Cursor studentYearSemester_cursor = mContext.getContentResolver().query(
                ClipMobileContract.StudentsYearSemester.CONTENT_URI,
                new String[] { ClipMobileContract.StudentsYearSemester._ID },
                ClipMobileContract.Students.REF_STUDENTS_ID + "=? AND " +
                        ClipMobileContract.StudentsYearSemester.YEAR + "=? AND " +
                        ClipMobileContract.StudentsYearSemester.SEMESTER + "=?",
                new String[] { studentId, year, String.valueOf(semester) }, null);

        if(studentYearSemester_cursor.getCount() == 0) {
            studentYearSemester_cursor.close();

//            Crashlytics.log("getYearSemesterId - COUNT==0");

            return null;
        }

        studentYearSemester_cursor.moveToFirst();
        String yearSemesterId = studentYearSemester_cursor.getString(0);

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
            String classId = studentClasses_cursor.getString(0);
            String className = studentClasses_cursor.getString(2);
            String classNumber = studentClasses_cursor.getString(3);
            int classSemester = studentClasses_cursor.getInt(4);

            StudentClass studentClass = new StudentClass();
            studentClass.setId(classId);
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

        // For two semesters (and one trimester)
        for (int semester = 1; semester <= 3; semester++) {
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

                // Set class Id
                String classId = String.valueOf(ContentUris.parseId(uri));
                cl.setId(classId);
            }

        }

    }

    /*
     * ////////////////////////////// STUDENT CLASSES DOCS //////////////////////////////
     */

    public static Student getStudentClassesDocs(Context mContext, String studentClassId, String docType) {

        // Get the student classes docs
        final Cursor studentClassesDocs_cursor = mContext.getContentResolver().query(
                ClipMobileContract.StudentClassesDocs.CONTENT_URI, null,
                ClipMobileContract.StudentClasses.REF_STUDENT_CLASSES_ID + "=? AND " +
                        ClipMobileContract.StudentClassesDocs.TYPE + "=?",
                new String[] { studentClassId, docType }, null);

        if(studentClassesDocs_cursor.getCount() == 0) {
            studentClassesDocs_cursor.close();

            return null;
        }

        Student student = new Student();
        while(studentClassesDocs_cursor.moveToNext()) {
            String docName = studentClassesDocs_cursor.getString(2);
            String docUrl = studentClassesDocs_cursor.getString(3);
            String docDate = studentClassesDocs_cursor.getString(4);
            String docSize = studentClassesDocs_cursor.getString(5);

            StudentClassDoc studentClassDoc = new StudentClassDoc();
            studentClassDoc.setName(docName);
            studentClassDoc.setUrl(docUrl);
            studentClassDoc.setDate(docDate);
            studentClassDoc.setSize(docSize);
            studentClassDoc.setType(docType);

            student.addClassDoc(studentClassDoc);
        }
        studentClassesDocs_cursor.close();

        return student;
    }

    public static void insertStudentClassesDocs(Context mContext, String studentClassId, Student student) {
        List<StudentClassDoc> classDocs = student.getClassesDocs();

        System.out.println("studentClassId !!!-> " + studentClassId);
        System.out.println("classes docs size -> " + classDocs.size());

        for(StudentClassDoc cl : classDocs) {
            ContentValues values = new ContentValues();
            values.put(ClipMobileContract.StudentClasses.REF_STUDENT_CLASSES_ID, studentClassId);
            values.put(ClipMobileContract.StudentClassesDocs.NAME, cl.getName());
            values.put(ClipMobileContract.StudentClassesDocs.URL, cl.getUrl());
            values.put(ClipMobileContract.StudentClassesDocs.DATE, cl.getDate());
            values.put(ClipMobileContract.StudentClassesDocs.SIZE, cl.getSize());
            values.put(ClipMobileContract.StudentClassesDocs.TYPE, cl.getType());

            Uri uri = mContext.getContentResolver().insert(ClipMobileContract.StudentClassesDocs.CONTENT_URI, values);
            System.out.println("class doc inserted! " + uri.getPath());
        }

    }

    /*
     * ////////////////////////////// STUDENT CALENDAR  //////////////////////////////
     */

    public static Student getStudentCalendar(Context mContext, String yearSemesterId) {

        // Get student calendar
        final Cursor studentCalendar_cursor = mContext.getContentResolver().query(
                ClipMobileContract.StudentCalendar.CONTENT_URI, null,
                ClipMobileContract.StudentsYearSemester.REF_STUDENTS_YEAR_SEMESTER_ID + "=?",
                new String[] { yearSemesterId }, null);

        if(studentCalendar_cursor.getCount() == 0) {
            studentCalendar_cursor.close();

            return null;
        }

        Student student = new Student();
        while(studentCalendar_cursor.moveToNext()) {
            int calendarAppointmentIsExam = studentCalendar_cursor.getInt(2);
            String calendarAppointmentName = studentCalendar_cursor.getString(3);
            String calendarAppointmentDate = studentCalendar_cursor.getString(4);
            String calendarAppointmentHour = studentCalendar_cursor.getString(5);
            String calendarAppointmentRooms = studentCalendar_cursor.getString(6);
            String calendarAppointmentNumber = studentCalendar_cursor.getString(7);

            System.out.println("REQUEST NAME:: " + calendarAppointmentName);

            StudentCalendar calendarAppointement = new StudentCalendar();
            calendarAppointement.setName(calendarAppointmentName);
            calendarAppointement.setDate(calendarAppointmentDate);
            calendarAppointement.setHour(calendarAppointmentHour);
            calendarAppointement.setRooms(calendarAppointmentRooms);
            calendarAppointement.setNumber(calendarAppointmentNumber);

            student.addStudentCalendarAppointment((calendarAppointmentIsExam == 1), calendarAppointement);
        }
        studentCalendar_cursor.close();

        return student;
    }

    public static void insertStudentCalendar(Context mContext, String yearSemesterId, Student student) {
        Map<Boolean, List<StudentCalendar>> studentCalendar = student.getStudentCalendar();

        System.out.println("yearSemesterId !!!-> " + yearSemesterId);
        System.out.println("calendar size -> " + studentCalendar.size());

        // For two types (exam and test)
        for (int type = 0; type <= 1; type++) {
            List<StudentCalendar> calendar = studentCalendar.get(type==1);

            // we don't have any calendar of this type, yet
            if (calendar == null)
                continue;

            for (StudentCalendar calendarAppointment : calendar) {
                ContentValues values = new ContentValues();
                values.put(ClipMobileContract.StudentsYearSemester.REF_STUDENTS_YEAR_SEMESTER_ID, yearSemesterId);
                values.put(ClipMobileContract.StudentCalendar.IS_EXAM, type==1);
                values.put(ClipMobileContract.StudentCalendar.NAME, calendarAppointment.getName());
                values.put(ClipMobileContract.StudentCalendar.DATE, calendarAppointment.getDate());
                values.put(ClipMobileContract.StudentCalendar.HOUR, calendarAppointment.getHour());
                values.put(ClipMobileContract.StudentCalendar.ROOMS, calendarAppointment.getRooms());
                values.put(ClipMobileContract.StudentCalendar.NUMBER, calendarAppointment.getNumber());

                Uri uri = mContext.getContentResolver().insert(ClipMobileContract.StudentCalendar.CONTENT_URI, values);
                System.out.println("calendar appointment inserted! " + uri.getPath());
            }
        }
    }


}
