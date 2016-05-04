package tacoma.uw.edu.tcss450.reminderproject.Modules;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import tacoma.uw.edu.tcss450.reminderproject.Modules.Reminder.Routine;
import tacoma.uw.edu.tcss450.reminderproject.R;


public class MainActivity extends AppCompatActivity implements RoutineFragment.OnListFragmentInteractionListener,
                            RoutineAddFragment.RoutineAddListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Change the code for Floating Action Button listener to launch the fragment
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RoutineAddFragment routineAddFragment = new RoutineAddFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, routineAddFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        if (savedInstanceState == null || getSupportFragmentManager().findFragmentById(R.id.list) == null) {
            RoutineFragment routineListFragment = new RoutineFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, routineListFragment)
                    .commit();
        }


    }

    @Override
    public void onListFragmentInteraction(Routine item) {
        RoutineDetailFragment routineDetailFragment = new RoutineDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(routineDetailFragment.ROUTINE_ITEM_SELECTED, item);
        routineDetailFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, routineDetailFragment)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void addRoutine(String url) {
        AddRoutineTask task = new AddRoutineTask();
        task.execute(new String[]{url.toString()});

        // Takes you back to the previous fragment by popping the current fragment out.
        getSupportFragmentManager().popBackStackImmediate();

    }

    private class AddRoutineTask extends AsyncTask<String, Void, String> {
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
                    response = "Unable to add course, Reason: "
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
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Course successfully added!"
                            , Toast.LENGTH_LONG)
                            .show();
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
