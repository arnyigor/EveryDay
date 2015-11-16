package com.android.everyday;

//imports start==========

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
//imports end==========

//==============Activity start=========================
@SuppressLint("SimpleDateFormat")
public class InfoActivity extends Activity {
    private static final int NOTIFY_ID = 101;
    private static final String TAG = "LOG_TAG";
    // =============Variables start================
    DB db;
    Timer timer = new Timer();
    long iventDateTime;
    Cursor cursor;
    String title, date, time, info;
    Button edtBtn;
    int intPosition;
    SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
    long diff, diffSeconds, diffMinutes, diffHours, diffDays;
    String intentPosition,remaintext;
    // =============Variables end================
    // ==============Forms variables start==============
    TextView infoTitleView, infoDateView, infoTimeView, infoInfoView,
            infoInfoLeft;

    // ==============Forms variables end==============
    // ====================onCreate start=========================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setTitle(getResources().getString(R.string.info));

        // ================Forms Ids start=========================
        // ================Forms Ids start=========================
        edtBtn = (Button) findViewById(R.id.buttonInfoOk);
        edtBtn.setText(getResources().getString(R.string.dialogok));
        infoTitleView = (TextView) findViewById(R.id.tvInfoTitle);
        infoDateView = (TextView) findViewById(R.id.tvInfoDate);
        infoTimeView = (TextView) findViewById(R.id.tvInfoTime);
        infoInfoView = (TextView) findViewById(R.id.tvInfo);
        infoInfoLeft = (TextView) findViewById(R.id.tvInfoDaysLeft);
        // ================Forms Ids end=========================

        // ==================onCreateCode start=========================
        db = new DB(this);
        try {
            Intent intent = getIntent();
            intentPosition = intent.getStringExtra("intentPosition");
            Log.i("LOG_TAG", intentPosition);
        } catch (Exception e) {
            Log.i("LOG_TAG1", e.toString());
        }

        try {
            intPosition = Integer.parseInt(intentPosition);
        } catch (Exception e) {
            Log.i("LOG_TAG11", e.toString());
        }

        try {
            db.open();
            cursor = db.getItem(intPosition);
            Log.i("LOG_TAG", "intPosition" + intPosition);
            if (cursor.moveToFirst()) {
                try {
                    int idColTitle = cursor.getColumnIndex(DB.ROW_TITLE);
                    int idColDateTime = cursor.getColumnIndex(DB.ROW_DATETIME);
                    int idColInfo = cursor.getColumnIndex(DB.ROW_INFO);
                    title = cursor.getString(idColTitle);
                    iventDateTime = Long.parseLong(cursor.getString(idColDateTime));
                    info = cursor.getString(idColInfo);
                    try {
                        infoTitleView.setText(title);
                        infoDateView.setText(convertDate(iventDateTime));
                        infoTimeView.setText(convertTime(iventDateTime));
                        infoInfoView.setText(info);
                    } catch (Exception e) {
                        Log.i("LOG_TAG", e.toString());
                    }
                } catch (Exception e) {
                    Log.i("LOG_TAG", e.toString());
                }
            }
            cursor.close();
        } catch (SQLException e) {
            Log.i("LOG_TAG", e.toString());
        }

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "iventDateTime: " + iventDateTime);
                        Log.i(TAG, "convertTime iventDateTime: " + convertDateTime(iventDateTime));
                        Calendar calendar = Calendar.getInstance();
                        long timeNow = calendar.getTimeInMillis();
                        Log.i(TAG, "timeNow: " + timeNow);
                        Log.i(TAG, "convertTime timeNow: " + convertDateTime(calendar.getTimeInMillis()));
                        try {

                            if (timeNow > iventDateTime) {
                                remaintext = getResources().getString(R.string.after);
                                diff = timeNow - iventDateTime;
                            } else {
                                remaintext = getResources().getString(R.string.remain);
                                diff = iventDateTime - timeNow;
                            }
                            diffSeconds = diff / 1000 % 60;
                            diffMinutes = diff / (60 * 1000) % 60;
                            diffHours = diff / (60 * 60 * 1000) % 24;
                            diffDays = diff / (24 * 60 * 60 * 1000);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        infoInfoLeft.setText(remaintext
                                + diffDays + getResources().getString(R.string.days)
                                + diffHours + getResources().getString(R.string.hours)
                                + diffMinutes + getResources().getString(R.string.minutes)
                                + diffSeconds + getResources().getString(R.string.seconds));
                    }
                });
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);

        // ==================onCreateCode end=========================

    }// ============onCreate end====================

    // ====================CustomCode

    public String convertDateTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        return format.format(date);
    }

    public String convertDate(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd MMM yyyy");
        return format.format(date);
    }

    public String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }
    // ====================CustomCode end======================================

    // ====================OnClicks======================================

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonInfoOk:
                finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("LOT_TAG", "onPause");
        timer.cancel();
    }

    // ====================OnClicks end======================================
}// ===================Activity end==================================
// ===================SimpleActivity==================================
