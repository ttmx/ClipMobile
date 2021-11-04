package com.migueljteixeira.clipmobile.util.tasks

import android.content.Context
import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException
import com.migueljteixeira.clipmobile.settings.ClipSettings.getStudentIdSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.getStudentNumberidSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.getStudentYearSemesterIdSelected
import com.migueljteixeira.clipmobile.util.StudentTools.updateStudentPage

class UpdateStudentPageTask(
    context: Context?,
    private val mListener: OnUpdateTaskFinishedListener<Student?>?
) : BaseTask<Void?, Void?, Student?>(
    context!!
) {
    override fun doInBackground(vararg params: Void?): Student? {
        val studentId = getStudentIdSelected(mContext)
        val studentNumberId = getStudentNumberidSelected(mContext)
        val studentYearSemesterId = getStudentYearSemesterIdSelected(mContext)
        return if (studentId == null || studentNumberId == null || studentYearSemesterId == null) null else try {
            // Update students info
            updateStudentPage(mContext, studentId, studentNumberId, studentYearSemesterId)
        } catch (e: ServerUnavailableException) {
            null
        }
    }

    override fun onPostExecute(result: Student?) {
        super.onPostExecute(result)
        mListener?.onUpdateTaskFinished(result)
    }
}