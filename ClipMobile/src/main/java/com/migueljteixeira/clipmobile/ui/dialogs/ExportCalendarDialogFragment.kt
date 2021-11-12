package com.migueljteixeira.clipmobile.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import com.migueljteixeira.clipmobile.util.StudentTools.exportCalendar
import android.os.Bundle
import com.migueljteixeira.clipmobile.ui.dialogs.ExportCalendarDialogFragment
import android.content.DialogInterface
import androidx.fragment.app.DialogFragment
import com.migueljteixeira.clipmobile.util.StudentTools

class ExportCalendarDialogFragment : DialogFragment() {
    private lateinit var calendarIds: LongArray
    private lateinit var calendarNames: Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendarIds = requireArguments().getLongArray(CALENDAR_ID)!!
        calendarNames = requireArguments().getStringArray(CALENDAR_NAME)!!
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
            .setTitle("Exportar para")
            .setItems(calendarNames) { dialog: DialogInterface?, which: Int ->
                exportCalendar(
                    requireActivity(),
                    calendarIds[which]
                )
            }
            .create()
    }

    companion object {
        const val CALENDAR_ID = "calendar_id"
        const val CALENDAR_NAME = "calendar_name"
    }
}