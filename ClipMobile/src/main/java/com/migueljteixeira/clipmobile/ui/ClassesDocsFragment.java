package com.migueljteixeira.clipmobile.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;

import com.migueljteixeira.clipmobile.R;
import com.migueljteixeira.clipmobile.adapters.StudentClassesDocsAdapter;
import com.migueljteixeira.clipmobile.databinding.FragmentStudentClassesDocsBinding;
import com.migueljteixeira.clipmobile.entities.Student;
import com.migueljteixeira.clipmobile.entities.StudentClassDoc;
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException;
import com.migueljteixeira.clipmobile.network.StudentClassesDocsRequest;
import com.migueljteixeira.clipmobile.settings.ClipSettings;
import com.migueljteixeira.clipmobile.util.StudentTools;
import com.migueljteixeira.clipmobile.util.tasks.GetStudentClassesDocsTask;

import java.util.LinkedList;
import java.util.List;


public class ClassesDocsFragment extends BaseFragment
        implements GetStudentClassesDocsTask.OnTaskFinishedListener {

    public static final String FRAGMENT_TAG = "classes_docs_tag";
    private int lastExpandedGroupPosition;
    private ExpandableListView mListView;
    private StudentClassesDocsAdapter mListAdapter;
    private List<StudentClassDoc> classDocs;
    private GetStudentClassesDocsTask mDocsTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lastExpandedGroupPosition = -1;
        classDocs = new LinkedList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentStudentClassesDocsBinding binding = FragmentStudentClassesDocsBinding.inflate(inflater);
        View view = binding.getRoot();
        super.bindHelperViews(view);

        mListView = binding.listView;

        return view;
    }

    private static class GreedyRunnable implements Runnable {

        Context mContext;
        int g;
        public GreedyRunnable(Context mContext, int g){
            this.mContext = mContext;
            this.g = g;
        }

        @Override
        public void run() {
            String yearFormatted = ClipSettings.getYearSelectedFormatted(mContext);
            int semester = ClipSettings.getSemesterSelected(mContext);
            String studentNumberId = ClipSettings.getStudentNumberidSelected(mContext);
            String studentClassIdSelected = ClipSettings.getStudentClassIdSelected(mContext);
            String studentClassSelected = ClipSettings.getStudentClassSelected(mContext);
            String docType = mContext.getResources()
                    .getStringArray(R.array.classes_docs_type_array)[g];
            try {
                StudentTools.getStudentClassesDocs(mContext, studentClassIdSelected, yearFormatted,
                        semester, studentNumberId, studentClassSelected, docType);
            } catch (ServerUnavailableException e) {
                e.printStackTrace();
            }

        }
    }

    private void greedyLoadFiles(){
        Runnable runnable;

        for (int i = 0; i < requireContext().getResources()
                .getStringArray(R.array.classes_docs_type_array).length; i++) {

            runnable = new GreedyRunnable(requireContext(),i);
            AsyncTask.execute(runnable);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] strings = getResources().getStringArray(R.array.classes_docs_array);
//        for (int i = 0; i < strings.length; i++) {
//            strings[i] += " [heh]";
//        }
        greedyLoadFiles();


        mListAdapter = new StudentClassesDocsAdapter(getActivity(),
                strings, classDocs);
        mListView.setAdapter(mListAdapter);
        mListView.setOnGroupClickListener(onGroupClickListener);
        mListView.setOnChildClickListener(onChildClickListener);

        // Unfinished task around?
        if (mDocsTask != null && mDocsTask.getStatus() != AsyncTask.Status.FINISHED)
            showProgressSpinnerOnly(true);

        /*// The view has been loaded already
        if(mListAdapter != null) {
            mListView.setAdapter(mListAdapter);
            mListView.setOnGroupClickListener(onGroupClickListener);
            mListView.setOnChildClickListener(onChildClickListener);
            return;
        }*/
    }

    ExpandableListView.OnGroupClickListener onGroupClickListener = new ExpandableListView.OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            if (mListView.isGroupExpanded(groupPosition))
                mListView.collapseGroup(groupPosition);

            else {
                showProgressSpinnerOnly(true);

                mDocsTask = new GetStudentClassesDocsTask(getActivity(), ClassesDocsFragment.this);
                mDocsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupPosition);
            }

            return true;
        }
    };

    @Override
    public void onTaskFinished(Student result, int groupPosition) {
        if (!isAdded())
            return;

        showProgressSpinnerOnly(false);

        // Server is unavailable right now
        if (result == null || result.getClassesDocs().size() == 0)
            return;

        // Collapse last expanded group
        if (lastExpandedGroupPosition != -1)
            mListView.collapseGroup(lastExpandedGroupPosition);

        lastExpandedGroupPosition = groupPosition;

        // Set new data and notify adapter
        classDocs.clear();
        classDocs.addAll(result.getClassesDocs());
        mListAdapter.notifyDataSetChanged();

        // Expand group position
        mListView.expandGroup(groupPosition, true);
    }

    ExpandableListView.OnChildClickListener onChildClickListener = new ExpandableListView.OnChildClickListener() {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            String name = classDocs.get(childPosition).getName();
            String url = classDocs.get(childPosition).getUrl();

            // Download document
            StudentClassesDocsRequest.downloadDoc(getActivity(), name, url);

            return true;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelTasks(mDocsTask);
    }
}
