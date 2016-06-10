package com.appsrox.remindme;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.appsrox.remindme.model.DbHelper;

public class RemindMe extends Application {
	
//	private static final String TAG = "RemindMe";
	
	public static DbHelper dbHelper;
	public static SQLiteDatabase db;
	public static SharedPreferences sp;
	
	public static final String TIME_OPTION = "time_option";
	public static final String DATE_RANGE = "date_range";
	public static final String DATE_FORMAT = "date_format";
	public static final String TIME_FORMAT = "time_format";
	public static final String VIBRATE_PREF = "vibrate_pref";
	public static final String RINGTONE_PREF = "ringtone_pref";
	public static final String SNOOZE_TIME = "snooze_time";
	public static final String AUTO_SNOOZE = "auto_snooze";
	public static final String LAST_SHOWCASE = "last_showcase";
	
	public static final String DEFAULT_DATE_FORMAT = "yyyy-M-d";
	public static final String DEFAULT_SNOOZE_TIME = "5";
	public static final int DEFAULT_SHOWCASE = 6;

	@Override
	public void onCreate() {
		super.onCreate();
		
		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
		PreferenceManager.setDefaultValues(this, R.xml.pref_notification, false);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		
		dbHelper = new DbHelper(this);
		db = dbHelper.getWritableDatabase();		
	}
	
	public static boolean showRemainingTime() {
		return "1".equals(sp.getString(TIME_OPTION, "0"));
	}
	
	public static int getDateRange() {
		return Integer.parseInt(sp.getString(DATE_RANGE, "0"));
	}
	public static void setDateRange(int dateRange) {
		sp.edit().putString(DATE_RANGE, String.valueOf(dateRange)).commit();
	}
	
	public static String getDateFormat() {
		return sp.getString(DATE_FORMAT, DEFAULT_DATE_FORMAT);
	}	
	
	public static boolean is24Hours() {
		return sp.getBoolean(TIME_FORMAT, true);
	}
	
	public static boolean isVibrate() {
		return sp.getBoolean(VIBRATE_PREF, true);
	}
	
	public static String getRingtone() {
		return sp.getString(RINGTONE_PREF, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI.toString());
	}
	
	public static int getSnoozeTime() {
		return Integer.parseInt(sp.getString(SNOOZE_TIME, DEFAULT_SNOOZE_TIME));
	}
	
	public static boolean isAutoSnooze() {
		return sp.getBoolean(AUTO_SNOOZE, false);
	}	

	public static boolean isShowcase() {
		return sp.getInt(LAST_SHOWCASE, 0) < DEFAULT_SHOWCASE;
	}
	
	public static void setShowcase() {
		sp.edit().putInt(LAST_SHOWCASE, DEFAULT_SHOWCASE).commit();
	}	
}
