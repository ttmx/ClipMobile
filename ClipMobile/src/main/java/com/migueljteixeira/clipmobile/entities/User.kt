package com.migueljteixeira.clipmobile.entities

import java.util.*


class User : Entity() {
    val students: MutableList<Student> = LinkedList()

    fun addStudent(student: Student) {
        students.add(student)
    }

    fun hasStudents(): Boolean {
        return students.isNotEmpty()
    }
}