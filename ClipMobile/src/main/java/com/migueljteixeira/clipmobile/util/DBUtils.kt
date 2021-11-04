package com.migueljteixeira.clipmobile.util

import com.migueljteixeira.clipmobile.provider.ClipMobileContract
import android.content.ContentValues
import android.content.ContentUris
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.migueljteixeira.clipmobile.entities.*

object DBUtils {
    /*
     * ////////////////////////////// USERS //////////////////////////////
     */
    @JvmStatic
    fun getUserId(mContext: Context, username: String): Long {
        val userCursor = mContext.contentResolver.query(
            ClipMobileContract.Users.CONTENT_URI, arrayOf(ClipMobileContract.Users._ID),
            ClipMobileContract.Users.USERNAME + "=?", arrayOf(username), null
        )
        if (userCursor!!.count == 0) {
            userCursor.close()
            return -1
        }
        userCursor.moveToFirst()
        val userId = userCursor.getInt(0).toLong()
        userCursor.close()
        return userId
    }

    @JvmStatic
    fun createUser(mContext: Context, username: String?): Long {
        val values = ContentValues()
        values.put(ClipMobileContract.Users.USERNAME, username)
        val uri = mContext.contentResolver.insert(ClipMobileContract.Users.CONTENT_URI, values)
        println("user inserted! " + uri!!.path)
        return ContentUris.parseId(uri)
    }

    /*
     * ////////////////////////////// STUDENTS //////////////////////////////
     */
    @JvmStatic
    fun getStudents(mContext: Context, userId: Long): User {
        val students_cursor = mContext.contentResolver.query(
            ClipMobileContract.Students.CONTENT_URI, null,
            ClipMobileContract.Users.REF_USERS_ID + "=?", arrayOf(userId.toString()), null
        )
        val user = User()
        while (students_cursor!!.moveToNext()) {
            val id = students_cursor.getString(0)
            val numberId = students_cursor.getString(2)
            val number = students_cursor.getString(3)
            val student = Student()
            student.id = id
            student.numberId = numberId
            student.number = number
            user.addStudent(student)
        }
        students_cursor.close()
        return user
    }

    @JvmStatic
    fun insertStudentsNumbers(mContext: Context, userId: Long, user: User) {
        for (student in user.students) {
            val values = ContentValues()
            values.put(ClipMobileContract.Users.REF_USERS_ID, userId)
            values.put(ClipMobileContract.Students.NUMBER_ID, student.numberId)
            values.put(ClipMobileContract.Students.NUMBER, student.number)
            val uri =
                mContext.contentResolver.insert(ClipMobileContract.Students.CONTENT_URI, values)
            println("student inserted! " + uri!!.path)
            val newId = ContentUris.parseId(uri).toString()
            student.id = newId
        }
    }

    /*
     * ////////////////////////////// STUDENTS YEARS //////////////////////////////
     */
    @JvmStatic
    fun getStudentYears(mContext: Context, student_id: String): Student {

        // Get student years (for the 1st semester)
        val studentYearsCursor = mContext.contentResolver.query(
            ClipMobileContract.StudentsYearSemester.CONTENT_URI,
            null,
            ClipMobileContract.Students.REF_STUDENTS_ID + "=? AND " +
                    ClipMobileContract.StudentsYearSemester.SEMESTER + "=?",
            arrayOf(student_id, "1"),
            null
        )
        val student = Student()
        while (studentYearsCursor!!.moveToNext()) {
            val id = studentYearsCursor.getString(0)
            val year = studentYearsCursor.getString(2)
            val studentYear = StudentYearSemester()
            studentYear.id = id
            studentYear.year = year
            student.addYear(studentYear)
        }
        studentYearsCursor.close()
        return student
    }

    @JvmStatic
    fun insertStudentYears(mContext: Context, studentId: String?, student: Student) {

        // For every year, lets add the 2 semesters (and the trimester) already
        for (year in student.getYears()) {
            for (semester in 1..3) {
                val values = ContentValues()
                values.put(ClipMobileContract.Students.REF_STUDENTS_ID, studentId)
                values.put(ClipMobileContract.StudentsYearSemester.YEAR, year.year)
                values.put(ClipMobileContract.StudentsYearSemester.SEMESTER, semester)
                val uri = mContext.contentResolver.insert(
                    ClipMobileContract.StudentsYearSemester.CONTENT_URI,
                    values
                )
                println("student year semester inserted! " + uri!!.path)
                val newId = ContentUris.parseId(uri).toString()
                year.id = newId
            }
        }
    }

    /*
     * ////////////////////////////// UPDATE STUDENT INFO //////////////////////////////
     */
    @JvmStatic
    fun deleteStudentsNumbers(mContext: Context, userId: Long) {

        // Delete Student Numbers
        mContext.contentResolver.delete(
            ClipMobileContract.Students.CONTENT_URI,
            ClipMobileContract.Users.REF_USERS_ID + "=?", arrayOf(userId.toString())
        )
    }

    @JvmStatic
    fun deleteStudentsInfo(mContext: Context, studentYearSemesterId: String?) {

        // Delete Students Info
        mContext.contentResolver.delete(
            ClipMobileContract.StudentsYearSemester.CONTENT_URI, null, null /*ClipMobileContract.StudentsYearSemester._ID + "=?",
                new String[] { studentYearSemesterId }*/
        )

        // TODO: what to do?
    }

    /*
     * ////////////////////////////// STUDENT SCHEDULE  //////////////////////////////
     */
    @JvmStatic
    fun getYearSemesterId(
        mContext: Context,
        studentId: String,
        year: String,
        semester: Int
    ): String? {

        // First, we get the yearSemester ID
        val studentYearSemester_cursor = mContext.contentResolver.query(
            ClipMobileContract.StudentsYearSemester.CONTENT_URI,
            arrayOf(ClipMobileContract.StudentsYearSemester._ID),
            ClipMobileContract.Students.REF_STUDENTS_ID + "=? AND " +
                    ClipMobileContract.StudentsYearSemester.YEAR + "=? AND " +
                    ClipMobileContract.StudentsYearSemester.SEMESTER + "=?",
            arrayOf(studentId, year, semester.toString()),
            null
        )
        if (studentYearSemester_cursor!!.count == 0) {
            studentYearSemester_cursor.close()

//            Crashlytics.log("getYearSemesterId - COUNT==0");
            return null
        }
        studentYearSemester_cursor.moveToFirst()
        val yearSemesterId = studentYearSemester_cursor.getString(0)
        studentYearSemester_cursor.close()
        return yearSemesterId
    }

    @JvmStatic
    fun getStudentSchedule(mContext: Context, yearSemesterId: String): Student? {

        // Then, we get the schedule days
        val studentScheduleDays_cursor = mContext.contentResolver.query(
            ClipMobileContract.ScheduleDays.CONTENT_URI,
            arrayOf(ClipMobileContract.ScheduleDays._ID, ClipMobileContract.ScheduleDays.DAY),
            ClipMobileContract.StudentsYearSemester.REF_STUDENTS_YEAR_SEMESTER_ID + "=?",
            arrayOf(yearSemesterId),
            null
        )
        if (studentScheduleDays_cursor!!.count == 0) {
            studentScheduleDays_cursor.close()
            return null
        }
        val student = Student()
        while (studentScheduleDays_cursor.moveToNext()) {
            val scheduleDayId = studentScheduleDays_cursor.getString(0)
            val scheduleDay = studentScheduleDays_cursor.getInt(1)
            println("--> scheduleDay: $scheduleDay")

            // Finally, we get the schedule classes
            val studentScheduleClassesCursor = mContext.contentResolver.query(
                ClipMobileContract.ScheduleClasses.CONTENT_URI,
                null,
                ClipMobileContract.ScheduleDays.REF_SCHEDULE_DAYS_ID + "=?",
                arrayOf(scheduleDayId),
                null
            )
            while (studentScheduleClassesCursor!!.moveToNext()) {
                val name = studentScheduleClassesCursor.getString(2)
                val nameAbbreviation = studentScheduleClassesCursor.getString(3)
                val type = studentScheduleClassesCursor.getString(4)
                val hourStart = studentScheduleClassesCursor.getString(5)
                val hourEnd = studentScheduleClassesCursor.getString(6)
                val room = studentScheduleClassesCursor.getString(7)
                val scheduleClass = StudentScheduleClass()
                scheduleClass.name = name
                scheduleClass.nameMin = nameAbbreviation
                scheduleClass.type = type
                scheduleClass.hourStart = hourStart
                scheduleClass.hourEnd = hourEnd
                scheduleClass.room = room
                student.addScheduleClass(scheduleDay, scheduleClass)
            }
            studentScheduleClassesCursor.close()
        }
        studentScheduleDays_cursor.close()
        return student
    }

    @JvmStatic
    fun insertStudentSchedule(mContext: Context, yearSemesterId: String, student: Student) {
        val schedule = student.getScheduleClasses()
        println("yearSemesterId !!!-> $yearSemesterId")
        println("schedulesize -> " + schedule.size)

        // From monday(2) to friday(6)
        for (day in 2..6) {
            println("dia: $day")

            // If we don't have classes today, continue
            if (schedule[day] == null) {
                println("UPS! dia: $day")
                continue
            }
            var values = ContentValues()
            values.put(
                ClipMobileContract.StudentsYearSemester.REF_STUDENTS_YEAR_SEMESTER_ID,
                yearSemesterId
            )
            values.put(ClipMobileContract.ScheduleDays.DAY, day)
            var uri =
                mContext.contentResolver.insert(ClipMobileContract.ScheduleDays.CONTENT_URI, values)
            println("schedule day inserted! " + uri!!.path)
            val dayId = ContentUris.parseId(uri).toString()
            for (classes in schedule[day]!!) {
                println(
                    "SCHEDULE class!!!  dayID:" + dayId + " , name:" + classes.name
                            + ", type:" + classes.type + ", hour:" + classes.hourStart + " , " + classes.hourEnd
                            + ", room:" + classes.room
                )
                values = ContentValues()
                values.put(ClipMobileContract.ScheduleDays.REF_SCHEDULE_DAYS_ID, dayId)
                values.put(ClipMobileContract.ScheduleClasses.NAME, classes.name)
                values.put(ClipMobileContract.ScheduleClasses.NAME_ABBREVIATION, classes.nameMin)
                values.put(ClipMobileContract.ScheduleClasses.TYPE, classes.type)
                values.put(ClipMobileContract.ScheduleClasses.HOUR_START, classes.hourStart)
                values.put(ClipMobileContract.ScheduleClasses.HOUR_END, classes.hourEnd)
                values.put(ClipMobileContract.ScheduleClasses.ROOM, classes.room)
                uri = mContext.contentResolver.insert(
                    ClipMobileContract.ScheduleClasses.CONTENT_URI,
                    values
                )
                println("schedule class inserted! " + uri!!.path)
            }
        }
    }

    /*
     * ////////////////////////////// STUDENT CLASSES  //////////////////////////////
     */
    @JvmStatic
    fun getStudentClasses(mContext: Context, yearSemesterId: String): Student? {

        // Get the student classes
        val studentClasses_cursor = mContext.contentResolver.query(
            ClipMobileContract.StudentClasses.CONTENT_URI,
            null,
            ClipMobileContract.StudentsYearSemester.REF_STUDENTS_YEAR_SEMESTER_ID + "=?",
            arrayOf(yearSemesterId),
            null
        )
        if (studentClasses_cursor!!.count == 0) {
            studentClasses_cursor.close()
            return null
        }
        val student = Student()
        while (studentClasses_cursor.moveToNext()) {
            val classId = studentClasses_cursor.getString(0)
            val className = studentClasses_cursor.getString(2)
            val classNumber = studentClasses_cursor.getString(3)
            val classSemester = studentClasses_cursor.getInt(4)
            val studentClass = StudentClass()
            studentClass.id = classId
            studentClass.name = className
            studentClass.number = classNumber
            studentClass.semester = classSemester
            student.addStudentClass(classSemester, studentClass)
        }
        studentClasses_cursor.close()
        return student
    }

    @JvmStatic
    fun insertStudentClasses(mContext: Context, yearSemesterId: String, student: Student) {
        val classes = student.classes
        println("yearSemesterId !!!-> $yearSemesterId")
        println("classes size -> " + classes.size)

        // For two semesters (and one trimester)
        for (semester in 1..3) {
            val studentClass = classes[semester] ?: continue

            // we don't have classes in this semester, yet
            for (cl in studentClass) {
                val values = ContentValues()
                values.put(
                    ClipMobileContract.StudentsYearSemester.REF_STUDENTS_YEAR_SEMESTER_ID,
                    yearSemesterId
                )
                values.put(ClipMobileContract.StudentClasses.NAME, cl.name)
                values.put(ClipMobileContract.StudentClasses.NUMBER, cl.number)
                values.put(ClipMobileContract.StudentClasses.SEMESTER, cl.semester)
                val uri = mContext.contentResolver.insert(
                    ClipMobileContract.StudentClasses.CONTENT_URI,
                    values
                )
                println("class inserted! " + uri!!.path)

                // Set class Id
                val classId = ContentUris.parseId(uri).toString()
                cl.id = classId
            }
        }
    }

    /*
     * ////////////////////////////// STUDENT CLASSES DOCS //////////////////////////////
     */
    @JvmStatic
    fun getStudentClassesDocs(
        mContext: Context,
        studentClassId: String,
        docType: String
    ): Student? {

        // Get the student classes docs
        val studentClassesDocsCursor = mContext.contentResolver.query(
            ClipMobileContract.StudentClassesDocs.CONTENT_URI,
            null,
            ClipMobileContract.StudentClasses.REF_STUDENT_CLASSES_ID + "=? AND " +
                    ClipMobileContract.StudentClassesDocs.TYPE + "=?",
            arrayOf(studentClassId, docType),
            null
        )
        if (studentClassesDocsCursor!!.count == 0) {
            studentClassesDocsCursor.close()
            return null
        }
        val student = Student()
        while (studentClassesDocsCursor.moveToNext()) {
            val docName = studentClassesDocsCursor.getString(2)
            val docUrl = studentClassesDocsCursor.getString(3)
            val docDate = studentClassesDocsCursor.getString(4)
            val docSize = studentClassesDocsCursor.getString(5)
            val studentClassDoc = StudentClassDoc()
            studentClassDoc.name = docName
            studentClassDoc.url = docUrl
            studentClassDoc.date = docDate
            studentClassDoc.size = docSize
            studentClassDoc.type = docType
            student.addClassDoc(studentClassDoc)
        }
        studentClassesDocsCursor.close()
        return student
    }

    @JvmStatic
    fun insertStudentClassesDocs(mContext: Context, studentClassId: String, student: Student) {
        val classDocs = student.classesDocs
        println("studentClassId !!!-> $studentClassId")
        println("classes docs size -> " + classDocs.size)
        for (cl in classDocs) {
            val values = ContentValues()
            values.put(ClipMobileContract.StudentClasses.REF_STUDENT_CLASSES_ID, studentClassId)
            values.put(ClipMobileContract.StudentClassesDocs.NAME, cl.name)
            values.put(ClipMobileContract.StudentClassesDocs.URL, cl.url)
            values.put(ClipMobileContract.StudentClassesDocs.DATE, cl.date)
            values.put(ClipMobileContract.StudentClassesDocs.SIZE, cl.size)
            values.put(ClipMobileContract.StudentClassesDocs.TYPE, cl.type)
            val uri = mContext.contentResolver.insert(
                ClipMobileContract.StudentClassesDocs.CONTENT_URI,
                values
            )
            println("class doc inserted! " + uri!!.path)
        }
    }

    /*
     * ////////////////////////////// STUDENT CALENDAR  //////////////////////////////
     */
    @JvmStatic
    fun getStudentCalendar(mContext: Context, yearSemesterId: String): Student? {

        // Get student calendar
        val studentCalendarCursor = mContext.contentResolver.query(
            ClipMobileContract.StudentCalendar.CONTENT_URI,
            null,
            ClipMobileContract.StudentsYearSemester.REF_STUDENTS_YEAR_SEMESTER_ID + "=?",
            arrayOf(yearSemesterId),
            null
        )
        if (studentCalendarCursor!!.count == 0) {
            studentCalendarCursor.close()
            return null
        }
        val student = Student()
        while (studentCalendarCursor.moveToNext()) {
            val calendarAppointmentIsExam = studentCalendarCursor.getInt(2)
            val calendarAppointmentName = studentCalendarCursor.getString(3)
            val calendarAppointmentDate = studentCalendarCursor.getString(4)
            val calendarAppointmentHour = studentCalendarCursor.getString(5)
            val calendarAppointmentRooms = studentCalendarCursor.getString(6)
            val calendarAppointmentNumber = studentCalendarCursor.getString(7)
            println("REQUEST NAME:: $calendarAppointmentName")
            val calendarAppointement = StudentCalendar()
            calendarAppointement.name = calendarAppointmentName
            calendarAppointement.date = calendarAppointmentDate
            calendarAppointement.hour = calendarAppointmentHour
            calendarAppointement.rooms = calendarAppointmentRooms
            calendarAppointement.number = calendarAppointmentNumber
            student.addStudentCalendarAppointment(
                calendarAppointmentIsExam == 1,
                calendarAppointement
            )
        }
        studentCalendarCursor.close()
        return student
    }

    @JvmStatic
    fun insertStudentCalendar(mContext: Context, yearSemesterId: String, student: Student) {
        val studentCalendar = student.getStudentCalendar()
        println("yearSemesterId !!!-> $yearSemesterId")
        println("calendar size -> " + studentCalendar.size)

        // For two types (exam and test)
        for (type in 0..1) {
            val calendar = studentCalendar[type == 1] ?: continue

            // we don't have any calendar of this type, yet
            for (calendarAppointment in calendar) {
                val values = ContentValues()
                values.put(
                    ClipMobileContract.StudentsYearSemester.REF_STUDENTS_YEAR_SEMESTER_ID,
                    yearSemesterId
                )
                values.put(ClipMobileContract.StudentCalendar.IS_EXAM, type == 1)
                values.put(ClipMobileContract.StudentCalendar.NAME, calendarAppointment.name)
                values.put(ClipMobileContract.StudentCalendar.DATE, calendarAppointment.date)
                values.put(ClipMobileContract.StudentCalendar.HOUR, calendarAppointment.hour)
                values.put(ClipMobileContract.StudentCalendar.ROOMS, calendarAppointment.rooms)
                values.put(ClipMobileContract.StudentCalendar.NUMBER, calendarAppointment.number)
                val uri = mContext.contentResolver.insert(
                    ClipMobileContract.StudentCalendar.CONTENT_URI,
                    values
                )
                println("calendar appointment inserted! " + uri!!.path)
            }
        }
    }

    class InsertHelper(var mDb: SQLiteDatabase, var mTableName: String) {
        fun insert(cv: ContentValues?): Long {
            return mDb.insert(mTableName, null, cv)
        }
    }
}