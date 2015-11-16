package com.android.everyday;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DB {
	static final String ROW_ID = "_id";
	static final String ROW_TITLE = "title";
	static final String ROW_INFO = "info";
	static final String ROW_DATE = "date";
	static final String ROW_DATETIME = "datetime";
	static final String ROW_TIME = "time";
	static final String ROW_REPEAT = "repeat";
	static final String ROW_REPEAT_TEXT = "repeat_text";
	static final String ROW_REPEAT_CNT = "repeat_cnt";
	static final String ROW_REPEAT_TYPE_ID = "repeat_type_id";
	static final String ROW_REPEAT_TYPE_TEXT = "repeat_type_text";
	static final String ROW_BEFORE = "before";
	static final String ROW_BEFORE_TEXT = "before_text";
	static final String ROW_BEFORE_CNT = "before_cnt";
	static final String ROW_BEFORE_TYPE_ID = "before_type_id";
	static final String ROW_BEFORE_TYPE_TEXT = "before_type_text";
	static final String ROW_ACTIVE = "active";
	static final String TAG = "LOG_TAG";

	static final String DATABASE_NAME = "MyDB";
	static final String TABLE_EVENTS = "events";
	static final int DATABASE_VERSION = 24;// Change when DB in changed

	static final String DATABASE_CREATE = "create table " + TABLE_EVENTS 
	+ " ("
	+ ROW_ID + " INTEGER PRIMARY KEY, " 
	+ ROW_TITLE + " TEXT, "
	+ ROW_INFO + " TEXT, " 
	+ ROW_DATE + " TEXT, " 
	+ ROW_TIME + " TEXT,"
	+ ROW_DATETIME + " TIMESTAMP,"
	+ ROW_REPEAT + " TEXT,"
	+ ROW_REPEAT_TEXT + " TEXT," 
	+ ROW_REPEAT_CNT + " TEXT,"
	+ ROW_REPEAT_TYPE_ID + " INTEGER," 
	+ ROW_REPEAT_TYPE_TEXT + " TEXT," 
	+ ROW_BEFORE_TEXT + " TEXT," 
	+ ROW_BEFORE + " TEXT," 
	+ ROW_BEFORE_CNT + " TEXT,"
	+ ROW_BEFORE_TYPE_ID + " INTEGER," 
	+ ROW_BEFORE_TYPE_TEXT + " TEXT," 
	+ ROW_ACTIVE + " TEXT" 
	+ ")";

	static final String DROP_TABLE = "drop table if exists " + TABLE_EVENTS;

	final Context context;

	DatabaseHelper DBHelper;
	SQLiteDatabase db;

	public DB(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			try {
				db.execSQL(DATABASE_CREATE);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL(DROP_TABLE);
			onCreate(db);
		}
	}

	// opens the database
	public DB open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// closes the database
	public void close() {
		DBHelper.close();
	}

	// insert a contact into the database
	public long insertItem(String title, String date, String time,long datetime, String info,
						   String repeat, String repeat_text,String repeat_cnt, int repeat_type_id,String repeat_type_text,
						   String before, String before_text,String before_cnt, int before_type_id,String before_type_text,
						   String active) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(ROW_TITLE, title);
		initialValues.put(ROW_DATE, date);
		initialValues.put(ROW_TIME, time);
		initialValues.put(ROW_DATETIME, datetime);
		initialValues.put(ROW_INFO, info);
		initialValues.put(ROW_REPEAT, repeat);
		initialValues.put(ROW_REPEAT_TEXT, repeat_text);
		initialValues.put(ROW_REPEAT_CNT, repeat_cnt);
		initialValues.put(ROW_REPEAT_TYPE_ID, repeat_type_id);
		initialValues.put(ROW_REPEAT_TYPE_TEXT, repeat_type_text);
		initialValues.put(ROW_BEFORE, before);
		initialValues.put(ROW_BEFORE_TEXT, before_text);
		initialValues.put(ROW_BEFORE_CNT, before_cnt);
		initialValues.put(ROW_BEFORE_TYPE_ID, before_type_id);
		initialValues.put(ROW_BEFORE_TYPE_TEXT, before_type_text);
		initialValues.put(ROW_ACTIVE, active);
		Log.i("LOG_TAG", "DB inserted: " 
				+ "\ntitle:"+ title 
				+ "\ntime:" + time 
				+ "\ndate:" + date 
				+ "\ndatetime:" + datetime 
				+ "\ninfo:" + info 
				+ "\nrepea:" + repeat
				+ "\nrepeat_text:" + repeat_text 
				+ "\nrepeat_cnt:" + repeat_cnt 
				+ "\nrepeat_type_id:" + repeat_type_id 
				+ "\nrepeat_type_tex:" + repeat_type_text
				+ "\nbefor:" + before
				+ "\nbefore_text:" + before_text 
				+ "\nbefore_cnt:" + before_cnt 
				+ "\nbefore_type_id:" + before_type_id 
				+ "\nbefore_type_tex:" + before_type_text
				+ "\nactive: " + active);
		return db.insert(TABLE_EVENTS, null, initialValues);
	}

	// deletes a particular contact
	public boolean deleteItem(long rowId) {
		return db.delete(TABLE_EVENTS, ROW_ID + "=" + rowId, null) > 0;
	}

	// deletes all contacts
	public int deleteItems() {
		return db.delete(TABLE_EVENTS, null, null);
	}

	// retrieves all the contacts
	
	
	public Cursor getAllItems() {
		return db.query(TABLE_EVENTS, new String[] { ROW_ID, ROW_TITLE,ROW_INFO, ROW_DATE, ROW_TIME, ROW_DATETIME,
			ROW_REPEAT,ROW_REPEAT_TEXT, ROW_REPEAT_CNT,ROW_REPEAT_TYPE_ID,ROW_REPEAT_TYPE_TEXT,
			ROW_BEFORE,ROW_BEFORE_TEXT, ROW_BEFORE_CNT,ROW_BEFORE_TYPE_ID,ROW_BEFORE_TYPE_TEXT,
							ROW_ACTIVE }, null, null, null, null, ROW_DATETIME+" asc");
	}

	// retrieves a particular contact
	public Cursor getItem(long rowId) throws SQLException {
		Cursor mCursor = db.query(true, TABLE_EVENTS, new String[] { ROW_ID,ROW_TITLE, ROW_DATE, ROW_TIME, ROW_DATETIME, ROW_INFO,
			ROW_REPEAT,ROW_REPEAT_TEXT, ROW_REPEAT_CNT, ROW_REPEAT_TYPE_ID,ROW_REPEAT_TYPE_TEXT,
			ROW_BEFORE,ROW_BEFORE_TEXT, ROW_BEFORE_CNT,ROW_BEFORE_TYPE_ID,ROW_BEFORE_TYPE_TEXT,
								  ROW_ACTIVE }, ROW_ID + "="
				+ rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// updates a contact
	public boolean updateItem(long rowId, String title, String date,String time,long datetime, String info,
		String repeat,String repeat_text,String repeat_cnt, int repeat_type_id,String repeat_type_text,
		String before, String before_text,String before_cnt, int before_type_id,String before_type_text,
		String active) {
		ContentValues args = new ContentValues();
		args.put(ROW_TITLE, title);
		args.put(ROW_DATE, date);
		args.put(ROW_TIME, time);
		args.put(ROW_DATETIME, datetime);
		args.put(ROW_INFO, info);
		args.put(ROW_REPEAT, repeat);
		args.put(ROW_REPEAT_TEXT, repeat_text);
		args.put(ROW_REPEAT_CNT, repeat_cnt);
		args.put(ROW_REPEAT_TYPE_ID, repeat_type_id);
		args.put(ROW_REPEAT_TYPE_TEXT, repeat_type_text);
		args.put(ROW_BEFORE, before);
		args.put(ROW_BEFORE_TEXT, before_text);
		args.put(ROW_BEFORE_CNT, before_cnt);
		args.put(ROW_BEFORE_TYPE_ID, before_type_id);
		args.put(ROW_BEFORE_TYPE_TEXT, before_type_text);
		args.put(ROW_ACTIVE, active);
		Log.i("LOG_TAG", "DB updated: " 
				+ "\ntitle:"+ title 
				+ "\ntime:" + time 
				+ "\ndate:" + date 
				+ "\ndatetime:" + datetime 
				+ "\ninfo:" + info 
				+ "\nrepea:" + repeat
				+ "\nrepeat_text:" + repeat_text 
				+ "\nrepeat_cnt:" + repeat_cnt 
				+ "\nrepeat_type_id:" + repeat_type_id 
				+ "\nrepeat_type_tex:" + repeat_type_text
				+ "\nbefor:" + before
				+ "\nbefore_text:" + before_text 
				+ "\nbefore_cnt:" + before_cnt 
				+ "\nbefore_type_id:" + before_type_id 
				+ "\nbefore_type_tex:" + before_type_text
				+ "\nactive: " + active);
		return db.update(TABLE_EVENTS, args, ROW_ID + "=" + rowId, null) > 0;
	}
}
