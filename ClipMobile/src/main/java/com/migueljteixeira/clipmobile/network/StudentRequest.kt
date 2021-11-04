package com.migueljteixeira.clipmobile.network

import android.content.Context
import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.entities.StudentYearSemester
import com.migueljteixeira.clipmobile.entities.User
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException

object StudentRequest : Request() {
    private const val GET_STUDENTS_NUMBERS = "https://clip.unl.pt/utente/eu"
    private const val GET_STUDENTS_YEARS = "https://clip.unl.pt/utente/eu/aluno?aluno="

    @Throws(ServerUnavailableException::class)
    fun signIn(context: Context, username: String, password: String): User {
        val links = requestNewCookie(context, username, password)
            .body()
            .select("a[href]")
        val user = User()
        for (link in links) {
            val linkHref = link.attr("href")
            if (linkHref.matches(Regex("/utente/eu/aluno[?][_a-zA-Z0-9=&.]*aluno=[0-9]*"))) {

                // Remove all the garbage
                var numbers = linkHref.split("&").toTypedArray()
                numbers = numbers[numbers.size - 1].split("=").toTypedArray()

                // Get student number ID and student number
                val student_numberID = numbers[1]
                val student_number = link.text()
                val student = Student()
                student.numberId = student_numberID
                student.number = student_number

//                Crashlytics.log("StudentRequest - signIn - numberID:" + student_numberID);
//                Crashlytics.log("StudentRequest - signIn - number:" + student_number);
                user.addStudent(student)
            }

            /*else if(linkHref.matches("/utente/eu")) {
                String[] full_user_name = link.getElementsByTag("span").text().split(" ");
                String user_name = full_user_name[0] + " " + full_user_name[full_user_name.length - 1];

                user.setName(user_name.toUpperCase());
            }*/
        }
        return user
    }

    @Throws(ServerUnavailableException::class)
    fun getStudentsNumbers(mContext: Context): User {
        val url = GET_STUDENTS_NUMBERS
        val links = request(mContext, url)
            .body()
            .select("a[href]")
        val user = User()
        for (link in links) {
            val linkHref = link.attr("href")
            if (linkHref.matches(Regex("/utente/eu/aluno[?][_a-zA-Z0-9=&.]*aluno=[0-9]*"))) {

                // Remove all the garbage
                var numbers = linkHref.split("&").toTypedArray()
                numbers = numbers[numbers.size - 1].split("=").toTypedArray()

                // Get student number ID and student number
                val student_numberID = numbers[1]
                val student_number = link.text()
                val student = Student()
                student.numberId = student_numberID
                student.number = student_number

//                Crashlytics.log("StudentRequest - getStudentsNumbers - numberID:" + student_numberID);
                println("StudentRequest - getStudentsNumbers - numberID:$student_numberID")

//                Crashlytics.log("StudentRequest - getStudentsNumbers - number:" + student_number);
                println("StudentRequest - getStudentsNumbers - number:$student_number")
                user.addStudent(student)
            }
        }
        return user
    }

    @Throws(ServerUnavailableException::class)
    fun getStudentsYears(mContext: Context, studentNumberId: String): Student {
        val url = GET_STUDENTS_YEARS + studentNumberId
        val links = request(mContext, url)
            .body()
            .select("a[href]")
        val student = Student()
        for (link in links) {
            val linkHref = link.attr("href")
            if (linkHref.matches(Regex("/utente/eu/aluno/ano_lectivo[?][_a-zA-Z0-9=;&.%]*ano_lectivo=[0-9]*"))) {
                val year = link.text()
                val studentYear = StudentYearSemester()
                studentYear.year = year

//                Crashlytics.log("StudentRequest - getStudentsYears - year:" + year);
                student.addYear(studentYear)
            }
        }
        return student
    }
}