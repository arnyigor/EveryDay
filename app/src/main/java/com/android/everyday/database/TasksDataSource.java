package com.android.everyday.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.android.everyday.models.Comparator;
import com.android.everyday.models.Task;

import java.util.ArrayList;

public class TasksDataSource {

    public static final int CURSOR_ID = 0;
    public static final int CURSOR_NAME = 1;
    public static final int CURSOR_COMPLETION = 2;
    public static final int CURSOR_HAS_DUE_DATE = 3;
    public static final int CURSOR_HAS_FINAL_DUE_DATE = 4;
    public static final int CURSOR_IS_REPEATING = 5;
    public static final int CURSOR_REPEAT_TYPE = 6;
    public static final int CURSOR_REPEAT_INTERVAL = 7;
    public static final int CURSOR_CREATION_DATE = 8;
    public static final int CURSOR_MODIFICATION_DATE = 9;
    public static final int CURSOR_DUE_DATE = 10;
    public static final int CURSOR_G_ID = 11;
    public static final int CURSOR_NOTES = 12;
    private SQLiteDatabase db;
    private DatabaseHandler handler;
    private static TasksDataSource instance;

    private TasksDataSource() {

    }

    private TasksDataSource(Context context) {
        handler = new DatabaseHandler(context);
    }

    /**
     * Call this to get access to the instance of TasksDataSource Singleton
     *
     * @param context
     * @return instance of TasksDataSource
     */
    public static synchronized TasksDataSource getInstance(Context context) {
        instance = new TasksDataSource(context);
        return instance;
    }

    private void open() throws SQLException {
        db = handler.getWritableDatabase();
    }

    private void close() {
        handler.close();
    }

    public Task getTask(int id) {
        open();
        Cursor cursor = db.query(DatabaseHandler.TABLE_TASKS, new String[]{
                        DatabaseHandler.KEY_ID,
                        DatabaseHandler.KEY_NAME,
                        DatabaseHandler.KEY_COMPLETION,
                        DatabaseHandler.KEY_HAS_DUE_DATE,
                        DatabaseHandler.KEY_HAS_FINAL_DUE_DATE,
                        DatabaseHandler.KEY_IS_REPEATING,
                        DatabaseHandler.KEY_REPEAT_TYPE,
                        DatabaseHandler.KEY_REPEAT_INTERVAL,
                        DatabaseHandler.KEY_CREATION_DATE,
                        DatabaseHandler.KEY_MODIFICATION_DATE,
                        DatabaseHandler.KEY_DUE_DATE,
                        DatabaseHandler.KEY_G_ID,
                        DatabaseHandler.KEY_NOTES},
                DatabaseHandler.KEY_ID + " = " + id,
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            Task task = initTask(cursor);
            close();
            cursor.close();
            return task;
        } else {
            close();
            cursor.close();
            return null;
        }
    }

    public ArrayList<Task> getAllTasks() {
        String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_TASKS;
        return getDbTasks(selectQuery);
    }

    public ArrayList<Task> getTasks(boolean all_tasks) {

        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * FROM " + DatabaseHandler.TABLE_TASKS);
        if (!all_tasks){
            builder.append(" WHERE " + DatabaseHandler.KEY_COMPLETION + " = 0");
        }
        return getDbTasks(builder.toString());
    }

    private ArrayList<Task> getDbTasks(String selectQuery) {
        ArrayList<Task> taskList = new ArrayList<>();
        open();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Task task = initTask(cursor);
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return taskList;
    }

    @NonNull
    private Task initTask(Cursor cursor) {
        return new Task(
                        cursor.getInt(CURSOR_ID),
                        cursor.getString(CURSOR_NAME),
                        cursor.getInt(CURSOR_COMPLETION) > 0,
                        cursor.getInt(CURSOR_HAS_DUE_DATE) > 0,
                        cursor.getInt(CURSOR_HAS_FINAL_DUE_DATE) > 0,
                        cursor.getInt(CURSOR_IS_REPEATING) > 0,
                        cursor.getInt(CURSOR_REPEAT_TYPE),
                        cursor.getInt(CURSOR_REPEAT_INTERVAL),
                        cursor.getLong(CURSOR_CREATION_DATE),
                        cursor.getLong(CURSOR_MODIFICATION_DATE),
                        cursor.getLong(CURSOR_DUE_DATE),
                        cursor.getString(CURSOR_G_ID),
                        cursor.getString(CURSOR_NOTES));
    }

    public int getNextID(String table) {

        String selectQuery = "SELECT MAX(" + DatabaseHandler.KEY_ID +
                ") FROM " + table;
        open();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            int i = cursor.getInt(0) + 1;
            cursor.close();
            close();
            return i;
        } else {
            cursor.close();
            close();
            return 1;
        }
    }

    /**
     * Insert a task to the tasks table
     *
     * @param task
     */
    public void addTask(Task task) {
        open();
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.KEY_ID, task.getID());
        values.put(DatabaseHandler.KEY_NAME, task.getName());
        values.put(DatabaseHandler.KEY_COMPLETION, task.isCompleted());
        values.put(DatabaseHandler.KEY_HAS_DUE_DATE, task.hasDateDue());
        values.put(DatabaseHandler.KEY_HAS_FINAL_DUE_DATE, task.hasFinalDateDue());
        values.put(DatabaseHandler.KEY_IS_REPEATING, task.isRepeating());
        values.put(DatabaseHandler.KEY_REPEAT_TYPE, task.getRepeatType());
        values.put(DatabaseHandler.KEY_REPEAT_INTERVAL, task.getRepeatInterval());
        values.put(DatabaseHandler.KEY_CREATION_DATE, task.getDateCreated());
        values.put(DatabaseHandler.KEY_MODIFICATION_DATE, task.getDateModified());
        values.put(DatabaseHandler.KEY_DUE_DATE, task.getDateDue());
        values.put(DatabaseHandler.KEY_G_ID, task.getgID());
        values.put(DatabaseHandler.KEY_NOTES, task.getNotes());

        // Inserting Row
        db.insert(DatabaseHandler.TABLE_TASKS, null, values);
        close();
    }

    /**
     * Update the database information about a task
     *
     * @param task
     * @return number of rows affected
     */
    public int updateTask(Task task) {
        open();
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.KEY_NAME, task.getName());
        values.put(DatabaseHandler.KEY_COMPLETION, task.isCompleted());
        values.put(DatabaseHandler.KEY_HAS_DUE_DATE, task.hasDateDue());
        values.put(DatabaseHandler.KEY_HAS_FINAL_DUE_DATE, task.hasFinalDateDue());
        values.put(DatabaseHandler.KEY_IS_REPEATING, task.isRepeating());
        values.put(DatabaseHandler.KEY_REPEAT_TYPE, task.getRepeatType());
        values.put(DatabaseHandler.KEY_REPEAT_INTERVAL, task.getRepeatInterval());
        values.put(DatabaseHandler.KEY_CREATION_DATE, task.getDateCreated());
        values.put(DatabaseHandler.KEY_MODIFICATION_DATE, task.getDateModified());
        values.put(DatabaseHandler.KEY_DUE_DATE, task.getDateDue());
        values.put(DatabaseHandler.KEY_G_ID, task.getgID());
        values.put(DatabaseHandler.KEY_NOTES, task.getNotes());

        // updating row
        int i = db.update(DatabaseHandler.TABLE_TASKS, values,
                DatabaseHandler.KEY_ID + " = ?", new String[]{String.valueOf(task.getID())});
        close();
        return i;
    }

    /**
     * Deletes a single task from the database
     *
     * @param task
     */
    public void deleteTask(Task task) {
        open();
        db.delete(DatabaseHandler.TABLE_TASKS,
                DatabaseHandler.KEY_ID + " = " + task.getID(), null);
        close();
    }

    /**
     * Deletes all finished tasks from the database. Repeating tasks will not
     * be deleted.
     *
     * @return the number of tasks deleted
     */
    public int deleteFinishedTasks() {
        open();
        int i = db.delete(DatabaseHandler.TABLE_TASKS,
                DatabaseHandler.KEY_COMPLETION + " = 1 AND " +
                        DatabaseHandler.KEY_IS_REPEATING + " = 0", null);
        close();
        return i;
    }

    /**
     * Deletes all tasks from the database.
     *
     * @return the number of tasks deleted
     */
    public int deleteAllTasks() {
        open();
        int i = db.delete(DatabaseHandler.TABLE_TASKS, null, null);
        close();
        return i;
    }

    /************************************************************
     * Comparators   											*
     ************************************************************/

    public Comparator getComparator(int id) {
        open();
        Cursor cursor = db.query(DatabaseHandler.TABLE_COMPARATORS, new String[]{DatabaseHandler.KEY_ID, DatabaseHandler.KEY_NAME, DatabaseHandler.KEY_ENABLED, DatabaseHandler.KEY_ORDER}, DatabaseHandler.KEY_ID + " = " + id, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            Comparator c = new Comparator(cursor.getInt(0), cursor.getString(1), cursor.getInt(2) > 0, cursor.getInt(3));
            cursor.close();
            close();
            return c;
        }
        return null;
    }

    /**
     * Creates an ArrayList of Comparators in the order specified by the comparators
     *
     * @return an ArrayList of Comparators
     */
    public ArrayList<Comparator> getComparators() {
        Comparator[] comparators = new Comparator[Comparator.NUM_COMPARATORS];

        // Select All Query
        String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_COMPARATORS;

        open();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                Comparator c = new Comparator(cursor.getInt(0), cursor.getString(1), cursor.getInt(2) > 0, cursor.getInt(3));
                // Add comparactor to array
                comparators[c.getOrder()] = c;
            } while (cursor.moveToNext());
        }

        cursor.close();
        close();

        // Copy array into ArrayList
        ArrayList<Comparator> comparator_list = new ArrayList<Comparator>(Comparator.NUM_COMPARATORS);
        for (int i = 0; i < Comparator.NUM_COMPARATORS; i++) {
            comparator_list.add(comparators[i]);
        }
        return comparator_list;
    }

    public int updateComparator(Comparator c) {
        open();
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.KEY_NAME, c.getName());
        values.put(DatabaseHandler.KEY_ENABLED, c.isEnabled());
        values.put(DatabaseHandler.KEY_ORDER, c.getOrder());
        // Update row
        int i = db.update(DatabaseHandler.TABLE_COMPARATORS, values, DatabaseHandler.KEY_ID + " = " + c.getId(), null);
        close();
        return i;
    }
}