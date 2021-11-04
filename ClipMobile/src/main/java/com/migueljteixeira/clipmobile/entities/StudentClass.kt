package com.migueljteixeira.clipmobile.entities;

public class StudentClass extends Entity {
    private String name, teacher_name, teacher_email, number;
    private int semester;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    /*public String getTeacherName() { return teacher_name; }
    public void setTeacherName(String teacher_name) { this.teacher_name = teacher_name; }

    public String getTeacherEmail() { return teacher_email; }
    public void setTeacherEmail(String teacher_email) { this.teacher_email = teacher_email; }*/

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
}
