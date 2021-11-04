package com.migueljteixeira.clipmobile.util.tasks;

import android.content.Context;

import com.migueljteixeira.clipmobile.entities.Student;
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException;
import com.migueljteixeira.clipmobile.settings.ClipSettings;
import com.migueljteixeira.clipmobile.util.StudentTools;

public class GetStudentClassesTask extends BaseTask<Void, Void, Student> {
    
    private final OnTaskFinishedListener<Student> mListener;
    
    public GetStudentClassesTask(Context context, OnTaskFinishedListener<Student> listener) {
        super(context);
        mListener = listener;
    }

    @Override
    protected Student doInBackground(Void... params) {
        String studentId = ClipSettings.getStudentIdSelected(mContext);
        String year = ClipSettings.getYearSelected(mContext);
        String yearFormatted = ClipSettings.getYearSelectedFormatted(mContext);
        int semester = ClipSettings.getSemesterSelected(mContext);
        String studentNumberId = ClipSettings.getStudentNumberidSelected(mContext);

        // Get student classes
        try {
            return StudentTools.getStudentClasses(mContext, studentId, year, yearFormatted, semester, studentNumberId);
        } catch (ServerUnavailableException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Student result) {
        super.onPostExecute(result);

        if (mListener != null)
            mListener.onTaskFinished(result);
    }
}