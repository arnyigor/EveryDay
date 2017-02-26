package com.android.everyday.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.everyday.R;
import com.android.everyday.database.DatabaseHandler;
import com.android.everyday.database.TasksDataSource;
import com.android.everyday.models.Task;
import com.android.everyday.models.ToastMaker;
import com.android.everyday.service.TaskAlarm;
import com.android.everyday.views.activities.MainActivity;

public class TasksFragment extends Fragment {
    private Context context;
    private Button btnAddTask;
    private TasksDataSource data_source;
    private Intent intent   ;

    public TasksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        View rootView = inflater.inflate(R.layout.fragment_tasks, container, false);
        data_source = TasksDataSource.getInstance(context);
        initUI(rootView);
        initListeners();
        return rootView;
    }

    private void initListeners() {
        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });
    }

    private void initUI(View rootView) {
        btnAddTask = (Button) rootView.findViewById(R.id.btnAddTask);
    }

    private boolean addTask() {
        // Get task name
//        String name = et_name.getText().toString().trim();
        String name = "Test";
        // If there is no task name, don't create the task
        if (name.equals("")) {
            ToastMaker.toast(context, R.string.toast_task_no_name);
            return false;
        }

        // Get repeat interval
        int interval = 1;
//        String interval_string = et_repeat_interval.getText().toString();
        String interval_string = "2";
        if (!interval_string.equals("")) {
            interval = Integer.parseInt(interval_string);
            if (interval == 0)
                interval = 1;
        }

        // Get task due date
        long due_date_ms = 0;
//        if (cb_due_date.isChecked())
//            due_date_ms = due_date_cal.getTimeInMillis();
        due_date_ms = System.currentTimeMillis() + 15000;

        // Current time
        long current_time = System.currentTimeMillis();

        // Create the task
        Task task = new Task(
                data_source.getNextID(DatabaseHandler.TABLE_TASKS),
                name,
                false,
                true,
                true,
                true,
//                cb_due_date.isChecked(),
//                cb_final_due_date.isChecked(),
//                cb_repeating.isChecked(),
                Task.MINUTES,
//                s_repeat_type.getSelectedItemPosition(),
                interval,
                current_time,
                current_time,
                due_date_ms,
                "",
                "test note");

        // Assign the task a unique ID and store it in the database
        task.setID(data_source.getNextID(DatabaseHandler.TABLE_TASKS));
        data_source.addTask(task);

        // Alarm logic: Add a task (AddTaskActivity)
        // * Task must be added to database first
        // * If has due date:
        // *	Set alarm
        // * 	(Repeating due date will be handled by the service after alarm rings)
        if (task.hasDateDue() && !task.isPastDue()) {
            TaskAlarm alarm = new TaskAlarm();
            alarm.setAlarm(context, task);
        }
        // Create the return intent and add the task ID
        intent = new Intent(context, MainActivity.class);
        intent.putExtra(Task.EXTRA_TASK_ID, task.getID());

        return true;
    }


}
