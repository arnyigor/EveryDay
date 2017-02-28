package com.android.everyday.views.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.everyday.R;
import com.android.everyday.database.DatabaseHandler;
import com.android.everyday.database.TasksDataSource;
import com.android.everyday.models.Task;
import com.android.everyday.models.ToastMaker;
import com.android.everyday.service.TaskAlarm;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TaskActivity extends AppCompatActivity {
    private TasksDataSource data_source;
    private Task task;
    private Intent intent;
    private CheckedTextView name;
    private ActionBar action_bar;
    private Toolbar toolbar;
    private TextView due_date, text_notes;
    private TaskActivity context;
    private ImageView image_repeat, image_alarm;
    private LinearLayout due_date_bar;
    private boolean modeEdit = false;//режим редактирования

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);
        context = TaskActivity.this;
        data_source = TasksDataSource.getInstance(context);
        initUI();
    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        action_bar = getSupportActionBar();
        if (action_bar != null) {
            action_bar.setHomeButtonEnabled(true);
            action_bar.setDisplayHomeAsUpEnabled(true);
        }
        name = (CheckedTextView) findViewById(R.id.text_view_task_name);
        due_date = (TextView) findViewById(R.id.text_date_due);
        text_notes = (TextView) findViewById(R.id.text_notes);
        image_repeat = (ImageView) findViewById(R.id.image_repeat);
        image_alarm = (ImageView) findViewById(R.id.image_alarm);
        due_date_bar = (LinearLayout) findViewById(R.id.due_date_bar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_view_task, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Get the task from the intent
        int id = getIntent().getIntExtra(Task.EXTRA_TASK_ID, 0);
        if (id == 0) {
            setModeEdit(false);
            return;
        }
        task = data_source.getTask(id);
        // Exit the task if it no longer exists (has been deleted)
        if (task == null) {
            setModeEdit(false);
            ToastMaker.toast(this, R.string.toast_error_no_task,true);
            finish();
            return;
        }
        setModeEdit(modeEdit);
        displayTask();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;

            case R.id.menu_view_task_edit:
//                Intent intent = new Intent(this, EditTaskActivity.class);
//                intent.putExtra(Task.EXTRA_TASK_ID, task.getID());
//                startActivityForResult(intent, MainActivity.EDIT_TASK_REQUEST);
                return true;

            case R.id.menu_view_task_delete:
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage(R.string.dialog_delete_single);
//                builder.setCancelable(true);
//                builder.setPositiveButton(R.string.menu_delete_task, this);
//                builder.setNegativeButton(R.string.menu_cancel, this);
//                builder.create().show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    View.OnClickListener nameOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            task.toggleIsCompleted();
            name.setChecked(task.isCompleted());
            task.setDateModified(System.currentTimeMillis());
            data_source.updateTask(task);
            TaskAlarm alarm = new TaskAlarm();
            alarm.cancelAlarm(context, task.getID());
            alarm.cancelNotification(context, task.getID());
            if (task.isCompleted()) {
                ToastMaker.toast(context, R.string.toast_task_completed,false);
                if (task.isRepeating()) {
                    task = alarm.setRepeatingAlarm(context, task.getID());
                    if (!task.isCompleted()) {
                        alarm.setAlarm(context, task);
                        ToastMaker.toast(context, ToastMaker.getRepeatMessage(context, R.string.toast_task_repeated, task.getDateDueCal()));
                    } else {
                        ToastMaker.toast(context, ToastMaker.getRepeatMessage(context, R.string.toast_task_repeat_delayed, task.getDateDueCal()));
                    }
                }
            } else {
                if (task.hasDateDue() && !task.isPastDue())
                    alarm.setAlarm(TaskActivity.this, task);
            }
            //  ???
            intent = new Intent(context, MainActivity.class);
            intent.putExtra(Task.EXTRA_TASK_ID, task.getID());
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    private void displayTask() {
        // Set name
        name.setText(task.getName());
        name.setChecked(task.isCompleted());
        name.setOnClickListener(nameOnClickListener);
        name.setTextColor(name.isChecked() ? Color.GRAY : Color.WHITE);
        action_bar.setTitle(R.string.title_activity_view_task);

        // Set due date
        if (task.hasDateDue()) {
            findViewById(R.id.due_date_bar).setVisibility(View.VISIBLE);
            if (task.isPastDue()) {
                due_date.setTextColor(Color.RED);
            } else {
                due_date.setTextColor(Color.LTGRAY);
            }
            Calendar current_cal = GregorianCalendar.getInstance();
            Calendar due_cal = task.getDateDueCal();
            if (due_cal.getTimeInMillis() >= current_cal.getTimeInMillis()) {
                if (due_cal.get(Calendar.YEAR) == current_cal.get(Calendar.YEAR)) {
                    due_date.setText(DateFormat.format("'Due' MMMM d 'at' h:mmaa", due_cal));
                } else {
                    due_date.setText(DateFormat.format("'Due' MMMM d, yyyy 'at' h:mmaa", due_cal));
                }
            } else {
                if (due_cal.get(Calendar.YEAR) == current_cal.get(Calendar.YEAR)) {
                    due_date.setText(DateFormat.format("'Was due' MMMM d 'at' h:mmaa", due_cal));
                } else {
                    due_date.setText(DateFormat.format("'Was due' MMMM d, yyyy 'at' h:mmaa", due_cal));
                }
            }

            // Set repetition
            if (task.isRepeating()) {
                image_repeat.setVisibility(View.VISIBLE);
            } else {
                image_repeat.setVisibility(View.GONE);
            }

            // Set procrastinator alarm
            if (task.hasFinalDateDue()) {
                image_alarm.setVisibility(View.VISIBLE);
            } else {
                image_alarm.setVisibility(View.GONE);
            }

        } else {
            due_date_bar.setVisibility(View.GONE);
        }
        // Set notes
        text_notes.setText(task.getNotes());
    }

    public boolean isModeEdit() {
        return modeEdit;
    }

    public void setModeEdit(boolean modeEdit) {
        this.modeEdit = modeEdit;
    }
}
