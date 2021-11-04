package com.migueljteixeira.clipmobile.util.tasks

import android.content.Context
import com.migueljteixeira.clipmobile.entities.User
import com.migueljteixeira.clipmobile.settings.ClipSettings.getLoggedInUserId
import com.migueljteixeira.clipmobile.util.StudentTools.getStudents

class GetStudentNumbersTask(context: Context?, private val mListener: OnTaskFinishedListener?) :
    BaseTask<Void?, Void?, User?>(
        context!!
    ) {
    interface OnTaskFinishedListener {
        fun onStudentNumbersTaskFinished(result: User?)
    }

    override fun doInBackground(vararg params: Void?): User {
        val userId = getLoggedInUserId(mContext)

        // Get students numbers
        return getStudents(mContext, userId)
    }

    override fun onPostExecute(result: User?) {
        super.onPostExecute(result)
        mListener?.onStudentNumbersTaskFinished(result)
    }
}