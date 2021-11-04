package com.migueljteixeira.clipmobile.util.tasks;

import android.content.Context;

import com.migueljteixeira.clipmobile.entities.Student;
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException;
import com.migueljteixeira.clipmobile.settings.ClipSettings;
import com.migueljteixeira.clipmobile.util.StudentTools;

public class GetStudentScheduleTask extends BaseTask<Void, Void, Student> {
    
    private final OnTaskFinishedListener<Student> mListener;

    public GetStudentScheduleTask(Context context, OnTaskFinishedListener<Student> listener) {
        super(context);
        mListener = listener;
    }

    @Override
    protected Student doInBackground(Void... params) {
        String studentId = ClipSettings.getStudentIdSelected(mContext);
        String year = ClipSettings.getYearSelected(mContext);
        String yearFormatted = ClipSettings.getYearSelectedFormatted(mContext);
        int semester = ClipSettings.getSemesterSelected(mContext);

        //String yearSemesterId = ClipSettings.getStudentYearSemesterIdSelected(mContext);
        String studentNumberId = ClipSettings.getStudentNumberidSelected(mContext);

        // Get student schedule
        try {
            System.out.println("studentId ->" + studentId);
            System.out.println("year ->" + year);
            System.out.println("yearFormatted ->" + yearFormatted);
            System.out.println("semester ->" + semester);

            return StudentTools.getStudentSchedule(mContext, studentId, year, yearFormatted, semester,
                    studentNumberId);
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