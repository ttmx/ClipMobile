package com.migueljteixeira.clipmobile.network;

import android.content.Context;
import android.text.Html;

import com.migueljteixeira.clipmobile.entities.Student;
import com.migueljteixeira.clipmobile.entities.StudentScheduleClass;
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StudentScheduleRequest extends Request {
    private static final String STUDENT_SCHEDULE_1 = "https://clip.unl.pt/utente/eu/aluno/ano_lectivo/hor%E1rio?" +
            "ano_lectivo=";
    private static final String STUDENT_SCHEDULE_2 = "&institui%E7%E3o=97747&aluno=";
    private static final String STUDENT_SCHEDULE_3 = "&tipo_de_per%EDodo_lectivo=s&per%EDodo_lectivo=";
    private static final String STUDENT_SCHEDULE_3_TRIMESTER = "&tipo_de_per%EDodo_lectivo=t&per%EDodo_lectivo=";

    public static Student getSchedule(Context mContext, String studentNumberId, String year, int semester)
            throws ServerUnavailableException {

        String url = STUDENT_SCHEDULE_1 + year + STUDENT_SCHEDULE_2 + studentNumberId;
        if(semester == 3) // Trimester
            url += STUDENT_SCHEDULE_3_TRIMESTER + (semester-1);
        else
            url += STUDENT_SCHEDULE_3 + semester;

        Elements trs = request(mContext, url)
                .body()
                .select("tr[valign=center]");

        Student student = new Student();
        for(Element tr : trs) {

            Elements tds = tr.select("td[class~=turno.* celulaDeCalendario]");
            for(Element td : tds) {
                List<Node> child = td.child(0).childNodes();

                // Remove all the garbage
                String[] href = child.get(2).attr("href").split("&");
                String dia = href[9];
                String turno = href[7].toUpperCase();

                Element horas_inicio = tr.child(0);
                Element horas_fim = tr.child(1);

                int scheduleDayNumber = Character.getNumericValue(dia.charAt(dia.length()-1));
                String scheduleClassType = turno.substring(5);
                scheduleClassType += href[8].substring(href[8].length()-1);
                String scheduleClassName = td.attr("title");
                String scheduleClassNameMin = child.get(0).toString();
                
                String scheduleClassRoom = null;
                if(child.size() > 4)
                    scheduleClassRoom = Html.fromHtml(child.get(4).toString()).toString();

                String scheduleClassDuration = td.attr("rowspan");

                // Calculate scheduleClassHourStart & End
                String scheduleClassHourStart = null;
                String scheduleClassHourEnd   = null;

                try {
                    SimpleDateFormat format1 = new SimpleDateFormat("k:mm");

                    int dateDuration = (Integer.parseInt(scheduleClassDuration) / 2);

                    if(horas_fim.text().length() == 1) {
                        // Start hour
                        scheduleClassHourStart = horas_inicio.text();

                        Date dateStart = format1.parse(scheduleClassHourStart);

                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.setTime(dateStart);
                        calendar1.add(Calendar.HOUR, dateDuration);

                        // End hour
                        scheduleClassHourEnd = calendar1.get(Calendar.HOUR_OF_DAY) + ":" + calendar1.get(Calendar.MINUTE) + "0";
                    } else {
                        // Calculate start hour
                        Date dateStart = format1.parse(horas_fim.text());

                        // Subtract 30 minutes to the start hour
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.setTime(dateStart);
                        calendar1.add(Calendar.MINUTE, -30);

                        scheduleClassHourStart = calendar1.get(Calendar.HOUR_OF_DAY) + ":" + calendar1.get(Calendar.MINUTE);

                        // Calculate end hour
                        calendar1.add(Calendar.HOUR_OF_DAY, dateDuration);

                        scheduleClassHourEnd = calendar1.get(Calendar.HOUR_OF_DAY) + ":" + calendar1.get(Calendar.MINUTE);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Create scheduleClass
                StudentScheduleClass scheduleClass = new StudentScheduleClass();
                scheduleClass.setName(scheduleClassName);
                scheduleClass.setNameMin(scheduleClassNameMin);
                scheduleClass.setType(scheduleClassType);
                scheduleClass.setHourStart(scheduleClassHourStart);
                scheduleClass.setHourEnd(scheduleClassHourEnd);
                scheduleClass.setRoom(scheduleClassRoom);

                // Add scheduleClass to scheduleDay
                student.addScheduleClass(scheduleDayNumber, scheduleClass);
            }

        }

        return student;
    }

}
