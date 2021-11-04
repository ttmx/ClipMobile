package com.migueljteixeira.clipmobile.ui

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.ExpandableListView.OnChildClickListener
import android.widget.ExpandableListView.OnGroupClickListener
import com.migueljteixeira.clipmobile.R
import com.migueljteixeira.clipmobile.adapters.StudentClassesDocsAdapter
import com.migueljteixeira.clipmobile.databinding.FragmentStudentClassesDocsBinding
import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.entities.StudentClassDoc
import com.migueljteixeira.clipmobile.exceptions.ServerUnavailableException
import com.migueljteixeira.clipmobile.network.StudentClassesDocsRequest.downloadDoc
import com.migueljteixeira.clipmobile.settings.ClipSettings.getSemesterSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.getStudentClassIdSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.getStudentClassSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.getStudentNumberidSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.getYearSelectedFormatted
import com.migueljteixeira.clipmobile.util.StudentTools.getStudentClassesDocs
import com.migueljteixeira.clipmobile.util.tasks.GetStudentClassesDocsTask
import java.util.*

class ClassesDocsFragment : BaseFragment(), GetStudentClassesDocsTask.OnTaskFinishedListener {
    private var lastExpandedGroupPosition = 0
    private var mListView: ExpandableListView? = null
    private var mListAdapter: StudentClassesDocsAdapter? = null
    private var classDocs: MutableList<StudentClassDoc>? = null
    private var mDocsTask: GetStudentClassesDocsTask? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lastExpandedGroupPosition = -1
        classDocs = LinkedList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentStudentClassesDocsBinding.inflate(inflater)
        val view: View = binding.root
        super.bindHelperViews(view)
        mListView = binding.listView
        return view
    }

    private class GreedyRunnable(var mContext: Context, var g: Int) : Runnable {
        override fun run() {
            val yearFormatted = getYearSelectedFormatted(mContext)
            val semester = getSemesterSelected(mContext)
            val studentNumberId = getStudentNumberidSelected(
                mContext
            )
            val studentClassIdSelected = getStudentClassIdSelected(
                mContext
            )
            val studentClassSelected = getStudentClassSelected(
                mContext
            )
            val docType = mContext.resources
                .getStringArray(R.array.classes_docs_type_array)[g]
            try {
                getStudentClassesDocs(
                    mContext, studentClassIdSelected!!, yearFormatted,
                    semester, studentNumberId!!, studentClassSelected!!, docType
                )
            } catch (e: ServerUnavailableException) {
                e.printStackTrace()
            }
        }
    }

    private fun greedyLoadFiles() {
        var runnable: Runnable
        for (i in requireContext().resources
            .getStringArray(R.array.classes_docs_type_array).indices) {
            runnable = GreedyRunnable(requireContext(), i)
            AsyncTask.execute(runnable)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val strings = resources.getStringArray(R.array.classes_docs_array)
        //        for (int i = 0; i < strings.length; i++) {
//            strings[i] += " [heh]";
//        }
        greedyLoadFiles()
        mListAdapter = StudentClassesDocsAdapter(
            activity,
            strings, classDocs
        )
        mListView!!.setAdapter(mListAdapter)
        mListView!!.setOnGroupClickListener(onGroupClickListener)
        mListView!!.setOnChildClickListener(onChildClickListener)

        // Unfinished task around?
        if (mDocsTask != null && mDocsTask!!.status != AsyncTask.Status.FINISHED) showProgressSpinnerOnly(
            true
        )

        /*// The view has been loaded already
        if(mListAdapter != null) {
            mListView.setAdapter(mListAdapter);
            mListView.setOnGroupClickListener(onGroupClickListener);
            mListView.setOnChildClickListener(onChildClickListener);
            return;
        }*/
    }

    var onGroupClickListener = OnGroupClickListener { parent, v, groupPosition, id ->
        if (mListView!!.isGroupExpanded(groupPosition)) mListView!!.collapseGroup(groupPosition) else {
            showProgressSpinnerOnly(true)
            mDocsTask = GetStudentClassesDocsTask(activity, this@ClassesDocsFragment)
            mDocsTask!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupPosition)
        }
        true
    }

    override fun onTaskFinished(result: Student?, groupPosition: Int) {
        if (!isAdded) return
        showProgressSpinnerOnly(false)

        // Server is unavailable right now
        if (result == null || result.classesDocs.isEmpty()) return

        // Collapse last expanded group
        if (lastExpandedGroupPosition != -1) mListView!!.collapseGroup(lastExpandedGroupPosition)
        lastExpandedGroupPosition = groupPosition

        // Set new data and notify adapter
        classDocs!!.clear()
        classDocs!!.addAll(result.classesDocs)
        mListAdapter!!.notifyDataSetChanged()

        // Expand group position
        mListView!!.expandGroup(groupPosition, true)
    }

    var onChildClickListener = OnChildClickListener { parent, v, groupPosition, childPosition, id ->
        val name = classDocs!![childPosition].name
        val url = classDocs!![childPosition].url

        // Download document
        downloadDoc(requireActivity(), name, url!!)
        true
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTasks(mDocsTask)
    }

    companion object {
        const val FRAGMENT_TAG = "classes_docs_tag"
    }
}