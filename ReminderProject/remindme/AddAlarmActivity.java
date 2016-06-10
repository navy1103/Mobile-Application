package com.appsrox.remindme;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher;

import com.appsrox.remindme.model.Alarm;
import com.appsrox.remindme.model.AlarmMsg;
import com.appsrox.remindme.model.AlarmTime;
import com.appsrox.remindme.model.DbHelper;
import com.appsrox.remindme.ui.TagAdapter;

public class AddAlarmActivity extends Activity {
	
//	private static final String TAG = "AddAlarmActivity";
	
	public static final String MODE = "mode";
	public static final int MODE_ADD = 101; 
	public static final int MODE_EDIT = 102;
	
	private static final int RINGTONE_PICKER_REQUEST = 1001;
	
	private EditText msgEdit;
	private DatePicker datePicker;
	private TimePicker timePicker;
	private TextView fromdateText;
	private TextView todateText;
	private TextView attimeText;
	
	private ImageButton vibrateIb;
	private ImageButton soundIb;
	private ImageButton insistentIb;
	private ImageButton ringtoneIb;
	private Spinner tagSpn;
	private ImageButton tagIb;
	
	private ToggleButton tb;
	private ViewSwitcher vs;
	private RadioGroup rg;
	private RelativeLayout rl3;
	private RelativeLayout rl4;
	
	private Spinner spinner1;
	private Spinner spinner2;
	private Spinner spinner3;
	
	private EditText minsEdit;
	private EditText hoursEdit;
	private EditText daysEdit;
	private EditText monthsEdit;
	private EditText yearsEdit;
	
	private SQLiteDatabase db;
	
	private long alarmId, alarmTimeId;
	private String _fromDate, _toDate, _at, _rule, _interval;
	private boolean isEdit;
	
	private boolean isVibrate;
	private boolean isSound;
	private boolean isInsistent;
	private Uri ringtoneUri;
	private Alarm.TAG tag;
	
	private static final int DIALOG_FROMDATE = 1;
	private static final int DIALOG_TODATE = 2;
	private static final int DIALOG_ATTIME = 3;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat();
	private Handler handler;
	
	private AdapterView.OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        	if (spinner1.getSelectedItemPosition() > 0 && spinner2.getSelectedItemPosition() > 0)
        		spinner1.setSelection(0);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
        }
    };
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("New Reminder");
        setContentView(R.layout.activity_add);
        findViews();
        initViews();
        db = RemindMe.db;
        handler = new Handler();
        
        switch(getIntent().getIntExtra(MODE, -1)) {
        case MODE_EDIT:
        	setTitle("Edit Reminder");
        	isEdit = true;
        	
        case MODE_ADD:
        	alarmId = getIntent().getLongExtra(Alarm.COL_ID, -1); 
        	Alarm alarm = new Alarm(alarmId);
        	alarm.load(db);
        	Cursor atC = AlarmTime.list(db, String.valueOf(alarm.getId()));
        	AlarmTime alarmTime = null;
        	if (atC != null) {
        		if (atC.moveToFirst()) {
        			alarmTimeId = atC.getLong(0);
        			alarmTime = new AlarmTime(alarmTimeId);
        			alarmTime.setAlarmId(alarmId);
        			alarmTime.setAt(atC.getString(1));
        		}
        		atC.close();
        	}
        	if (alarmTime != null) {
        		populate(alarm, alarmTime);
        	}
        	break;
        	
    	default:
            isVibrate = RemindMe.isVibrate();
            isSound = true;
            isInsistent = false;
            ringtoneUri = !TextUtils.isEmpty(RemindMe.getRingtone()) ? Uri.parse(RemindMe.getRingtone()) : null;
            tag = null;
            break;
        }
    }
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("vs", vs.getDisplayedChild());
		outState.putInt("date", datePicker.getDayOfMonth());
		outState.putInt("month", datePicker.getMonth());
		outState.putInt("year", datePicker.getYear());
		outState.putInt("hour", timePicker.getCurrentHour());
		outState.putInt("min", timePicker.getCurrentMinute());
		outState.putCharSequence("fromdate", fromdateText.getText());
		outState.putCharSequence("todate", todateText.getText());
		outState.putCharSequence("attime", attimeText.getText());
		outState.putParcelable("ringtoneUri", ringtoneUri);
		outState.putBoolean("isInsistent", isInsistent);
		outState.putBoolean("isVibrate", isVibrate);
		outState.putSerializable("tag", tag);
		outState.putBoolean("isSound", isSound);
	}	

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		vs.setDisplayedChild(state.getInt("vs"));
		datePicker.updateDate(state.getInt("year"), state.getInt("month"), state.getInt("date"));
		timePicker.setCurrentHour(state.getInt("hour"));
		timePicker.setCurrentMinute(state.getInt("min"));
		fromdateText.setText(state.getCharSequence("fromdate"));
		todateText.setText(state.getCharSequence("todate"));
		attimeText.setText(state.getCharSequence("attime"));
		ringtoneUri = (Uri) state.getParcelable("ringtoneUri");
		isInsistent = state.getBoolean("isInsistent");
		isVibrate = state.getBoolean("isVibrate");
		tag = (Alarm.TAG) state.getSerializable("tag");
		isSound = state.getBoolean("isSound");
	}    
        
	@Override
	protected void onResume() {
		super.onResume();
		sdf.applyPattern(RemindMe.getDateFormat());
		
		ringtoneIb.setColorFilter(ringtoneUri!=null ? Color.WHITE : Color.BLACK);
		insistentIb.setColorFilter(isInsistent ? Color.WHITE : Color.BLACK);
		vibrateIb.setColorFilter(isVibrate ? Color.WHITE : Color.BLACK);
		
		tagSpn.setSelection(tag==null ? 0 : tag.ordinal()+1);
	}

	private void findViews() {
		msgEdit = (EditText) findViewById(R.id.msg_et);
		vibrateIb = (ImageButton) findViewById(R.id.vibrate_ib);
		soundIb = (ImageButton) findViewById(R.id.sound_ib);
		insistentIb = (ImageButton) findViewById(R.id.insistent_ib);
		ringtoneIb = (ImageButton) findViewById(R.id.ringtone_ib);
		tagSpn = (Spinner) findViewById(R.id.tag_spn);
		tagIb = (ImageButton) findViewById(R.id.tag_ib);
		datePicker = (DatePicker) findViewById(R.id.datePicker);
		timePicker = (TimePicker) findViewById(R.id.timePicker);
		fromdateText = (TextView) findViewById(R.id.fromdate_tv);
		todateText = (TextView) findViewById(R.id.todate_tv);
		attimeText = (TextView) findViewById(R.id.attime_tv);
		tb = (ToggleButton) findViewById(R.id.toggleButton1);
        vs = (ViewSwitcher) findViewById(R.id.view_switcher);
        rg = (RadioGroup) findViewById(R.id.radioGroup);
        rl3 = (RelativeLayout) findViewById(R.id.relativeLayout3);
        rl4 = (RelativeLayout) findViewById(R.id.relativeLayout4);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner3 = (Spinner) findViewById(R.id.spinner3);
        
        minsEdit = (EditText) findViewById(R.id.mins_et);
        hoursEdit = (EditText) findViewById(R.id.hours_et);
        daysEdit = (EditText) findViewById(R.id.days_et);
        monthsEdit = (EditText) findViewById(R.id.months_et);
        yearsEdit = (EditText) findViewById(R.id.years_et);
	}
	
	private void initViews() {
		Calendar cal = Calendar.getInstance();
		
		datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		
		timePicker.setIs24HourView(RemindMe.is24Hours());
		timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));

        rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId) {
				case R.id.radio0:
					rl3.setVisibility(View.VISIBLE);
					rl4.setVisibility(View.GONE);
					break;
				case R.id.radio1:
					rl4.setVisibility(View.VISIBLE);
					rl3.setVisibility(View.GONE);					
					break;					
				}
			}
		});
        
        spinner1.setOnItemSelectedListener(spinnerListener);
        spinner2.setOnItemSelectedListener(spinnerListener);
        
        vibrateIb.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isVibrate = !isVibrate;
				vibrateIb.setColorFilter(isVibrate ? Color.WHITE : Color.BLACK);
				Toast.makeText(AddAlarmActivity.this, "Vibration "+(isVibrate ? "ON" : "OFF"), Toast.LENGTH_SHORT).show();
			}
		});
        
        soundIb.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isSound = !isSound;
				soundIb.setColorFilter(isSound ? Color.WHITE : Color.BLACK);
			}
		});
        
        insistentIb.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isInsistent = !isInsistent;
				insistentIb.setColorFilter(isInsistent ? Color.WHITE : Color.BLACK);
				Toast.makeText(AddAlarmActivity.this, "Insistent "+(isInsistent ? "ON" : "OFF"), Toast.LENGTH_SHORT).show();
			}
		});
        
        ringtoneIb.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
			    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
			    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Choose Ringtone");
			    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtoneUri);
			    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
			    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);

			    startActivityForResult(intent, RINGTONE_PICKER_REQUEST);
			}
		});
        
        tagIb.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tagSpn.performClick();
			}
		});
        
        TagAdapter tagAdapter = new TagAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.nav_arr));
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagSpn.setAdapter(tagAdapter);
        
        tagSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				tag = position!=0 ? Alarm.TAG.values()[position-1] : null;
				tagIb.setColorFilter(tag!=null ? Color.WHITE : Color.BLACK);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	
	private boolean validate() {
		if (TextUtils.isEmpty(msgEdit.getText())) {
			msgEdit.requestFocus();
			Toast.makeText(this, "Enter a message", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (vs.getDisplayedChild() == 1) {
			if (TextUtils.isEmpty(fromdateText.getText())) {
				Toast.makeText(this, "Specify from date", Toast.LENGTH_SHORT).show();
				return false;
			}
			if (TextUtils.isEmpty(todateText.getText())) {
				Toast.makeText(this, "Specify to date", Toast.LENGTH_SHORT).show();
				return false;
			}
			try {
				if (sdf.parse(fromdateText.getText().toString()).after(sdf.parse(todateText.getText().toString()))) {
					Toast.makeText(this, "From date is after To date!", Toast.LENGTH_SHORT).show();
					return false;
				}
			} catch (ParseException e) {}			
			if (TextUtils.isEmpty(attimeText.getText())) {
				Toast.makeText(this, "Specify time", Toast.LENGTH_SHORT).show();
				return false;
			}			
		}
		return true;
	}
	
	/* Load */
	private void populate(Alarm alarm, AlarmTime alarmTime) {
		msgEdit.setText(alarm.getName());
		isVibrate = alarm.getVibrate();
		ringtoneUri = TextUtils.isEmpty(alarm.getRingtone()) ? null : Uri.parse(alarm.getRingtone());
		isInsistent = alarm.getInsistent();
		isSound = alarm.getSound();
		tag = TextUtils.isEmpty(alarm.getTag()) ? null : Alarm.TAG.valueOf(alarm.getTag());
		
		String fromDate = alarm.getFromDate();
		String toDate = alarm.getToDate();
		
		if (TextUtils.isEmpty(toDate)) { //one time
			tb.setChecked(false);
			vs.setDisplayedChild(0);
			
			String[] dateTokens = DbHelper.getDateTokens(fromDate);
			datePicker.updateDate(Integer.parseInt(dateTokens[0]), Integer.parseInt(dateTokens[1])-1, Integer.parseInt(dateTokens[2]));
			
			String[] timeTokens = DbHelper.getTimeTokens(alarmTime.getAt());
			timePicker.setCurrentHour(Integer.parseInt(timeTokens[0]));
			timePicker.setCurrentMinute(Integer.parseInt(timeTokens[1]));
			
		} else { //repeating
			tb.setChecked(true);
			vs.setDisplayedChild(1);
			
			fromdateText.setText(Util.fromPersistentDate(fromDate, sdf));
			todateText.setText(Util.fromPersistentDate(toDate, sdf));
			attimeText.setText(Util.fromPersistentTime(alarmTime.getAt()));
			
			if (!TextUtils.isEmpty(alarm.getInterval())) {
				String[] intervalTokens = alarm.getInterval().split(" ");
				minsEdit.setText(intervalTokens[0]);
				hoursEdit.setText(intervalTokens[1]);
				daysEdit.setText(intervalTokens[2]);
				monthsEdit.setText(intervalTokens[3]);
				yearsEdit.setText(intervalTokens[4]);
				
				rg.check(R.id.radio1);
				
			} else if (!TextUtils.isEmpty(alarm.getRule())) {
				String[] ruleTokens = alarm.getRule().split(" ");
				spinner1.setSelection(Integer.parseInt(ruleTokens[0]));
				spinner2.setSelection(Integer.parseInt(ruleTokens[1]));
				spinner3.setSelection(Integer.parseInt(ruleTokens[2]));
				
				rg.check(R.id.radio0);
			}
		}
		
		_fromDate = alarm.getFromDate();
		_toDate = alarm.getToDate();
		_at = alarmTime.getAt();
		_rule = alarm.getRule();
		_interval = alarm.getInterval();
		
	}
	
	/* Save */
	public void create(View v) {
		if (!validate()) return;
		
		Alarm alarm = new Alarm(alarmId);
		alarm.setName(msgEdit.getText().toString());
		alarm.setVibrate(isVibrate);
		alarm.setRingtone(ringtoneUri!=null ? ringtoneUri.toString() : "");
		alarm.setInsistent(isInsistent);
		alarm.setSound(ringtoneUri!=null);//isSound
		alarm.setTag(tag!=null ? tag.name() : "");
		AlarmTime alarmTime = new AlarmTime(alarmTimeId);
		
		switch(vs.getDisplayedChild()) {
		case 0: //one time
			alarm.setFromDate(DbHelper.getDateStr(datePicker.getYear(), datePicker.getMonth()+1, datePicker.getDayOfMonth()));
			alarmId = alarm.persist(db);
			
			alarmTime.setAt(DbHelper.getTimeStr(timePicker.getCurrentHour(), timePicker.getCurrentMinute()));
			alarmTime.setAlarmId(alarmId);
			alarmTime.persist(db);
			break;
			
		case 1: //repeating
			alarm.setFromDate(Util.toPersistentDate(fromdateText.getText().toString(), sdf));
			alarm.setToDate(Util.toPersistentDate(todateText.getText().toString(), sdf));
			switch(rg.getCheckedRadioButtonId()) {
			case R.id.radio0: //rule
				alarm.setRule(Util.concat(spinner1.getSelectedItemPosition(), " ", 
											spinner2.getSelectedItemPosition(), " ", 
											spinner3.getSelectedItemPosition()));
				break;
			case R.id.radio1: //interval
				alarm.setInterval(Util.concat(minsEdit.getText(), " ", 
								hoursEdit.getText(), " ", 
								daysEdit.getText(), " ", 
								monthsEdit.getText(), " ", 
								yearsEdit.getText()));
				break;						
			}					
			alarmId = alarm.persist(db);
			
			alarmTime.setAt(Util.toPersistentTime(attimeText.getText().toString()));
			alarmTime.setAlarmId(alarmId);
			alarmTime.persist(db);					
			break;				
		}
		
		try {
			if (isEdit) {
				if (Util.notEquals(_fromDate, alarm.getFromDate()) ||
						Util.notEquals(_toDate, alarm.getToDate()) ||
						Util.notEquals(_at, alarmTime.getAt()) ||
						Util.notEquals(_rule, alarm.getRule()) ||
						Util.notEquals(_interval, alarm.getInterval())) {
					
					RemindMe.dbHelper.cancelNotification(db, alarmId, String.valueOf(System.currentTimeMillis()));
					
					Intent cancelRepeating = new Intent(this, AlarmService.class);
					cancelRepeating.putExtra(AlarmMsg.COL_ALARMID, String.valueOf(alarmId));
					cancelRepeating.setAction(AlarmService.CANCEL);
					startService(cancelRepeating);
					
				} else {
					return;
				}
			}
			
			Intent service = new Intent(this, AlarmService.class);
			service.putExtra(AlarmMsg.COL_ALARMID, String.valueOf(alarmId));
			service.setAction(AlarmService.POPULATE);
			startService(service);
			
		} finally {
			v.setEnabled(false);
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					finish();
				}
			}, 100);
		}
	}
    
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.toggleButton1:
			vs.showNext();
			break;
			
		case R.id.fromdate_tv:
		case R.id.fromdate_lb:
			showDialog(DIALOG_FROMDATE);
			break;
			
		case R.id.todate_tv:
		case R.id.todate_lb:
			showDialog(DIALOG_TODATE);
			break;
			
		case R.id.attime_tv:
		case R.id.attime_lb:
			showDialog(DIALOG_ATTIME);
			break;			
		}
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		Calendar cal = Calendar.getInstance();
		switch(id) {
		case DIALOG_ATTIME:
			TimePickerDialog.OnTimeSetListener mTimeSetListener =
			    new TimePickerDialog.OnTimeSetListener() {
			        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						attimeText.setText(Util.getActualTime(hourOfDay, minute));
			        }
			    };
			return new TimePickerDialog(this, mTimeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), RemindMe.is24Hours());
		
		case DIALOG_FROMDATE:
		case DIALOG_TODATE:
			DatePickerDialog.OnDateSetListener dateListener =
			    new DatePickerDialog.OnDateSetListener() {
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						String txt = DbHelper.getDateStr(year, monthOfYear+1, dayOfMonth);
						try {
							txt = sdf.format(DbHelper.sdf.parse(txt));
						} catch (ParseException e) {}
						
						if (id == DIALOG_FROMDATE) {
							fromdateText.setText(txt);
						} else if (id == DIALOG_TODATE) {
							todateText.setText(txt);
						}
					}
				};
			return new DatePickerDialog(this, dateListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
		}		
		
		return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		
		switch(id) {
		case DIALOG_ATTIME:
			if (!TextUtils.isEmpty(attimeText.getText())) {
				String[] arr = Util.toPersistentTime(attimeText.getText().toString()).split(":");
				((TimePickerDialog)dialog).updateTime(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
			}			
			break;
			
		case DIALOG_FROMDATE:
			if (!TextUtils.isEmpty(fromdateText.getText())) {
				String[] arr = Util.toPersistentDate(fromdateText.getText().toString(), sdf).split("-");
				((DatePickerDialog)dialog).updateDate(Integer.parseInt(arr[0]), Integer.parseInt(arr[1])-1, Integer.parseInt(arr[2]));
			}			
			break;
			
		case DIALOG_TODATE:
			if (!TextUtils.isEmpty(todateText.getText())) {
				String[] arr = Util.toPersistentDate(todateText.getText().toString(), sdf).split("-");
				((DatePickerDialog)dialog).updateDate(Integer.parseInt(arr[0]), Integer.parseInt(arr[1])-1, Integer.parseInt(arr[2]));
			}			
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case RINGTONE_PICKER_REQUEST:
			if (resultCode == Activity.RESULT_OK && data != null) {
				ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				ringtoneIb.setColorFilter(ringtoneUri!=null ? Color.WHITE : Color.BLACK);
			}
			break;
		}
	}	

}
