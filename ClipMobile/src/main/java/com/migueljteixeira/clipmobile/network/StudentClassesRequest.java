package com.migueljteixeira.clipmobile.network;

import android.content.Context;

import com.migueljteixeira.clipmobile.entities.Student;
import com.migueljteixeira.clipmobile.entities.StudentClass;
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StudentClassesRequest extends Request {
    private static final String STUDENT_CLASSES_1 = "https://clip.unl.pt/utente/eu/aluno/ano_lectivo?aluno=";
    private static final String STUDENT_CLASSES_2 = "&institui%E7%E3o=97747&ano_lectivo=";

    public static Student getClasses(Context mContext, String studentNumberId, String year)
            throws ServerUnavailableException {

        String url = STUDENT_CLASSES_1 + studentNumberId + STUDENT_CLASSES_2 + year;

        Elements hrefs = request(mContext, url)
                .body()
                .select("a[href]");

        Student student = new Student();

        for(Element href : hrefs) {
            String linkHref = href.attr("href");

            if(linkHref.matches("/utente/eu/aluno/ano_lectivo/unidades[?](.)*&tipo_de_per%EDodo_lectivo=s&(.)*")) {
                String[] class_url = linkHref.split("&");

                String semester = class_url[class_url.length - 1];
                String classID  = class_url[class_url.length - 3];

                String className = href.text();
                int semester_final = Integer.parseInt(semester.substring(semester.length() - 1));
                String classID_final  = classID.substring(8);

                System.out.println("-> CLASS!" + className + ", " + semester_final + ", " + classID_final);

                StudentClass cl = new StudentClass();
                cl.setName(className);
                cl.setSemester(semester_final);
                cl.setNumber(classID_final);

                student.addStudentClass(semester_final, cl);
            }

            else if(linkHref.matches("/utente/eu/aluno/ano_lectivo/unidades[?](.)*&tipo_de_per%EDodo_lectivo=t&(.)*")) {
                String[] class_url = linkHref.split("&");

                //String semester = class_url[class_url.length - 1];
                String classID  = class_url[class_url.length - 3];

                String className = href.text();
                //int semester_final = Integer.valueOf(semester.substring(semester.length() - 1));
                String classID_final  = classID.substring(8);

                //System.out.println("-> CLASS!" + className + ", " + semester_final + ", " + classID_final);

                StudentClass cl = new StudentClass();
                cl.setName(className);
                cl.setSemester(3);
                cl.setNumber(classID_final);

                student.addStudentClass(3, cl);
            }
        }

        return student;
    }

}
