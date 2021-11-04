package com.migueljteixeira.clipmobile.network

import kotlin.Throws
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException
import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.entities.StudentClassDoc
import com.migueljteixeira.clipmobile.settings.ClipSettings
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment

object StudentClassesDocsRequest : Request() {
    private const val STUDENT_CLASS_DOCS_1 =
        "https://clip.unl.pt/utente/eu/aluno/ano_lectivo/unidades/unidade_curricular/actividade/documentos?tipo_de_per%EDodo_lectivo=s&ano_lectivo="
    private const val STUDENT_CLASS_DOCS_1_TRIMESTER =
        "https://clip.unl.pt/utente/eu/aluno/ano_lectivo/unidades/unidade_curricular/actividade/documentos?tipo_de_per%EDodo_lectivo=t&ano_lectivo="
    private const val STUDENT_CLASS_DOCS_2 = "&per%EDodo_lectivo="
    private const val STUDENT_CLASS_DOCS_3 = "&aluno="
    private const val STUDENT_CLASS_DOCS_4 = "&institui%E7%E3o=97747&unidade_curricular="
    private const val STUDENT_CLASS_DOCS_5 = "&tipo_de_documento_de_unidade="
    private const val STUDENT_CLASS_DOCS_DOWNLOAD = "https://clip.unl.pt"
    @Throws(ServerUnavailableException::class)
    fun getClassesDocs(
        mContext: Context, studentNumberId: String,
        year: String, semester: Int, course: String,
        docType: String
    ): Student {
        var url: String = if (semester == 3) // Trimester
            STUDENT_CLASS_DOCS_1_TRIMESTER + year +
                    STUDENT_CLASS_DOCS_2 + (semester - 1) else STUDENT_CLASS_DOCS_1 + year +
                STUDENT_CLASS_DOCS_2 + semester
        url += STUDENT_CLASS_DOCS_3 + studentNumberId +
                STUDENT_CLASS_DOCS_4 + course +
                STUDENT_CLASS_DOCS_5 + docType
        val docs = request(mContext, url)
            .body()
            .select("form[method=post]")[1].select("tr")
        val student = Student()
        for (doc in docs) {
            if (!doc.hasAttr("bgcolor")) continue
            val docName = doc.child(0).text()
            val docUrl = doc.child(1).child(0).attr("href")
            val docDate = doc.child(2).text()
            val docSize = doc.child(3).text()
            val docTeacher = doc.child(4).text()
            println(
                "-> " + docName + ", " + docUrl + ", " + docDate + ", " + docSize + ", " +
                        docTeacher
            )
            val document = StudentClassDoc()
            document.name = docName
            document.url = docUrl
            document.date = docDate
            document.size = docSize
            document.type = docType
            student.addClassDoc(document)
        }
        return student
    }

    @JvmStatic
    fun downloadDoc(mContext: Context, name: String?, url: String) {
        val cookie = ClipSettings.getCookie(mContext)
        val request = DownloadManager.Request(
            Uri.parse(STUDENT_CLASS_DOCS_DOWNLOAD + url)
        )
        request.addRequestHeader("Cookie", "$COOKIE_NAME=$cookie")
        request.setTitle(name)

        // In order for this to run, you must use the android 3.2 to compile your app
//        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)

        // Get download service and enqueue file
        val manager = mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }
}