package com.migueljteixeira.clipmobile.network;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.migueljteixeira.clipmobile.entities.Student;
import com.migueljteixeira.clipmobile.entities.StudentClassDoc;
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException;
import com.migueljteixeira.clipmobile.settings.ClipSettings;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StudentClassesDocsRequest extends Request {
    private static final String STUDENT_CLASS_DOCS_1 = "https://clip.unl.pt/utente/eu/aluno/ano_lectivo/unidades/unidade_curricular/actividade/documentos?tipo_de_per%EDodo_lectivo=s&ano_lectivo=";
    private static final String STUDENT_CLASS_DOCS_1_TRIMESTER = "https://clip.unl.pt/utente/eu/aluno/ano_lectivo/unidades/unidade_curricular/actividade/documentos?tipo_de_per%EDodo_lectivo=t&ano_lectivo=";
    private static final String STUDENT_CLASS_DOCS_2 = "&per%EDodo_lectivo=";
    private static final String STUDENT_CLASS_DOCS_3 = "&aluno=";
    private static final String STUDENT_CLASS_DOCS_4 = "&institui%E7%E3o=97747&unidade_curricular=";
    private static final String STUDENT_CLASS_DOCS_5 = "&tipo_de_documento_de_unidade=";
    private static final String STUDENT_CLASS_DOCS_DOWNLOAD = "https://clip.unl.pt";


    public static Student getClassesDocs(Context mContext, String studentNumberId,
                                       String year, int semester, String course,
                                       String docType) throws ServerUnavailableException {

        String url;
        if(semester == 3) // Trimester
            url = STUDENT_CLASS_DOCS_1_TRIMESTER + year + 
                    STUDENT_CLASS_DOCS_2 + (semester-1);
        else
            url = STUDENT_CLASS_DOCS_1 + year +
                    STUDENT_CLASS_DOCS_2 + semester;
            
        url +=  STUDENT_CLASS_DOCS_3 + studentNumberId +
                STUDENT_CLASS_DOCS_4 + course +
                STUDENT_CLASS_DOCS_5 + docType;

        Elements docs = request(mContext, url)
                .body()
                .select("form[method=post]").get(1).select("tr");

        Student student = new Student();
        for(Element doc : docs) {
            if(!doc.hasAttr("bgcolor")) continue;

            String doc_name = doc.child(0).text();
            String doc_url = doc.child(1).child(0).attr("href");
            String doc_date = doc.child(2).text();
            String doc_size = doc.child(3).text();
            String doc_teacher = doc.child(4).text();

            System.out.println("-> " + doc_name + ", " + doc_url + ", " + doc_date + ", " + doc_size + ", " +
                    doc_teacher);

            StudentClassDoc document = new StudentClassDoc();
            document.setName(doc_name);
            document.setUrl(doc_url);
            document.setDate(doc_date);
            document.setSize(doc_size);
            document.setType(docType);

            student.addClassDoc(document);
        }

        return student;
    }

    public static void downloadDoc(Context mContext, String name, String url) {
        String cookie = ClipSettings.getCookie(mContext);

        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(STUDENT_CLASS_DOCS_DOWNLOAD + url));
        request.addRequestHeader("Cookie", COOKIE_NAME + "=" + cookie);
        request.setTitle(name);

        // In order for this to run, you must use the android 3.2 to compile your app
//        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);

        // Get download service and enqueue file
        DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

}
