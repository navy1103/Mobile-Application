package tacoma.uw.edu.tcss450.reminderproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import tacoma.uw.edu.tcss450.Modules.MainActivity;

/**
 * The LoiginActivity is the class which handles all the actions for login, register, and forget password.
 */
public class LoginActivity extends AppCompatActivity implements LoginFragment.LoginAddListener {

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS)
                , Context.MODE_PRIVATE);
        if (!mSharedPreferences.getBoolean(getString(R.string.LOGGEDIN), false)) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.login_container, new LoginFragment())
                    .commit();
        } else {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onBackPressed()
    {
        getSupportFragmentManager().popBackStackImmediate();
    }

    /**
     * The login action
     * @param url is the login URL which includes username and password
     */
    @Override
    public void login(String url) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            //Check if the login and password are valid
            LoginTask task = new LoginTask("login");
            task.execute(new String[]{ url.toString() });
        }
        else {
            Toast.makeText(this, "No network connection available. Cannot authenticate user",
                    Toast.LENGTH_SHORT) .show();
            return;
        }
        // Takes you back to the previous fragment by popping the current fragment out.
        //getSupportFragmentManager().popBackStackImmediate();
    }

    /**
     * When user click on the register text on the login screen that will launch the register fragment.
     */
    @Override
    public void register_link() {
        if(findViewById(R.id.login_container) != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.login_container, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * The register action
     * @param url is the register URL which contains all the information for new user
     */
    @Override
    public void register(String url) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoginTask task = new LoginTask("register");
            task.execute(new String[]{ url.toString() });

            // Takes you back to the previous fragment by popping the current fragment out.
            getSupportFragmentManager().popBackStackImmediate();
        }
        else {
            Toast.makeText(this, "No network connection available. Cannot authenticate user",
                    Toast.LENGTH_SHORT) .show();
            return;
        }
    }

    /**
     *
     */
    @Override
    public void forget_password() {

    }

    /**
     * The AsyncTask class called LoginTask that will allow us to call the
     * service for login.
     */
    private class LoginTask extends AsyncTask<String, Void, String> {
        /**
         * The dialog variable
         */
        private ProgressDialog loginDialog;

        /**
         * The string which tell what do to
         */
        private String task;

        /**
         * Constructor
         * @param task is the task
         */
        LoginTask(String task){
            this.task = task;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginDialog = new ProgressDialog(LoginActivity.this);
            //loginDialog.setTitle("Contacting Servers");
            if(task.equalsIgnoreCase("login")){
                loginDialog.setMessage("Authenticating ...");
            } else if(task.equalsIgnoreCase("register")){
                loginDialog.setMessage("Registering ...");
            }
            loginDialog.setIndeterminate(false);
            loginDialog.setCancelable(true);
            loginDialog.show();
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
                    if(task.equalsIgnoreCase("login")){
                        response = "Unable to Login, Reason: " + e.getMessage();
                    }

                    if(task.equalsIgnoreCase("register")){
                        response = "Unable to Register, Reason: " + e.getMessage();
                    }
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }

            Log.i("LoginResult", response);

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
            // Something wrong with the network or the URL.
            switch (task){
                case "login":
                    processLogin(result);
                    break;
                case "register":
                    processRegister(result);
                    break;
                default:
                    break;
            }
        }

        /**
         * When user do login
         * @param result is a string which is processed by php file
         */
        private void processLogin(String result){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("result");
                //Log.i("LoginResult", status);

                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Successfully Login!"
                            , Toast.LENGTH_LONG).show();

                    //Store User Information into local file
                    mSharedPreferences
                            .edit().putBoolean(getString(R.string.LOGGEDIN), true)
                            .commit();

                    rememberUser(jsonObject.getString("username"));

                    //after successfully login
                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                    main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(main);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to login: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }

            loginDialog.dismiss();
        }

        /**
         * When user do register
         * @param result is a string which is processed by php file
         */
        private void processRegister(String result){
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Successfully Register!"
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to register: "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }

            loginDialog.dismiss();
        }

        /**
         * Store username in the LOGIN_FILE
         * @param username
         */
        private void rememberUser(String username){
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                        openFileOutput(getString(R.string.LOGIN_FILE), Context.MODE_PRIVATE));
                outputStreamWriter.write(username);
                //outputStreamWriter.write("password = " + pass);
                outputStreamWriter.close();

//                        Toast.makeText(getApplicationContext(),"Stored in File Successfully!", Toast.LENGTH_LONG)
//                                .show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
