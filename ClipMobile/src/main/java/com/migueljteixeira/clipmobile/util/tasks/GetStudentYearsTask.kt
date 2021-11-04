package com.migueljteixeira.clipmobile.util.tasks

import android.content.Context
import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException
import com.migueljteixeira.clipmobile.util.StudentTools.getStudentsYears

class GetStudentYearsTask(context: Context, private val mListener: OnTaskFinishedListener?) :
    BaseTask<Any?, Void?, Student?>(context) {
    interface OnTaskFinishedListener {
        fun onStudentYearsTaskFinished(resultCode: Student?, groupPosition: Int)
    }

    private var groupPosition: Int = -1
    override fun doInBackground(vararg params: Any?): Student? {
        val student = params[0] as Student
        groupPosition = params[1] as Int
        return try {
            // Get students years
            getStudentsYears(mContext, student.id!!, student.numberId!!)
        } catch (e: ServerUnavailableException) {
            null
        }
    }

    override fun onPostExecute(result: Student?) {
        super.onPostExecute(result)
        mListener?.onStudentYearsTaskFinished(result, groupPosition)
    }
}