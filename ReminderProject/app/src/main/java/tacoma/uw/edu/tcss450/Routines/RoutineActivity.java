package tacoma.uw.edu.tcss450.Routines;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.GregorianCalendar;

import tacoma.uw.edu.tcss450.Alarm.AlarmReceiver;
import tacoma.uw.edu.tcss450.reminderproject.LoginActivity;
import tacoma.uw.edu.tcss450.Routines.model.Routine;
import tacoma.uw.edu.tcss450.reminderproject.R;

/**
 * The RoutineActivity class is use to add, update reminders.
 */
public class RoutineActivity extends AppCompatActivity implements RoutineFragment.OnListFragmentInteractionListener,
        RoutineAddFragment.RoutineListener {
    private String routineNote;
    private int routineHour, routineMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

        //Change the code for Floating Action Button listener to launch the fragment
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_float_btn);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RoutineAddFragment routineAddFragment = new RoutineAddFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.routine_container, routineAddFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        if (savedInstanceState == null || getSupportFragmentManager().findFragmentById(R.id.routine_list) == null) {
            RoutineFragment routineListFragment = new RoutineFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.routine_container, routineListFragment)
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
        getMenuInflater().inflate(R.menu.detail_menu, menu);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile) {
            return true;
        }

        //When user choose the log out
        if (id == R.id.action_logout) {
            SharedPreferences sharedPreferences =
                    getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(getString(R.string.LOGGEDIN), false).apply();

            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();

            Toast.makeText(this, "Successful log out", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(Routine item) {
        RoutineDetailFragment routineDetailFragment = new RoutineDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(routineDetailFragment.ROUTINE_ITEM_SELECTED, item);
        routineDetailFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.routine_container, routineDetailFragment)
                .addToBackStack(null)
                .commit();

    }

    /**
     * Adding new reminder
     * @param url is the URL which contains all information of new reminder
     */
    @Override
    public void addRoutine(String url) {
        RoutineTask task = new RoutineTask("add");
        task.execute(new String[]{url.toString()});

//        // Takes you back to the previous fragment by popping the current fragment out.
//        getSupportFragmentManager().popBackStackImmediate();
    }

    /**
     * Updating new reminder
     * @param url is the URL which contains all information of new reminder
     */
    @Override
    public void updateRoutine(String url) {
        RoutineTask task = new RoutineTask("update");
        task.execute(new String[]{url.toString()});
    }

    /**
     * Get the input value
     * @param hour
     * @param min
     * @param note
     */
    @Override
    public void passValue(int hour, int min, String note) {
        this.routineHour = hour;
        this.routineMin = min;
        this.routineNote = note;
    }

    /**
     * Set alarm when add new routines
     */
    public void setAlarm(){
        Long alertTime = new GregorianCalendar().getTimeInMillis() + 5*1000;

        Intent alertIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        //pass the note into the intent with keyword 'routineNote'
        alertIntent.putExtra("routineNote", this.routineNote);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime,
                PendingIntent.getBroadcast(getApplicationContext(), 1,  //1 is the private request code for the sender
                        alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    /**
     * The AsyncTask class called RoutineTask that will allow us to call the
     * service for add, update routines.
     */
    private class RoutineTask extends AsyncTask<String, Void, String> {
        private final String task;

        /**
         * Constructor of the RoutineTask class
         * @param task is the string which determine the action
         */
        RoutineTask(String task){
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
            switch (task){
                case "add":
                    processAddRoutine(result);
                    break;
                case "update":
                    processUpdateRoutine(result);
                    break;
                default:
                    break;
            }

        }

        /**
         * Add new routine into database
         * @param result is the jsonObject from php file
         */
        private void processAddRoutine(String result) {
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Routine successfully added!"
                            , Toast.LENGTH_LONG)
                            .show();

                    setAlarm();
                    Log.i("Alarm", "Alarm set");

                    // Takes you back to the previous fragment by popping the current fragment out.
                    getSupportFragmentManager().popBackStackImmediate();


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

        /**
         * Update exist routine into database
         * @param result is the jsonObject from php file
         */
        private void processUpdateRoutine(String result) {
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Routine successfully updated!"
                            , Toast.LENGTH_LONG)
                            .show();
                    // Takes you back to the previous fragment by popping the current fragment out.
                    getSupportFragmentManager().popBackStackImmediate();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to update: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
