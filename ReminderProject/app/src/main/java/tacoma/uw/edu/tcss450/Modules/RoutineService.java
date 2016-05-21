package tacoma.uw.edu.tcss450.Modules;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


/**
 * Created by Navy on 5/9/2016.
 */
public class RoutineService extends IntentService {

    private NotificationManager alarmNotificationManager;

    public RoutineService() {
        super("Routine Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("AlarmService", "Preparing to send notification...: ");

        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder alamNotificationBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");


        alamNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, alamNotificationBuilder.build());

        Log.d("AlarmService", "Notification sent.");
    }
}
