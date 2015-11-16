	package com.android.everyday;//Package

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

//==============Activity start=========================
@SuppressLint("SimpleDateFormat")
public class MainActivity extends ListActivity {

	// =============Variables start================
	private static final int CM_DELETE_ID = 100;
	private static final int CM_EDIT_ID = 101;
	final static int DIALOG_INFO = 200;
	private AlarmReceiver mAlarmReceiver;
	long item_position;
	int items_count;
	final Context context = this;
	Dialog dialog;
	ListView lvData;
	TextView tvInfoTitle, tvInfo, tvInfoDate, tvInfoTime, textToday,
			textTotalEvents;;
	LinearLayout dlgInfoView;
	DB db;
	SimpleCursorAdapter scAdapter;
	Cursor cursor,cursorDelete,cursorTest;

	// =============Variables end================

	// ==============Forms variables start==============
	// ==============Forms variables end==============
	// ====================onCreate start=========================
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// ================Forms Ids start=========================
		textToday = (TextView) findViewById(R.id.textToday);
		textTotalEvents = (TextView) findViewById(R.id.textTotalEvents);
		// ================Forms Ids end=========================

		// ==================onCreateCode start=========================

		Timer t = new Timer();
		final SimpleDateFormat format = new SimpleDateFormat(
				"dd MMM yyyy '\n' HH:mm:ss");
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Date now = new Date();
						String Today = format.format(now);
						textToday.setText(getResources().getString(R.string.time)+":\n" + Today);
					}
				});
			}
		};

		t.scheduleAtFixedRate(task, 0, 1000);

		try {
			db = new DB(this);
			db.open();
			cursor = db.getAllItems();
			startManagingCursor(cursor);
			items_count = cursor.getCount();
			textTotalEvents.setText(getResources().getString(
					R.string.todayevents)
					+ String.valueOf(items_count));
			String[] from = new String[] { DB.ROW_TITLE, DB.ROW_DATE, DB.ROW_TIME,DB.ROW_REPEAT_TEXT,DB.ROW_BEFORE_TEXT};
			int[] to = new int[] { R.id.tvTitleItem, R.id.tvDateItem, R.id.tvTimeItem, R.id.tvRepeatViewItem,R.id.tvBeforeItem};

			scAdapter = new SimpleCursorAdapter(this, R.layout.activity_item,
					cursor, from, to);
			ListView lvData = (ListView) findViewById(android.R.id.list);
			lvData.setAdapter(scAdapter);

			registerForContextMenu(lvData);
		} catch (Exception e) {
			Log.i("LOG_TAG", e.toString());
		}

		// ==================onCreateCode end=========================
		 // Initialize alarm
        mAlarmReceiver = new AlarmReceiver();
	}// ============onCreate end====================
	// ====================CustomCode  start=====================================
	
	
	// On clicking item
	@SuppressLint("InflateParams")
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		item_position = l.getAdapter().getItemId(position);
		Intent intent = new Intent(this, InfoActivity.class);
		Log.i("LOG_TAG", "Intent");
		Log.i("LOG_TAG", String.valueOf(item_position));
		intent.putExtra("intentPosition", String.valueOf(item_position));
		Log.i("LOG_TAG", "Start");
		startActivity(intent);
	}

	// Create menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	 // Setup menu
	@SuppressLint("InflateParams")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_AddEvent:
			Intent intent = new Intent(this, AddEditActivity.class);
			startActivity(intent);
			break;
		case R.id.action_DeleteEvents:
			AlertDialog.Builder quitDialog = new AlertDialog.Builder(
					MainActivity.this);
			quitDialog.setTitle(getString(R.string.deleteevents) + "?");
			quitDialog
					.setNegativeButton(getString(R.string.dialogcancel), null);
			quitDialog.setPositiveButton(getString(R.string.dialogok),
					new DialogInterface.OnClickListener() {
						@SuppressWarnings("deprecation")
						@Override
						public void onClick(DialogInterface dialog, int id) {
							cursorDelete = db.getAllItems();
							if (cursorDelete.moveToFirst()) {
								try {
									do {
										int idColId = cursorDelete.getColumnIndex(DB.ROW_ID);
										int idColBefore = cursorDelete.getColumnIndex(DB.ROW_BEFORE);
										int mReceivedID = Integer.parseInt(cursorDelete.getString(idColId));
										mAlarmReceiver.cancelAlarm(getApplicationContext(),mReceivedID);
										if (cursorDelete.getString(idColBefore).equals("true")) {
											mAlarmReceiver.cancelBeforeAlarm(getApplicationContext(),mReceivedID);
										}
									} while (cursorDelete.moveToNext());
								} catch (Exception e) {
									Log.i("LOG_TAG",e.toString());
								}
							}//cursorDelete.moveToFirst
							cursorDelete.close();
							db.deleteItems();
							cursor.requery();// обновляем курсор
							items_count = cursor.getCount();
							textTotalEvents.setText(getResources().getString(
									R.string.totalevents)
									+ String.valueOf(items_count));
						}
					});
			AlertDialog alert = quitDialog.create();
			alert.show();
			break;
		case R.id.action_settings:
			Intent settingsActivity = new Intent(getBaseContext(),Preferences.class);
			startActivity(settingsActivity);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CM_EDIT_ID, 0, R.string.edititem);
		menu.add(0, CM_DELETE_ID, 0, R.string.deleteevent);
	}

	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int id = item.getItemId();
		switch (id) {
		case CM_DELETE_ID:
			AlertDialog.Builder quitDialog = new AlertDialog.Builder(
					MainActivity.this);
			quitDialog.setTitle(getString(R.string.deleteevent) + "?");
			quitDialog
					.setNegativeButton(getString(R.string.dialogcancel), null);
			quitDialog.setPositiveButton(getString(R.string.dialogok),
					new DialogInterface.OnClickListener() {
						@SuppressWarnings("deprecation")
						@Override
						public void onClick(DialogInterface dialog, int id) {
							
							try {
								Cursor cursordel = db.getItem(acmi.id);
								Log.i("LOG_TAG", "intPosition" + acmi.id);
								if (cursordel.moveToFirst()) {
									try {
										int idColId = cursorDelete
												.getColumnIndex(DB.ROW_ID);
										int idColBefore = cursorDelete
												.getColumnIndex(DB.ROW_BEFORE);
										int mReceivedID = Integer
												.parseInt(cursorDelete
														.getString(idColId));
										mAlarmReceiver.cancelAlarm(
												getApplicationContext(),
												mReceivedID);
										if (cursorDelete.getString(idColBefore)
												.equals("true")) {
											mAlarmReceiver.cancelBeforeAlarm(
													getApplicationContext(),
													mReceivedID);
										}
									} catch (Exception e) {
										Log.i("LOG_TAG", e.toString());
									}
								}
								cursordel.close();
							} catch (SQLException e) {
								Log.i("LOG_TAG", e.toString());
							}
							
							db.deleteItem(acmi.id);// извлекаем id записи и
							cursor.requery();// обновляем курсор
							items_count = cursor.getCount();
							textTotalEvents.setText(getResources().getString(
									R.string.totalevents)
									+ String.valueOf(items_count));
						}
					});
			AlertDialog alert = quitDialog.create();
			alert.show();
			break;
		case CM_EDIT_ID:
			try {
				final String mRowId = String.valueOf(acmi.id);
				Intent intent = new Intent(this, AddEditActivity.class);
				intent.putExtra(DB.ROW_ID, mRowId);
				startActivity(intent);
			} catch (Exception e) {
				Log.i("LOG_TAG error EDIT", e.toString());
			}
			break;
		}

		return super.onContextItemSelected(item);
	}
	
	

	protected void onResume() {
		super.onResume();
		Log.i("LOG_TAG","----------onResume-------------");
		//MyApplication.activityResumed();
		items_count = cursor.getCount();
		textTotalEvents.setText(getResources().getString(R.string.totalevents)
				+ String.valueOf(items_count));
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i("LOG_TAG","----------onPause-------------");
		//MyApplication.activityPaused();
	}

	protected void onDestroy() {
		super.onDestroy();
		Log.i("LOG_TAG","----------onDestroy-------------");
		// закрываем подключение при выходе
		db.close();
	}

	// ====================CustomCode end======================================

	// ====================OnClicks======================================
	// ======================Exit dialog==============================
	// ======================Exit dialog end==============================
	// ====================OnClicks end======================================
}// ===================Activity end==================================
// ===================SimpleActivity==================================
