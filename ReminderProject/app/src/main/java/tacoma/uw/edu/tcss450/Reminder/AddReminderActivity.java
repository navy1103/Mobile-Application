package tacoma.uw.edu.tcss450.Reminder;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.GregorianCalendar;

import tacoma.uw.edu.tcss450.Alarm.AlarmReceiver;
import tacoma.uw.edu.tcss450.reminderproject.R;

public class AddReminderActivity extends AppCompatActivity {
    protected static final String REMINDER_ADD_URL = "http://cssgate.insttech.washington.edu/~navy1103/Reminder/addReminder.php?";

//    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private EditText mReminderNote, mReminderEmail, mReminderPhone, mReminderLocation;

    private CalendarView mCalendar;
    private int reminderID, rYear, rMonth, rDay;
    private String date;
    private Boolean dateChange = false;
    private Boolean setAlarm = true;
    private Calendar targetCal, current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        setTitle("Add Reminder");
        current = Calendar.getInstance();
        targetCal = Calendar.getInstance();

        mCalendar = (CalendarView) findViewById(R.id.reminderAddCalendar);
        mCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                reminderID = year + month + dayOfMonth;
                targetCal.set(year, month, dayOfMonth);
                date = String.valueOf(year) + "-" +String.valueOf(month+1) + "-" + String.valueOf(dayOfMonth);
                dateChange = true;
            }
        });

//        mDatePicker = (DatePicker) findViewById(R.id.reminderDatePicker);
        mTimePicker = (TimePicker) findViewById(R.id.reminderTimePicker);

        mReminderNote = (EditText) findViewById(R.id.reminderAddNote);
        mReminderEmail = (EditText) findViewById(R.id.reminderAddEmail);
        mReminderPhone = (EditText) findViewById(R.id.reminderAddPhone);
        mReminderLocation = (EditText) findViewById(R.id.reminderAddLocation);

    }

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(this, ReminderActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    /**
     * Create the option menu
     * @param menu is a menu
     * @return the menu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    /**
     * When user choose the option in the menu
     * @param item is menu option
     * @return the action for that menu
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //When user choose save option
        if (id == R.id.action_add) {

            if(dateChange){
                reminderID += mTimePicker.getHour() +mTimePicker.getMinute();
            } else {
                reminderID = targetCal.get(Calendar.YEAR) + targetCal.get(Calendar.MONTH) + targetCal.get(Calendar.DAY_OF_MONTH)
                        + mTimePicker.getHour() +mTimePicker.getMinute();

                int m = targetCal.get(Calendar.MONTH) + 1;
                date = targetCal.get(Calendar.YEAR) + "-" + m + "-" + targetCal.get(Calendar.DAY_OF_MONTH);
            }

            targetCal.set(Calendar.HOUR_OF_DAY, mTimePicker.getHour());
            targetCal.set(Calendar.MINUTE, mTimePicker.getMinute());

            Log.i("current", String.valueOf(current.getTime()));
            Log.i("target", String.valueOf(targetCal.getTime()));

            Log.i("current", String.valueOf(current.getTimeInMillis()));
            Log.i("target", String.valueOf(targetCal.getTimeInMillis()));

            //if(targetCal.compareTo(current) <= 0){
           if(mReminderNote.getText().length() < 1){
                Toast.makeText(getApplicationContext(), "The note field can not be empty", Toast.LENGTH_SHORT).show();
            } else {
                ReminderTask task = new ReminderTask();
                task.execute(new String[] { buildRoutineURL().toString() });
                return true;
            }
        }

        //When user choose cancel option
        if (id == R.id.action_cancel) {
            Intent i = new Intent(this, ReminderActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set alarm when add new routines
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void setAlarm(Calendar targetCal){
        Intent alertIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        Bundle b = new Bundle();
        b.putString("id", String.valueOf(reminderID));
        b.putString("note", mReminderNote.getText().toString());
        alertIntent.putExtra("reminder", b);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
                PendingIntent.getBroadcast(getApplicationContext(), reminderID,  //1 is the private request code for the sender
                        alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    @TargetApi(Build.VERSION_CODES.M)
    private String buildRoutineURL() {
        StringBuilder sb = new StringBuilder(REMINDER_ADD_URL);

        try {
            sb.append("tag=add");
            sb.append("&setID=");
            sb.append(reminderID);

            //int correctMonth = mDatePicker.getMonth()+ 1;

            sb.append("&setDate=");
            sb.append(date);
            //sb.append(mDatePicker.getYear() + "-" + correctMonth + "-" + mDatePicker.getDayOfMonth());

            sb.append("&setHour=");
            sb.append(mTimePicker.getHour());

            sb.append("&setMin=");
            sb.append(mTimePicker.getMinute());

            sb.append("&note=");
            sb.append(URLEncoder.encode(mReminderNote.getText().toString(), "UTF-8"));

            sb.append("&email=");
            sb.append(URLEncoder.encode(mReminderEmail.getText().toString(), "UTF-8"));

            sb.append("&phone=");
            sb.append(URLEncoder.encode(mReminderPhone.getText().toString(), "UTF-8"));

            sb.append("&location=");
            sb.append(URLEncoder.encode(mReminderLocation.getText().toString(), "UTF-8"));

            sb.append("&username=");
            sb.append(URLEncoder.encode(ReminderActivity.USERNAME, "UTF-8"));

            Log.i("RoutineAddFragment", sb.toString());

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    /**
     * The AsyncTask class called RoutineTask that will allow us to call the
     * service for add, update routines.
     */
    private class ReminderTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to add routine, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }


        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Reminder successfully added!"
                            , Toast.LENGTH_LONG)
                            .show();

                    setAlarm(targetCal);
                    Log.i("Alarm", "Alarm set");

                    Intent i = new Intent(getApplication(), ReminderActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }
}
