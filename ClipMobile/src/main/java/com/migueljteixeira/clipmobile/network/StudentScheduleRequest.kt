package com.migueljteixeira.clipmobile.network

import android.content.Context
import android.text.Html
import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.entities.StudentScheduleClass
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object StudentScheduleRequest : Request() {
    private const val STUDENT_SCHEDULE_1 =
        "https://clip.unl.pt/utente/eu/aluno/ano_lectivo/hor%E1rio?" +
                "ano_lectivo="
    private const val STUDENT_SCHEDULE_2 = "&institui%E7%E3o=97747&aluno="
    private const val STUDENT_SCHEDULE_3 = "&tipo_de_per%EDodo_lectivo=s&per%EDodo_lectivo="
    private const val STUDENT_SCHEDULE_3_TRIMESTER =
        "&tipo_de_per%EDodo_lectivo=t&per%EDodo_lectivo="

    @Throws(ServerUnavailableException::class)
    fun getSchedule(
        mContext: Context,
        studentNumberId: String,
        year: String,
        semester: Int
    ): Student {
        var url = STUDENT_SCHEDULE_1 + year + STUDENT_SCHEDULE_2 + studentNumberId
        url += if (semester == 3) // Trimester
            STUDENT_SCHEDULE_3_TRIMESTER + (semester - 1) else STUDENT_SCHEDULE_3 + semester
        val trs = request(mContext, url)
            .body()
            .select("tr[valign=center]")
        val student = Student()
        for (tr in trs) {
            val tds = tr.select("td[class~=turno.* celulaDeCalendario]")
            for (td in tds) {
                val child = td.child(0).childNodes()

                // Remove all the garbage
                val href = child[2].attr("href").split("&").toTypedArray()
                val dia = href[9]
                val turno = href[7].toUpperCase()
                val horas_inicio = tr.child(0)
                val horas_fim = tr.child(1)
                val scheduleDayNumber = Character.getNumericValue(dia[dia.length - 1])
                var scheduleClassType = turno.substring(5)
                scheduleClassType += href[8].substring(href[8].length - 1)
                val scheduleClassName = td.attr("title")
                val scheduleClassNameMin = child[0].toString()
                var scheduleClassRoom: String? = null
                if (child.size > 4) scheduleClassRoom =
                    Html.fromHtml(child[4].toString()).toString()
                val scheduleClassDuration = td.attr("rowspan")

                // Calculate scheduleClassHourStart & End
                var scheduleClassHourStart: String? = null
                var scheduleClassHourEnd: String? = null
                try {
                    val format1 = SimpleDateFormat("k:mm")
                    val dateDuration = scheduleClassDuration.toInt() / 2
                    if (horas_fim.text().length == 1) {
                        // Start hour
                        scheduleClassHourStart = horas_inicio.text()
                        val dateStart = format1.parse(scheduleClassHourStart)
                        val calendar1 = Calendar.getInstance()
                        calendar1.time = dateStart
                        calendar1.add(Calendar.HOUR, dateDuration)

                        // End hour
                        scheduleClassHourEnd =
                            calendar1[Calendar.HOUR_OF_DAY].toString() + ":" + calendar1[Calendar.MINUTE] + "0"
                    } else {
                        // Calculate start hour
                        val dateStart = format1.parse(horas_fim.text())

                        // Subtract 30 minutes to the start hour
                        val calendar1 = Calendar.getInstance()
                        calendar1.time = dateStart
                        calendar1.add(Calendar.MINUTE, -30)
                        scheduleClassHourStart =
                            calendar1[Calendar.HOUR_OF_DAY].toString() + ":" + calendar1[Calendar.MINUTE]

                        // Calculate end hour
                        calendar1.add(Calendar.HOUR_OF_DAY, dateDuration)
                        scheduleClassHourEnd =
                            calendar1[Calendar.HOUR_OF_DAY].toString() + ":" + calendar1[Calendar.MINUTE]
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                // Create scheduleClass
                val scheduleClass = StudentScheduleClass()
                scheduleClass.name = scheduleClassName
                scheduleClass.nameMin = scheduleClassNameMin
                scheduleClass.type = scheduleClassType
                scheduleClass.hourStart = scheduleClassHourStart
                scheduleClass.hourEnd = scheduleClassHourEnd
                scheduleClass.room = scheduleClassRoom

                // Add scheduleClass to scheduleDay
                student.addScheduleClass(scheduleDayNumber, scheduleClass)
            }
        }
        return student
    }
}