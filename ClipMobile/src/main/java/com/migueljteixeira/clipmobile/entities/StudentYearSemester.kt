package com.migueljteixeira.clipmobile.entities;

public class StudentYearSemester extends Entity {
    private String year, semester;

    public StudentYearSemester() {}

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
