package com.migueljteixeira.clipmobile.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.DialogFragment
import com.migueljteixeira.clipmobile.R
import com.migueljteixeira.clipmobile.adapters.DrawerAdapter
import com.migueljteixeira.clipmobile.entities.Student
import com.migueljteixeira.clipmobile.settings.ClipSettings.getSemesterSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.getYearSelected
import com.migueljteixeira.clipmobile.settings.ClipSettings.logoutUser
import com.migueljteixeira.clipmobile.settings.ClipSettings.saveSemesterSelected
import com.migueljteixeira.clipmobile.ui.dialogs.AboutDialogFragment
import com.migueljteixeira.clipmobile.ui.dialogs.ExportCalendarDialogFragment
import com.migueljteixeira.clipmobile.util.StudentTools.confirmExportCalendar
import com.migueljteixeira.clipmobile.util.tasks.BaseTask.OnUpdateTaskFinishedListener
import com.migueljteixeira.clipmobile.util.tasks.UpdateStudentPageTask
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import androidx.core.app.ActivityCompat

import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED


class NavDrawerActivity : BaseActivity(), OnItemClickListener,
    OnUpdateTaskFinishedListener<Student?>,ActivityCompat.OnRequestPermissionsResultCallback {
    private final val CALLBACK_ID = 0xca1
    private var mDrawerLayout: DrawerLayout? = null
    private lateinit var mDrawerList: ListView
    private var mUpdateTask: UpdateStudentPageTask? = null
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dualpane)
        setupActionBar()

//        Crashlytics.log("NavDrawerActivity - onCreate");
        setupNavDrawer()

        // Set toolbar title
        if (savedInstanceState == null) {
            setTitle(R.string.drawer_schedule)
            hideActionBarShadow()
        } else {
            val title = savedInstanceState.getString(CURRENT_FRAGMENT_TITLE_TAG)
            if (title != null) {
                setTitle(title)
                val position = savedInstanceState.getInt(CURRENT_FRAGMENT_POSITION_TAG)
                if (position == MENU_ITEM_SCHEDULE_POSITION ||
                    position == MENU_ITEM_CALENDAR_POSITION
                ) hideActionBarShadow()
            }
        }
        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.content_frame)
        if (fragment == null) {
            fragment = ScheduleViewPager()
            fm.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CURRENT_FRAGMENT_TITLE_TAG, title.toString())
        outState.putInt(CURRENT_FRAGMENT_POSITION_TAG, mDrawerList.checkedItemPosition)
    }

    private fun setupNavDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout)

        // Setup menu adapter
        val drawerAdapter = DrawerAdapter(this)
        drawerAdapter.add(DrawerTitle(getYearSelected(this)))
        drawerAdapter.add(DrawerDivider())
        drawerAdapter.add(DrawerItem(getString(R.string.drawer_schedule), R.drawable.ic_books))
        drawerAdapter.add(DrawerItem(getString(R.string.drawer_calendar), R.drawable.ic_calendar))
        drawerAdapter.add(DrawerItem(getString(R.string.drawer_classes), R.drawable.ic_folders))
        drawerAdapter.add(DrawerTitle(getString(R.string.drawer_info_title)))
        drawerAdapter.add(DrawerDivider())
        drawerAdapter.add(DrawerItem(getString(R.string.drawer_info_map), R.drawable.ic_map))
        drawerAdapter.add(DrawerItem(getString(R.string.drawer_info_contacts), R.drawable.ic_phone))
        mDrawerList = findViewById(R.id.left_drawer)
        mDrawerList.adapter = drawerAdapter
        mDrawerList.setItemChecked(MENU_ITEM_SCHEDULE_POSITION, true)
        mDrawerList.onItemClickListener = this

        // If the device is smaller than 7',
        // hide the drawer and set the icon
        if (!resources.getBoolean(R.bool.drawer_opened)) {
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            mToolbar.setNavigationIcon(R.drawable.ic_drawer)
            mToolbar.setNavigationContentDescription(R.string.drawer_open)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        if (mDrawerList.checkedItemPosition == MENU_ITEM_CALENDAR_POSITION) inflater.inflate(
            R.menu.menu_student_page_calendar,
            menu
        ) else inflater.inflate(R.menu.menu_student_page, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        when (getSemesterSelected(this)) {
            1 -> menu.findItem(R.id.semester1).isChecked =
                true
            2 -> menu.findItem(R.id.semester2).isChecked =
                true
            else -> menu.findItem(R.id.trimester2).isChecked = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // Check if we should toggle the navigation drawer
        if (item.itemId == android.R.id.home) {
            if (mDrawerLayout!!.isDrawerVisible(GravityCompat.START)) mDrawerLayout!!.closeDrawer(
                GravityCompat.START
            ) else mDrawerLayout!!.openDrawer(GravityCompat.START)
        } else if (item.itemId == R.id.refresh) {
//            Crashlytics.log("NavDrawerActivity - refresh");

            // Refreshing
            Toast.makeText(
                this, getString(R.string.refreshing),
                Toast.LENGTH_LONG
            ).show()

            // Start AsyncTask
            mUpdateTask = UpdateStudentPageTask(this, this@NavDrawerActivity)
            mUpdateTask!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        } else if (item.itemId == R.id.export_calendar) {

            checkPermission(CALLBACK_ID, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
        } else if (item.itemId == R.id.logout) {
            // Clear user personal data
            logoutUser(this)
            val intent = Intent(this, ConnectClipActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out)
            finish()
        } else if (item.itemId == R.id.about) {
            // Create an instance of the dialog fragment and show it
            val dialog: DialogFragment = AboutDialogFragment()
            dialog.show(supportFragmentManager, "AboutDialogFragment")
        } else if (!item.isChecked && (item.itemId == R.id.semester1 || item.itemId == R.id.semester2)
            || item.itemId == R.id.trimester2
        ) {
            // Check item
            item.isChecked = true
            when (item.itemId) {
                R.id.semester1 -> saveSemesterSelected(
                    this,
                    1
                )
                R.id.semester2 -> saveSemesterSelected(
                    this,
                    2
                )
                else -> saveSemesterSelected(this, 3)
            }

            // Refresh current view
            mDrawerList.performItemClick(
                mDrawerList.getChildAt(mDrawerList.checkedItemPosition),
                mDrawerList.checkedItemPosition,
                mDrawerList.adapter.getItemId(mDrawerList.checkedItemPosition)
            )
        } else return super.onOptionsItemSelected(item)
        return true
    }

    private fun exportCalendar(){

        val calendarsNames: Map<Long, String?> = confirmExportCalendar(this)
        val ids = LongArray(calendarsNames.size)
        val names = arrayOfNulls<String>(calendarsNames.size)
        var count = 0
        for ((key, value) in calendarsNames) {
            ids[count] = key
            names[count] = value
            count++
        }
        val bundle = Bundle()
        bundle.putLongArray(ExportCalendarDialogFragment.CALENDAR_ID, ids)
        bundle.putStringArray(ExportCalendarDialogFragment.CALENDAR_NAME, names)

        // Create an instance of the dialog fragment and show it
        val dialog: DialogFragment = ExportCalendarDialogFragment()
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, "ExportCalendarDialogFragment")
    }

    private fun checkPermission(callbackId: Int, vararg permissionsId: String) {
        var permissions = true
        for (p in permissionsId) {
            permissions =
                permissions && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED
        }
        if (!permissions) ActivityCompat.requestPermissions(this, permissionsId, callbackId)
        else exportCalendar()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == CALLBACK_ID){
            if(grantResults.all { a-> a == PERMISSION_GRANTED }){
                exportCalendar()
            }else{
                Toast.makeText(this,"Permissões rejeitadas, não é possível exportar",Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // If the device is bigger than 7', keep the drawer opened
        if (!resources.getBoolean(R.bool.drawer_opened)) {
            mDrawerLayout!!.closeDrawer(GravityCompat.START)
            mDrawerLayout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
        //mDrawerLayout.openDrawer(GravityCompat.START);
        //mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
    }

    override fun onBackPressed() {
        if (resources.getBoolean(R.bool.drawer_opened) ||
            !mDrawerLayout!!.isDrawerOpen(GravityCompat.START)
        ) {
            val fm = supportFragmentManager
            val fragment = fm.findFragmentByTag(ClassesDocsFragment.FRAGMENT_TAG)
            if (fragment != null) {
                // Refresh current view
                mDrawerList.performItemClick(
                    mDrawerList.getChildAt(mDrawerList.checkedItemPosition),
                    mDrawerList.checkedItemPosition,
                    mDrawerList.adapter.getItemId(mDrawerList.checkedItemPosition)
                )
                return
            }
            val intent = Intent(this, StudentNumbersActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out)
            finish()
        } else mDrawerLayout!!.closeDrawer(GravityCompat.START)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.content_frame)
        invalidateOptionsMenu()
        when (position) {
            MENU_ITEM_SCHEDULE_POSITION -> {
                setTitle(R.string.drawer_schedule)
                hideActionBarShadow()
                fragment = ScheduleViewPager()
            }
            MENU_ITEM_CALENDAR_POSITION -> {
                setTitle(R.string.drawer_calendar)
                hideActionBarShadow()
                fragment = CalendarViewPager()
            }
            MENU_ITEM_CLASSES_POSITION -> {
                setTitle(R.string.drawer_classes)
                setActionBarShadow()
                fragment = ClassesFragment()
            }
            MENU_ITEM_INFO_MAP_POSITION -> {
                setTitle(R.string.drawer_info_map)
                setActionBarShadow()
                fragment = InfoMapFragment()
            }
            MENU_ITEM_INFO_CONTACTS_POSITION -> {
                setTitle(R.string.drawer_info_contacts)
                setActionBarShadow()
                fragment = InfoContactsFragment()
            }
        }
        if (isFinishing) return

        // Replace fragment and close drawer
        fm.beginTransaction()
            .replace(R.id.content_frame, fragment!!)
            .commitAllowingStateLoss()

        // If the device is bigger than 7', don't close the drawer
        if (!resources.getBoolean(R.bool.drawer_opened)) mDrawerLayout!!.closeDrawer(GravityCompat.START)
    }

    override fun onUpdateTaskFinished(result: Student?) {
        if (isFinishing) return

        // Refresh current view
        mDrawerList.performItemClick(
            mDrawerList.getChildAt(mDrawerList.checkedItemPosition),
            mDrawerList.checkedItemPosition,
            mDrawerList.adapter.getItemId(mDrawerList.checkedItemPosition)
        )
    }

    public override fun onDestroy() {
        super.onDestroy()
        cancelTasks(mUpdateTask)
    }

    open class DrawerItem(var mTitle: String?, var mIconRes: Int)
    class DrawerTitle(title: String?) : DrawerItem(title, 0)
    class DrawerDivider : DrawerItem(null, 0)
    companion object {
        private const val CURRENT_FRAGMENT_TITLE_TAG = "current_fragment_title"
        private const val CURRENT_FRAGMENT_POSITION_TAG = "current_fragment_position"
        private const val MENU_ITEM_SCHEDULE_POSITION = 2
        private const val MENU_ITEM_CALENDAR_POSITION = 3
        private const val MENU_ITEM_CLASSES_POSITION = 4
        private const val MENU_ITEM_INFO_MAP_POSITION = 7
        private const val MENU_ITEM_INFO_CONTACTS_POSITION = 8
    }
}