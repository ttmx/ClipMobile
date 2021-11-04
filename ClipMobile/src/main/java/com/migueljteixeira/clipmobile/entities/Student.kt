package com.migueljteixeira.clipmobile.entities;

import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Student extends Entity {
    private String numberId; // number on URL
    private String number;   // real student number
    private List<StudentYearSemester> years;
    private final Map<Integer, List<StudentScheduleClass>> scheduleClasses; // <semester, scheduleClasses>
    private final Map<Integer, List<StudentClass>> studentClasses; // <semester, classes>
    private final Map<Boolean, List<StudentCalendar>> studentCalendar; // <isExam, calendar>
    private final List<StudentClassDoc> studentClassesDocs;

    public Student(){
        this.years = new LinkedList<>();
        this.scheduleClasses = new HashMap<>(5);
        this.studentClasses = new HashMap<>(2);
        this.studentCalendar = new HashMap<>(2);
        this.studentClassesDocs = new LinkedList<>();
    }

    public String getNumberId() {
        return numberId;
    }

    public void setNumberId(String student_number_id) {
        this.numberId = student_number_id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String student_number) {
        this.number = student_number;
    }

    public List<StudentYearSemester> getYears() {
        return years;
    }

    public void setYears(List<StudentYearSemester> years) {
        this.years = years;
    }

    public void addYear(StudentYearSemester year) {
        years.add(year);
    }

    public boolean hasStudentYears() {
        return !years.isEmpty();
    }

    public Map<Integer, List<StudentScheduleClass>> getScheduleClasses() {
        return scheduleClasses;
    }

    public void addScheduleClass(int day, StudentScheduleClass scheduleClass) {
        List<StudentScheduleClass> classes = this.scheduleClasses.get(day);
        if(classes == null)
            classes = new LinkedList<>();

        classes.add(scheduleClass);

        Log.d("Student","--!!! dia: " + day + " , " + classes.size());

        this.scheduleClasses.put(day, classes);
    }

    public Map<Integer, List<StudentClass>> getClasses() {
        return studentClasses;
    }

    public void addStudentClass(int semester, StudentClass scheduleClass) {
        List<StudentClass> classes = this.studentClasses.get(semester);
        if(classes == null)
            classes = new LinkedList<>();

        classes.add(scheduleClass);

        this.studentClasses.put(semester, classes);
    }

    public List<StudentClassDoc> getClassesDocs() {
        return studentClassesDocs;
    }

    public void addClassDoc(StudentClassDoc classDoc) {
        studentClassesDocs.add(classDoc);
    }

    public Map<Boolean, List<StudentCalendar>> getStudentCalendar() {
        return this.studentCalendar;
    }

    public void addStudentCalendarAppointment(boolean isExam, StudentCalendar calendarAppointment) {
        List<StudentCalendar> calendar = this.studentCalendar.get(isExam);
        if(calendar == null)
            calendar = new LinkedList<>();

        calendar.add(calendarAppointment);

        this.studentCalendar.put(isExam, calendar);
    }
}
