package com.migueljteixeira.clipmobile.util.tasks

import android.content.Context
import com.migueljteixeira.clipmobile.R
import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException
import com.migueljteixeira.clipmobile.settings.ClipSettings.getSemesterSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.getStudentClassIdSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.getStudentClassSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.getStudentNumberidSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.getYearSelectedFormatted
import com.migueljteixeira.clipmobile.util.StudentTools.getStudentClassesDocs

class GetStudentClassesDocsTask(context: Context?, private val mListener: OnTaskFinishedListener?) :
    BaseTask<Int?, Void?, Student?>(
        context!!
    ) {
    interface OnTaskFinishedListener {
        fun onTaskFinished(result: Student?, groupPosition: Int)
    }

    private var groupPosition: Int? = null
    override fun doInBackground(vararg params: Int?): Student? {
        groupPosition = params[0]

        //String studentId = ClipSettings.getStudentIdSelected(mContext);
        val yearFormatted = getYearSelectedFormatted(mContext)
        val semester = getSemesterSelected(mContext)
        val studentNumberId = getStudentNumberidSelected(mContext)
        val studentClassIdSelected = getStudentClassIdSelected(mContext)
        val studentClassSelected = getStudentClassSelected(mContext)
        val docType = mContext.resources
            .getStringArray(R.array.classes_docs_type_array)[groupPosition!!]

        /*System.out.println("DOINBACKGROUND -> studentID" + studentId + ", year:" + year
                + ", semester:" + semester
                + ", studentNumberID:" + studentNumberId);*/

        // Get student class docs
        return try {
            getStudentClassesDocs(
                mContext, studentClassIdSelected!!, yearFormatted,
                semester, studentNumberId!!, studentClassSelected!!, docType
            )
        } catch (e: ServerUnavailableException) {
            null
        }
    }

    override fun onPostExecute(result: Student?) {
        super.onPostExecute(result)
        mListener?.onTaskFinished(result, groupPosition!!)
    }
}