package com.migueljteixeira.clipmobile.network

import android.content.Context
import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.entities.StudentClass
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException

object StudentClassesRequest : Request() {
    private const val STUDENT_CLASSES_1 = "https://clip.unl.pt/utente/eu/aluno/ano_lectivo?aluno="
    private const val STUDENT_CLASSES_2 = "&institui%E7%E3o=97747&ano_lectivo="

    @Throws(ServerUnavailableException::class)
    fun getClasses(mContext: Context, studentNumberId: String, year: String): Student {
        val url = STUDENT_CLASSES_1 + studentNumberId + STUDENT_CLASSES_2 + year
        val hrefs = request(mContext, url)
            .body()
            .select("a[href]")
        val student = Student()
        for (href in hrefs) {
            val linkHref = href.attr("href")
            if (linkHref.matches(Regex("/utente/eu/aluno/ano_lectivo/unidades[?](.)*&tipo_de_per%EDodo_lectivo=s&(.)*"))) {
                val class_url = linkHref.split("&").toTypedArray()
                val semester = class_url[class_url.size - 1]
                val classID = class_url[class_url.size - 3]
                val className = href.text()
                val semester_final = semester.substring(semester.length - 1).toInt()
                val classID_final = classID.substring(8)
                println("-> CLASS!$className, $semester_final, $classID_final")
                val cl = StudentClass()
                cl.name = className
                cl.semester = semester_final
                cl.number = classID_final
                student.addStudentClass(semester_final, cl)
            } else if (linkHref.matches(Regex("/utente/eu/aluno/ano_lectivo/unidades[?](.)*&tipo_de_per%EDodo_lectivo=t&(.)*"))) {
                val class_url = linkHref.split("&").toTypedArray()

                //String semester = class_url[class_url.length - 1];
                val classID = class_url[class_url.size - 3]
                val className = href.text()
                //int semester_final = Integer.valueOf(semester.substring(semester.length() - 1));
                val classID_final = classID.substring(8)

                //System.out.println("-> CLASS!" + className + ", " + semester_final + ", " + classID_final);
                val cl = StudentClass()
                cl.name = className
                cl.semester = 3
                cl.number = classID_final
                student.addStudentClass(3, cl)
            }
        }
        return student
    }
}