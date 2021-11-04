package com.migueljteixeira.clipmobile.util.tasks

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import com.migueljteixeira.clipmobile.R

abstract class BaseTask<A, B, C>(protected var mContext: Context) : AsyncTask<A, B, C?>() {
    interface OnTaskFinishedListener<C> {
        fun onTaskFinished(result: C?)
    }

    interface OnUpdateTaskFinishedListener<C> {
        fun onUpdateTaskFinished(result: C)
    }

    override fun onPostExecute(result: C?) {
        super.onPostExecute(result)

        // Server is unavailable right now
        if (result == null) Toast.makeText(
            mContext, mContext.getString(R.string.connection_failed),
            Toast.LENGTH_SHORT
        ).show()
    }
}