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
import android.widget.DatePicker;
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

import tacoma.uw.edu.tcss450.Alarm.AlarmReceiver;
import tacoma.uw.edu.tcss450.reminderproject.R;

public class UpdateReminderActivity extends AppCompatActivity {
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;

    private EditText mReminderNote, mReminderEmail, mReminderPhone, mReminderLocation;

    private String date;
    private String reminderID;
    private Calendar targetCal, currentCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_reminder);

        setTitle("Edit Reminder");
        currentCal = Calendar.getInstance();

        mDatePicker = (DatePicker) findViewById(R.id.reminderUpdateDate);
        mTimePicker = (TimePicker) findViewById(R.id.reminderUpdateTime);

        mReminderNote = (EditText) findViewById(R.id.reminderUpdateNote);
        mReminderEmail = (EditText) findViewById(R.id.reminderUpdateEmail);
        mReminderPhone = (EditText) findViewById(R.id.reminderUpdatePhone);
        mReminderLocation = (EditText) findViewById(R.id.reminderUpdateLocation);

        setUp();

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setUp() {
        Intent in = getIntent();
        Bundle infor = in.getBundleExtra("reminder");
        reminderID = infor.getString("id");

        //Update the current date to reminder's date
        String parts[] = infor.getString("date").split("-");

        int day = Integer.parseInt(parts[2]);
        int month = Integer.parseInt(parts[1]) - 1;
        int year = Integer.parseInt(parts[0]);

        mDatePicker.updateDate(year, month, day);
        mDatePicker.setCalendarViewShown(false);

        date = infor.getString("date");

        mTimePicker.setCurrentHour(Integer.parseInt(infor.getString("hour")));
        mTimePicker.setCurrentMinute(Integer.parseInt(infor.getString("min")));

        mReminderNote.setText(infor.getString("note"));
        mReminderEmail.setText(infor.getString("email"));
        mReminderPhone.setText(infor.getString("phone"));
        mReminderLocation.setText(infor.getString("location"));
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
        int id = item.getItemId();

        //When user choose save option
        if (id == R.id.action_add) {
            date = mDatePicker.getYear() + "-" + (mDatePicker.getMonth() + 1) + "-" + mDatePicker.getDayOfMonth();

            targetCal = Calendar.getInstance();
            targetCal.set(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth(), mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute());

            if(mReminderNote.getText().length() < 1){
                Toast.makeText(getApplicationContext(), "The note field can not be empty", Toast.LENGTH_SHORT).show();
            } else if(currentCal.getTimeInMillis() >= targetCal.getTimeInMillis()){
                Toast.makeText(getApplicationContext(), "Please set the date and time", Toast.LENGTH_LONG).show();
            } else {
                ReminderTask task = new ReminderTask();
                task.execute(new String[] { buildRoutineURL().toString() });
                return true;
            }
        } else if (id == R.id.action_cancel) {
            Intent i = new Intent(this, ReminderActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private String buildRoutineURL() {
        StringBuilder sb = new StringBuilder(AddReminderActivity.REMINDER_ADD_URL);

        try {
            sb.append("tag=update");
            sb.append("&setID=");
            sb.append(reminderID);

            Log.i("update id", reminderID);

            sb.append("&setDate=");
            sb.append(date);

            sb.append("&setHour=");
            sb.append(mTimePicker.getCurrentHour());

            sb.append("&setMin=");
            sb.append(mTimePicker.getCurrentMinute());

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
     * Set alarm when add new routines
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void setAlarm(Calendar targetCal){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent alertIntent = new Intent(getApplicationContext(), AlarmReceiver.class);

        Bundle b = new Bundle();
        b.putString("id", reminderID);
        b.putString("note", mReminderNote.getText().toString());
        alertIntent.putExtra("reminder", b);

        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
                PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(reminderID),  //1 is the private request code for the sender
                        alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
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
                    Toast.makeText(getApplicationContext(), "Reminder successfully updated!"
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
