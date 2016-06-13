package tacoma.uw.edu.tcss450.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import tacoma.uw.edu.tcss450.Reminder.ReminderActivity;
import tacoma.uw.edu.tcss450.Reminder.ReminderDetailFragment;
import tacoma.uw.edu.tcss450.reminderproject.LoginActivity;
import tacoma.uw.edu.tcss450.reminderproject.R;
import tacoma.uw.edu.tcss450.reminderproject.RegisterFragment;

public class UserProfile extends AppCompatActivity implements ChangePassFragment.ChangePassListener, UserProfileFragment.UpdateProfileListener{
    private static final String PROFILE_URL = "http://cssgate.insttech.washington.edu/~navy1103/Reminder/login.php?";
    protected static String profileUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Bundle b = getIntent().getBundleExtra("bundle");
        profileUser = b.getString("username");

        if(findViewById(R.id.user_profile_container) != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.user_profile_container, new UserProfileFragment())
                    .addToBackStack(null)
                    .commit();
        }

        setTitle(profileUser);
    }

    /**
     * Create the option menu
     * @param menu is a menu
     * @return the menu is created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }


    /**
     * When user choose the option in the menu
     * @param item is menu option
     * @return the action for that menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_profile_cancel:
                Intent main = new Intent(getApplicationContext(), ReminderActivity.class);
                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);
                finish();
                return true;
            case R.id.action_profile_logout:
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
    public void onBackPressed(){
        if(getSupportFragmentManager().getBackStackEntryCount() != 1){
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            Intent main = new Intent(getApplicationContext(), ReminderActivity.class);
            main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
            finish();
        }
    }

    @Override
    public void changePassword(String url) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            ChangePassTask task = new ChangePassTask("change");
            task.execute(new String[]{ url.toString() });

            // Takes you back to the previous fragment by popping the current fragment out.
            //getSupportFragmentManager().popBackStackImmediate();
        }
        else {
            Toast.makeText(this, "No network connection available. Check your connection.",
                    Toast.LENGTH_SHORT) .show();
            return;
        }
    }

    @Override
    public void updateProfile(String url) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            ChangePassTask task = new ChangePassTask("update");
            task.execute(new String[]{ url.toString() });

            // Takes you back to the previous fragment by popping the current fragment out.
            //getSupportFragmentManager().popBackStackImmediate();
        }
        else {
            Toast.makeText(this, "No network connection available. Check your connection.",
                    Toast.LENGTH_SHORT) .show();
            return;
        }
    }



    /**
     * The AsyncTask class called RoutineTask that will allow us to call the
     * service for add, update routines.
     */
    private class ChangePassTask extends AsyncTask<String, Void, String> {
        private String task;

        ChangePassTask(String task){
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
                    if(task.equalsIgnoreCase("change")){
                        response = "Unable to change user password, Reason: "
                                + e.getMessage();
                    } else {
                        response = "Unable to update user, Reason: "
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
            if(task.equalsIgnoreCase("change")){
                changeProcess(result);
            } else {
                updateProcess(result);
            }
        }

        private void changeProcess(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Password successfully changed!"
                            , Toast.LENGTH_LONG)
                            .show();

                    getSupportFragmentManager().popBackStackImmediate();

                } else {
                    Toast.makeText(getApplicationContext(), "Failed to change password: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }

        private void updateProcess(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "User profile successfully updated!"
                            , Toast.LENGTH_LONG)
                            .show();


                } else {
                    Toast.makeText(getApplicationContext(), "Failed to update user profile: "
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
