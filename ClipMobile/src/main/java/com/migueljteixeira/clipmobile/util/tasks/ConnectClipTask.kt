package com.migueljteixeira.clipmobile.util.tasks

import android.content.Context
import android.widget.Toast
import com.migueljteixeira.clipmobile.R
import com.migueljteixeira.clipmobile.enums.Result
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException
import com.migueljteixeira.clipmobile.util.StudentTools.signIn

class ConnectClipTask(context: Context?, private val mListener: OnTaskFinishedListener<Result>?) :
    BaseTask<String?, Void?, Result?>(
        context!!
    ) {
    override fun doInBackground(vararg params: String?): Result? {
        // Get user data
        val username = params[0]!!
        val password = params[1]!!
        return try {
            signIn(mContext, username, password)
        } catch (e: ServerUnavailableException) {
            Result.OFFLINE
        }
    }

    override fun onPostExecute(result: Result?) {
        super.onPostExecute(result)
        when (result) {
            Result.OFFLINE -> Toast.makeText(
                mContext,
                mContext.getString(R.string.connection_failed), Toast.LENGTH_SHORT
            ).show()
            Result.ERROR -> Toast.makeText(
                mContext,
                mContext.getString(R.string.error_fields_incorrect), Toast.LENGTH_SHORT
            ).show()
        }
        mListener?.onTaskFinished(result)
    }
}