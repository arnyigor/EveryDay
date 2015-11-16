package com.android.everyday;

//imports start==========

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

//imports end==========

//==============Activity start=========================
@SuppressLint("SimpleDateFormat")
public class AddEditActivity extends Activity {
	static final int DIALOG_DATE = 101;
	static final int DIALOG_TIME = 102;
	// Values for orientation change
	// Constant Intent String
	public static final String EXTRA_REMINDER_ID = "Reminder_ID";
	// =============Variables start================
	private String mRepeat,mRepeatText,mRepeatBefore,mRepeatBeforeText,mRepeatCnt,mRepeatBeforeCnt,mRepeatTypeText,mRepeatBeforeTypeText,mActive,myTimestamp;
	long ID, mRepeatTime,mRepeatBeforeTime,mRowId,mDBTimeStamp;
	int maxLengthofEditText = 34;
	DB db;
	boolean mBeforeMoreRepeat;
	Calendar mCalendar,mCalendarBefore;
	Cursor cursor;
	SimpleDateFormat datetimeformat = new SimpleDateFormat("dd MMM yyyy HH:mm");
	SimpleDateFormat datetimesecformat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
	String title, date, time, info, strMonth, formattedDate;
	int before,mRepeatTypeId,mRepeatBeforeTypeId,mReceivedID;

	// Constant values in milliseconds
	private static final long milMinute = 60000L;
	private static final long milHour = 3600000L;
	private static final long milDay = 86400000L;
	private static final long milWeek = 604800000L;
	private static final long milMonth = 2592000000L;
	// =============Variables end================
	// ==============Forms variables start==============
	CheckBox checkRepeat,checkRepeatBefore;
	Spinner repeatType,repeatBeforeType;
	Button buttonAddEdit; 
	Switch mSwitchActive;
	EditText TitleInput, DateInput, TimeInput, beforeInput, InfoInput,repeatInput;
	private int mYear, mMonth, mDay, mHour, mMinute;

	// ==============Forms variables end==============
	// ====================onCreate start=========================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_add);
		TitleInput = (EditText) findViewById(R.id.editTitle);
		TitleInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				maxLengthofEditText) });
		DateInput = (EditText) findViewById(R.id.editDate);
		TimeInput = (EditText) findViewById(R.id.editTime);
		beforeInput = (EditText) findViewById(R.id.editRepeatBeforeCnt);
		InfoInput = (EditText) findViewById(R.id.editInfo);
		repeatInput = (EditText) findViewById(R.id.editRepeatCount);
		checkRepeat = (CheckBox) findViewById(R.id.checkRepeat);
		checkRepeatBefore = (CheckBox) findViewById(R.id.checkRepeatBefore);
		repeatType = (Spinner) findViewById(R.id.spinnerRepeatType);
		repeatBeforeType = (Spinner) findViewById(R.id.spinnerRepeatBeforeType);
		mSwitchActive = (Switch) findViewById(R.id.switchActive);
		buttonAddEdit = (Button) findViewById(R.id.buttonAddEditEvent);
		// ================Forms Ids end=========================

		// ==================onCreateCode start=========================
		mCalendar = Calendar.getInstance();
		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);

		db = new DB(this);
		db.open();
		// Get reminder id from intent
		try {
			mRowId = Integer.parseInt(getIntent().getStringExtra(DB.ROW_ID));
			if (mRowId>0) {
				buttonAddEdit.setText(getResources().getString(R.string.edititem));
			}
		} catch (Exception e) {
			Log.i("LOG_TAG", e.toString());
		}
		Log.i("LOG_TAG","mRowId: "+mRowId);
		
		mSwitchActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                	mActive="true";
                } else {
                	mActive="false";
                }
                Log.i("LOG_TAG", "mActive: "+mActive);
            }
        });

		checkRepeat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					mRepeat = "true";
					mRepeatText = getResources().getString(R.string.repeat);
					mRepeatCnt = "1";
					mRepeatTypeId = repeatType.getSelectedItemPosition();
					mRepeatTypeText = repeatType.getItemAtPosition(mRepeatTypeId).toString();
					Log.i("LOG_TAG", "mRepeatTypeId: " + mRepeatTypeId);
					Log.i("LOG_TAG", "mRepeatTypeText: " + mRepeatTypeText);
					repeatInput.setEnabled(true);
					repeatType.setEnabled(true);
					repeatInput.setText(String.valueOf(mRepeatCnt));
					checkRepeat.setText(mRepeatText);
				} else {
					mRepeat = "false";
					mRepeatText = getResources().getString(R.string.repeatoff);
					mRepeatCnt = "";
					mRepeatTypeId = repeatType.getSelectedItemPosition();
					mRepeatTypeText = "";
					Log.i("LOG_TAG", "mRepeatTypeId: " + mRepeatTypeId);
					Log.i("LOG_TAG", "mRepeatTypeText: " + mRepeatTypeText);
					repeatInput.setEnabled(false);
					repeatType.setEnabled(false);
					repeatInput.setText(String.valueOf(mRepeatCnt));
					checkRepeat.setText(mRepeatText);
				}
			}
		});
		
		checkRepeatBefore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					mRepeatBefore = "true";
					mRepeatBeforeText = getResources().getString(R.string.before);
					mRepeatBeforeCnt = "1";
					mRepeatBeforeTypeId = repeatBeforeType.getSelectedItemPosition();
					mRepeatBeforeTypeText = repeatBeforeType.getItemAtPosition(mRepeatBeforeTypeId).toString();
					Log.i("LOG_TAG", "mRepeatBeforeTypeId: " + mRepeatBeforeTypeId);
					Log.i("LOG_TAG", "mRepeatBeforeTypeText: " + mRepeatBeforeTypeText);
					beforeInput.setEnabled(true);
					repeatBeforeType.setEnabled(true);
					beforeInput.setText(String.valueOf(mRepeatBeforeCnt));
					checkRepeatBefore.setText(mRepeatBeforeText);
				} else {
					mRepeatBefore = "false";
					mRepeatBeforeText = getResources().getString(R.string.beforeoff);
					mRepeatBeforeCnt = "";
					mRepeatBeforeTypeId = repeatBeforeType.getSelectedItemPosition();
					mRepeatBeforeTypeText = "";
					Log.i("LOG_TAG", "mRepeatBeforeTypeId: " + mRepeatBeforeTypeId);
					Log.i("LOG_TAG", "mRepeatBeforeTypeText: " + mRepeatBeforeTypeText);
					beforeInput.setEnabled(false);
					repeatBeforeType.setEnabled(false);
					beforeInput.setText(String.valueOf(mRepeatBeforeCnt));
					checkRepeatBefore.setText(mRepeatBeforeText);
				}
			}
		});

		repeatType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent,
					View itemSelected, int selectedItemPosition,
					long selectedId) {
				String[] choose = getResources().getStringArray(
						R.array.repeattypes);
				mRepeatTypeText = choose[selectedItemPosition];
			}
			
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		repeatBeforeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View itemSelected, int selectedItemPosition,
							long selectedId) {
						String[] choose = getResources().getStringArray(
								R.array.repeattypes);
						mRepeatBeforeTypeText = choose[selectedItemPosition];
					}

					public void onNothingSelected(AdapterView<?> parent) {
					}
				});
			new AlarmReceiver();

	}// ============onCreate end====================

	/*// To save state on device rotation
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// outState.putCharSequence(KEY_TITLE, TitleInput.getText());
		if (mRowId != 0) {
			outState.putLong(DB.ROW_ID, mRowId);
		}

	}*/

	/*private void setRowIdFromIntent() {
		if (mRowId == 0) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(DB.ROW_ID) : null;

		}
	}*/

	@Override
	protected void onPause() {
		super.onPause();
		db.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("LOG_TAG", "onResume");
		db.open();
//		setRowIdFromIntent();
		Log.i("LOG_TAG", "mRowId: " + mRowId);
		fillInputs();
	}
	
	private void fillInputs() {
		Log.i("LOG_TAG","-------------fillInputs---------");
		if (mRowId != 0) {
			cursor = db.getItem(mRowId);
			if (cursor.moveToFirst()) {
				try {
					int idColTitle = cursor.getColumnIndex(DB.ROW_TITLE);
					int idColDate = cursor.getColumnIndex(DB.ROW_DATE);
					int idColTime = cursor.getColumnIndex(DB.ROW_TIME);
					int idColInfo = cursor.getColumnIndex(DB.ROW_INFO);
					int idColRepeat = cursor.getColumnIndex(DB.ROW_REPEAT);
					int idColRepeatText = cursor.getColumnIndex(DB.ROW_REPEAT_TEXT);
					int idColRepeatTypeId = cursor.getColumnIndex(DB.ROW_REPEAT_TYPE_ID);
					int idColRepeatCnt = cursor.getColumnIndex(DB.ROW_REPEAT_CNT);
					int idColRepeatTypeText = cursor.getColumnIndex(DB.ROW_REPEAT_TYPE_TEXT);
					int idColRepeatBefore = cursor.getColumnIndex(DB.ROW_BEFORE);
					int idColRepeatBeforeText = cursor.getColumnIndex(DB.ROW_BEFORE_TEXT);
					int idColRepeatBeforeTypeId = cursor.getColumnIndex(DB.ROW_BEFORE_TYPE_ID);
					int idColRepeatBeforeCnt = cursor.getColumnIndex(DB.ROW_BEFORE_CNT);
					int idColRepeatBeforeTypeText = cursor.getColumnIndex(DB.ROW_BEFORE_TYPE_TEXT);
					int idColActive = cursor.getColumnIndex(DB.ROW_ACTIVE);
					title = cursor.getString(idColTitle);
					date = cursor.getString(idColDate);
					time = cursor.getString(idColTime);
					info = cursor.getString(idColInfo);
					mRepeat = cursor.getString(idColRepeat);
					mRepeatText = cursor.getString(idColRepeatText);
					mRepeatTypeText = cursor.getString(idColRepeatTypeText);
					mRepeatTypeId = Integer.parseInt(cursor.getString(idColRepeatTypeId));
					mRepeatCnt = cursor.getString(idColRepeatCnt);
					mRepeatBefore = cursor.getString(idColRepeatBefore);
					mRepeatBeforeText = cursor.getString(idColRepeatBeforeText);
					mRepeatBeforeTypeText = cursor.getString(idColRepeatBeforeTypeText);
					mRepeatBeforeTypeId = Integer.parseInt(cursor.getString(idColRepeatBeforeTypeId));
					mRepeatBeforeCnt = cursor.getString(idColRepeatBeforeCnt);
					mActive = cursor.getString(idColActive);
					Log.i("LOG_TAG", "mRepeat: " + String.valueOf(mRepeat));
					Log.i("LOG_TAG", "mRepeatBefore: " + String.valueOf(mRepeat));
					Log.i("LOG_TAG", "mActive: " + String.valueOf(mActive));
					TitleInput.setText(title);
					DateInput.setText(date);
					TimeInput.setText(time);
					InfoInput.setText(info);

					if (mActive.equals("true")) {
						mSwitchActive.setChecked(true);
					}else{
						mSwitchActive.setChecked(false);
					}

					if (mRepeat.equals("true")) {
						checkRepeat.setChecked(true);
						checkRepeat.setText(getResources().getString(R.string.repeat));
						repeatInput.setEnabled(true);
						repeatInput.setText(mRepeatCnt);
						repeatType.setEnabled(true);
						Log.i("LOG_TAG","repeatTypeId: "+ String.valueOf(mRepeatTypeId));
						repeatType.setSelection(mRepeatTypeId);
					} else {
						checkRepeat.setChecked(false);
						checkRepeat.setText(getResources().getString(R.string.repeatoff));
						repeatInput.setEnabled(false);
						repeatInput.setText(mRepeatCnt);
						repeatType.setEnabled(false);
						
					}// mRepeat=true

					if (mRepeatBefore.equals("true")) {
						checkRepeatBefore.setChecked(true);
						checkRepeatBefore.setText(getResources().getString(R.string.before));
						beforeInput.setEnabled(true);
						beforeInput.setText(mRepeatBeforeCnt);
						repeatBeforeType.setEnabled(true);
						Log.i("LOG_TAG","repeatBeforeTypeId: "+ String.valueOf(mRepeatBeforeTypeId));
						repeatBeforeType.setSelection(mRepeatBeforeTypeId);
					} else {
						checkRepeatBefore.setChecked(false);
						checkRepeatBefore.setText(getResources().getString(R.string.beforeoff));
						beforeInput.setEnabled(false);
						beforeInput.setText(mRepeatBeforeCnt);
						repeatBeforeType.setEnabled(false);

					}// mRepeatBefore=true

				} catch (Exception e) {
					Log.i("LOG_TAG", e.toString());
				}
			}// cursor
		} else {
			mRepeat = "false";
			mActive = "false";
			mRepeatText = getResources().getString(R.string.repeatoff);
			mRepeatCnt = "";
			mRepeatTypeId = repeatType.getSelectedItemPosition();
			mRepeatTypeText = "";
			checkRepeat.setChecked(false);
			checkRepeat.setText(mRepeatText);
			repeatInput.setEnabled(false);
			repeatInput.setText(mRepeatCnt);
			repeatType.setEnabled(false);
			mRepeatBefore = "false";
			mRepeatBeforeText = getResources().getString(R.string.beforeoff);
			mRepeatBeforeCnt = "";
			mRepeatBeforeTypeId = repeatBeforeType.getSelectedItemPosition();
			mRepeatBeforeTypeText = "";
			checkRepeatBefore.setChecked(false);
			checkRepeatBefore.setText(mRepeatBeforeText);
			beforeInput.setEnabled(false);
			beforeInput.setText(mRepeatBeforeCnt);
			repeatBeforeType.setEnabled(false);
		}// row!=0
		Log.i("LOG_TAG", "mRepeat: " + String.valueOf(mRepeat));
		Log.i("LOG_TAG", "mRepeatText: " + String.valueOf(mRepeatText));
		Log.i("LOG_TAG", "mRepeatTypeText: " + String.valueOf(mRepeatTypeText));
		Log.i("LOG_TAG", "mRepeatTypeId: " + String.valueOf(mRepeatTypeId));
		Log.i("LOG_TAG", "mRepeatCnt: " + String.valueOf(mRepeatCnt));
		Log.i("LOG_TAG", "mRepeatBefore: " + String.valueOf(mRepeatBefore));
		Log.i("LOG_TAG", "mRepeatBeforeText: " + String.valueOf(mRepeatBeforeText));
		Log.i("LOG_TAG", "mRepeatBeforeTypeText: " + String.valueOf(mRepeatBeforeTypeText));
		Log.i("LOG_TAG", "mRepeatBeforeTypeId: " + String.valueOf(mRepeatBeforeTypeId));
		Log.i("LOG_TAG", "mRepeatBeforeCnt: " + String.valueOf(mRepeatBeforeCnt));
		Log.i("LOG_TAG", "mActive: " + String.valueOf(mActive));
		Log.i("LOG_TAG","-------------fillInputs end---------");
	}// fillinputs

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_TIME:
			return new TimePickerDialog(this, timePickerListener, mHour,
					mMinute, true);
		case DIALOG_DATE:
			return new DatePickerDialog(this, datePickerListener, mYear,
					mMonth, mDay);
		}
		return null;
	}

	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			mHour = selectedHour;
			mMinute = selectedMinute;
			TimeInput.setText(new StringBuilder().append(pad(mHour))
					.append(":").append(pad(mMinute)));
		}
	};

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int Year, int monthOfYear,
				int dayOfMonth) {
			mYear = Year;
			String strDateFormat = "MMM";
			strMonth = new DateFormatSymbols().getMonths()[monthOfYear];
			Date date = null;
			try {
				date = new SimpleDateFormat(strDateFormat).parse(strMonth);
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
			formattedDate = new SimpleDateFormat("MMM").format(date);
			mDay = dayOfMonth;
			DateInput.setText(mDay + " " + formattedDate + " " + mYear);
		}
	};

	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}

	@SuppressWarnings({ "deprecation" })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.editDate:
			showDialog(DIALOG_DATE);
			break;
		case R.id.editTime:
			showDialog(DIALOG_TIME);
			break;
		case R.id.buttonAddEditEvent:
			saveState();
			//Toast.makeText(getApplication(),getString(R.string.task_saved_message), Toast.LENGTH_LONG).show();
			//finish();
			break;
		}
	}
	
	public long insertDB() {
		long id;
		id = db.insertItem(title, date, time, mDBTimeStamp, info, 
				mRepeat, mRepeatText,mRepeatCnt, mRepeatTypeId, mRepeatTypeText,
				mRepeatBefore, mRepeatBeforeText,mRepeatBeforeCnt, mRepeatBeforeTypeId, mRepeatBeforeTypeText,
				mActive);
		return id;
	}
	
	public void updateDB() {
		db.updateItem(mRowId, title, date, time,mDBTimeStamp, info, 
				mRepeat, mRepeatText,mRepeatCnt, mRepeatTypeId, mRepeatTypeText,
				mRepeatBefore, mRepeatBeforeText,mRepeatBeforeCnt, mRepeatBeforeTypeId, mRepeatBeforeTypeText,
				mActive);
		return;
	}
	
	public Calendar CurrentCal() {
		Calendar curCal = Calendar.getInstance();
		return curCal;
	}
	
	public Calendar beforeTimeAlarmSet(Calendar intCal,int mRepeatBeforeCnt, int mRepeatBeforeTypeId){
	    int value = -1*mRepeatBeforeCnt;
	    Calendar resultCal = GregorianCalendar.getInstance();
	    resultCal.setTime(intCal.getTime());
		switch (mRepeatBeforeTypeId) {
		case 0:
			resultCal.add(Calendar.MINUTE,value);
			break;
		case 1:
			resultCal.add(Calendar.HOUR,value);
			break;
		case 2:
			resultCal.add(Calendar.DAY_OF_MONTH,value);
			break;
		case 3:
			resultCal.add(Calendar.WEEK_OF_MONTH,value);
			break;
		case 4:
			resultCal.add(Calendar.MONTH,value);
			break;
		}
		return resultCal;
	}
	
	public void insertFieldsToDB() {
		Log.i("LOG_TAG","insertFieldsToDB");
		if (mActive.equals("true")) {
			Log.i("LOG_TAG","mActive: "+mActive);
			if (mRepeat.equals("true")) {
				Log.i("LOG_TAG","mRepeat: "+mRepeat);
				if (mRepeatBefore.equals("true")) {
					Log.i("LOG_TAG","mRepeatBefore: "+mRepeatBefore);
					if (mBeforeMoreRepeat) {
						Log.i("LOG_TAG","mBeforeMoreRepeat: "+mBeforeMoreRepeat);
						Toast.makeText(getApplication(), getResources().getString(R.string.addcorrectbefore), Toast.LENGTH_LONG).show();
						return;
					}else{//before less repeat
						Log.i("LOG_TAG","mBeforeMoreRepeat: "+mBeforeMoreRepeat);
						ID = insertDB();
						Log.i("LOG_TAG","inserted ID: "+ID);
						mCalendarBefore =beforeTimeAlarmSet(mCalendar, Integer.parseInt((mRepeatBeforeCnt)), mRepeatBeforeTypeId); 
						new AlarmReceiver().setBeforeAlarm(getApplicationContext(),mCalendarBefore, (int) ID);
						new AlarmReceiver().setRepeatAlarm(getApplicationContext(),mCalendar, (int) ID, mRepeatTime);
						finish();
					}
				}else{//before false
					Log.i("LOG_TAG","mRepeatBefore: "+mRepeatBefore);
					ID = insertDB();
					Log.i("LOG_TAG","inserted ID: "+ID);
					new AlarmReceiver().setRepeatAlarm(getApplicationContext(),mCalendar, (int) ID, mRepeatTime);
					finish();
				}
			}else{//repeat false
				Log.i("LOG_TAG","mRepeat: "+mRepeat);
				if (mRepeatBefore.equals("true")) {
					Log.i("LOG_TAG","mRepeatBefore: "+mRepeatBefore);
					ID = insertDB();
					Log.i("LOG_TAG","inserted ID: "+ID);
					mCalendarBefore =beforeTimeAlarmSet(mCalendar, Integer.parseInt((mRepeatBeforeCnt)), mRepeatBeforeTypeId);
					new AlarmReceiver().setBeforeAlarm(getApplicationContext(),mCalendarBefore, (int) ID);
					new AlarmReceiver().setAlarm(getApplicationContext(),mCalendar, (int) ID);
					finish();
				}else{//before false
					Log.i("LOG_TAG","mRepeatBefore: "+mRepeatBefore);
					ID = insertDB();
					Log.i("LOG_TAG","inserted ID: "+ID);
					new AlarmReceiver().setAlarm(getApplicationContext(),mCalendar, (int) ID);
					finish();
				}//mRepeatBefore
			}//mRepeat
		}else{//Active false
			if (mRepeat.equals("true")) {
				Log.i("LOG_TAG","mRepeat: "+mRepeat);
				if (mRepeatBefore.equals("true")) {
					Log.i("LOG_TAG","mRepeatBefore: "+mRepeatBefore);
					if (mBeforeMoreRepeat) {
						Log.i("LOG_TAG","mBeforeMoreRepeat: "+mBeforeMoreRepeat);
						Toast.makeText(getApplication(), getResources().getString(R.string.addcorrectbefore), Toast.LENGTH_LONG).show();
						return;
					}else{
						Log.i("LOG_TAG","mBeforeMoreRepeat: "+mBeforeMoreRepeat);
						ID = insertDB();
						Log.i("LOG_TAG","inserted ID: "+ID);
						finish();
					}
				} else {
					Log.i("LOG_TAG","mRepeatBefore: "+mRepeatBefore);
					ID = insertDB();
					Log.i("LOG_TAG", "inserted ID: " + ID);
					finish();
				}
			} else {
				Log.i("LOG_TAG","mRepeat: "+mRepeat);
				ID = insertDB();
				Log.i("LOG_TAG", "inserted ID: " + ID);
				finish();
			}

		}
	}
	
	public static String getDurationBreakdown(long millis)
    {
        if(millis < 0)
        {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        if(days>0){
        	sb.append(days);
            sb.append(" Дней ");
        }
        if (hours>0) {
        	sb.append(hours);
            sb.append(" Часов ");
		}
        if (minutes>0) {
        	sb.append(minutes);
            sb.append(" Минут ");
		}
        if (seconds>0) {
        	sb.append(seconds);
            sb.append(" секунд ");
		}

        return(sb.toString());
    }
	
	
	public String diffCalendars(Calendar end) {
		String str = "";
		Calendar cur = Calendar.getInstance();
		long diffTime = cur.getTimeInMillis() - end.getTimeInMillis();
		if (diffTime>=0) {
			str = getDurationBreakdown(diffTime);
		}else{
			str="";
		}
		return str;
		
		
	}
	
	/*private static AtomicLong idCounter = new AtomicLong();
	public static String createID()
	{
	    return String.valueOf(9999+idCounter.getAndIncrement());
	}*/
	
	public void updateFieldsInDB() {
		Log.i("LOG_TAG","updateFieldsInDB");
		if (mActive.equals("true")) {
			Log.i("LOG_TAG","mActive: "+mActive);
			if (mRepeat.equals("true")) {
				Log.i("LOG_TAG","mRepeat: "+mRepeat);
				if (mRepeatBefore.equals("true")) {
					Log.i("LOG_TAG","mRepeatBefore: "+mRepeatBefore);
					if (mBeforeMoreRepeat) {
						Log.i("LOG_TAG","mBeforeMoreRepeat: "+mBeforeMoreRepeat);
						Toast.makeText(getApplication(), getResources().getString(R.string.addcorrectbefore), Toast.LENGTH_LONG).show();
						return;
					}else{//before less repeat
						Log.i("LOG_TAG","mBeforeMoreRepeat: "+mBeforeMoreRepeat);
						updateDB();
						Log.i("LOG_TAG","updated ID: "+ mReceivedID);
						mCalendarBefore =beforeTimeAlarmSet(mCalendar, Integer.parseInt((mRepeatBeforeCnt)), mRepeatBeforeTypeId); 
						new AlarmReceiver().setBeforeAlarm(getApplicationContext(),mCalendarBefore, mReceivedID);
						new AlarmReceiver().setRepeatAlarm(getApplicationContext(),mCalendar, mReceivedID, mRepeatTime);
						finish();
					}
				}else{//before false
					Log.i("LOG_TAG","mRepeatBefore: "+mRepeatBefore);
					updateDB();
					Log.i("LOG_TAG","updated ID: "+ mReceivedID);
					new AlarmReceiver().setRepeatAlarm(getApplicationContext(),mCalendar, mReceivedID, mRepeatTime);
					finish();
				}
			}else{//repeat false
				Log.i("LOG_TAG","mRepeat: "+mRepeat);
				if (mRepeatBefore.equals("true")) {
					Log.i("LOG_TAG","mRepeatBefore: "+mRepeatBefore);
					updateDB();
					Log.i("LOG_TAG","updated ID: "+ mReceivedID);
					mCalendarBefore =beforeTimeAlarmSet(mCalendar, Integer.parseInt((mRepeatBeforeCnt)), mRepeatBeforeTypeId);
					new AlarmReceiver().setBeforeAlarm(getApplicationContext(),mCalendarBefore, mReceivedID);
					new AlarmReceiver().setAlarm(getApplicationContext(),mCalendar, mReceivedID);
					finish();
				}else{//before false
					Log.i("LOG_TAG","mRepeatBefore: "+mRepeatBefore);
					updateDB();
					Log.i("LOG_TAG","updated ID: "+ mReceivedID);
					new AlarmReceiver().setAlarm(getApplicationContext(),mCalendar, mReceivedID);
					finish();
				}//mRepeatBefore
			}//mRepeat
		}else{//Active false
			if (mRepeat.equals("true")) {
				Log.i("LOG_TAG","mRepeat: "+mRepeat);
				if (mRepeatBefore.equals("true")) {
					Log.i("LOG_TAG","mRepeatBefore: "+mRepeatBefore);
					if (mBeforeMoreRepeat) {
						Log.i("LOG_TAG","mBeforeMoreRepeat: "+mBeforeMoreRepeat);
						Toast.makeText(getApplication(), getResources().getString(R.string.addcorrectbefore), Toast.LENGTH_LONG).show();
						return;
					}else{
						Log.i("LOG_TAG","mBeforeMoreRepeat: "+mBeforeMoreRepeat);
						updateDB();
						Log.i("LOG_TAG","updated ID: "+ mReceivedID);
						finish();
					}
				} else {
					Log.i("LOG_TAG","mRepeatBefore: "+mRepeatBefore);
					updateDB();
					Log.i("LOG_TAG", "updated ID: " + ID);
					finish();
				}
			} else {
				Log.i("LOG_TAG","mRepeat: "+mRepeat);
				updateDB();
				Log.i("LOG_TAG", "updated ID: " + ID);
				finish();
			}

		}
	}
	
	

	private void saveState() {
		Log.i("LOG_TAG","-------------saveState---------");
//		Log.i("LOG_TAG","createID: "+createID());
//		Log.i("LOG_TAG","createID2: "+createID());
		if (checkRepeat.isChecked() == true) {
			mRepeat = "true";
		} else {
			mRepeat = "false";
		}
		if (checkRepeatBefore.isChecked() == true) {
			mRepeatBefore = "true";
		} else {
			mRepeatBefore = "false";
		}
		if (mSwitchActive.isChecked() == true) {
			mActive = "true";
		} else {
			mActive = "false";
		}
		title = TitleInput.getText().toString();
		date = DateInput.getText().toString();
		time = TimeInput.getText().toString();
		info = InfoInput.getText().toString();

		if (title.equals("")) {
			title = getResources().getString(R.string.notitle);
		}

		if (info.equals("")) {
			info = getResources().getString(R.string.noinfo);
		}

		//=================================Time create==========================================
		Calendar calendar = Calendar.getInstance();
		//if null date=now
		if (date.equals("")) {
			DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
			date = dateFormat.format(calendar.getTime());
		}
		//if null time=now
		if (time.equals("")) {
			DateFormat timeFormat = new SimpleDateFormat("HH:mm");
			time = timeFormat.format(calendar.getTime());
		}
		myTimestamp = date + " " + time;
		Log.i("LOG_TAG", "myTimestamp  : " + myTimestamp);
		
		//---------convert from date and time format to datetime format-----------------
		/*Date DateTime = null;
		try {
			DateTime = datetimeformat.parse(myTimestamp);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
			SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
			String newDateStr = dateFormat.format(DateTime);
			String newTimeStr = timeFormat.format(DateTime);
			Log.i("LOG_TAG", "Date  : " + newDateStr);
			Log.i("LOG_TAG", "Time  : " + newTimeStr);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		SimpleDateFormat curFormater = new SimpleDateFormat("dd MMM yyyy HH:mm");
		Date DateTime = null;
		try {
			DateTime = curFormater.parse(myTimestamp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//-----------------------------------------------------------------------------
		
		mCalendar.setTime(DateTime);
		mDBTimeStamp = DateTime.getTime();
		Log.i("LOG_TAG", "mCalendar: " + String.valueOf(mCalendar.getTime()));
		Log.i("LOG_TAG", "mRowId: " + String.valueOf(mRowId));
		//=============================================================================

		if (mRepeat == "true") {
			mRepeatCnt = repeatInput.getText().toString();
			mRepeatTypeId = (int) repeatType.getSelectedItemId();
			Log.i("LOG_TAG", "mRepeatTypeId: " + String.valueOf(mRepeatTypeId));
			mRepeatTypeText = repeatType.getSelectedItem().toString();
			mRepeatText = getResources().getString(R.string.repeat) + mRepeatCnt + " " + mRepeatTypeText;
			// Check repeat type
			switch (mRepeatTypeId) {
			case 0:
				mRepeatTime = Integer.parseInt((mRepeatCnt)) * milMinute;
				break;
			case 1:
				mRepeatTime = Integer.parseInt((mRepeatCnt)) * milHour;
				break;
			case 2:
				mRepeatTime = Integer.parseInt((mRepeatCnt)) * milDay;
				break;
			case 3:
				mRepeatTime = Integer.parseInt((mRepeatCnt)) * milWeek;
				break;
			case 4:
				mRepeatTime = Integer.parseInt((mRepeatCnt)) * milMonth;
				break;
			}
		} else {
			mRepeatText = getResources().getString(R.string.repeatoff);
			mRepeatCnt = "";
			mRepeatTypeText = "";
		}

		if (mRepeatBefore == "true") {
			mRepeatBeforeCnt = beforeInput.getText().toString();
			mRepeatBeforeTypeId = (int) repeatBeforeType.getSelectedItemId();
			Log.i("LOG_TAG", "mRepeatBeforeTypeId: " + String.valueOf(mRepeatBeforeTypeId));
			mRepeatBeforeTypeText = repeatBeforeType.getSelectedItem().toString();
			mRepeatBeforeText = getResources().getString(R.string.before) + mRepeatBeforeCnt + " " + mRepeatBeforeTypeText;
			// Check repeat type
			switch (mRepeatBeforeTypeId) {
			case 0:
				mRepeatBeforeTime = Integer.parseInt((mRepeatBeforeCnt)) * milMinute;
				break;
			case 1:
				mRepeatBeforeTime = Integer.parseInt((mRepeatBeforeCnt)) * milHour;
				break;
			case 2:
				mRepeatBeforeTime = Integer.parseInt((mRepeatBeforeCnt)) * milDay;
				break;
			case 3:
				mRepeatBeforeTime = Integer.parseInt((mRepeatBeforeCnt)) * milWeek;
				break;
			case 4:
				mRepeatBeforeTime = Integer.parseInt((mRepeatBeforeCnt)) * milMonth;
				break;
			}
		} else {
			mRepeatBeforeText = getResources().getString(R.string.beforeoff);
			mRepeatBeforeCnt = "";
			mRepeatBeforeTypeText = "";
		}
		mBeforeMoreRepeat=mRepeatBeforeTime>=mRepeatTime;//before time must be less than repeat time
		
		Log.i("LOG_TAG","mRowId: "+mRowId);
		Log.i("LOG_TAG","mActive: "+mActive);
		Log.i("LOG_TAG","mRepeat: "+mRepeat);
		Log.i("LOG_TAG","mRepeatBefore: "+mRepeatBefore);
		Log.i("LOG_TAG","mRepeatTime: "+mRepeatTime);
		Log.i("LOG_TAG","mRepeatBeforeTime: "+mRepeatBeforeTime);
		Log.i("LOG_TAG","mBeforeMoreRepeat: "+String.valueOf(mBeforeMoreRepeat));
		
		//Alarm logic
		if (mRowId == 0) {//insert
			insertFieldsToDB();
		} else {// mRowId!=0->update
			mReceivedID = (int) (long) mRowId;
			new AlarmReceiver().cancelAlarm(getApplicationContext(),mReceivedID);
			updateFieldsInDB();
		}//row=end
		Log.i("LOG_TAG","-------------saveState end---------");
		
	}//-------------saveState end---------
	// ====================Custom function end======================================
}// ===================Activity end==================================
