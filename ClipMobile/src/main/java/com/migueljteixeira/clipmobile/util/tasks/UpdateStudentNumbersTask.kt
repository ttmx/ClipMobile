package com.migueljteixeira.clipmobile.util.tasks

import android.content.Context
import com.migueljteixeira.clipmobile.entities.User
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException
import com.migueljteixeira.clipmobile.settings.ClipSettings.getLoggedInUserId
import com.migueljteixeira.clipmobile.util.StudentTools.updateStudentNumbersAndYears

class UpdateStudentNumbersTask(
    context: Context?,
    private val mListener: OnUpdateTaskFinishedListener<User?>?
) : BaseTask<Void?, Void?, User?>(
    context!!
) {
    override fun doInBackground(vararg params: Void?): User? {
        val userId = getLoggedInUserId(mContext)
        println("UPDATE STUDENT NUMBERS TASK userId:: $userId")
        return try {
            // Update students numbers and years
            updateStudentNumbersAndYears(mContext, userId)
        } catch (e: ServerUnavailableException) {
            null
        }
    }

    override fun onPostExecute(result: User?) {
        super.onPostExecute(result)
        mListener?.onUpdateTaskFinished(result)
    }
}