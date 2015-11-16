package com.android.everyday;//Package

import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("InflateParams") public class FragmentHeader extends Fragment {
	
	DB db;
	Cursor cursor;
	int items_count;
	Context context;

	@SuppressLint("SimpleDateFormat")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_header, null);

		TextView textToday = (TextView) v.findViewById(R.id.textToday);
		//TextView textTotalEvents = (TextView) v.findViewById(R.id.textTotalEvents);
		SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy hh:mm:ss");
		Date now = new Date();
		String Today = format.format(now);
		textToday.setText(getResources().getString(R.string.time)+Today);
		
//		db = new DB(context);
//		db.open();
//		cursor = db.getAllItems();
//		items_count=cursor.getCount();
//		textTotalEvents.setText(getResources().getString(R.string.todayevents)+String.valueOf(items_count));
//		
		
		return v;
	}
}