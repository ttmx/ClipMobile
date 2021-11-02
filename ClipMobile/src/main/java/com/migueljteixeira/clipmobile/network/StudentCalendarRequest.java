package com.migueljteixeira.clipmobile.network;

import android.content.Context;

import com.migueljteixeira.clipmobile.entities.Student;
import com.migueljteixeira.clipmobile.entities.StudentCalendar;
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.List;

public class StudentCalendarRequest extends Request {
    private static final String STUDENT_CALENDAR_EXAM_1 = "https://clip.unl.pt/utente/eu/aluno/ano_lectivo/calend%E1rio?ano_lectivo=";
    private static final String STUDENT_CALENDAR_EXAM_2 = "&aluno=";
    private static final String STUDENT_CALENDAR_EXAM_3 = "&institui%E7%E3o=97747&tipo_de_per%EDodo_lectivo=s&per%EDodo_lectivo=";
    private static final String STUDENT_CALENDAR_EXAM_3_TRIMESTER = "&institui%E7%E3o=97747&tipo_de_per%EDodo_lectivo=t&per%EDodo_lectivo=";

    private static final String STUDENT_CALENDAR_TEST_1 = "https://clip.unl.pt/utente/eu/aluno/acto_curricular/inscri%E7%E3o/testes_de_avalia%E7%E3o?institui%E7%E3o=97747&aluno=";
    private static final String STUDENT_CALENDAR_TEST_2 = "&ano_lectivo=";
    private static final String STUDENT_CALENDAR_TEST_3 = "&tipo_de_per%EDodo_lectivo=s&per%EDodo_lectivo=";
    private static final String STUDENT_CALENDAR_TEST_3_TRIMESTER = "&tipo_de_per%EDodo_lectivo=t&per%EDodo_lectivo=";

    public static void getExamCalendar(Context mContext, Student student, String studentNumberId,
                                          String year, int semester)
            throws ServerUnavailableException {

        String url = STUDENT_CALENDAR_EXAM_1 + year + STUDENT_CALENDAR_EXAM_2 + studentNumberId;
        if(semester == 3) // Trimester
            url += STUDENT_CALENDAR_EXAM_3_TRIMESTER + (semester-1);
        else
            url += STUDENT_CALENDAR_EXAM_3 + semester;

        Elements exams = request(mContext, url)
                .body()
                .select("tr[class=texto_tabela]");

        for(Element exam : exams) {
            String name = exam.child(0).text();
            Elements recurso = exam.child(2).select("tr");

            if(recurso.first() == null) continue;

            String date = recurso.first().child(0).text();
            //String url = recurso.first().child(1) //.get(1).child(0).attr("href");
            String hour = recurso.get(1).child(0).child(0).text();

            StudentCalendar calendarAppointement = new StudentCalendar();
            calendarAppointement.setName(name);
            calendarAppointement.setDate(date);
            calendarAppointement.setHour(hour);

            student.addStudentCalendarAppointment(true, calendarAppointement);
        }

    }

    public static void getTestCalendar(Context mContext, Student student, String studentNumberId,
                                          String year, int semester)
            throws ServerUnavailableException {

        String url = STUDENT_CALENDAR_TEST_1 + studentNumberId + STUDENT_CALENDAR_TEST_2 + year;
        if(semester == 3) // Trimester
            url += STUDENT_CALENDAR_TEST_3_TRIMESTER + (semester-1);
        else
            url += STUDENT_CALENDAR_TEST_3 + semester;
        
        Element body = request(mContext, url)
                .body();

        // There is no calendar available!
        if(body.childNodeSize() == 0)
            return;

        Elements tests = body.select("form[method=post]");

        // There is no calendar available!
        if(tests.size() == 1)
            return;
        
        tests = tests.get(2).select("tr");

        for(Element test : tests) {
            if(!test.hasAttr("bgcolor")) continue;

            String name = test.child(1).textNodes().get(0).text();
            String number = test.child(2).text();
            String date = test.child(3).childNode(0).toString();
            String hour = test.child(3).childNode(2).toString();
            List<TextNode> rooms = test.child(4).child(0).textNodes();

            StringBuilder rooms_final = new StringBuilder();
            for(int i = 0; i<rooms.size(); i++) {
                if(i == rooms.size()-1)
                    rooms_final.append(rooms.get(i).getWholeText());
                else
                    rooms_final.append(rooms.get(i).getWholeText()).append(", ");
            }

            StudentCalendar calendarAppointement = new StudentCalendar();
            calendarAppointement.setName(name);
            calendarAppointement.setNumber(number);
            calendarAppointement.setDate(date);
            calendarAppointement.setHour(hour.substring(1, hour.length()-1 ));
            calendarAppointement.setRooms(rooms_final.toString());

            student.addStudentCalendarAppointment(false, calendarAppointement);
        }

    }

}
