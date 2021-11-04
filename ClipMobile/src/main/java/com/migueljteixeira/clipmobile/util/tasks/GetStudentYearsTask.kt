package com.migueljteixeira.clipmobile.util.tasks;

import android.content.Context;

import com.migueljteixeira.clipmobile.entities.Student;
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException;
import com.migueljteixeira.clipmobile.util.StudentTools;

public class GetStudentYearsTask extends BaseTask<Object, Void, Student> {

    public interface OnTaskFinishedListener {

        void onStudentYearsTaskFinished(Student resultCode, int groupPosition);
    }

    private final OnTaskFinishedListener mListener;
    private Integer groupPosition;

    public GetStudentYearsTask(Context context, OnTaskFinishedListener listener) {
        super(context);
        mListener = listener;
    }

    @Override
    protected Student doInBackground(Object... params) {
        Student student = (Student) params[0];
        groupPosition = (Integer) params[1];
        
        try {
            // Get students years
            return StudentTools.getStudentsYears(mContext, student.getId(), student.getNumberId());
        } catch (ServerUnavailableException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Student result) {
        super.onPostExecute(result);

        if (mListener != null)
            mListener.onStudentYearsTaskFinished(result, groupPosition);
    }
}
