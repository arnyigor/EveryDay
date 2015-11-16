package com.android.everyday;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

@SuppressLint("SimpleDateFormat") public class BootReceiver extends BroadcastReceiver {

	//private String mTitle;
	private String mTime;
	private String mDate;
	//private String mInfo;
//	private String mActive;
	private String mRepeat;
	private int mRepeatCnt;
	private int mRepeatTypeId;
	private int mReceivedID;
	private long mRepeatTime;
	private String myTimestamp;

	private Calendar mCalendar;
	private Cursor cursor;
	private AlarmReceiver mAlarmReceiver;

	// Constant values in milliseconds
	private static final long milMinute = 60000L;
	private static final long milHour = 3600000L;
	private static final long milDay = 86400000L;
	private static final long milWeek = 604800000L;
	private static final long milMonth = 2592000000L;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			Log.i("LOG_TAG","BootReceiver start");
			DB db = new DB(context);
			mCalendar = Calendar.getInstance();
			mAlarmReceiver = new AlarmReceiver();
			db.open();
			cursor = db.getAllItems();

			if (cursor.moveToFirst()) {
				try {
					do {
						int idColId = cursor.getColumnIndex(DB.ROW_ID);
						//int idColTitle = cursor.getColumnIndex(DB.ROW_TITLE);
						int idColDate = cursor.getColumnIndex(DB.ROW_DATE);
						int idColTime = cursor.getColumnIndex(DB.ROW_TIME);
						//int idColInfo = cursor.getColumnIndex(DB.ROW_INFO);
						int idColRepeat = cursor.getColumnIndex(DB.ROW_REPEAT);
						int idColRepeatTypeId = cursor.getColumnIndex(DB.ROW_REPEAT_TYPE_ID);
						int idColRepeatCnt = cursor.getColumnIndex(DB.ROW_REPEAT_CNT);
						mReceivedID = Integer.parseInt(cursor.getString(idColId));
						//mTitle = cursor.getString(idColTitle);
						mDate = cursor.getString(idColDate);
						mTime = cursor.getString(idColTime);
						//mInfo = cursor.getString(idColInfo);
						mRepeat = cursor.getString(idColRepeat);
						mRepeatCnt = Integer.parseInt(cursor.getString(idColRepeatCnt));
						mRepeatTypeId = Integer.parseInt(cursor.getString(idColRepeatTypeId));
						myTimestamp = mDate + " " + mTime;
						SimpleDateFormat curFormater = new SimpleDateFormat("dd MMM yyyy HH:mm");
						Date dateObj = null;
						try {
							dateObj = curFormater.parse(myTimestamp);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						mCalendar.setTime(dateObj);
						
						switch (mRepeatTypeId) {
						case 0:
							mRepeatTime = mRepeatCnt * milMinute;
							break;
						case 1:
							mRepeatTime = mRepeatCnt * milHour;
							break;
						case 2:
							mRepeatTime = mRepeatCnt * milDay;
							break;
						case 3:
							mRepeatTime = mRepeatCnt * milWeek;
							break;
						case 4:
							mRepeatTime = mRepeatCnt * milMonth;
							break;
						}
						
						 // Create a new notification if (mActive.equals("true")) 
						{
							if (mRepeat.equals("true")) {
								mAlarmReceiver.setRepeatAlarm(context,mCalendar, mReceivedID, mRepeatTime);
							} else if (mRepeat.equals("false")) {
								mAlarmReceiver.setAlarm(context, mCalendar, mReceivedID);
							}
						}
						
					} while (cursor.moveToNext());
				} catch (Exception e) {
					Log.i("LOG_TAG",e.toString());
				}
			}//cursor.moveToFirst
			cursor.close();
			db.close();
		}// BOOT_COMPLETED
	}
	
	public Calendar CurrentCal() {
		Calendar curCal = Calendar.getInstance();
		return curCal;
	}
	
	
	
}