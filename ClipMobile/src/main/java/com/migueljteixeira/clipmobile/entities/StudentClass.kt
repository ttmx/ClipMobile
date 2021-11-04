package com.migueljteixeira.clipmobile.entities

class StudentClass : Entity() {
    var name: String? = null
    private val teacherName: String? = null
    private val teacherEmail: String? = null

    /*public String getTeacherName() { return teacher_name; }
    public void setTeacherName(String teacher_name) { this.teacher_name = teacher_name; }

    public String getTeacherEmail() { return teacher_email; }
    public void setTeacherEmail(String teacher_email) { this.teacher_email = teacher_email; }*/
    var number: String? = null
    var semester = 0
}