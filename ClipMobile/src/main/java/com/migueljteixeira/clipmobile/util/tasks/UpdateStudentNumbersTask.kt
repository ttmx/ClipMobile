package com.migueljteixeira.clipmobile.util.tasks;

import android.content.Context;

import com.migueljteixeira.clipmobile.entities.User;
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException;
import com.migueljteixeira.clipmobile.settings.ClipSettings;
import com.migueljteixeira.clipmobile.util.StudentTools;

public class UpdateStudentNumbersTask extends BaseTask<Void, Void, User> {
    
    private final OnUpdateTaskFinishedListener<User> mListener;

    public UpdateStudentNumbersTask(Context context, OnUpdateTaskFinishedListener<User> listener) {
        super(context);
        mListener = listener;
    }

    @Override
    protected User doInBackground(Void... params) {
        long userId = ClipSettings.getLoggedInUserId(mContext);

        System.out.println("UPDATE STUDENT NUMBERS TASK userId:: " + userId);

        try {
            // Update students numbers and years
            return StudentTools.updateStudentNumbersAndYears(mContext, userId);
        } catch (ServerUnavailableException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(User result) {
        super.onPostExecute(result);

        if (mListener != null)
            mListener.onUpdateTaskFinished(result);
    }
}
