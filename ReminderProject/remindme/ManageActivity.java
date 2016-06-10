package com.appsrox.remindme;

import java.text.SimpleDateFormat;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appsrox.remindme.model.Alarm;
import com.appsrox.remindme.model.AlarmMsg;

public class ManageActivity extends ActionBarActivity {
	
	private ListView lv;
	private TextView empty;
	private Button activeBtn;
	private Button expiredBtn;
	
	private SQLiteDatabase db;
	private SimpleCursorAdapter adapter;
	
	private boolean isExpired;
	
	private long selectedId;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage);
		
		sdf.applyPattern(RemindMe.getDateFormat());
		
		db = RemindMe.db;
		lv = (ListView) findViewById(android.R.id.list);
		empty = (TextView) findViewById(android.R.id.empty);
		activeBtn = (Button) findViewById(R.id.active_btn);
		expiredBtn = (Button) findViewById(R.id.expired_btn);
		
		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.header));
		
		adapter = new SimpleCursorAdapter(this, 
				R.layout.row_manage, 
				createCursor(), 
				new String[]{Alarm.COL_NAME, Alarm.COL_FROMDATE, Alarm.COL_TODATE, "count"}, 
				new int[]{R.id.name_tv, R.id.from_tv, R.id.to_tv, R.id.pending_tv}, 
				0);
		
		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				switch(view.getId()) {
				case R.id.from_tv:
					String from = cursor.getString(columnIndex);
					((TextView)view).setText(Util.fromPersistentDate(from, sdf));
					return true;				
				
				case R.id.to_tv:
					String to = cursor.getString(columnIndex);
					((TextView)view).setText(TextUtils.isEmpty(to) ? null : " to " + Util.fromPersistentDate(to, sdf));
					return true;
					
				case R.id.name_tv:
					long id = cursor.getLong(cursor.getColumnIndex(Alarm.COL_ID));
					if (id == selectedId) 
						((CheckedTextView)view).setChecked(true);
					else 
						((CheckedTextView)view).setChecked(false);
					
					if (isExpired) 
						((TextView)view).setTextColor(Color.parseColor("#555555"));
					else 
						((TextView)view).setTextColor(Color.parseColor("#587498"));
					break;					
				}

				return false;
			}
		});
		
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedId = id;
				lv.invalidateViews();
			}
		});
	}
	
	private Cursor createCursor() {
		Cursor c = RemindMe.dbHelper.listAlarms(db, !isExpired);
		startManagingCursor(c);
		
		if(c.getCount() == 0) {
			lv.setVisibility(View.GONE);
			empty.setVisibility(View.VISIBLE);
		} else {
			lv.setVisibility(View.VISIBLE);
			empty.setVisibility(View.GONE);
		}
		
		return c;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		adapter.getCursor().requery();
    	adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		long alarmId;
		Intent intent;
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
			
		case R.id.action_add:
			alarmId = getCheckedId();
			if (alarmId != -1) {
				intent = new Intent(this, AddAlarmActivity.class);
				intent.putExtra(AddAlarmActivity.MODE, AddAlarmActivity.MODE_ADD);
				intent.putExtra(Alarm.COL_ID, alarmId);
				startActivity(intent);
			}
			return true;
			
		case R.id.action_edit:
			alarmId = getCheckedId();
			if (alarmId != -1) {
				intent = new Intent(this, AddAlarmActivity.class);
				intent.putExtra(AddAlarmActivity.MODE, AddAlarmActivity.MODE_EDIT);
				intent.putExtra(Alarm.COL_ID, alarmId);
				startActivity(intent);				
			}
			return true;
			
		case R.id.action_delete:
			alarmId = getCheckedId();
			if (alarmId != -1) {
				RemindMe.dbHelper.cancelNotification(db, alarmId, true);
				
				Intent cancelRepeating = new Intent(ManageActivity.this, AlarmService.class);
				cancelRepeating.putExtra(AlarmMsg.COL_ALARMID, String.valueOf(alarmId));
				cancelRepeating.setAction(AlarmService.CANCEL);
				startService(cancelRepeating);
				
		    	adapter.getCursor().requery();
		    	adapter.notifyDataSetChanged();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private long getCheckedId() {
		if (selectedId > 0) {
			return selectedId;
		}
		
		Toast.makeText(this, "No item selected!", Toast.LENGTH_SHORT).show();
		return -1;
	}
	
    private void changeAdapterCursor() {
    	stopManagingCursor(adapter.getCursor());
    	adapter.changeCursor(createCursor());
    }
	
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.active_btn:
			if (!isExpired) return;
			
			activeBtn.setBackgroundResource(0);
			expiredBtn.setBackgroundResource(R.drawable.shade);
			
			isExpired = false;
			changeAdapterCursor();
			break;
			
		case R.id.expired_btn:
			if (isExpired) return;
			
			expiredBtn.setBackgroundResource(0);
			activeBtn.setBackgroundResource(R.drawable.shade);
			
			isExpired = true;
			changeAdapterCursor();
			break;
		}
	}

}
