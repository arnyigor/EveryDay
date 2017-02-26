/*
 * CustomSortActivity.java
 * 
 * Copyright 2012 Jonathan Hasenzahl, James Celona, Dhimitraq Jorgji
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.android.everyday.views.activities;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.everyday.R;
import com.android.everyday.adapter.ComparatorListAdapter;
import com.android.everyday.database.TasksDataSource;
import com.android.everyday.models.Comparator;


/**
 * 
 * @author Jonathan Hasenzahl
 */
public final class CustomSortActivity extends AppCompatActivity {

	/**************************************************************************
	 * Static fields and methods                                              *
	 **************************************************************************/

	/**************************************************************************
	 * Private fields                                                         *
	 **************************************************************************/

	private TasksDataSource data_source;
	private static ComparatorListAdapter adapter;
	private Toolbar toolbar;
	private ActionBar action_bar;
	private ListView custom_sort_list;

	/**************************************************************************
	 * Class methods                                                          *
	 **************************************************************************/

	/**************************************************************************
	 * Overridden parent methods                                              *
	 **************************************************************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Assign the layout to this activity
		setContentView(R.layout.activity_custom_sort);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		action_bar = getSupportActionBar();
		if (action_bar != null) {
			action_bar.setHomeButtonEnabled(true);
			action_bar.setDisplayHomeAsUpEnabled(true);
		}
		custom_sort_list = (ListView) findViewById(R.id.custom_sort_list);
		// Open the database
		data_source = TasksDataSource.getInstance(this);

		// Create an adapter for the task list
		adapter = new ComparatorListAdapter(this, data_source.getComparators());
		custom_sort_list.setAdapter(adapter);

		custom_sort_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Comparator comparator = adapter.getItem(i);
				if (comparator !=null){
					comparator.toggleEnabled();
					adapter.notifyDataSetChanged();
					data_source.updateComparator(comparator);
				}
			}
		});
	}

	@Override
	public void onStop() {
		// Update homescreen widget (after change has been saved to DB)
//		TaskButlerWidgetProvider.updateWidget(this);
		
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_custom_sort, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
			
		case R.id.menu_custom_sort_help:
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setTitle(R.string.dialog_sorting_title);
    		builder.setIcon(R.drawable.ic_help);
    		builder.setMessage(R.string.dialog_sorting_help);
    		builder.setCancelable(true);
    		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
				
			});
    		builder.create().show();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}