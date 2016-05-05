package tacoma.uw.edu.tcss450.Modules;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URLEncoder;

import tacoma.uw.edu.tcss450.reminderproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoutineAddFragment extends Fragment {
    /**
     * The RoutineListener object
     */
    private RoutineListener mListener;

    /**
     * The url to pass to the web service
     */
    private final static String COURSE_ADD_URL
            = "http://cssgate.insttech.washington.edu/~navy1103/Reminder/addRoutine.php?";

    /**
     * EditText variables
     */
    private EditText mRoutineDateEditText;
    private EditText mRoutineTimeEditText;
    private EditText mRoutineNoteEditText;

    /**
     * The interface which uses to add, update reminder
     */
    public interface RoutineListener {
        void addRoutine(String url);
        void updateRoutine(String url);
    }

    /**
     * Constructor
     */
    public RoutineAddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_routine_add, container, false);
        getActivity().setTitle("Add New Routine");
        mRoutineDateEditText = (EditText) v.findViewById(R.id.add_routine_date);
        mRoutineTimeEditText = (EditText) v.findViewById(R.id.add_routine_time);
        mRoutineNoteEditText = (EditText) v.findViewById(R.id.add_routine_note);

        //DatePicker date = (DatePicker) v.findViewById(R.id.routine_date_picker);

        FloatingActionButton floatingActionButton = (FloatingActionButton)
                getActivity().findViewById(R.id.add_float_btn);
        floatingActionButton.hide();

        Button addCourseButton = (Button) v.findViewById(R.id.add_routine_btn);
        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = buildRoutineURL(v);
                mListener.addRoutine(url);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RoutineListener) {
            mListener = (RoutineListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RoutineListener");
        }
    }

    /**
     * Building the url for webservice
     * @param v is the current fragment
     * @return the url as string
     */
    private String buildRoutineURL(View v) {
        StringBuilder sb = new StringBuilder(COURSE_ADD_URL);

        try {

            String date = mRoutineDateEditText.getText().toString();
            sb.append("setDate=");
            sb.append(date);

            String time = mRoutineTimeEditText.getText().toString();
            sb.append("&setTime=");
            sb.append(URLEncoder.encode(time, "UTF-8"));

            String note = mRoutineNoteEditText.getText().toString();
            sb.append("&note=");
            sb.append(URLEncoder.encode(note, "UTF-8"));

            Log.i("RoutineAddFragment", sb.toString());

        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }

        return sb.toString();
    }

}
