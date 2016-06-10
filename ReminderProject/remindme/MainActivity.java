package com.appsrox.remindme;

import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import com.appsrox.common.Disclaimer;
import com.appsrox.remindme.model.Alarm;
import com.appsrox.remindme.model.AlarmMsg;
import com.appsrox.remindme.ui.NavAdapter;
import com.appsrox.remindme.ui.OnSwipeTouchListener;

public class MainActivity extends ListActivity {
	
//	private static final String TAG = "MainActivity";
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] navArr;
    private TypedArray icons;
    private int drawerPos;
	
	private TextView headingText;
	private TextView rangeText;
//	private ViewSwitcher vs;
	
	private SQLiteDatabase db;
	private Typeface font;
	private AlertDialog disclaimer;
	
	public final Calendar cal = Calendar.getInstance();
	public final Date dt = new Date();
	private String[] monthArr;
	
	private AlarmMsg alarmMsg = new AlarmMsg();
	private Alarm.TAG tag;
	
	private Resources res;
	private OnSwipeTouchListener swipeListener;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        findViews();
        db = RemindMe.db;
        res = getResources();
        font = Typeface.createFromAsset(getAssets(), "fonts/FjallaOne-Regular.ttf");
        headingText.setTypeface(font);
        monthArr = res.getStringArray(R.array.spinner3_arr);
        icons = res.obtainTypedArray(R.array.icon_arr);
        
        //navigation drawer
        navArr = res.getStringArray(R.array.nav_arr);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new NavAdapter(this, R.layout.drawer_list_item, navArr));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				drawerPos = position;
				resetDrawer(drawerPos);
				changeAdapterCursor();
			}
		});
//        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {});
        
        resetCal();
		
		registerForContextMenu(getListView());
		
		swipeListener = new OnSwipeTouchListener(this) {

			@Override
			public void onSwipeLeft() {
				move(nextOccurrence());
				rangeText.setText(getRangeStr());
				changeAdapterCursor();
			}

			@Override
			public void onSwipeRight() {
				move(prevOccurrence());
				rangeText.setText(getRangeStr());
				changeAdapterCursor();
			}
			
		};
		mDrawerLayout.setOnTouchListener(swipeListener);
		getListView().setOnTouchListener(swipeListener);		
		
		disclaimer = Disclaimer.show(this);
    }

	private void resetDrawer(int position) {
        mDrawerList.setItemChecked(position, true);
        headingText.setText(position==0 ? getString(R.string.app_name) : navArr[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
        tag = position!=0 ? Alarm.TAG.values()[position-1] : null;
    }
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("cal", cal.getTimeInMillis());
		outState.putInt("drawerPos", drawerPos);
	}	

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		cal.setTimeInMillis(state.getLong("cal"));
		drawerPos = state.getInt("drawerPos");
	}

	private void findViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		headingText = (TextView) findViewById(R.id.heading_tv);
		rangeText = (TextView) findViewById(R.id.range_tv);
//		vs = (ViewSwitcher) findViewById(R.id.view_switcher);
	}
	
	private String getRangeStr() {
		int date = cal.get(Calendar.DATE);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		dt.setTime(System.currentTimeMillis());
		
		switch(RemindMe.getDateRange()) {
		case 0: // Daily
			if (date==dt.getDate() && month==dt.getMonth() && year==dt.getYear()+1900) return "Today";
			else return date+" "+monthArr[month+1];
			
		case 1: // Weekly
			return date+" "+monthArr[month+1] + move(+6, 0) + " - " + cal.get(Calendar.DATE)+" "+monthArr[cal.get(Calendar.MONTH)+1] + move(-6, 0);
			
		case 2: // Monthly
			return monthArr[month+1]+" "+year;
			
		case 3: // Yearly
			return year+"";
		}
		return null;
	}
	
	private Cursor createCursor() {
		Cursor c = RemindMe.dbHelper.listNotifications(db, cal.getTimeInMillis()+move(+1), cal.getTimeInMillis()+move(-1), tag!=null ? tag.name() : null);
		startManagingCursor(c);
		return c;
	}
	
	private int nextOccurrence() {
		int step = +1;
		Cursor c = RemindMe.dbHelper.listNotifications(db, move(+1)+cal.getTimeInMillis(), null, tag!=null ? tag.name() : null);
		move(-1);
		if (c != null) {
			if (c.moveToFirst()) {
				long time = c.getLong(c.getColumnIndex(AlarmMsg.COL_DATETIME));
				step = calculateStep(time);
			}
			c.close();
		}
		return step;
	}
	
	private int prevOccurrence() {
		int step = -1;
		Cursor c = RemindMe.dbHelper.listNotifications(db, null, String.valueOf(cal.getTimeInMillis()), tag!=null ? tag.name() : null);
		if (c != null) {
			if (c.moveToLast()) {
				long time = c.getLong(c.getColumnIndex(AlarmMsg.COL_DATETIME));
				step = calculateStep(time)-1;
			}
			c.close();
		}
		return step;
	}	
    
    @Override
	protected void onResume() {
		super.onResume();
		
		//help screen
		if ((disclaimer==null || !disclaimer.isShowing()) && RemindMe.isShowcase()) {
			startActivity(new Intent(getApplicationContext(), ShowcaseActivity.class));
		}		
		
		resetDrawer(drawerPos);

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				this, 
				R.layout.row_main, 
				createCursor(), 
				new String[]{Alarm.COL_NAME, AlarmMsg.COL_DATETIME, AlarmMsg.COL_DATETIME, AlarmMsg.COL_DATETIME, AlarmMsg.COL_DATETIME, Alarm.COL_TAG}, 
				new int[]{R.id.msg_tv, R.id.year_tv, R.id.month_tv, R.id.date_tv, R.id.time_tv, R.id.icon_iv});
		
		adapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if (view.getId() == R.id.msg_tv) return false;
				
				if (view.getId() == R.id.icon_iv) {
					ImageView iv = (ImageView)view;
					String _tagName = cursor.getString(columnIndex);
					Alarm.TAG _tag = !TextUtils.isEmpty(_tagName) ? Alarm.TAG.valueOf(_tagName) : null;
					if (_tag!=null) {
						iv.setVisibility(View.VISIBLE);
						iv.setImageResource(icons.getResourceId(_tag.ordinal()+1, -1));
					} else {
						iv.setVisibility(View.GONE);
					}
					return true;
				}

				TextView tv = (TextView)view;
				long time = cursor.getLong(columnIndex);
				dt.setTime(time);
				switch(view.getId()) {
				case R.id.year_tv:
					tv.setText(String.valueOf(dt.getYear()+1900));
					break;
				case R.id.month_tv:
					tv.setText(monthArr[dt.getMonth()+1]);
					break;
				case R.id.date_tv:
					tv.setText(String.valueOf(dt.getDate()));
					break;
				case R.id.time_tv:
					long now = System.currentTimeMillis();
					String txt = RemindMe.showRemainingTime() ? Util.getRemainingTime(time, now) : Util.getActualTime(dt.getHours(), dt.getMinutes());
					if (TextUtils.isEmpty(txt)) txt = Util.getActualTime(dt.getHours(), dt.getMinutes());
					tv.setText(txt);
					
					RelativeLayout parent = (RelativeLayout)tv.getParent();
					TextView tvY = (TextView) parent.findViewById(R.id.year_tv);
					TextView tvM = (TextView) parent.findViewById(R.id.month_tv);
					TextView tvD = (TextView) parent.findViewById(R.id.date_tv);
					TextView tv2 = (TextView) parent.findViewById(R.id.msg_tv);
					ImageView iv= (ImageView) parent.findViewById(R.id.icon_iv);
					if (time < now) {
						tvY.setTextColor(Color.parseColor("#777777"));
						tvM.setTextColor(Color.parseColor("#777777"));
						tvD.setTextColor(Color.parseColor("#777777"));
						tv2.setTextColor(Color.parseColor("#555555"));
						iv.setColorFilter(Color.parseColor("#cc999999"));
						tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.clock, 0, 0, 0);
					} else {
						tvY.setTextColor(res.getColor(R.color.bkg));
						tvM.setTextColor(res.getColor(R.color.bkg));
						tvD.setTextColor(res.getColor(R.color.bkg));
						tv2.setTextColor(Color.parseColor("#587498"));
						iv.setColorFilter(null);
						tv.setCompoundDrawablesWithIntrinsicBounds(RemindMe.showRemainingTime() ? R.drawable.hourglass : R.drawable.clock, 0, 0, 0);
					}
					break;
				}
				return true;
			}
		});
		setListAdapter(adapter);
		
		rangeText.setText(getRangeStr());
	}
    
	private String move(int... args) {
		int step = (args!=null && args.length>0) ? args[0] : 0;
		int r = (args!=null && args.length>1) ? args[1] : RemindMe.getDateRange();
		switch(r) {
		case 0:
			cal.add(Calendar.DATE, 1*step);
			break;		
		case 1:
			cal.add(Calendar.DATE, 7*step);
			break;
		case 2:
			cal.add(Calendar.MONTH, 1*step);
			break;
		case 3:
			cal.add(Calendar.YEAR, 1*step);
			break;			
		}
		return "";
	}
	
	private int calculateStep(long time) {
		switch(RemindMe.getDateRange()) {
		case 0:
			return (int) ((time-cal.getTimeInMillis()) / Util.DAY);
		case 1:
			return (int) ((time-cal.getTimeInMillis()) / Util.WEEK);
		case 2:
			return (int) ((time-cal.getTimeInMillis()) / Util.MONTH);
		case 3:
			return (int) ((time-cal.getTimeInMillis()) / Util.YEAR);
		}
		return 0;
	}
    
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageButton1:
			startActivity(new Intent(this, SettingsActivity.class));
			break;		
		case R.id.imageButton2:
			startActivity(new Intent(this, AddAlarmActivity.class));
			break;
/*		case R.id.imageButton3:
			move(-1);
			rangeText.setText(getRangeStr());
			changeAdapterCursor();
			break;
		case R.id.imageButton4:
			move(+1);
			rangeText.setText(getRangeStr());
			changeAdapterCursor();
			break;*/
		case R.id.imageButton5:
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
			break;
		case R.id.imageButton6:
			startActivity(new Intent(getApplicationContext(), ManageActivity.class));
			break;			
/*		case R.id.toggleButton1:
			vs.showNext();
			break;*/
		case R.id.range_tv:
			showDialog(R.id.range_tv);
			break;			
		}
	}
	
    private void changeAdapterCursor() {
    	SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
    	stopManagingCursor(adapter.getCursor());
    	adapter.changeCursor(createCursor());
    }	

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == android.R.id.list) {
			getMenuInflater().inflate(R.menu.context_menu, menu);
			menu.setHeaderTitle("Choose an Option");
			menu.setHeaderIcon(R.drawable.ic_dialog_menu_generic);
			
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			alarmMsg.setId(info.id);
			alarmMsg.load(db);
			if (alarmMsg.getDateTime() < System.currentTimeMillis()) 
				menu.removeItem(R.id.menu_edit);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		boolean refresh = false;
		
		switch (item.getItemId()) {
		case R.id.menu_edit:
			alarmMsg.setId(info.id);
			alarmMsg.load(db);
			
			Intent intent = new Intent(this, AddAlarmActivity.class);
			intent.putExtra(AddAlarmActivity.MODE, AddAlarmActivity.MODE_EDIT);
			intent.putExtra(Alarm.COL_ID, alarmMsg.getAlarmId());
			startActivity(intent);
			break;
			
		case R.id.menu_delete:
			RemindMe.dbHelper.cancelNotification(db, info.id, false);
			refresh = true;
			
			Intent cancelThis = new Intent(this, AlarmService.class);
			cancelThis.putExtra(AlarmMsg.COL_ID, String.valueOf(info.id));
			cancelThis.setAction(AlarmService.CANCEL);
			startService(cancelThis);
			break;
			
		case R.id.menu_delete_repeating:
			alarmMsg.setId(info.id);
			alarmMsg.load(db);
			RemindMe.dbHelper.cancelNotification(db, alarmMsg.getAlarmId(), true);
			refresh = true;
			
			Intent cancelRepeating = new Intent(this, AlarmService.class);
			cancelRepeating.putExtra(AlarmMsg.COL_ALARMID, String.valueOf(alarmMsg.getAlarmId()));
			cancelRepeating.setAction(AlarmService.CANCEL);
			startService(cancelRepeating);
			break;
		}
		
		if (refresh) {
			SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
	    	adapter.getCursor().requery();
	    	adapter.notifyDataSetChanged();
		}
		
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		openContextMenu(v);
	}
	
	private void resetCal() {
		cal.setTimeInMillis(System.currentTimeMillis());
		
        int r = RemindMe.getDateRange();
		switch(r) {
		case 3: // Yearly
			cal.set(Calendar.MONTH, 0);
			
		case 2: // Monthly
			if (r!=1) cal.set(Calendar.DATE, 1);
			
		case 1: // Weekly
			if (r==1) cal.add(Calendar.DATE, 1-cal.get(Calendar.DAY_OF_WEEK));
			
		case 0: // Daily
	        cal.set(Calendar.HOUR_OF_DAY, 0);
	    	cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {			
		case R.id.range_tv:
			return new AlertDialog.Builder(this)
			   .setItems(res.getStringArray(R.array.range_option_arr), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						RemindMe.setDateRange(which);
						resetCal();
						
						rangeText.setText(getRangeStr());
						changeAdapterCursor();						
					}
				})
		       .setCancelable(true)
		       .create();
		}
		return super.onCreateDialog(id);
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
        if (getListAdapter().isEmpty()) {
        	menu.findItem(R.id.menu_delete_all).setEnabled(false);
        } else {
        	menu.findItem(R.id.menu_delete_all).setEnabled(true);
        }
		return true;
	}	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_delete_all:
			String startTime = cal.getTimeInMillis()+move(+1);
			String endTime = cal.getTimeInMillis()+move(-1);
			RemindMe.dbHelper.cancelNotification(db, startTime, endTime);
			
			Intent cancelAll = new Intent(this, AlarmService.class);
			cancelAll.putExtra(Alarm.COL_FROMDATE, startTime);
			cancelAll.putExtra(Alarm.COL_TODATE, endTime);
			cancelAll.setAction(AlarmService.CANCEL);
			startService(cancelAll);
			
			SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
	    	adapter.getCursor().requery();
	    	adapter.notifyDataSetChanged();			
			return true;
		}		
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		if (disclaimer != null) 
			disclaimer.dismiss();
		super.onDestroy();
	}
	
}