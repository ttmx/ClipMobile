/*
 * Modifications:
 * - Imported from https://github.com/UweTrottmann/SeriesGuide/blob/
 *   master/SeriesGuide/src/main/java/com/battlelancer/seriesguide/
 *   util/SelectionBuilder.java
 * - Minor changes
 */
package com.migueljteixeira.clipmobile.util

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import java.util.*

/**
 * Helper for building selection clauses for [SQLiteDatabase]. Each
 * appended clause is combined using `AND`. This class is *not*
 * thread safe.
 */
class SelectionBuilder {
    private var mTable: String? = null
    private val mProjectionMap: MutableMap<String, String> = HashMap()
    private val mSelection = StringBuilder()
    private val mSelectionArgs = ArrayList<String>()

    /**
     * Reset any internal state, allowing this builder to be recycled.
     */
    fun reset(): SelectionBuilder {
        mTable = null
        mSelection.setLength(0)
        mSelectionArgs.clear()
        return this
    }

    /**
     * Append the given selection clause to the internal state. Each clause is
     * surrounded with parenthesis and combined using `AND`.
     */
    fun where(selection: String?, selectionArgs: Array<String>?): SelectionBuilder {
        if (TextUtils.isEmpty(selection)) {
            require(selectionArgs == null || selectionArgs.isEmpty()) {
                "Valid selection required when including arguments="
            }

            // Shortcut when clause is empty
            return this
        }
        if (mSelection.isNotEmpty()) {
            mSelection.append(" AND ")
        }
        mSelection.append("(").append(selection).append(")")
        mSelectionArgs.addAll(selectionArgs!!)
//        Collections.addAll(mSelectionArgs, *selectionArgs)
        return this
    }

    fun table(table: String?): SelectionBuilder {
        mTable = table
        return this
    }

    private fun assertTable() {
        checkNotNull(mTable) { "Table not specified" }
    }

    fun mapToTable(column: String, table: String): SelectionBuilder {
        mProjectionMap[column] = "$table.$column"
        return this
    }

    fun map(fromColumn: String, toClause: String): SelectionBuilder {
        mProjectionMap[fromColumn] = "$toClause AS $fromColumn"
        return this
    }

    /**
     * Return selection string for current internal state.
     *
     * @see .getSelectionArgs
     */
    private val selection: String
        get() = mSelection.toString()

    /**
     * Return selection arguments for current internal state.
     *
     * @see .getSelection
     */
    private val selectionArgs: Array<String>
        get() = mSelectionArgs.toTypedArray()

    private fun mapColumns(columns: Array<String>) {
        for (i in columns.indices) {
            val target = mProjectionMap[columns[i]]
            if (target != null) {
                columns[i] = target
            }
        }
    }

    override fun toString(): String {
        return ("SelectionBuilder[table=" + mTable + ", selection=" + selection
                + ", selectionArgs=" + Arrays.toString(selectionArgs) + "]")
    }

    /**
     * Execute query using the current internal state as `WHERE` clause.
     */
    fun query(db: SQLiteDatabase, columns: Array<String>?, orderBy: String?): Cursor {
        return query(db, columns, null, null, orderBy, null)
    }

    /**
     * Execute query using the current internal state as `WHERE` clause.
     */
    fun query(
        db: SQLiteDatabase, columns: Array<String>?, groupBy: String?, having: String?,
        orderBy: String?, limit: String?
    ): Cursor {
        assertTable()
        columns?.let { mapColumns(it) }
        return db.query(
            mTable, columns, selection, selectionArgs, groupBy, having,
            orderBy, limit
        )
    }

    /**
     * Execute update using the current internal state as `WHERE` clause.
     */
    fun update(db: SQLiteDatabase, values: ContentValues?): Int {
        assertTable()
        return db.update(mTable, values, selection, selectionArgs)
    }

    /**
     * Execute delete using the current internal state as `WHERE` clause.
     */
    fun delete(db: SQLiteDatabase): Int {
        assertTable()
        return db.delete(mTable, selection, selectionArgs)
    }
}