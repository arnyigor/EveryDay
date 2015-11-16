package com.android.everyday;


import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;


public class AlarmReceiver extends WakefulBroadcastReceiver {
    AlarmManager mAlarmManager;
    PendingIntent mPendingIntent;
    Cursor cursor;
	Context contextPref,context;
    String title,info,date,time,ringtonePreference;
	public void getPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(contextPref);
        ringtonePreference = prefs.getString("ringtonePref","DEFAULT_RINGTONE_URI");
    }
    @Override
    public void onReceive(Context context, Intent intent) {
    	contextPref=context;
    	getPrefs();
		Log.i("LOG_TAG","onReceive start");
		int ROW_ID = Integer.parseInt(intent.getStringExtra(DB.ROW_ID));
        int mReceivedID = Integer.parseInt(intent.getStringExtra(DB.ROW_ID));
        Log.i("LOG_TAG","ROW_ID: "+ROW_ID);
        Log.i("LOG_TAG","mReceivedID: "+mReceivedID);
        // Get notification title from Reminder Database
        DB db = new DB(context);
        db.open();
        try {
        	cursor = db.getItem(ROW_ID);
		} catch (Exception e) {
			Log.i("LOG_TAG",e.toString());
		}
		if (cursor.moveToFirst()) {
			try {
				int idColTitle = cursor.getColumnIndex(DB.ROW_TITLE);
				int idColInfo = cursor.getColumnIndex(DB.ROW_INFO);
				int idColDate = cursor.getColumnIndex(DB.ROW_DATE);
				int idColTime = cursor.getColumnIndex(DB.ROW_TIME);
				title = cursor.getString(idColTitle);
				info = cursor.getString(idColInfo);
				date = cursor.getString(idColDate);
				time = cursor.getString(idColTime);
			} catch (Exception e) {
				Log.i("LOG_TAG", e.toString());
			}
		}// cursor
		cursor.close();
		db.close();
		Log.i("LOG_TAG","title: "+title);
		Log.i("LOG_TAG","info: "+info);
		Log.i("LOG_TAG","date: "+date);
		Log.i("LOG_TAG","time: "+time);

        // Create intent to open AddActivity on notification click
        Intent editIntent = new Intent(context, AddEditActivity.class);
        editIntent.putExtra(DB.ROW_ID, Integer.toString(ROW_ID));
        PendingIntent mClick = PendingIntent.getActivity(context, mReceivedID, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create Notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setTicker(context.getResources().getString(R.string.app_name))
				.setContentText(title)
			    .setSound(Uri.parse(ringtonePreference))
                .setContentIntent(mClick)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setOnlyAlertOnce(true);

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(mReceivedID, mBuilder.build());
    }

    public void setAlarm(Context context,Calendar alarmCal, int ID) {
    	Log.i("LOG_TAG","setAlarm");
    	Log.i("LOG_TAG","ID: "+ID);
        // Calculate notification time
    	Calendar currentCal = Calendar.getInstance();
        long currentTime = currentCal.getTimeInMillis();
        Log.i("LOG_TAG","currentCal: "+String.valueOf(currentCal.getTime()));
        Log.i("LOG_TAG","alarmCal: "+String.valueOf(alarmCal.getTime()));
        long diffTime = alarmCal.getTimeInMillis() - currentTime;
        Log.i("LOG_TAG","diffTime: "+String.valueOf(diffTime));
       
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
     // Put Reminder ID in Intent Extra
       Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(DB.ROW_ID, Integer.toString(ID));
        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Log.i("LOG_TAG","mPendingIntent: "+String.valueOf(mPendingIntent));
        // Start alarm using notification time
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME,SystemClock.elapsedRealtime() + diffTime,mPendingIntent);
        Log.i("LOG_TAG","Start alarm using notification time");
        
        // Restart alarm if device is rebooted
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
    
    public void setBeforeAlarm(Context context,Calendar alarmCal, int ID) {
    	Log.i("LOG_TAG","setBeforeAlarm");
    	Log.i("LOG_TAG","ID: "+ID);
        // Calculate notification time
    	Calendar currentCal = Calendar.getInstance();
        long currentTime = currentCal.getTimeInMillis();
        Log.i("LOG_TAG","currentCal: "+String.valueOf(currentCal.getTime()));
        Log.i("LOG_TAG","alarmCal: "+String.valueOf(alarmCal.getTime()));
        long diffTime = alarmCal.getTimeInMillis() - currentTime;
        Log.i("LOG_TAG","diffTime: "+String.valueOf(diffTime));
       
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
     // Put Reminder ID in Intent Extra
       Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(DB.ROW_ID, Integer.toString(ID));
        //ID+1!!!!!!!!!!!!Kostil!!!!!!!!!!!!
        mPendingIntent = PendingIntent.getBroadcast(context, ID+1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Log.i("LOG_TAG","mPendingIntent: "+String.valueOf(mPendingIntent));
        // Start alarm using notification time
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME,SystemClock.elapsedRealtime() + diffTime,mPendingIntent);
        Log.i("LOG_TAG","Start alarm using notification time");
        
        // Restart alarm if device is rebooted
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void setRepeatAlarm(Context context, Calendar calendar, int ID, long RepeatTime) {
    	Log.i("LOG_TAG","setRepeatAlarm");
    	Log.i("LOG_TAG","ID: "+ID);
    	Log.i("LOG_TAG","RepeatTime: "+RepeatTime);
        // Calculate notification time in
        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        Log.i("LOG_TAG","currentCalendar: "+String.valueOf(c.getTime()));
        Log.i("LOG_TAG","calendarIntent: "+String.valueOf(calendar.getTime()));
        long diffTime = calendar.getTimeInMillis() - currentTime;
        Log.i("LOG_TAG","diffTime: "+String.valueOf(diffTime));
        
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Put Reminder ID in Intent Extra
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(DB.ROW_ID, Integer.toString(ID));
        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);


        // Start alarm using initial notification time and repeat interval time
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime,
                RepeatTime , mPendingIntent);

        // Restart alarm if device is rebooted
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context, int ID) {
    	mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    	
    	// Cancel Alarm using Reminder ID
    	mPendingIntent = PendingIntent.getBroadcast(context, ID, new Intent(context, AlarmReceiver.class), 0);
    	mAlarmManager.cancel(mPendingIntent);
    	Log.i("LOG_TAG","mAlarmManager canceled with id: "+ID);
    	// Disable alarm
    	ComponentName receiver = new ComponentName(context, BootReceiver.class);
    	PackageManager pm = context.getPackageManager();
    	pm.setComponentEnabledSetting(receiver,
    			PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
    			PackageManager.DONT_KILL_APP);
    }
    public void cancelBeforeAlarm(Context context, int ID) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Cancel Alarm using Reminder ID+1!!!!!!!!!!!!Kostil!!!!!!!!!!!!
        mPendingIntent = PendingIntent.getBroadcast(context, ID+1, new Intent(context, AlarmReceiver.class), 0);
        mAlarmManager.cancel(mPendingIntent);
        Log.i("LOG_TAG","mAlarmManager canceled with id: "+ID);
        // Disable alarm
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
    
    
}
