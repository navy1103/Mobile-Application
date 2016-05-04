package tacoma.uw.edu.tcss450.reminderproject.Modules;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tacoma.uw.edu.tcss450.reminderproject.Modules.Reminder.Routine;
import tacoma.uw.edu.tcss450.reminderproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoutineDetailFragment extends Fragment {

    private TextView mtoDateView;
    private TextView mbyTimeView;
    private TextView mnoteView;
    private TextView mcreated_atView;

    public static final String ROUTINE_ITEM_SELECTED = "routineItemSelected";

    public RoutineDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_routine_detail, container, false);
        mtoDateView = (TextView) view.findViewById(R.id.routine_toDate);
        mbyTimeView = (TextView) view.findViewById(R.id.routine_time);
        mnoteView = (TextView) view.findViewById(R.id.routine_note);

        FloatingActionButton floatingActionButton = (FloatingActionButton)
                getActivity().findViewById(R.id.fab);
        floatingActionButton.show();

        return view;
    }

    public void updateView(Routine routine) {
        if (routine != null) {
            mtoDateView.setText(routine.getRoutineToDate());
            mbyTimeView.setText(routine.getRoutineTime());
            mnoteView.setText(routine.getRoutineNote());
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

}
