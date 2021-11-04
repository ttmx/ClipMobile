package com.migueljteixeira.clipmobile.provider

import android.net.Uri
import android.provider.BaseColumns
import com.migueljteixeira.clipmobile.ClipMobileApplication

object ClipMobileContract {
    private val BASE_CONTENT_URI = Uri.parse(
        "content://"
                + ClipMobileApplication.CONTENT_AUTHORITY
    )
    const val PATH_USERS = "users"
    const val PATH_STUDENTS = "students"
    const val PATH_STUDENTS_YEAR_SEMESTER = "students_year_semester"
    const val PATH_SCHEDULE_DAYS = "schedule_days"
    const val PATH_SCHEDULE_CLASSES = "schedule_classes"
    const val PATH_STUDENT_CLASSES = "student_classes"
    const val PATH_STUDENT_CLASSES_DOCS = "student_classes_docs"
    const val PATH_STUDENT_CALENDAR = "student_calendar"

    internal interface UsersColumns {
        companion object {
            /**
             * This column is NOT in this table, it is for reference purposes only.
             */
            const val REF_USERS_ID = "users_id"
            const val USERNAME = "users_username"
        }
    }

    internal interface StudentsColumns {
        companion object {
            /**
             * This column is NOT in this table, it is for reference purposes only.
             */
            const val REF_STUDENTS_ID = "students_id"
            const val NUMBER_ID = "students_number_id"
            const val NUMBER = "students_number"
        }
    }

    internal interface StudentsYearSemesterColumns {
        companion object {
            /**
             * This column is NOT in this table, it is for reference purposes only.
             */
            const val REF_STUDENTS_YEAR_SEMESTER_ID = "students_year_semester_id"
            const val YEAR = "students_year_semester_year"
            const val SEMESTER = "students_year_semester_semester"
        }
    }

    internal interface ScheduleDaysColumns {
        companion object {
            /**
             * This column is NOT in this table, it is for reference purposes only.
             */
            const val REF_SCHEDULE_DAYS_ID = "schedule_days_id"
            const val DAY = "schedule_days_day"
        }
    }

    internal interface ScheduleClassesColumns {
        companion object {
            /**
             * This column is NOT in this table, it is for reference purposes only.
             */
            const val REF_SCHEDULE_CLASSES_ID = "schedule_classes_id"
            const val NAME = "schedule_classes_name"
            const val NAME_ABBREVIATION = "schedule_classes_name_abbreviation"
            const val TYPE = "schedule_classes_type"
            const val HOUR_START = "schedule_classes_hour_start"
            const val HOUR_END = "schedule_classes_hour_end"
            const val ROOM = "schedule_classes_room"
        }
    }

    internal interface StudentClassesColumns {
        companion object {
            /**
             * This column is NOT in this table, it is for reference purposes only.
             */
            const val REF_STUDENT_CLASSES_ID = "student_classes_id"
            const val NAME = "student_classes_name"
            const val NUMBER = "student_classes_number"
            const val SEMESTER = "student_classes_semester"
        }
    }

    internal interface StudentClassesDocsColumns {
        companion object {
            /**
             * This column is NOT in this table, it is for reference purposes only.
             */
            const val REF_STUDENT_CLASSES_DOCS_ID = "student_classes_docs_id"
            const val NAME = "student_classes_docs_name"
            const val URL = "student_classes_docs_url"
            const val DATE = "student_classes_docs_date"
            const val SIZE = "student_classes_docs_size"
            const val TYPE = "student_classes_docs_type"
        }
    }

    internal interface StudentCalendarColumns {
        companion object {
            /**
             * This column is NOT in this table, it is for reference purposes only.
             */
            const val REF_STUDENT_CALENDAR_ID = "student_calendar_id"
            const val IS_EXAM = "student_calendar_is_exam"
            const val NAME = "student_calendar_name"
            const val DATE = "student_calendar_date"
            const val HOUR = "student_calendar_hour"
            const val ROOMS = "student_calendar_rooms"
            const val NUMBER = "student_calendar_number"
        }
    }

    object Users : UsersColumns, BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS)
            .build()
        const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.clipmobile.users"
        fun buildUri(userId: String?): Uri {
            return CONTENT_URI.buildUpon().appendPath(userId).build()
        }

        val REF_USERS_ID = UsersColumns.REF_USERS_ID
        val USERNAME = UsersColumns.USERNAME

        const val _ID = BaseColumns._ID
        const val _COUNT = BaseColumns._COUNT

    }

    object Students : StudentsColumns, BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STUDENTS)
            .build()
        const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.clipmobile.students"
        fun buildUri(studentId: String?): Uri {
            return CONTENT_URI.buildUpon().appendPath(studentId).build()
        }
        const val _ID = BaseColumns._ID
        const val _COUNT = BaseColumns._COUNT
        const val NUMBER = StudentsColumns.NUMBER
        const val NUMBER_ID = StudentsColumns.NUMBER_ID
        const val REF_STUDENTS_ID = StudentsColumns.REF_STUDENTS_ID
    }

    object StudentsYearSemester : StudentsYearSemesterColumns, BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STUDENTS_YEAR_SEMESTER)
            .build()
        const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.clipmobile.students_year_semester"
        fun buildUri(studentYearId: String?): Uri {
            return CONTENT_URI.buildUpon().appendPath(studentYearId).build()
        }
        const val _ID = BaseColumns._ID
        const val _COUNT = BaseColumns._COUNT

        const val SEMESTER = StudentsYearSemesterColumns.SEMESTER
        const val YEAR = StudentsYearSemesterColumns.YEAR
        const val REF_STUDENTS_YEAR_SEMESTER_ID = StudentsYearSemesterColumns.REF_STUDENTS_YEAR_SEMESTER_ID
    }

    object ScheduleDays : ScheduleDaysColumns, BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCHEDULE_DAYS)
            .build()
        const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.clipmobile.schedule_days"
        fun buildUri(studentScheduleDayId: String?): Uri {
            return CONTENT_URI.buildUpon().appendPath(studentScheduleDayId).build()
        }
        const val _ID = BaseColumns._ID
        const val _COUNT = BaseColumns._COUNT

        const val DAY = ScheduleDaysColumns.DAY
        const val REF_SCHEDULE_DAYS_ID = ScheduleDaysColumns.REF_SCHEDULE_DAYS_ID
    }

    object ScheduleClasses : ScheduleClassesColumns, BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCHEDULE_CLASSES)
            .build()
        const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.clipmobile.schedule_classes"
        fun buildUri(studentScheduleClassId: String?): Uri {
            return CONTENT_URI.buildUpon().appendPath(studentScheduleClassId).build()
        }
        const val _ID = BaseColumns._ID
        const val _COUNT = BaseColumns._COUNT

        const val HOUR_END = ScheduleClassesColumns.HOUR_END
        const val HOUR_START = ScheduleClassesColumns.HOUR_START
        const val NAME = ScheduleClassesColumns.NAME
        const val NAME_ABBREVIATION = ScheduleClassesColumns.NAME_ABBREVIATION
        const val REF_SCHEDULE_CLASSES_ID = ScheduleClassesColumns.REF_SCHEDULE_CLASSES_ID
        const val ROOM = ScheduleClassesColumns.ROOM
        const val TYPE = ScheduleClassesColumns.TYPE
    }

    object StudentClasses : StudentClassesColumns, BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STUDENT_CLASSES)
            .build()
        const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.clipmobile.student_classes"
        fun buildUri(studentClassId: String?): Uri {
            return CONTENT_URI.buildUpon().appendPath(studentClassId).build()
        }
        const val _ID = BaseColumns._ID
        const val _COUNT = BaseColumns._COUNT

        const val NAME = StudentClassesColumns.NAME
        const val NUMBER = StudentClassesColumns.NUMBER
        const val REF_STUDENT_CLASSES_ID = StudentClassesColumns.REF_STUDENT_CLASSES_ID
        const val SEMESTER = StudentClassesColumns.SEMESTER
    }

    object StudentClassesDocs : StudentClassesDocsColumns, BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STUDENT_CLASSES_DOCS)
            .build()
        const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.clipmobile.student_classes_docs"
        fun buildUri(studentClassDocId: String?): Uri {
            return CONTENT_URI.buildUpon().appendPath(studentClassDocId).build()
        }
        const val _ID = BaseColumns._ID
        const val _COUNT = BaseColumns._COUNT

        const val NAME = StudentClassesDocsColumns.NAME
        const val DATE = StudentClassesDocsColumns.DATE
        const val REF_STUDENT_CLASSES_DOCS_ID = StudentClassesDocsColumns.REF_STUDENT_CLASSES_DOCS_ID
        const val SIZE = StudentClassesDocsColumns.SIZE
        const val TYPE = StudentClassesDocsColumns.TYPE
        const val URL = StudentClassesDocsColumns.URL
    }

    object StudentCalendar : StudentCalendarColumns, BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STUDENT_CALENDAR)
            .build()
        const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.clipmobile.student_calendar"
        fun buildUri(studentCalendarId: String?): Uri {
            return CONTENT_URI.buildUpon().appendPath(studentCalendarId).build()
        }
        const val _ID = BaseColumns._ID
        const val _COUNT = BaseColumns._COUNT

        const val DATE = StudentCalendarColumns.DATE
        const val HOUR = StudentCalendarColumns.HOUR
        const val IS_EXAM = StudentCalendarColumns.IS_EXAM
        const val NAME = StudentCalendarColumns.NAME
        const val NUMBER = StudentCalendarColumns.NUMBER
        const val REF_STUDENT_CALENDAR_ID = StudentCalendarColumns.REF_STUDENT_CALENDAR_ID
        const val ROOMS = StudentCalendarColumns.ROOMS
    }
}