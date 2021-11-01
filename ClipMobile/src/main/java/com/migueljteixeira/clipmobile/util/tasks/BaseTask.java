package com.migueljteixeira.clipmobile.util.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.migueljteixeira.clipmobile.R;

public abstract class BaseTask<A,B,C> extends AsyncTask<A, B, C> {

    public interface OnTaskFinishedListener<C> {

        void onTaskFinished(C result);
    }

    public interface OnUpdateTaskFinishedListener<C> {

        void onUpdateTaskFinished(C result);
    }

    protected Context mContext;

    public BaseTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPostExecute(C result) {
        super.onPostExecute(result);

        // Server is unavailable right now
        if(result == null)
            Toast.makeText(mContext, mContext.getString(R.string.connection_failed),
                    Toast.LENGTH_SHORT).show();
    }
    
}