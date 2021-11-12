package com.migueljteixeira.clipmobile.util.tasks

import android.content.Context
import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException
import com.migueljteixeira.clipmobile.settings.ClipSettings.getSemesterSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.getStudentIdSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.getStudentNumberidSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.getYearSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.getYearSelectedFormatted
import com.migueljteixeira.clipmobile.util.StudentTools.getStudentCalendar

class GetStudentCalendarTask(
    context: Context,
    private val mListener: OnTaskFinishedListener<Student?>?
) : BaseTask<Void?, Void?, Student?>(context) {
    override fun doInBackground(vararg params: Void?): Student? {
        val studentId = getStudentIdSelected(mContext)
        val year = getYearSelected(mContext)
        val yearFormatted = getYearSelectedFormatted(mContext)
        val semester = getSemesterSelected(mContext)
        val studentNumberId = getStudentNumberidSelected(mContext)

        if(listOf(studentId,year,yearFormatted,semester,studentNumberId).contains(null))
            return null

        // Get student calendar
        return try {
            getStudentCalendar(
                mContext, studentId!!, year!!, yearFormatted, semester,
                studentNumberId!!
            )!!
        } catch (e: ServerUnavailableException) {
            null
        }
    }

    override fun onPostExecute(result: Student?) {
        super.onPostExecute(result)
        mListener?.onTaskFinished(result)
    }
}