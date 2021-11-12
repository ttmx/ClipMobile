package com.migueljteixeira.clipmobile.util.tasks

import android.content.Context
import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException
import com.migueljteixeira.clipmobile.settings.ClipSettings
import com.migueljteixeira.clipmobile.util.StudentTools.getStudentClasses

class GetStudentClassesTask(
    context: Context,
    private val mListener: OnTaskFinishedListener<Student?>?
) : BaseTask<Void?, Void?, Student?>(context) {
    override fun doInBackground(vararg params: Void?): Student? {
        val studentId = ClipSettings.getStudentIdSelected(mContext)
        val year = ClipSettings.getYearSelected(mContext)
        val yearFormatted = ClipSettings.getYearSelectedFormatted(mContext)
        val semester = ClipSettings.getSemesterSelected(mContext)
        val studentNumberId = ClipSettings.getStudentNumberidSelected(mContext)
        if (listOf(studentId, year, yearFormatted, semester, studentNumberId).contains(null)) {
            return null
        }

        // Get student classes
        return try {
            getStudentClasses(mContext, studentId!!, year!!, yearFormatted, semester, studentNumberId!!)
        } catch (e: ServerUnavailableException) {
            null
        }
    }

    override fun onPostExecute(result: Student?) {
        super.onPostExecute(result)
        mListener?.onTaskFinished(result)
    }
}