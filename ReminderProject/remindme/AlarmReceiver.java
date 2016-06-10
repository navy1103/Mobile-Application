package com.appsrox.remindme;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.appsrox.remindme.model.Alarm;
import com.appsrox.remindme.model.AlarmMsg;

public class AlarmReceiver extends BroadcastReceiver {
	
//	private static final String TAG = "AlarmReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		long alarmMsgId = intent.getLongExtra(AlarmMsg.COL_ID, -1);
		AlarmMsg alarmMsg = new AlarmMsg(alarmMsgId);
		alarmMsg.load(RemindMe.db);
		if (AlarmMsg.CANCELLED.equals(alarmMsg.getStatus())) return;
		
		long alarmId = alarmMsg.getAlarmId();
		Alarm alarm = new Alarm(alarmId);
		alarm.load(RemindMe.db);
		
		Intent userIntent = new Intent(context, UserActivity.class);
		userIntent.setAction("com.appsrox.remindme."+alarmMsgId);
		userIntent.putExtra(AlarmMsg.COL_ID, alarmMsgId);
		userIntent.putExtra(AlarmMsg.COL_ALARMID, alarmId);
		userIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		Notification n = new Notification(R.drawable.ic_launcher, alarm.getName(), alarmMsg.getDateTime());
		PendingIntent userPi = PendingIntent.getActivity(context, 0, userIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		n.setLatestEventInfo(context, alarm.getName(), "Click here to dismiss", userPi);		
		if (alarm.getVibrate()) {
			n.defaults |= Notification.DEFAULT_VIBRATE;
		}
		if (alarm.getSound()) {
			n.sound = Uri.parse(RemindMe.getRingtone());
//			n.defaults |= Notification.DEFAULT_SOUND;
		}
		
		if (alarm.getSound() && alarm.getInsistent()) {
			n.flags |= Notification.FLAG_INSISTENT;
			
		} else if (RemindMe.isAutoSnooze()) {
			doRepeat(context, userIntent);
		}
		
		n.flags |= Notification.FLAG_AUTO_CANCEL;		
		n.flags |= Notification.FLAG_NO_CLEAR;
		
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify((int)alarmMsgId, n);
		
		alarmMsg.reset();
		alarmMsg.setId(alarmMsgId);
		alarmMsg.setStatus(AlarmMsg.EXPIRED);
		alarmMsg.persist(RemindMe.db);
	}
	
	private void doRepeat(Context context, Intent intent) {
		Intent i = new Intent(context, AlarmReceiver.class);
		i.putExtra(AlarmMsg.COL_ID, intent.getLongExtra(AlarmMsg.COL_ID, -1));
		i.putExtra(AlarmMsg.COL_ALARMID, intent.getLongExtra(AlarmMsg.COL_ALARMID, -1));
		
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		long time = System.currentTimeMillis() + RemindMe.getSnoozeTime()*60*1000;
		
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, time, pi);
	}

}
