package com.migueljteixeira.clipmobile.provider

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.migueljteixeira.clipmobile.provider.ClipMobileContract.ScheduleClassesColumns
import com.migueljteixeira.clipmobile.provider.ClipMobileContract.ScheduleDaysColumns
import com.migueljteixeira.clipmobile.provider.ClipMobileContract.StudentCalendarColumns
import com.migueljteixeira.clipmobile.provider.ClipMobileContract.StudentClassesColumns
import com.migueljteixeira.clipmobile.provider.ClipMobileContract.StudentClassesDocsColumns
import com.migueljteixeira.clipmobile.provider.ClipMobileContract.StudentsColumns
import com.migueljteixeira.clipmobile.provider.ClipMobileContract.StudentsYearSemesterColumns
import com.migueljteixeira.clipmobile.provider.ClipMobileContract.UsersColumns
import com.migueljteixeira.clipmobile.util.DBUtils

class ClipMobileDatabase(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private var mUsersInserter: DBUtils.InsertHelper? = null
    private var mStudentsInserter: DBUtils.InsertHelper? = null
    private var mStudentsYearSemesterInserter: DBUtils.InsertHelper? = null
    private var mScheduleDaysInserter: DBUtils.InsertHelper? = null
    private var mScheduleClassesInserter: DBUtils.InsertHelper? = null
    private var mStudentClassesInserter: DBUtils.InsertHelper? = null
    private var mStudentClassesDocsInserter: DBUtils.InsertHelper? = null
    private var mStudentCalendarInserter: DBUtils.InsertHelper? = null

    interface Tables {
        companion object {
            const val USERS = "users"
            const val STUDENTS = "students"
            const val STUDENTS_YEAR_SEMESTER = "students_year_semester"
            const val SCHEDULE_DAYS = "schedule_days"
            const val SCHEDULE_CLASSES = "schedule_classes"
            const val STUDENT_CLASSES = "student_classes"
            const val STUDENT_CLASSES_DOCS = "student_classes_docs"
            const val STUDENT_CALENDAR = "student_calendar"
        }
    }

    internal interface References {
        companion object {
            const val USER_ID = "REFERENCES " + Tables.USERS +
                    "(" + BaseColumns._ID + ")"
            const val STUDENT_ID = "REFERENCES " + Tables.STUDENTS +
                    "(" + BaseColumns._ID + ")"
            const val STUDENTS_YEAR_SEMESTER_ID = "REFERENCES " + Tables.STUDENTS_YEAR_SEMESTER +
                    "(" + BaseColumns._ID + ")"
            const val STUDENT_SCHEDULE_DAY_ID = "REFERENCES " + Tables.SCHEDULE_DAYS +
                    "(" + BaseColumns._ID + ")"
            const val STUDENT_CLASSES_ID = "REFERENCES " + Tables.STUDENT_CLASSES +
                    "(" + BaseColumns._ID + ")"
        }
    }

    fun insertUsers(values: ContentValues?): Long {
        return mUsersInserter!!.insert(values)
    }

    fun insertStudents(values: ContentValues?): Long {
        return mStudentsInserter!!.insert(values)
    }

    fun insertStudentYears(values: ContentValues?): Long {
        return mStudentsYearSemesterInserter!!.insert(values)
    }

    fun insertScheduleDays(values: ContentValues?): Long {
        return mScheduleDaysInserter!!.insert(values)
    }

    fun insertScheduleClasses(values: ContentValues?): Long {
        return mScheduleClassesInserter!!.insert(values)
    }

    fun insertStudentClasses(values: ContentValues?): Long {
        return mStudentClassesInserter!!.insert(values)
    }

    fun insertStudentClassesDocs(values: ContentValues?): Long {
        return mStudentClassesDocsInserter!!.insert(values)
    }

    fun insertStudentCalendar(values: ContentValues?): Long {
        return mStudentCalendarInserter!!.insert(values)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_USERS_TABLE)
        db.execSQL(CREATE_STUDENTS_TABLE)
        db.execSQL(CREATE_STUDENTS_YEAR_SEMESTER_TABLE)
        db.execSQL(CREATE_SCHEDULE_DAYS_TABLE)
        db.execSQL(CREATE_SCHEDULE_CLASSES_TABLE)
        db.execSQL(CREATE_STUDENT_CLASSES_TABLE)
        db.execSQL(CREATE_STUDENT_CLASSES_DOCS_TABLE)
        db.execSQL(CREATE_STUDENT_CALENDAR_TABLE)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        if (!db.isReadOnly) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;")
        }
        mUsersInserter = DBUtils.InsertHelper(db, Tables.USERS)
        mStudentsInserter = DBUtils.InsertHelper(db, Tables.STUDENTS)
        mStudentsYearSemesterInserter = DBUtils.InsertHelper(db, Tables.STUDENTS_YEAR_SEMESTER)
        mScheduleDaysInserter = DBUtils.InsertHelper(db, Tables.SCHEDULE_DAYS)
        mScheduleClassesInserter = DBUtils.InsertHelper(db, Tables.SCHEDULE_CLASSES)
        mStudentClassesInserter = DBUtils.InsertHelper(db, Tables.STUDENT_CLASSES)
        mStudentClassesDocsInserter = DBUtils.InsertHelper(db, Tables.STUDENT_CLASSES_DOCS)
        mStudentCalendarInserter = DBUtils.InsertHelper(db, Tables.STUDENT_CALENDAR)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        Crashlytics.log("ClipMobileDatabse - onUpgrade from " + oldVersion + " to " + newVersion);
        if (oldVersion == 1) {
            upgradeToSecond(db)
        }
    }

    private fun upgradeToSecond(db: SQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL("DROP TABLE IF EXISTS " + Tables.STUDENTS_YEAR_SEMESTER)
            db.execSQL("DROP TABLE IF EXISTS " + Tables.SCHEDULE_DAYS)
            db.execSQL("DROP TABLE IF EXISTS " + Tables.SCHEDULE_CLASSES)
            db.execSQL("DROP TABLE IF EXISTS " + Tables.STUDENT_CLASSES)
            db.execSQL("DROP TABLE IF EXISTS " + Tables.STUDENT_CLASSES_DOCS)
            db.execSQL("DROP TABLE IF EXISTS " + Tables.STUDENT_CALENDAR)
            db.execSQL(CREATE_STUDENTS_YEAR_SEMESTER_TABLE)
            db.execSQL(CREATE_SCHEDULE_DAYS_TABLE)
            db.execSQL(CREATE_SCHEDULE_CLASSES_TABLE)
            db.execSQL(CREATE_STUDENT_CLASSES_TABLE)
            db.execSQL(CREATE_STUDENT_CLASSES_DOCS_TABLE)
            db.execSQL(CREATE_STUDENT_CALENDAR_TABLE)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    companion object {
        const val DATABASE_NAME = "clip_mobile_database"
        const val DATABASE_VERSION = 2
        private const val CREATE_USERS_TABLE = ("CREATE TABLE " + Tables.USERS
                + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                + UsersColumns.USERNAME + " TEXT NOT NULL,"
                + "UNIQUE (" + UsersColumns.USERNAME + ")"
                + ");")
        private const val CREATE_STUDENTS_TABLE = ("CREATE TABLE " + Tables.STUDENTS
                + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                + UsersColumns.REF_USERS_ID + " TEXT " + References.USER_ID + " ON DELETE CASCADE,"
                + StudentsColumns.NUMBER_ID + " TEXT NOT NULL,"
                + StudentsColumns.NUMBER + " TEXT NOT NULL"
                + ");")
        private const val CREATE_STUDENTS_YEAR_SEMESTER_TABLE =
            ("CREATE TABLE " + Tables.STUDENTS_YEAR_SEMESTER
                    + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                    + StudentsColumns.REF_STUDENTS_ID + " TEXT " + References.STUDENT_ID + " ON DELETE CASCADE,"
                    + StudentsYearSemesterColumns.YEAR + " TEXT NOT NULL,"
                    + StudentsYearSemesterColumns.SEMESTER + " TEXT NOT NULL"
                    + ");")
        private const val CREATE_SCHEDULE_DAYS_TABLE = ("CREATE TABLE " + Tables.SCHEDULE_DAYS
                + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                + StudentsYearSemesterColumns.REF_STUDENTS_YEAR_SEMESTER_ID + " TEXT " +
                References.STUDENTS_YEAR_SEMESTER_ID + " ON DELETE CASCADE,"
                + ScheduleDaysColumns.DAY + " TEXT NOT NULL"
                + ");")
        private const val CREATE_SCHEDULE_CLASSES_TABLE = ("CREATE TABLE " + Tables.SCHEDULE_CLASSES
                + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                + ScheduleDaysColumns.REF_SCHEDULE_DAYS_ID + " TEXT " +
                References.STUDENT_SCHEDULE_DAY_ID + " ON DELETE CASCADE,"
                + ScheduleClassesColumns.NAME + " TEXT NOT NULL,"
                + ScheduleClassesColumns.NAME_ABBREVIATION + " TEXT NOT NULL,"
                + ScheduleClassesColumns.TYPE + " TEXT NOT NULL,"
                + ScheduleClassesColumns.HOUR_START + " TEXT NOT NULL,"
                + ScheduleClassesColumns.HOUR_END + " TEXT NOT NULL,"
                + ScheduleClassesColumns.ROOM + " TEXT"
                + ");")
        private const val CREATE_STUDENT_CLASSES_TABLE = ("CREATE TABLE " + Tables.STUDENT_CLASSES
                + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                + StudentsYearSemesterColumns.REF_STUDENTS_YEAR_SEMESTER_ID + " TEXT " +
                References.STUDENTS_YEAR_SEMESTER_ID + " ON DELETE CASCADE,"
                + StudentClassesColumns.NAME + " TEXT NOT NULL,"
                + StudentClassesColumns.NUMBER + " TEXT NOT NULL,"
                + StudentClassesColumns.SEMESTER + " TEXT NOT NULL"
                + ");")
        private const val CREATE_STUDENT_CLASSES_DOCS_TABLE =
            ("CREATE TABLE " + Tables.STUDENT_CLASSES_DOCS
                    + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                    + StudentClassesColumns.REF_STUDENT_CLASSES_ID + " TEXT " +
                    References.STUDENT_CLASSES_ID + " ON DELETE CASCADE,"
                    + StudentClassesDocsColumns.NAME + " TEXT NOT NULL,"
                    + StudentClassesDocsColumns.URL + " TEXT NOT NULL,"
                    + StudentClassesDocsColumns.DATE + " TEXT NOT NULL,"
                    + StudentClassesDocsColumns.SIZE + " TEXT NOT NULL,"
                    + StudentClassesDocsColumns.TYPE + " TEXT NOT NULL"
                    + ");")
        private const val CREATE_STUDENT_CALENDAR_TABLE = ("CREATE TABLE " + Tables.STUDENT_CALENDAR
                + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                + StudentsYearSemesterColumns.REF_STUDENTS_YEAR_SEMESTER_ID + " TEXT " +
                References.STUDENTS_YEAR_SEMESTER_ID + " ON DELETE CASCADE,"
                + StudentCalendarColumns.IS_EXAM + " INTEGER,"
                + StudentCalendarColumns.NAME + " TEXT NOT NULL,"
                + StudentCalendarColumns.DATE + " TEXT NOT NULL,"
                + StudentCalendarColumns.HOUR + " TEXT NOT NULL,"
                + StudentCalendarColumns.ROOMS + " TEXT,"
                + StudentCalendarColumns.NUMBER + " TEXT"
                + ");")
    }
}