package com.migueljteixeira.clipmobile.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.migueljteixeira.clipmobile.R;
import com.migueljteixeira.clipmobile.adapters.StudentClassesDocsAdapter;
import com.migueljteixeira.clipmobile.entities.Student;
import com.migueljteixeira.clipmobile.entities.StudentClassDoc;
import com.migueljteixeira.clipmobile.network.StudentClassesDocsRequest;
import com.migueljteixeira.clipmobile.util.tasks.GetStudentClassesDocsTask;
//import com.uwetrottmann.androidutils.AndroidUtils;

import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;

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
        classDocs = new LinkedList<StudentClassDoc>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_classes_docs, container, false);
        ButterKnife.bind(this, view);

        mListView = (ExpandableListView) view.findViewById(R.id.list_view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListAdapter = new StudentClassesDocsAdapter(getActivity(),
                getResources().getStringArray(R.array.classes_docs_array), classDocs);
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
            if(mListView.isGroupExpanded(groupPosition))
                mListView.collapseGroup(groupPosition);

            else {
                showProgressSpinnerOnly(true);

                mDocsTask = new GetStudentClassesDocsTask(getActivity(), ClassesDocsFragment.this);
                mDocsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupPosition);
//                AndroidUtils.executeOnPool(mDocsTask, groupPosition);
            }

            return true;
        }
    };

    @Override
    public void onTaskFinished(Student result, int groupPosition) {
        if(!isAdded())
            return;

        showProgressSpinnerOnly(false);

        // Server is unavailable right now
        if(result == null || result.getClassesDocs().size() == 0)
            return;

        // Collapse last expanded group
        if(lastExpandedGroupPosition != -1)
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
