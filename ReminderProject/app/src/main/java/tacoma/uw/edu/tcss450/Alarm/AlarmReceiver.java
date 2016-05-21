package tacoma.uw.edu.tcss450.Alarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import tacoma.uw.edu.tcss450.Reminder.AddReminderActivity;
import tacoma.uw.edu.tcss450.Reminder.ReminderActivity;
import tacoma.uw.edu.tcss450.Routines.RoutineActivity;
import tacoma.uw.edu.tcss450.reminderproject.R;

/**
 * Created by Navy on 5/9/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra("reminder");

        PendingIntent pend = PendingIntent.getActivities(context, Integer.parseInt(bundle.getString("id")),
                new Intent[]{new Intent(context, ReminderActivity.class)}, 0 );

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Reminder")
                .setContentText(bundle.getString("note"));

        mBuilder.setContentIntent(pend);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);

        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.i("Get update id", bundle.getString("id"));
        notifManager.notify(Integer.parseInt(bundle.getString("id")), mBuilder.build());
    }
}
