package tacoma.uw.edu.tcss450.Reminder;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

import tacoma.uw.edu.tcss450.Alarm.AlarmReceiver;
import tacoma.uw.edu.tcss450.Reminder.model.Reminder;
import tacoma.uw.edu.tcss450.User.UserProfile;
import tacoma.uw.edu.tcss450.reminderproject.LoginActivity;
import tacoma.uw.edu.tcss450.reminderproject.R;

public class ReminderActivity extends AppCompatActivity implements ReminderListFragment.OnListFragmentInteractionListener, ReminderDetailFragment.UpdateListener{

    protected static String USERNAME;
    private String deleteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        if (savedInstanceState == null || getSupportFragmentManager().findFragmentById(R.id.reminder_list) == null) {
            ReminderListFragment reminderListFragment = new ReminderListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.reminder_container, reminderListFragment)
                    .commit();
        }
    }

    /**
     * Create the option menu
     * @param menu is a menu
     * @return the menu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reminder_menu, menu);
        menu.findItem(R.id.action_user).setTitle(USERNAME);
        return true;
    }

    /**
     * When user choose the option in the menu
     * @param item is menu option
     * @return the action for that menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_add:
                Intent main = new Intent(getApplicationContext(), AddReminderActivity.class);
                //main.putExtra("username", this.username);
                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);
                finish();
                return true;

            case R.id.action_logout:
                SharedPreferences sharedPreferences =
                        getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(getString(R.string.LOGGEDIN), false).apply();

                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();

                Toast.makeText(this, "Successful log out", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_profile:
                DeleteTask task = new DeleteTask("profile");
                StringBuilder sb = new StringBuilder("http://cssgate.insttech.washington.edu/~navy1103/Reminder/login.php?");

                try {
                    sb.append("tag=profile");
                    sb.append("&username=");
                    sb.append(URLEncoder.encode(ReminderActivity.USERNAME, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                task.execute(new String[] {sb.toString()});
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(Reminder item) {
        ReminderDetailFragment reminderDetailFragment = new ReminderDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(reminderDetailFragment.REMINDER_ITEM_SELECTED, item);
        reminderDetailFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.reminder_container, reminderDetailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void deleteReminder(Reminder item) {
        deleteID = item.getReminderID();
        //Cancel alarm of deleting reminder
        Intent alertIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        Bundle b = new Bundle();
        b.putString("id", deleteID);
        b.putString("note",item.getReminderNote());
        alertIntent.putExtra("reminder", b);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        PendingIntent pend = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(deleteID),
                alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pend);

        //Building an alert dialog for delete
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do you want to delete this reminder '" + item.getReminderNote() + "'");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                DeleteTask task = new DeleteTask("delete");

                task.execute(new String[] { buildRoutineURL()});
                //finish();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.reminder_container, new ReminderListFragment())
                        .commit();
            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });

        //Create and display an alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void open(View view){

    }

    @Override
    public void updateReminder(String id, String date, String hour, String min, String note, String email, String phone, String location) {
        Bundle b = new Bundle();
        b.putString("id", id);
        b.putString("date", date);
        b.putString("hour", hour);
        b.putString("min", min);
        b.putString("note", note);
        b.putString("email", email);
        b.putString("phone", phone);
        b.putString("location", location);

        Intent in = new Intent(this.getApplication(), UpdateReminderActivity.class);
        in.putExtra("reminder", b);
        startActivity(in);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private String buildRoutineURL() {
        StringBuilder sb = new StringBuilder(AddReminderActivity.REMINDER_ADD_URL);

        try {
            sb.append("tag=delete");
            sb.append("&setID=");
            sb.append(deleteID);

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
    private class DeleteTask extends AsyncTask<String, Void, String> {
        private String task;

        DeleteTask(String task){
            this.task = task;
        }

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
                    if(task.equalsIgnoreCase("delete")){
                        response = "Unable to delete reminder, Reason: "
                                + e.getMessage();
                    } else {
                        response = "Unable to get user profile, Reason: "
                                + e.getMessage();
                    }

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
            if(task.equalsIgnoreCase("delete")){
                deleteReminderProcess(result);
            } else {
                getProfile(result);
            }
        }

        private void deleteReminderProcess(String result){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Reminder successfully deleted!"
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to delete: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        private void getProfile(String result){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("username", USERNAME);
                    bundle.putString("first", (String) jsonObject.get("first"));
                    bundle.putString("last", (String) jsonObject.get("last"));
                    bundle.putString("email", (String) jsonObject.get("email"));

                    Log.i("Profile", (String) jsonObject.get("first") + " " + (String) jsonObject.get("last") + " " + (String) jsonObject.get("email"));

                    Intent profile = new Intent(getApplicationContext(), UserProfile.class);
                    profile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    profile.putExtra("bundle", bundle);
                    startActivity(profile);
                    finish();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
