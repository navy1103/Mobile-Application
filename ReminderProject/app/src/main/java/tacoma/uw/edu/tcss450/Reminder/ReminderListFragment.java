package tacoma.uw.edu.tcss450.Reminder;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import tacoma.uw.edu.tcss450.Reminder.model.ReminderDB;
import tacoma.uw.edu.tcss450.reminderproject.R;
import tacoma.uw.edu.tcss450.Reminder.model.Reminder;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ReminderListFragment extends Fragment {

    /**
     * The url for webservice
     */
    private static String REMINDER_URL = "http://cssgate.insttech.washington.edu/~navy1103/Reminder/getRoutines.php?cmd=reminder";

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private ReminderDB mReminderDB;
    private List<Reminder> mReminderList;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ReminderListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ReminderListFragment newInstance(int columnCount) {
        ReminderListFragment fragment = new ReminderListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUser();
        REMINDER_URL += "&username=" + ReminderActivity.USERNAME;

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }

        //Check the network
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            DownloadCoursesTask task = new DownloadCoursesTask();
            task.execute(new String[]{REMINDER_URL});
        } else {
            Toast.makeText(view.getContext(),
                    "No network connection available. Cannot display courses",
                    Toast.LENGTH_SHORT) .show();

            if (mReminderDB == null) {
                mReminderDB = new ReminderDB(getActivity());
            }
            if (mReminderList == null) {
                mReminderList = mReminderDB.getReminder();
            }
            mRecyclerView.setAdapter(new MyReminderRecyclerViewAdapter(mReminderList, mListener));
        }

        DownloadCoursesTask task = new DownloadCoursesTask();
        task.execute(new String[]{REMINDER_URL});

        return view;
    }

    /**
     * Checking user is stored in LOGIN_FILE or not
     */
    private void getUser() {
        try {
            InputStream inputStream = getActivity().openFileInput(getString(R.string.LOGIN_FILE));

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();

                //set username
                ReminderActivity.USERNAME = stringBuilder.toString();
                getActivity().setTitle("Reminder List");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Reminder item);
        void deleteReminder(Reminder item);
    }

    /**
     * the private class DownloadCoursesTask to setup of asynchronous loading of the data.
     */
    private class DownloadCoursesTask extends AsyncTask<String, Void, String> {

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
                    response = "Unable to download the list of courses, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            List<Reminder> reminderList = new ArrayList<Reminder>();
            result = Reminder.parseReminderJSON(result, reminderList);
            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            // Everything is good, show the list of courses.
            if (!reminderList.isEmpty()) {
                mRecyclerView.setAdapter(new MyReminderRecyclerViewAdapter(reminderList, mListener));

                if (mReminderDB == null) {
                    mReminderDB = new ReminderDB(getActivity());
                }

                // Delete old data so that you can refresh the local
                // database with the network data.
                mReminderDB.deleteReminder();


                // Also, add to the local database
                for (int i=0; i<reminderList.size(); i++) {
                    Reminder reminder = reminderList.get(i);
                    mReminderDB.insertReminder(reminder.getReminderID(),
                            reminder.getDate(), reminder.getReminderHour(),
                            reminder.getReminderMin(), reminder.getReminderNote(),
                            reminder.getReminderEmail(), reminder.getReminderPhone(),
                            reminder.getReminderLocation(), reminder.getUsername());
                }

            }
        }

    }
}
