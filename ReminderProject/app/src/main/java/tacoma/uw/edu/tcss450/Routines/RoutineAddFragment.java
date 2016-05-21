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
    private final static String ROUTINE_ADD_URL
            = "http://cssgate.insttech.washington.edu/~navy1103/Reminder/addRoutine.php?";

    /**
     * EditText variables
     */
    private TimePicker mSetRoutineTime;
    private EditText mRoutineNoteEditText;

    /**
     * The interface which uses to add, update reminder
     */
    public interface RoutineListener {
        void addRoutine(String url);
        void updateRoutine(String url);
        void passValue(int hour, int min, String note);
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
        mSetRoutineTime = (TimePicker) v.findViewById(R.id.routine_time_picker);
        mRoutineNoteEditText = (EditText) v.findViewById(R.id.add_routine_note);

        //DatePicker date = (DatePicker) v.findViewById(R.id.routine_date_picker);

        FloatingActionButton floatingActionButton = (FloatingActionButton)
                getActivity().findViewById(R.id.add_float_btn);
        floatingActionButton.hide();

        Button addCourseButton = (Button) v.findViewById(R.id.add_routine_btn);
        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                String url = buildRoutineURL(v);
                mListener.addRoutine(url);
                mListener.passValue(mSetRoutineTime.getHour(), mSetRoutineTime.getMinute(), mRoutineNoteEditText.getText().toString());
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
     *
     * @param v is the current fragment
     * @return the url as string
     */
    @TargetApi(Build.VERSION_CODES.M)
    private String buildRoutineURL(View v) {
        StringBuilder sb = new StringBuilder(ROUTINE_ADD_URL);

        try {

            int hour = mSetRoutineTime.getHour();
            sb.append("setHour=");
            sb.append(hour);

            int min = mSetRoutineTime.getMinute();
            sb.append("&setMin=");
            sb.append(min);

            String note = mRoutineNoteEditText.getText().toString();
            sb.append("&note=");
            sb.append(URLEncoder.encode(note, "UTF-8"));

            Log.i("RoutineAddFragment", sb.toString());

        } catch (Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }

        return sb.toString();
    }
}
