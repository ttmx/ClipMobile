package com.migueljteixeira.clipmobile.util.tasks

import android.content.Context
import com.migueljteixeira.clipmobile.util.StudentTools.getStudentSchedule
import com.migueljteixeira.clipmobile.util.tasks.BaseTask
import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.settings.ClipSettings
import com.migueljteixeira.clipmobile.util.StudentTools
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException

class GetStudentScheduleTask(
    context: Context,
    private val mListener: OnTaskFinishedListener<Student?>?
) : BaseTask<Void?, Void?, Student?>(context) {
    override fun doInBackground(vararg params: Void?): Student? {
        val studentId = ClipSettings.getStudentIdSelected(mContext)
        val year = ClipSettings.getYearSelected(mContext)
        val yearFormatted = ClipSettings.getYearSelectedFormatted(mContext)
        val semester = ClipSettings.getSemesterSelected(mContext)
        val studentNumberId = ClipSettings.getStudentNumberidSelected(mContext)
        //String yearSemesterId = ClipSettings.getStudentYearSemesterIdSelected(mContext);
        if (listOf(studentId,year,yearFormatted,semester).contains(null)){
            return null
        }

        // Get student schedule
        return try {
            println("studentId ->$studentId")
            println("year ->$year")
            println("yearFormatted ->$yearFormatted")
            println("semester ->$semester")
            getStudentSchedule(
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