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

import tacoma.uw.edu.tcss450.Modules.Reminder.Routine;
import tacoma.uw.edu.tcss450.reminderproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoutineDetailFragment extends Fragment {

    private static final String COURSE_UPDATE_URL = "http://cssgate.insttech.washington.edu/~navy1103/Reminder/updateRoutine.php?";
    private EditText mRoutineDateEditText;
    private EditText mRoutineTimeEditText;
    private EditText mRoutineNoteEditText;
    private String mRoutineID;

    public static final String ROUTINE_ITEM_SELECTED = "routineItemSelected";
    
    private RoutineEditListener mListener;

    public RoutineDetailFragment() {
        // Required empty public constructor
    }

    public interface RoutineEditListener {
        void updateRoutine(String url);
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_routine_detail, container, false);

        getActivity().setTitle("Routine Details");

        mRoutineDateEditText = (EditText) view.findViewById(R.id.routine_toDate);
        mRoutineTimeEditText = (EditText) view.findViewById(R.id.routine_time);
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

    public void updateView(Routine routine) {
        if (routine != null) {
            mRoutineDateEditText.setText(routine.getRoutineToDate());
            mRoutineTimeEditText.setText(routine.getRoutineTime());
            mRoutineNoteEditText.setText(routine.getRoutineNote());
            mRoutineID = routine.getID().toString();

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
        if (context instanceof RoutineEditListener) {
            mListener = (RoutineEditListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RoutineAddListener");
        }
    }

    private String buildRoutineURL(View v) {
        StringBuilder sb = new StringBuilder(COURSE_UPDATE_URL);

        try {

            String date = mRoutineDateEditText.getText().toString();
            sb.append("toDate=");
            sb.append(date);


            String time = mRoutineTimeEditText.getText().toString();
            sb.append("&byTime=");
            sb.append(URLEncoder.encode(time, "UTF-8"));


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
