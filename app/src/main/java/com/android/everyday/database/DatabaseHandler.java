package com.android.everyday.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.everyday.models.Comparator;

/**
 * Creates SQLite table for storing tasks to a database. DO NOT call this class directly
 * get an instance of TasksDataSource instead.
 * @author Dhimitraq Jorgji, Jonathan Hasenzahl
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    private static final int RC1_DATABASE = 1;

    // Database Name
    private static final String DATABASE_NAME = "EveryDay.db";

    // Table names
    public static final String TABLE_TASKS = "tasks";
    public static final String TABLE_COMPARATORS = "comparators";

    // Column names
    public static final String KEY_ID = "id";										 // INTEGER PRIMARY KEY
    public static final String KEY_NAME = "name"; 									 // TEXT
    public static final String KEY_COMPLETION = "completion"; 						 // INTEGER, indirectly boolean
    public static final String KEY_HAS_DUE_DATE = "hasDueDate"; 					 // INTEGER, indirectly boolean
    public static final String KEY_HAS_FINAL_DUE_DATE = "hasFinalDueDate"; 			 // INTEGER, indirectly boolean
    public static final String KEY_IS_REPEATING = "isRepeating"; 					 // INTEGER, indirectly boolean
    public static final String KEY_REPEAT_TYPE = "repeatType"; 						 // INTEGER
    public static final String KEY_REPEAT_INTERVAL = "repeatInterval"; 				 // INTEGER
    public static final String KEY_CREATION_DATE = "creationDate"; 					 // DATETIME
    public static final String KEY_MODIFICATION_DATE = "modificationDate"; 			 // DATETIME
    public static final String KEY_DUE_DATE = "dueDate"; 							 // DATETIME
    public static final String KEY_NOTES = "notes"; 								 // TEXT, can be null
    public static final String KEY_G_ID = "gID";									 // STRING
    public static final String KEY_ENABLED = "enabled";								 // INTEGER, indirectly boolean, used in comparators table
    public static final String KEY_ORDER = "list_order";							 // INTEGER, used in comparators table


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private void createTasksTable(SQLiteDatabase db) {
        String create_tasks_table = "CREATE TABLE " + TABLE_TASKS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_COMPLETION + " INTEGER,"
                + KEY_HAS_DUE_DATE + " INTEGER,"
                + KEY_HAS_FINAL_DUE_DATE + " INTEGER,"
                + KEY_IS_REPEATING + " INTEGER,"
                + KEY_REPEAT_TYPE + " INTEGER,"
                + KEY_REPEAT_INTERVAL + " INTEGER,"
                + KEY_CREATION_DATE + " DATETIME,"
                + KEY_MODIFICATION_DATE + " DATETIME,"
                + KEY_DUE_DATE + " DATETIME,"
                + KEY_G_ID + " TEXT,"
                + KEY_NOTES + " TEXT)";

        db.execSQL(create_tasks_table);
    }

    private void createComparatorsTable(SQLiteDatabase db) {
        String create_comparators_table = "CREATE TABLE " + TABLE_COMPARATORS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_ENABLED + " INTEGER,"
                + KEY_ORDER + " INTEGER)";

        db.execSQL(create_comparators_table);

        // Create all entries of comparators table
        ContentValues values = new ContentValues();
        values.put(KEY_ID, Comparator.NAME);
        values.put(KEY_NAME, "Task name");
        values.put(KEY_ENABLED, 0);
        values.put(KEY_ORDER, 0);
        db.insert(TABLE_COMPARATORS, null, values);
        values = new ContentValues();
        values.put(KEY_ID, Comparator.COMPLETION);
        values.put(KEY_NAME, "Completion status");
        values.put(KEY_ENABLED, 0);
        values.put(KEY_ORDER, 1);
        db.insert(TABLE_COMPARATORS, null, values);
        values = new ContentValues();
        values.put(KEY_ID, Comparator.PRIORITY);
        values.put(KEY_NAME, "Priority");
        values.put(KEY_ENABLED, 0);
        values.put(KEY_ORDER, 2);
        db.insert(TABLE_COMPARATORS, null, values);
        values = new ContentValues();
        values.put(KEY_ID, Comparator.CATEGORY);
        values.put(KEY_NAME, "Category");
        values.put(KEY_ENABLED, 0);
        values.put(KEY_ORDER, 3);
        db.insert(TABLE_COMPARATORS, null, values);
        values = new ContentValues();
        values.put(KEY_ID, Comparator.DATE_DUE);
        values.put(KEY_NAME, "Due date");
        values.put(KEY_ENABLED, 0);
        values.put(KEY_ORDER, 4);
        db.insert(TABLE_COMPARATORS, null, values);
        values = new ContentValues();
        values.put(KEY_ID, Comparator.DATE_CREATED);
        values.put(KEY_NAME, "Date created");
        values.put(KEY_ENABLED, 0);
        values.put(KEY_ORDER, 5);
        db.insert(TABLE_COMPARATORS, null, values);
        values = new ContentValues();
        values.put(KEY_ID, Comparator.DATE_MODIFIED);
        values.put(KEY_NAME, "Date modified");
        values.put(KEY_ENABLED, 0);
        values.put(KEY_ORDER, 6);
        db.insert(TABLE_COMPARATORS, null, values);
    }

    // Creating Table
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTasksTable(db);
        createComparatorsTable(db);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (db.getVersion()){
            case RC1_DATABASE:
//                db.execSQL("DROP TABLE " + TABLE_TASKS);
                createTasksTable(db);
//                db.execSQL("DROP TABLE " + TABLE_COMPARATORS);
                createComparatorsTable(db);
                break;
        }
    }
}
