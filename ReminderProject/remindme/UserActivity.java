package com.appsrox.remindme;

import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.appsrox.remindme.model.Alarm;
import com.appsrox.remindme.model.AlarmMsg;

public class UserActivity extends Activity {
	
//	private static final String TAG = "UserActivity";
	
	private TypedArray icons;
	
	private TimePicker picker;
	private TextView tv;
	private TextView timeText;
	
	private long alarmMsgId, alarmId;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user);
        
        icons = getResources().obtainTypedArray(R.array.icon_arr);
        
        tv = (TextView) findViewById(R.id.textView1);
        timeText = (TextView) findViewById(R.id.time_tv);
        
        picker = (TimePicker) findViewById(R.id.timePicker1);
        picker.setIs24HourView(true);
        
        int hr = RemindMe.getSnoozeTime() / 60;
        int min = RemindMe.getSnoozeTime() - hr*60;
        
        picker.setCurrentHour(hr);
        picker.setCurrentMinute(min);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
	}	

	@Override
	protected void onResume() {
		super.onResume();
		
        Intent intent = getIntent();
		alarmMsgId = intent.getLongExtra(AlarmMsg.COL_ID, -1);
		alarmId = intent.getLongExtra(AlarmMsg.COL_ALARMID, -1);
		
		Alarm alarm = new Alarm(alarmId);
		alarm.load(RemindMe.db);
        tv.setText(alarm.getName());
        
		String _tagName = alarm.getTag();
		Alarm.TAG _tag = !TextUtils.isEmpty(_tagName) ? Alarm.TAG.valueOf(_tagName) : null;
		if (_tag!=null) {
			tv.setCompoundDrawablesWithIntrinsicBounds(icons.getResourceId(_tag.ordinal()+1, -1), 0, 0, 0);
		}
        
        AlarmMsg alarmMsg = new AlarmMsg(alarmMsgId);
        alarmMsg.load(RemindMe.db);
        Date dt = new Date(alarmMsg.getDateTime());
        timeText.setText(Util.getActualTime(dt.getHours(), dt.getMinutes()));
        
        doCancel(intent);		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("hour", picker.getCurrentHour());
	}	

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		picker.setCurrentHour(state.getInt("hour"));
	}    
    
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			finish();
			break;
			
		case R.id.button2:
			long when = System.currentTimeMillis() + (picker.getCurrentHour()*60 + picker.getCurrentMinute())*60*1000;
			
			AlarmMsg alarmMsg = new AlarmMsg(alarmMsgId);
			alarmMsg.setStatus(AlarmMsg.ACTIVE);//TODO DEFERRED
			alarmMsg.setDateTime(when);
			alarmMsg.persist(RemindMe.db);
			
			Intent service = new Intent(this, AlarmService.class);
			service.putExtra(AlarmMsg.COL_ID, String.valueOf(alarmMsgId));
			service.setAction(AlarmService.CREATE);
			startService(service);			
			
			finish();
			break;
		}
	}
	
	private void doCancel(Intent intent) {
		Intent i = new Intent(this, AlarmReceiver.class);
		i.putExtra(AlarmMsg.COL_ID, intent.getLongExtra(AlarmMsg.COL_ID, -1));
		i.putExtra(AlarmMsg.COL_ALARMID, intent.getLongExtra(AlarmMsg.COL_ALARMID, -1));
		
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);		
		
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
	}

}
