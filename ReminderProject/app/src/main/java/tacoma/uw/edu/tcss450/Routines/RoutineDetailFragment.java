package tacoma.uw.edu.tcss450.Routines;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.net.URLEncoder;

import tacoma.uw.edu.tcss450.Routines.model.Routine;
import tacoma.uw.edu.tcss450.reminderproject.R;

/**
 * The class is show the detail of reminder
 */
public class RoutineDetailFragment extends Fragment {
    /**
     * The url to call webservice
     */
    private static final String COURSE_UPDATE_URL = "http://cssgate.insttech.washington.edu/~navy1103/Reminder/updateRoutine.php?";

    /**
     * The EditText variables to display and edit the reminder field
     */
    private TimePicker mRoutineTimeEditText;
    private EditText mRoutineNoteEditText;

    /**
     * The reminder id in the database
     */
    private String mRoutineID;

    /**
     * The string which have all reminder information
     */
    public static final String ROUTINE_ITEM_SELECTED = "routineItemSelected";

    /**
     * RoutineListener interface object
     */
    private RoutineAddFragment.RoutineListener mListener;

    /**
     * Constructor
     */
    public RoutineDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_routine_detail, container, false);

        getActivity().setTitle("Routine Details");


        mRoutineTimeEditText = (TimePicker) view.findViewById(R.id.routine_time_edit);
        mRoutineNoteEditText = (EditText) view.findViewById(R.id.routine_note);

        FloatingActionButton floatingActionButton = (FloatingActionButton)
                getActivity().findViewById(R.id.add_float_btn);
        floatingActionButton.show();

        Button edit = (Button) view.findViewById(R.id.routine_edit);
        edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String url = buildRoutineURL(v);
                mListener.updateRoutine(url);
            }
        });

        return view;
    }

    /**
     * Update the information of the selected reminder
     * @param routine is the selected reminder
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void updateView(Routine routine) {
        if (routine != null) {
            mRoutineTimeEditText.setHour(Integer.parseInt(routine.getRoutineHour()));
            mRoutineTimeEditText.setMinute(Integer.parseInt(routine.getRoutineMin()));
            mRoutineNoteEditText.setText(routine.getRoutineNote());
            mRoutineID = routine.getID();

            Log.i("Routine ID", mRoutineID);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set article based on argument passed in
            updateView((Routine) args.getSerializable(ROUTINE_ITEM_SELECTED));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RoutineAddFragment.RoutineListener) {
            mListener = (RoutineAddFragment.RoutineListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RoutineListener");
        }
    }

    /**
     * Build the url for webservice
     * @param v is the current view
     * @return the url as a string
     */
    @TargetApi(Build.VERSION_CODES.M)
    private String buildRoutineURL(View v) {
        StringBuilder sb = new StringBuilder(COURSE_UPDATE_URL);

        try {
            int hour = mRoutineTimeEditText.getHour();
            sb.append("setHour=");
            sb.append(hour);

            int min = mRoutineTimeEditText.getMinute();
            sb.append("&setMin=");
            sb.append(min);

            String note = mRoutineNoteEditText.getText().toString();
            sb.append("&note=");
            sb.append(URLEncoder.encode(note, "UTF-8"));

            String id = mRoutineID;
            sb.append("&id=");
            sb.append(URLEncoder.encode(id, "UTF-8"));

            Log.i("RoutineUpdateFragment", sb.toString());

        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }

        return sb.toString();
    }
}
