package com.migueljteixeira.clipmobile.util.tasks;

import android.content.Context;

import com.migueljteixeira.clipmobile.entities.Student;
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException;
import com.migueljteixeira.clipmobile.settings.ClipSettings;
import com.migueljteixeira.clipmobile.util.StudentTools;

public class UpdateStudentPageTask extends BaseTask<Void, Void, Student> {
    
    private final OnUpdateTaskFinishedListener<Student> mListener;

    public UpdateStudentPageTask(Context context, OnUpdateTaskFinishedListener<Student> listener) {
        super(context);
        mListener = listener;
    }

    @Override
    protected Student doInBackground(Void... params) {
        String studentId = ClipSettings.getStudentIdSelected(mContext);
        String studentNumberId = ClipSettings.getStudentNumberidSelected(mContext);
        String studentYearSemesterId = ClipSettings.getStudentYearSemesterIdSelected(mContext);

        try {
            // Update students info
            return StudentTools.updateStudentPage(mContext, studentId, studentNumberId, studentYearSemesterId);
        } catch (ServerUnavailableException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Student result) {
        super.onPostExecute(result);

        if (mListener != null)
            mListener.onUpdateTaskFinished(result);
    }
}
