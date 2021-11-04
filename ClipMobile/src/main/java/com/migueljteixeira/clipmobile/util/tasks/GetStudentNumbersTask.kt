package com.migueljteixeira.clipmobile.util.tasks;

import android.content.Context;

import com.migueljteixeira.clipmobile.entities.User;
import com.migueljteixeira.clipmobile.settings.ClipSettings;
import com.migueljteixeira.clipmobile.util.StudentTools;

public class GetStudentNumbersTask extends BaseTask<Void, Void, User> {

    public interface OnTaskFinishedListener {

        void onStudentNumbersTaskFinished(User result);
    }

    private final OnTaskFinishedListener mListener;

    public GetStudentNumbersTask(Context context, OnTaskFinishedListener listener) {
        super(context);
        mListener = listener;
    }

    @Override
    protected User doInBackground(Void... params) {
        long userId = ClipSettings.getLoggedInUserId(mContext);

        // Get students numbers
        return StudentTools.getStudents(mContext, userId);
    }

    @Override
    protected void onPostExecute(User result) {
        super.onPostExecute(result);

        if (mListener != null)
            mListener.onStudentNumbersTaskFinished(result);
    }
}
