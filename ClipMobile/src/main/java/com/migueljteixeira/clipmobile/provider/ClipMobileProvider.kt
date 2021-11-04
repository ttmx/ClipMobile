package com.migueljteixeira.clipmobile.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.migueljteixeira.clipmobile.provider.ClipMobileDatabase.Tables
import com.migueljteixeira.clipmobile.util.SelectionBuilder

class ClipMobileProvider : ContentProvider() {
    private val mApplyingBatch = ThreadLocal<Boolean?>()
    private var mDbHelper: ClipMobileDatabase? = null
    private var mDb: SQLiteDatabase? = null
    override fun onCreate(): Boolean {
        val context = context
        sUriMatcher = buildUriMatcher(context)
        mDbHelper = ClipMobileDatabase(context)
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val db = mDbHelper!!.readableDatabase
        val match = sUriMatcher!!.match(uri)
        val builder = buildSelection(uri, match)
        val query = builder
            .where(selection, selectionArgs)
            .query(db, projection, sortOrder)
        query.setNotificationUri(context!!.contentResolver, uri)
        return query
    }

    override fun getType(uri: Uri): String? {
        return when (sUriMatcher!!.match(uri)) {
            USERS -> ClipMobileContract.Users.CONTENT_TYPE
            STUDENTS -> ClipMobileContract.Students.CONTENT_TYPE
            STUDENTS_YEAR_SEMESTER -> ClipMobileContract.StudentsYearSemester.CONTENT_TYPE
            SCHEDULE_DAYS -> ClipMobileContract.ScheduleDays.CONTENT_TYPE
            SCHEDULE_CLASSES -> ClipMobileContract.ScheduleClasses.CONTENT_TYPE
            STUDENT_CLASSES -> ClipMobileContract.StudentClasses.CONTENT_TYPE
            STUDENT_CLASSES_DOCS -> ClipMobileContract.StudentClassesDocs.CONTENT_TYPE
            STUDENT_CALENDAR -> ClipMobileContract.StudentCalendar.CONTENT_TYPE
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val newItemUri: Uri?
        if (!applyingBatch()) {
            val db = mDbHelper!!.writableDatabase
            db.beginTransaction()
            try {
                newItemUri = insertInTransaction(uri, values)
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        } else {
            newItemUri = insertInTransaction(uri, values)
        }

        /*if (newItemUri != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }*/return newItemUri
    }

    private fun insertInTransaction(uri: Uri, values: ContentValues?): Uri? {
        val newItemUri: Uri = when (sUriMatcher!!.match(uri)) {
            USERS -> {
                val id = mDbHelper!!.insertUsers(values)
                if (id < 0) {
                    return null
                }
                ClipMobileContract.Users.buildUri(id.toString())
            }
            STUDENTS -> {
                val id = mDbHelper!!.insertStudents(values)
                if (id < 0) {
                    return null
                }
                ClipMobileContract.Students.buildUri(id.toString())
            }
            STUDENTS_YEAR_SEMESTER -> {
                val id = mDbHelper!!.insertStudentYears(values)
                if (id < 0) {
                    return null
                }
                ClipMobileContract.StudentsYearSemester.buildUri(id.toString())
            }
            SCHEDULE_DAYS -> {
                val id = mDbHelper!!.insertScheduleDays(values)
                if (id < 0) {
                    return null
                }
                ClipMobileContract.ScheduleDays.buildUri(id.toString())
            }
            SCHEDULE_CLASSES -> {
                val id = mDbHelper!!.insertScheduleClasses(values)
                if (id < 0) {
                    return null
                }
                ClipMobileContract.ScheduleClasses.buildUri(id.toString())
            }
            STUDENT_CLASSES -> {
                val id = mDbHelper!!.insertStudentClasses(values)
                if (id < 0) {
                    return null
                }
                ClipMobileContract.StudentClasses.buildUri(id.toString())
            }
            STUDENT_CLASSES_DOCS -> {
                val id = mDbHelper!!.insertStudentClassesDocs(values)
                if (id < 0) {
                    return null
                }
                ClipMobileContract.StudentClassesDocs.buildUri(id.toString())
            }
            STUDENT_CALENDAR -> {
                val id = mDbHelper!!.insertStudentCalendar(values)
                if (id < 0) {
                    return null
                }
                ClipMobileContract.StudentCalendar.buildUri(id.toString())
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        return newItemUri
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val count: Int
        if (!applyingBatch()) {
            val db = mDbHelper!!.writableDatabase
            db.beginTransaction()
            try {
                count = buildSelection(uri, sUriMatcher!!.match(uri))
                    .where(selection, selectionArgs)
                    .update(db, values)
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        } else {
            mDb = mDbHelper!!.writableDatabase
            count = buildSelection(uri, sUriMatcher!!.match(uri))
                .where(selection, selectionArgs)
                .update(mDb!!, values)
        }

        /*if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }*/return count
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val count: Int
        if (!applyingBatch()) {
            val db = mDbHelper!!.writableDatabase
            db.beginTransaction()
            try {
                count = buildSelection(uri, sUriMatcher!!.match(uri))
                    .where(selection, selectionArgs)
                    .delete(db)
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        } else {
            mDb = mDbHelper!!.writableDatabase
            count = buildSelection(uri, sUriMatcher!!.match(uri))
                .where(selection, selectionArgs)
                .delete(mDb!!)
        }

        /*if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }*/return count
    }

    /**
     * Allows users to do multiple inserts into a table using the same statement
     */
    private fun applyingBatch(): Boolean {
        return mApplyingBatch.get() != null && mApplyingBatch.get()!!
    }

    override fun shutdown() {
        super.shutdown()
        if (mDbHelper != null) {
            mDbHelper!!.close()
            mDbHelper = null
            mDb = null
        }
    }

    companion object {
        private var sUriMatcher: UriMatcher? = null
        private const val USERS = 1
        private const val STUDENTS = 2
        private const val STUDENTS_YEAR_SEMESTER = 3
        private const val SCHEDULE_DAYS = 4
        private const val SCHEDULE_CLASSES = 5
        private const val STUDENT_CLASSES = 6
        private const val STUDENT_CLASSES_DOCS = 7
        private const val STUDENT_CALENDAR = 8

        /**
         * Build and return a [UriMatcher] that catches all [Uri] variations supported by
         * this [ContentProvider].
         */
        private fun buildUriMatcher(context: Context?): UriMatcher {
            val matcher = UriMatcher(UriMatcher.NO_MATCH)
            val authority = context!!.packageName + ".provider"

            // Users
            matcher.addURI(authority, ClipMobileContract.PATH_USERS, USERS)

            // Students
            matcher.addURI(authority, ClipMobileContract.PATH_STUDENTS, STUDENTS)

            // Students Year-Semester
            matcher.addURI(
                authority,
                ClipMobileContract.PATH_STUDENTS_YEAR_SEMESTER,
                STUDENTS_YEAR_SEMESTER
            )

            // Students Schedule Days
            matcher.addURI(authority, ClipMobileContract.PATH_SCHEDULE_DAYS, SCHEDULE_DAYS)

            // Students Schedule Classes
            matcher.addURI(authority, ClipMobileContract.PATH_SCHEDULE_CLASSES, SCHEDULE_CLASSES)

            // Student Classes
            matcher.addURI(authority, ClipMobileContract.PATH_STUDENT_CLASSES, STUDENT_CLASSES)

            // Student Classes Docs
            matcher.addURI(
                authority,
                ClipMobileContract.PATH_STUDENT_CLASSES_DOCS,
                STUDENT_CLASSES_DOCS
            )

            // Student Calendar
            matcher.addURI(authority, ClipMobileContract.PATH_STUDENT_CALENDAR, STUDENT_CALENDAR)
            return matcher
        }

        /**
         * Builds selection using a [SelectionBuilder] to match the requested [Uri].
         */
        private fun buildSelection(uri: Uri, match: Int): SelectionBuilder {
            val builder = SelectionBuilder()
            return when (match) {
                USERS -> builder.table(Tables.USERS)
                STUDENTS -> builder.table(Tables.STUDENTS)
                STUDENTS_YEAR_SEMESTER -> builder.table(Tables.STUDENTS_YEAR_SEMESTER)
                SCHEDULE_DAYS -> builder.table(Tables.SCHEDULE_DAYS)
                SCHEDULE_CLASSES -> builder.table(Tables.SCHEDULE_CLASSES)
                STUDENT_CLASSES -> builder.table(Tables.STUDENT_CLASSES)
                STUDENT_CLASSES_DOCS -> builder.table(Tables.STUDENT_CLASSES_DOCS)
                STUDENT_CALENDAR -> builder.table(Tables.STUDENT_CALENDAR)
                else -> throw UnsupportedOperationException("Unknown uri: $uri")
            }
        }
    }
}