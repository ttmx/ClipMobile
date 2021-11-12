package com.migueljteixeira.clipmobile.ui

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import android.widget.ExpandableListView
import android.widget.ExpandableListView.OnChildClickListener
import android.widget.ExpandableListView.OnGroupClickListener
import android.widget.Toast
import com.migueljteixeira.clipmobile.R
import com.migueljteixeira.clipmobile.adapters.StudentNumbersAdapter
import com.migueljteixeira.clipmobile.databinding.FragmentStudentNumbersBinding
import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.entities.User
import com.migueljteixeira.clipmobile.settings.ClipSettings.logoutUser
import com.migueljteixeira.clipmobile.settings.ClipSettings.saveStudentIdSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.saveStudentNumberId
import com.migueljteixeira.clipmobile.settings.ClipSettings.saveStudentYearSemesterIdSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.saveYearSelected
import com.migueljteixeira.clipmobile.ui.NavDrawerActivity
import com.migueljteixeira.clipmobile.ui.dialogs.AboutDialogFragment
import com.migueljteixeira.clipmobile.util.tasks.BaseTask.OnUpdateTaskFinishedListener
import com.migueljteixeira.clipmobile.util.tasks.GetStudentNumbersTask
import com.migueljteixeira.clipmobile.util.tasks.GetStudentYearsTask
import com.migueljteixeira.clipmobile.util.tasks.UpdateStudentNumbersTask

class StudentNumbersFragment : BaseFragment(), GetStudentNumbersTask.OnTaskFinishedListener,
    GetStudentYearsTask.OnTaskFinishedListener, OnUpdateTaskFinishedListener<User?> {
    private var mListAdapter: StudentNumbersAdapter? = null
    private lateinit var students: MutableList<Student>
    private lateinit var mListView: ExpandableListView
    private var mYearsTask: GetStudentYearsTask? = null
    private var mUpdateTask: UpdateStudentNumbersTask? = null
    private var mNumbersTask: GetStudentNumbersTask? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentStudentNumbersBinding.inflate(inflater)
        mListView = binding.listView
        super.bindHelperViews(binding.root)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Unfinished task around?
        if (mYearsTask != null && mYearsTask!!.status != AsyncTask.Status.FINISHED ||
            mUpdateTask != null && mUpdateTask!!.status != AsyncTask.Status.FINISHED
        ) showProgressSpinnerOnly(true)

        // The view has been loaded already
        if (mListAdapter != null) {
            mListView.setAdapter(mListAdapter)
            mListView.setOnGroupClickListener(onGroupClickListener)
            mListView.setOnChildClickListener(onChildClickListener)
            return
        }
        showProgressSpinner(true)

        // Start AsyncTask
        mNumbersTask = GetStudentNumbersTask(activity, this@StudentNumbersFragment)
        mNumbersTask!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        //        AndroidUtils.executeOnPool(mNumbersTask);
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_student_numbers, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh -> {
                //                Crashlytics.log("StudentNumbersFragment - refresh");
                Toast.makeText(
                    activity, requireActivity().getString(R.string.refreshing),
                    Toast.LENGTH_LONG
                ).show()

                // Start AsyncTask
                mUpdateTask = UpdateStudentNumbersTask(
                    activity,
                    this@StudentNumbersFragment
                )
                mUpdateTask!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                //                AndroidUtils.executeOnPool(mUpdateTask);
                true
            }
            R.id.logout -> {
                //                Crashlytics.log("StudentNumbersFragment - logout");

                // Clear user personal data
                logoutUser(requireActivity())
                val intent = Intent(activity, ConnectClipActivity::class.java)
                startActivity(intent)
                requireActivity().overridePendingTransition(
                    R.anim.slide_right_in,
                    R.anim.slide_right_out
                )
                requireActivity().finish()
                true
            }
            R.id.about -> {
                // Create an instance of the dialog fragment and show it
                val dialog = AboutDialogFragment()
                dialog.show(requireActivity().supportFragmentManager, "AboutDialogFragment")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private var onGroupClickListener =
        OnGroupClickListener { parent, v, groupPosition, id -> //            Crashlytics.log("StudentNumbersFragment - group clicked");

            // If the yearsTask is running, do not allow group click
            if (mYearsTask != null && mYearsTask!!.status != AsyncTask.Status.FINISHED) {
                println("YEARS TASK IS ALREADY RUNNING!")
                return@OnGroupClickListener true
            }
            if (mListView.isGroupExpanded(groupPosition)) mListView.collapseGroup(groupPosition) else {
                showProgressSpinnerOnly(true)
                println(
                    "GETSTUDENTS YEARS TASK student.getId() " + students[groupPosition].id +
                            ", student.getNumberId() " + students[groupPosition].numberId
                )
                mYearsTask = GetStudentYearsTask(requireActivity(), this@StudentNumbersFragment)
                mYearsTask!!.executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR,
                    students[groupPosition],
                    groupPosition
                )
            }
            true
        }
    private var onChildClickListener =
        OnChildClickListener { parent, v, groupPosition, childPosition, id -> //            Crashlytics.log("StudentNumbersFragment - child clicked");

            // If the updateTask is running, do not allow child click
            if (mUpdateTask != null && mUpdateTask!!.status != AsyncTask.Status.FINISHED) {
                println("UPDATE TASK IS RUNNING!")
                return@OnChildClickListener true
            }

            // Save year, studentId and studentNumberId selected
            val yearSelected = students[groupPosition].getYears()[childPosition].year
            saveYearSelected(requireActivity(), yearSelected)
            saveStudentIdSelected(requireActivity(), students[groupPosition].id)
            saveStudentNumberId(requireActivity(), students[groupPosition].numberId)
            saveStudentYearSemesterIdSelected(
                requireActivity(), students[groupPosition]
                    .getYears()[childPosition].id
            )

            // Lets go to NavDrawerActivity
            val intent = Intent(activity, NavDrawerActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out)
            requireActivity().finish()
            true
        }

    override fun onStudentNumbersTaskFinished(result: User?) {
        if (!isAdded) return

//        Crashlytics.log("StudentNumbersFragment - onStudentNumbersTaskFinished");
        students = result!!.students
        showProgressSpinner(false)
        mListAdapter = StudentNumbersAdapter(requireContext(), students)
        mListView.setAdapter(mListAdapter)
        mListView.setOnGroupClickListener(onGroupClickListener)
        mListView.setOnChildClickListener(onChildClickListener)
    }

    override fun onStudentYearsTaskFinished(result: Student?, groupPosition: Int) {
        if (!isAdded) return

//        Crashlytics.log("StudentNumbersFragment - onStudentYearsTaskFinished");
        showProgressSpinnerOnly(false)

        // Server is unavailable right now
        if (result == null) return

        // Set new data and notifyDataSetChanged
        students[groupPosition].setYears(result.getYears())
        mListAdapter!!.notifyDataSetChanged()

        // Expand group position
        mListView.expandGroup(groupPosition, true)
    }

    override fun onUpdateTaskFinished(result: User?) {
        if (!isAdded) return

//        Crashlytics.log("StudentNumbersFragment - onUpdateTaskFinished");

        // Server is unavailable right now
        if (result == null) return

        // Set new data and notifyDataSetChanged
        students.clear()
        students.addAll(result.students)
        mListAdapter!!.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTasks(mYearsTask)
        cancelTasks(mUpdateTask)
        cancelTasks(mNumbersTask)
    }
}