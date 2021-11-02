package com.migueljteixeira.clipmobile.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.migueljteixeira.clipmobile.provider.ClipMobileContract.ScheduleClasses;
import com.migueljteixeira.clipmobile.provider.ClipMobileContract.ScheduleDays;
import com.migueljteixeira.clipmobile.provider.ClipMobileContract.StudentCalendar;
import com.migueljteixeira.clipmobile.provider.ClipMobileContract.StudentClasses;
import com.migueljteixeira.clipmobile.provider.ClipMobileContract.StudentClassesDocs;
import com.migueljteixeira.clipmobile.provider.ClipMobileContract.Students;
import com.migueljteixeira.clipmobile.provider.ClipMobileContract.StudentsYearSemester;
import com.migueljteixeira.clipmobile.provider.ClipMobileContract.Users;
import com.migueljteixeira.clipmobile.provider.ClipMobileDatabase.Tables;
import com.migueljteixeira.clipmobile.util.SelectionBuilder;

public class ClipMobileProvider extends ContentProvider {

    private static UriMatcher sUriMatcher;

    private static final int USERS = 1;

    private static final int STUDENTS = 2;

    private static final int STUDENTS_YEAR_SEMESTER = 3;

    private static final int SCHEDULE_DAYS = 4;

    private static final int SCHEDULE_CLASSES = 5;

    private static final int STUDENT_CLASSES = 6;

    private static final int STUDENT_CLASSES_DOCS = 7;

    private static final int STUDENT_CALENDAR = 8;


    /**
     * Build and return a {@link UriMatcher} that catches all {@link Uri} variations supported by
     * this {@link ContentProvider}.
     */
    private static UriMatcher buildUriMatcher(Context context) {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = context.getPackageName() + ".provider";

        // Users
        matcher.addURI(authority, ClipMobileContract.PATH_USERS, USERS);

        // Students
        matcher.addURI(authority, ClipMobileContract.PATH_STUDENTS, STUDENTS);

        // Students Year-Semester
        matcher.addURI(authority, ClipMobileContract.PATH_STUDENTS_YEAR_SEMESTER, STUDENTS_YEAR_SEMESTER);

        // Students Schedule Days
        matcher.addURI(authority, ClipMobileContract.PATH_SCHEDULE_DAYS, SCHEDULE_DAYS);

        // Students Schedule Classes
        matcher.addURI(authority, ClipMobileContract.PATH_SCHEDULE_CLASSES, SCHEDULE_CLASSES);

        // Student Classes
        matcher.addURI(authority, ClipMobileContract.PATH_STUDENT_CLASSES, STUDENT_CLASSES);

        // Student Classes Docs
        matcher.addURI(authority, ClipMobileContract.PATH_STUDENT_CLASSES_DOCS, STUDENT_CLASSES_DOCS);

        // Student Calendar
        matcher.addURI(authority, ClipMobileContract.PATH_STUDENT_CALENDAR, STUDENT_CALENDAR);

        return matcher;
    }

    private final ThreadLocal<Boolean> mApplyingBatch = new ThreadLocal<>();

    private ClipMobileDatabase mDbHelper;

    protected SQLiteDatabase mDb;

    @Override
    public boolean onCreate() {
        Context context = getContext();

        sUriMatcher = buildUriMatcher(context);

        mDbHelper = new ClipMobileDatabase(context);

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {

        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);

        final SelectionBuilder builder = buildSelection(uri, match);

        Cursor query = builder
                .where(selection, selectionArgs)
                .query(db, projection, sortOrder);
        if (query != null) {
            query.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return query;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS :
                return Users.CONTENT_TYPE;
            case STUDENTS :
                return Students.CONTENT_TYPE;
            case STUDENTS_YEAR_SEMESTER :
                return StudentsYearSemester.CONTENT_TYPE;
            case SCHEDULE_DAYS :
                return ScheduleDays.CONTENT_TYPE;
            case SCHEDULE_CLASSES :
                return ScheduleClasses.CONTENT_TYPE;
            case STUDENT_CLASSES :
                return StudentClasses.CONTENT_TYPE;
            case STUDENT_CLASSES_DOCS :
                return StudentClassesDocs.CONTENT_TYPE;
            case STUDENT_CALENDAR :
                return StudentCalendar.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri newItemUri;

        if (!applyingBatch()) {
            final SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                newItemUri = insertInTransaction(uri, values);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } else {
            newItemUri = insertInTransaction(uri, values);
        }

        /*if (newItemUri != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }*/

        return newItemUri;
    }

    private Uri insertInTransaction(Uri uri, ContentValues values) {
        Uri newItemUri = null;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS: {
                long id = mDbHelper.insertUsers(values);
                if (id < 0) {
                    break;
                }
                newItemUri = Users.buildUri(String.valueOf(id));

                break;
            }
            case STUDENTS: {
                long id = mDbHelper.insertStudents(values);
                if (id < 0) {
                    break;
                }
                newItemUri = Students.buildUri(String.valueOf(id));
                break;
            }
            case STUDENTS_YEAR_SEMESTER: {
                long id = mDbHelper.insertStudentYears(values);
                if (id < 0) {
                    break;
                }
                newItemUri = StudentsYearSemester.buildUri(String.valueOf(id));
                break;
            }
            case SCHEDULE_DAYS: {
                long id = mDbHelper.insertScheduleDays(values);
                if (id < 0) {
                    break;
                }
                newItemUri = ScheduleDays.buildUri(String.valueOf(id));
                break;
            }
            case SCHEDULE_CLASSES: {
                long id = mDbHelper.insertScheduleClasses(values);
                if (id < 0) {
                    break;
                }
                newItemUri = ScheduleClasses.buildUri(String.valueOf(id));
                break;
            }
            case STUDENT_CLASSES: {
                long id = mDbHelper.insertStudentClasses(values);
                if (id < 0) {
                    break;
                }
                newItemUri = StudentClasses.buildUri(String.valueOf(id));
                break;
            }
            case STUDENT_CLASSES_DOCS: {
                long id = mDbHelper.insertStudentClassesDocs(values);
                if (id < 0) {
                    break;
                }
                newItemUri = StudentClassesDocs.buildUri(String.valueOf(id));
                break;
            }
            case STUDENT_CALENDAR: {
                long id = mDbHelper.insertStudentCalendar(values);
                if (id < 0) {
                    break;
                }
                newItemUri = StudentCalendar.buildUri(String.valueOf(id));
                break;
            }

            default :
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return newItemUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;

        if (!applyingBatch()) {
            final SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                count = buildSelection(uri, sUriMatcher.match(uri))
                        .where(selection, selectionArgs)
                        .update(db, values);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } else {
            mDb = mDbHelper.getWritableDatabase();
            count = buildSelection(uri, sUriMatcher.match(uri))
                    .where(selection, selectionArgs)
                    .update(mDb, values);
        }

        /*if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }*/

        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;

        if (!applyingBatch()) {
            final SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                count = buildSelection(uri, sUriMatcher.match(uri))
                        .where(selection, selectionArgs)
                        .delete(db);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } else {
            mDb = mDbHelper.getWritableDatabase();
            count = buildSelection(uri, sUriMatcher.match(uri))
                    .where(selection, selectionArgs)
                    .delete(mDb);
        }

        /*if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }*/

        return count;
    }

    /**
     * Allows users to do multiple inserts into a table using the same statement
     */
    private boolean applyingBatch() {
        return mApplyingBatch.get() != null && mApplyingBatch.get();
    }

    /**
     * Builds selection using a {@link SelectionBuilder} to match the requested {@link Uri}.
     */
    private static SelectionBuilder buildSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();

        switch (match) {
            case USERS :
                return builder.table(Tables.USERS);
            case STUDENTS :
                return builder.table(Tables.STUDENTS);
            case STUDENTS_YEAR_SEMESTER :
                return builder.table(Tables.STUDENTS_YEAR_SEMESTER);
            case SCHEDULE_DAYS :
                return builder.table(Tables.SCHEDULE_DAYS);
            case SCHEDULE_CLASSES :
                return builder.table(Tables.SCHEDULE_CLASSES);
            case STUDENT_CLASSES :
                return builder.table(Tables.STUDENT_CLASSES);
            case STUDENT_CLASSES_DOCS :
                return builder.table(Tables.STUDENT_CLASSES_DOCS);
            case STUDENT_CALENDAR :
                return builder.table(Tables.STUDENT_CALENDAR);

            default :
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();

        if (mDbHelper != null) {
            mDbHelper.close();
            mDbHelper = null;
            mDb = null;
        }
    }
}
