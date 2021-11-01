package com.migueljteixeira.clipmobile.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.migueljteixeira.clipmobile.R;
import com.migueljteixeira.clipmobile.adapters.DrawerAdapter;
import com.migueljteixeira.clipmobile.entities.Student;
import com.migueljteixeira.clipmobile.settings.ClipSettings;
import com.migueljteixeira.clipmobile.ui.dialogs.AboutDialogFragment;
import com.migueljteixeira.clipmobile.ui.dialogs.ExportCalendarDialogFragment;
import com.migueljteixeira.clipmobile.util.StudentTools;
import com.migueljteixeira.clipmobile.util.tasks.UpdateStudentPageTask;

import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

//import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NavDrawerActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        UpdateStudentPageTask.OnUpdateTaskFinishedListener<Student> {

    private static final String CURRENT_FRAGMENT_TITLE_TAG = "current_fragment_title";
    private static final String CURRENT_FRAGMENT_POSITION_TAG = "current_fragment_position";

    private static final int MENU_ITEM_SCHEDULE_POSITION = 2;
    private static final int MENU_ITEM_CALENDAR_POSITION = 3;
    private static final int MENU_ITEM_CLASSES_POSITION = 4;
    private static final int MENU_ITEM_INFO_MAP_POSITION = 7;
    private static final int MENU_ITEM_INFO_CONTACTS_POSITION = 8;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private UpdateStudentPageTask mUpdateTask;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dualpane);

        setupActionBar();

//        Crashlytics.log("NavDrawerActivity - onCreate");

        setupNavDrawer();

        // Set toolbar title
        if (savedInstanceState == null) {
            setTitle(R.string.drawer_schedule);
            hideActionBarShadow();
        } else {
            String title = savedInstanceState.getString(CURRENT_FRAGMENT_TITLE_TAG);
            if (title != null) {
                setTitle(title);

                int position = savedInstanceState.getInt(CURRENT_FRAGMENT_POSITION_TAG);
                if (position == MENU_ITEM_SCHEDULE_POSITION ||
                        position == MENU_ITEM_CALENDAR_POSITION)
                    hideActionBarShadow();
            }
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.content_frame);
        if (fragment == null) {
            fragment = new ScheduleViewPager();
            fm.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(CURRENT_FRAGMENT_TITLE_TAG, getTitle().toString());
        outState.putInt(CURRENT_FRAGMENT_POSITION_TAG, mDrawerList.getCheckedItemPosition());
    }

    public void setupNavDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Setup menu adapter
        DrawerAdapter drawerAdapter = new DrawerAdapter(this);
        drawerAdapter.add(new DrawerTitle(ClipSettings.getYearSelected(this)));
        drawerAdapter.add(new DrawerDivider());
        drawerAdapter.add(new DrawerItem(getString(R.string.drawer_schedule), R.drawable.ic_books));
        drawerAdapter.add(new DrawerItem(getString(R.string.drawer_calendar), R.drawable.ic_calendar));
        drawerAdapter.add(new DrawerItem(getString(R.string.drawer_classes), R.drawable.ic_folders));
        drawerAdapter.add(new DrawerTitle(getString(R.string.drawer_info_title)));
        drawerAdapter.add(new DrawerDivider());
        drawerAdapter.add(new DrawerItem(getString(R.string.drawer_info_map), R.drawable.ic_map));
        drawerAdapter.add(new DrawerItem(getString(R.string.drawer_info_contacts), R.drawable.ic_phone));

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(drawerAdapter);
        mDrawerList.setItemChecked(MENU_ITEM_SCHEDULE_POSITION, true);
        mDrawerList.setOnItemClickListener(this);

        // If the device is smaller than 7',
        // hide the drawer and set the icon
        if (!getResources().getBoolean(R.bool.drawer_opened)) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            mToolbar.setNavigationIcon(R.drawable.ic_drawer);
            mToolbar.setNavigationContentDescription(R.string.drawer_open);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if (mDrawerList.getCheckedItemPosition() == MENU_ITEM_CALENDAR_POSITION)
            inflater.inflate(R.menu.menu_student_page_calendar, menu);
        else
            inflater.inflate(R.menu.menu_student_page, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int semester = ClipSettings.getSemesterSelected(this);

        if (semester == 1)
            menu.findItem(R.id.semester1).setChecked(true);
        else if (semester == 2)
            menu.findItem(R.id.semester2).setChecked(true);
        else
            menu.findItem(R.id.trimester2).setChecked(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Check if we should toggle the navigation drawer
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START))
                mDrawerLayout.closeDrawer(GravityCompat.START);
            else
                mDrawerLayout.openDrawer(GravityCompat.START);
        } else if (item.getItemId() == R.id.refresh) {
//            Crashlytics.log("NavDrawerActivity - refresh");

            // Refreshing
            Toast.makeText(this, getString(R.string.refreshing),
                    Toast.LENGTH_LONG).show();

            // Start AsyncTask
            mUpdateTask = new UpdateStudentPageTask(this, NavDrawerActivity.this);
            mUpdateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (item.getItemId() == R.id.export_calendar) {
            Map<Long, String> calendarsNames = StudentTools.confirmExportCalendar(this);

            long[] ids = new long[calendarsNames.size()];
            String[] names = new String[calendarsNames.size()];

            int count = 0;
            for (Map.Entry<Long, String> calendarItem : calendarsNames.entrySet()) {
                ids[count] = calendarItem.getKey();
                names[count] = calendarItem.getValue();

                count++;
            }

            Bundle bundle = new Bundle();
            bundle.putLongArray(ExportCalendarDialogFragment.CALENDAR_ID, ids);
            bundle.putStringArray(ExportCalendarDialogFragment.CALENDAR_NAME, names);

            // Create an instance of the dialog fragment and show it
            DialogFragment dialog = new ExportCalendarDialogFragment();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "ExportCalendarDialogFragment");
        } else if (item.getItemId() == R.id.logout) {
            // Clear user personal data
            ClipSettings.logoutUser(this);

            Intent intent = new Intent(this, ConnectClipActivity.class);
            startActivity(intent);

            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            finish();
        } else if (item.getItemId() == R.id.about) {
            // Create an instance of the dialog fragment and show it
            DialogFragment dialog = new AboutDialogFragment();
            dialog.show(getSupportFragmentManager(), "AboutDialogFragment");
        } else if (!item.isChecked() && (item.getItemId() == R.id.semester1 || item.getItemId() == R.id.semester2)
                || item.getItemId() == R.id.trimester2) {
            // Check item
            item.setChecked(true);

            if (item.getItemId() == R.id.semester1)
                ClipSettings.saveSemesterSelected(this, 1);
            else if (item.getItemId() == R.id.semester2)
                ClipSettings.saveSemesterSelected(this, 2);
            else
                ClipSettings.saveSemesterSelected(this, 3);

            // Refresh current view
            mDrawerList.performItemClick(mDrawerList.getChildAt(mDrawerList.getCheckedItemPosition()),
                    mDrawerList.getCheckedItemPosition(),
                    mDrawerList.getAdapter().getItemId(mDrawerList.getCheckedItemPosition()));
        } else
            return super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // If the device is bigger than 7', keep the drawer opened
        if (getResources().getBoolean(R.bool.drawer_opened)) {
            //mDrawerLayout.openDrawer(GravityCompat.START);
            //mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        } else {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }

    }

    @Override
    public void onBackPressed() {
        if (getResources().getBoolean(R.bool.drawer_opened) ||
                !mDrawerLayout.isDrawerOpen(GravityCompat.START)) {

            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentByTag(ClassesDocsFragment.FRAGMENT_TAG);
            if (fragment != null) {
                // Refresh current view
                mDrawerList.performItemClick(mDrawerList.getChildAt(mDrawerList.getCheckedItemPosition()),
                        mDrawerList.getCheckedItemPosition(),
                        mDrawerList.getAdapter().getItemId(mDrawerList.getCheckedItemPosition()));

                return;
            }

            Intent intent = new Intent(this, StudentNumbersActivity.class);
            startActivity(intent);

            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
            finish();
        } else
            mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.content_frame);

        invalidateOptionsMenu();

        switch (position) {
            case MENU_ITEM_SCHEDULE_POSITION:
                setTitle(R.string.drawer_schedule);
                hideActionBarShadow();
                fragment = new ScheduleViewPager();
                break;

            case MENU_ITEM_CALENDAR_POSITION:
                setTitle(R.string.drawer_calendar);
                hideActionBarShadow();
                fragment = new CalendarViewPager();
                break;

            case MENU_ITEM_CLASSES_POSITION:
                setTitle(R.string.drawer_classes);
                setActionBarShadow();
                fragment = new ClassesFragment();
                break;

            case MENU_ITEM_INFO_MAP_POSITION:
                setTitle(R.string.drawer_info_map);
                setActionBarShadow();
                fragment = new InfoMapFragment();
                break;

            case MENU_ITEM_INFO_CONTACTS_POSITION:
                setTitle(R.string.drawer_info_contacts);
                setActionBarShadow();
                fragment = new InfoContactsFragment();
                break;
        }

        if (isFinishing())
            return;

        // Replace fragment and close drawer
        fm.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commitAllowingStateLoss();

        // If the device is bigger than 7', don't close the drawer
        if (!getResources().getBoolean(R.bool.drawer_opened))
            mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onUpdateTaskFinished(Student result) {
        if (isFinishing())
            return;

        // Refresh current view
        mDrawerList.performItemClick(mDrawerList.getChildAt(mDrawerList.getCheckedItemPosition()),
                mDrawerList.getCheckedItemPosition(),
                mDrawerList.getAdapter().getItemId(mDrawerList.getCheckedItemPosition()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelTasks(mUpdateTask);
    }

    public class DrawerItem {

        public String mTitle;
        public int mIconRes;

        public DrawerItem(String title, int iconRes) {
            mTitle = title;
            mIconRes = iconRes;
        }
    }

    public class DrawerTitle extends DrawerItem {

        public DrawerTitle(String title) {
            super(title, 0);
        }
    }

    public class DrawerDivider extends DrawerItem {

        public DrawerDivider() {
            super(null, 0);
        }
    }

}