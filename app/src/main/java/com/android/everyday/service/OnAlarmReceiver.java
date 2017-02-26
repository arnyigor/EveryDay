package com.android.everyday.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.everyday.database.TasksDataSource;
import com.android.everyday.models.Task;

public class OnAlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		WakefulService.acquireStaticLock(context); //acquire a partial WakeLock
		
		//send notification, bundle intent with taskID
		NotificationHelper notification = new NotificationHelper();
		Bundle bundle = intent.getExtras();
		int id = bundle.getInt(Task.EXTRA_TASK_ID);
		TasksDataSource db = TasksDataSource.getInstance(context);
		Task task = db.getTask(id);
		notification.sendBasicNotification(context, task); // send basic notification
		context.startService(new Intent(context, TaskService.class)); //start TaskService
	}
}