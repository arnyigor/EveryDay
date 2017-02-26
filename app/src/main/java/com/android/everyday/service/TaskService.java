package com.android.everyday.service;

import android.content.Intent;

import com.android.everyday.database.TasksDataSource;
import com.android.everyday.models.Task;

import java.util.List;

public class TaskService extends WakefulService {

	public TaskService() {
		super("TaskService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		TasksDataSource db = TasksDataSource.getInstance(this); //get access to the instance of TasksDataSource
		TaskAlarm alarm = new TaskAlarm();


		List<Task> tasks = db.getAllTasks(); //Get a list of all the tasks there
		for (Task task : tasks) {
			// Cancel existing alarm
			alarm.cancelAlarm(this, task.getID());
			
			//Procrastinator and Reminder alarm
			if(task.isPastDue()){
				alarm.setReminder(this, task.getID());
			}
			
			//handle repeat alarms
			if(task.isRepeating() && task.isCompleted()){
				task = alarm.setRepeatingAlarm(this, task.getID());
			}
			
			//regular alarms
			if(!task.isCompleted() && (task.getDateDue() >= System.currentTimeMillis())){
				alarm.setAlarm(this, task);	
			}
		}
		super.onHandleIntent(intent);
	}
}