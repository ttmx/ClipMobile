package com.migueljteixeira.clipmobile.ui

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.migueljteixeira.clipmobile.R
import com.migueljteixeira.clipmobile.adapters.ClassListViewAdapter
import com.migueljteixeira.clipmobile.databinding.FragmentClassesBinding
import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.entities.StudentClass
import com.migueljteixeira.clipmobile.settings.ClipSettings.getSemesterSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.saveStudentClassIdSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.saveStudentClassSelected
import com.migueljteixeira.clipmobile.util.tasks.BaseTask
import com.migueljteixeira.clipmobile.util.tasks.GetStudentClassesTask

class ClassesFragment : BaseFragment(), BaseTask.OnTaskFinishedListener<Student?> {
    private lateinit var mListView: ListView
    private lateinit var mTask: GetStudentClassesTask
    private lateinit var adapter: ClassListViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //        ButterKnife.bind(this, view);
        val binding = FragmentClassesBinding.inflate(inflater)
        val root = binding.root
        super.bindHelperViews(root)
        mListView = root.findViewById(R.id.list_view)

        // Show progress spinner
        showProgressSpinner(true)

        // Start AsyncTask
        mTask = GetStudentClassesTask(requireActivity(), this)
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        return root
    }

    override fun onTaskFinished(result: Student?) {
        if (!isAdded) return
        showProgressSpinner(false)

        // Server is unavailable right now
        if (result == null) return
        mListView.adapter = getAdapterItems(result)
        mListView.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                val item = adapter.getItem(position) as ListViewItem?

                // Save class selected and internal classId
                saveStudentClassSelected(requireActivity(), item!!.number)
                saveStudentClassIdSelected(requireActivity(), item.id)
                val fm = requireActivity().supportFragmentManager
                val fragment: Fragment = ClassesDocsFragment()

                // Replace current fragment by ClassesDocsFragment
                fm.beginTransaction().replace(
                    R.id.content_frame, fragment,
                    ClassesDocsFragment.FRAGMENT_TAG
                ).commit()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTasks(mTask)
    }

    private fun getAdapterItems(result: Student): ClassListViewAdapter {
        adapter = ClassListViewAdapter(requireContext())
        val semester = getSemesterSelected(requireActivity())
        if (result.classes == null || result.classes[semester] == null) return adapter!!
        val classes: List<StudentClass> = result.classes[semester]!!
        for (c in classes) adapter.add(ListViewItem(c.id, c.name, c.number))
        return adapter
    }

    class ListViewItem(var id: String?, var name: String?, var number: String?)
}