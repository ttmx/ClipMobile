package com.migueljteixeira.clipmobile.network

import android.content.Context
import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.entities.StudentCalendar
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException

object StudentCalendarRequest : Request() {
    private const val STUDENT_CALENDAR_EXAM_1 =
        "https://clip.unl.pt/utente/eu/aluno/ano_lectivo/calend%E1rio?ano_lectivo="
    private const val STUDENT_CALENDAR_EXAM_2 = "&aluno="
    private const val STUDENT_CALENDAR_EXAM_3 =
        "&institui%E7%E3o=97747&tipo_de_per%EDodo_lectivo=s&per%EDodo_lectivo="
    private const val STUDENT_CALENDAR_EXAM_3_TRIMESTER =
        "&institui%E7%E3o=97747&tipo_de_per%EDodo_lectivo=t&per%EDodo_lectivo="
    private const val STUDENT_CALENDAR_TEST_1 =
        "https://clip.unl.pt/utente/eu/aluno/acto_curricular/inscri%E7%E3o/testes_de_avalia%E7%E3o?institui%E7%E3o=97747&aluno="
    private const val STUDENT_CALENDAR_TEST_2 = "&ano_lectivo="
    private const val STUDENT_CALENDAR_TEST_3 = "&tipo_de_per%EDodo_lectivo=s&per%EDodo_lectivo="
    private const val STUDENT_CALENDAR_TEST_3_TRIMESTER =
        "&tipo_de_per%EDodo_lectivo=t&per%EDodo_lectivo="

    @Throws(ServerUnavailableException::class)
    fun getExamCalendar(
        mContext: Context, student: Student, studentNumberId: String,
        year: String, semester: Int
    ) {
        var url = STUDENT_CALENDAR_EXAM_1 + year + STUDENT_CALENDAR_EXAM_2 + studentNumberId
        url += if (semester == 3) // Trimester
            STUDENT_CALENDAR_EXAM_3_TRIMESTER + (semester - 1) else STUDENT_CALENDAR_EXAM_3 + semester
        val exams = request(mContext, url)
            .body()
            .select("tr[class=texto_tabela]")
        for (exam in exams) {
            val name = exam.child(0).text()
            val recurso = exam.child(2).select("tr")
            if (recurso.first() == null) continue
            val date = recurso.first().child(0).text()
            //String url = recurso.first().child(1) //.get(1).child(0).attr("href");
            val hour = recurso[1].child(0).child(0).text()
            val calendarAppointement = StudentCalendar()
            calendarAppointement.name = name
            calendarAppointement.date = date
            calendarAppointement.hour = hour
            student.addStudentCalendarAppointment(true, calendarAppointement)
        }
    }

    @Throws(ServerUnavailableException::class)
    fun getTestCalendar(
        mContext: Context, student: Student, studentNumberId: String,
        year: String, semester: Int
    ) {
        var url = STUDENT_CALENDAR_TEST_1 + studentNumberId + STUDENT_CALENDAR_TEST_2 + year
        url += if (semester == 3) // Trimester
            STUDENT_CALENDAR_TEST_3_TRIMESTER + (semester - 1) else STUDENT_CALENDAR_TEST_3 + semester
        val body = request(mContext, url)
            .body()

        // There is no calendar available!
        if (body.childNodeSize() == 0) return
        var tests = body.select("form[method=post]")

        // There is no calendar available!
        if (tests.size == 1) return
        tests = tests[2].select("tr")
        for (test in tests) {
            if (!test.hasAttr("bgcolor")) continue
            val name = test.child(1).textNodes()[0].text()
            val number = test.child(2).text()
            val date = test.child(3).childNode(0).toString()
            val hour = test.child(3).childNode(2).toString()
            val rooms = test.child(4).child(0).textNodes()
            val rooms_final = StringBuilder()
            for (i in rooms.indices) {
                if (i == rooms.size - 1) rooms_final.append(rooms[i].wholeText) else rooms_final.append(
                    rooms[i].wholeText
                ).append(", ")
            }
            val calendarAppointement = StudentCalendar()
            calendarAppointement.name = name
            calendarAppointement.number = number
            calendarAppointement.date = date
            calendarAppointement.hour = hour.substring(1, hour.length - 1)
            calendarAppointement.rooms = rooms_final.toString()
            student.addStudentCalendarAppointment(false, calendarAppointement)
        }
    }
}