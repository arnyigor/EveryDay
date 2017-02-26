package com.android.everyday.views.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.android.everyday.R;
import com.android.everyday.adapter.TaskListAdapter;
import com.android.everyday.service.TaskService;
import com.android.everyday.service.WakefulService;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    public static final String AUTO_SORT = "auto_sort";
    public static final String CUSTOM_SORT = "custom_sort";
    public static final String HIDE_COMPLETED = "hide_completed";
    public static final String DEFAULT_HOUR = "default_hour";
    public static final String VIBRATE_ON_ALARM = "vibrate_on_alarm";
    public static final String REMINDER_TIME = "reminder_time";
    public static final String ALARM_TIME = "alarm_time";
    public static final String SORT_TYPE = "sort_type";
    public static final String DISPLAY_CATEGORY = "display_category";
    public static final String DEFAULT_REMINDER_TIME = "6";
    public static final String DEFAULT_ALARM_TIME = "15";
    public static final String DEFAULT_HOUR_VALUE = "12";
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefs_editor;
    private CheckBoxPreference cbp_auto_sort;
    private CheckBoxPreference cbp_custom_sort;
    private CheckBoxPreference cpb_vibrate;
    private ListPreference lp_reminder_time;
    private ListPreference lp_alarm_time;
    private ListPreference lp_default_hour;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = SettingsActivity.this;
        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addPreferencesFromResource(R.xml.preferences);

        // Read preferences from file
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs_editor = prefs.edit();

        // Initialize preferences objects
        cbp_auto_sort = (CheckBoxPreference) findPreference(AUTO_SORT);
        cbp_custom_sort = (CheckBoxPreference) findPreference(CUSTOM_SORT);
        cpb_vibrate = (CheckBoxPreference)  findPreference(VIBRATE_ON_ALARM);
        lp_reminder_time = (ListPreference)  findPreference(REMINDER_TIME);
        lp_alarm_time = (ListPreference)  findPreference(ALARM_TIME);
        lp_default_hour = (ListPreference)  findPreference(DEFAULT_HOUR);

        // Set listeners
        cbp_auto_sort.setOnPreferenceClickListener(onPreferenceClickListener);
        cbp_custom_sort.setOnPreferenceClickListener(onPreferenceClickListener);
        lp_reminder_time.setOnPreferenceChangeListener(onPreferenceChangeListener);
        lp_alarm_time.setOnPreferenceChangeListener(onPreferenceChangeListener);
        lp_default_hour.setOnPreferenceChangeListener(onPreferenceChangeListener);
        cpb_vibrate.setOnPreferenceChangeListener(onPreferenceChangeListener);

        // Set checkbox states
        if (prefs.getInt(SORT_TYPE, TaskListAdapter.AUTO_SORT) == TaskListAdapter.AUTO_SORT) {
            cbp_auto_sort.setChecked(true);
            cbp_custom_sort.setChecked(false);
        } else {
            cbp_auto_sort.setChecked(false);
            cbp_custom_sort.setChecked(true);
        }

        // Set ListPreferences
        lp_reminder_time.setSummary(getReminderSummary(REMINDER_TIME,
                prefs.getString(REMINDER_TIME, DEFAULT_REMINDER_TIME)));
        lp_alarm_time.setSummary(getReminderSummary(ALARM_TIME,
                prefs.getString(ALARM_TIME, DEFAULT_ALARM_TIME)));
        lp_default_hour.setSummary(getHourSummary(prefs.getString(DEFAULT_HOUR, DEFAULT_HOUR_VALUE)));


    }

    /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preferences, target);
    }

    private String getReminderSummary(String key, String value) {
        StringBuilder builder = new StringBuilder();

        builder.append("Every ");
        builder.append(value);

        if (key.equals(REMINDER_TIME))
            builder.append(" hours");
        else
            builder.append(" minutes");

        return builder.toString();
    }

    private String getHourSummary(String value) {
        String summary;

        if (value.equals("0"))
            summary = "Midnight";
        else if (value.equals("12"))
            summary = "Noon";
        else if (value.equals("6") || value.equals("9"))
            summary = value + ":00 am";
        else if (value.equals("15"))
            summary = "3:00 pm";
        else
            summary = "6:00 pm";

        return summary;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String key = preference.getKey();
            if (key.equals(REMINDER_TIME) || key.equals(ALARM_TIME) || key.equals(VIBRATE_ON_ALARM)) {
                if (key.equals(REMINDER_TIME))
                    lp_reminder_time.setSummary(getReminderSummary(REMINDER_TIME, (String) newValue));
                else if (key.equals(ALARM_TIME))
                    lp_alarm_time.setSummary(getReminderSummary(ALARM_TIME, (String) newValue));
                // Start service which will update all of the task alarms with the new reminder intervals
                WakefulService.acquireStaticLock(context);
                context.startService(new Intent(context, TaskService.class));
                return true;
            }
            if (key.equals(DEFAULT_HOUR)) {
                lp_default_hour.setSummary(getHourSummary((String) newValue));
                return true;
            }
            return false;
        }
    };

    Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();

            if (key.equals(AUTO_SORT)) {
                cbp_auto_sort.setChecked(true);
                cbp_custom_sort.setChecked(false);
                prefs_editor.putInt(SORT_TYPE, TaskListAdapter.AUTO_SORT);
                prefs_editor.commit();

//                // Update homescreen widget (after change has been saved to DB)
//                TaskButlerWidgetProvider.updateWidget(this);
                return true;
            }

            if (key.equals(CUSTOM_SORT)) {
                cbp_auto_sort.setChecked(false);
                cbp_custom_sort.setChecked(true);
                prefs_editor.putInt(SORT_TYPE, TaskListAdapter.CUSTOM_SORT);
                prefs_editor.commit();
                startActivity(new Intent(context, CustomSortActivity.class));
                return true;
            }

            return false;
        }
    };


    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();

        if (key.equals(DEFAULT_HOUR)) {
            lp_default_hour.setSummary(getHourSummary((String) newValue));
            return true;
        }

        return false;
    }

}
