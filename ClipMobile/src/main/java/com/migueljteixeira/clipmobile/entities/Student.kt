package com.migueljteixeira.clipmobile.entities

import android.util.Log
import java.util.*

class Student : Entity() {
    var numberId // number on URL
            : String? = null
    var number // real student number
            : String? = null
    private var years: MutableList<StudentYearSemester>
    private val scheduleClasses // <semester, scheduleClasses>
            : MutableMap<Int, MutableList<StudentScheduleClass>>
    private val studentClasses // <semester, classes>
            : MutableMap<Int, MutableList<StudentClass>>
    private val studentCalendar // <isExam, calendar>
            : MutableMap<Boolean, MutableList<StudentCalendar>>
    private val studentClassesDocs: MutableList<StudentClassDoc>
    fun getYears(): List<StudentYearSemester> {
        return years
    }

    fun setYears(years: MutableList<StudentYearSemester>) {
        this.years = years
    }

    fun addYear(year: StudentYearSemester) {
        years.add(year)
    }

    fun hasStudentYears(): Boolean {
        return years.isNotEmpty()
    }

    fun getScheduleClasses(): Map<Int, MutableList<StudentScheduleClass>> {
        return scheduleClasses
    }

    fun addScheduleClass(day: Int, scheduleClass: StudentScheduleClass) {
        var classes = scheduleClasses[day]
        if (classes == null) classes = LinkedList()
        classes.add(scheduleClass)
        Log.d("Student", "--!!! dia: " + day + " , " + classes.size)
        scheduleClasses[day] = classes
    }

    val classes: Map<Int, MutableList<StudentClass>>
        get() = studentClasses

    fun addStudentClass(semester: Int, scheduleClass: StudentClass) {
        var classes = studentClasses[semester]
        if (classes == null) classes = LinkedList()
        classes.add(scheduleClass)
        studentClasses[semester] = classes
    }

    val classesDocs: List<StudentClassDoc>
        get() = studentClassesDocs

    fun addClassDoc(classDoc: StudentClassDoc) {
        studentClassesDocs.add(classDoc)
    }

    fun getStudentCalendar(): Map<Boolean, MutableList<StudentCalendar>> {
        return studentCalendar
    }

    fun addStudentCalendarAppointment(isExam: Boolean, calendarAppointment: StudentCalendar) {
        var calendar = studentCalendar[isExam]
        if (calendar == null) calendar = LinkedList()
        calendar.add(calendarAppointment)
        studentCalendar[isExam] = calendar
    }

    init {
        years = LinkedList()
        scheduleClasses = HashMap(5)
        studentClasses = HashMap(2)
        studentCalendar = HashMap(2)
        studentClassesDocs = LinkedList()
    }
}