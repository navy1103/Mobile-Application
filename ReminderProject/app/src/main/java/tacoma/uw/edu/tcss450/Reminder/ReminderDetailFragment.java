package tacoma.uw.edu.tcss450.Reminder;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import tacoma.uw.edu.tcss450.Reminder.model.Reminder;
import tacoma.uw.edu.tcss450.Routines.RoutineAddFragment;
import tacoma.uw.edu.tcss450.reminderproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReminderDetailFragment extends Fragment {

    public static final String REMINDER_ITEM_SELECTED = "reminderItemSelected";
    //private static final String COURSE_UPDATE_URL = "http://cssgate.insttech.washington.edu/~navy1103/Reminder/getRoutines.php?cmd=reminder";

   // private DatePicker mDatePicker;
    //private TimePicker mTimePicker;

    private TextView mReminderDate, mReminderTime, mReminderNote, mReminderEmail,
            mReminderPhone, mReminderLocation;

    private UpdateListener mListener;
    public String id, hour, min;

    public interface UpdateListener{
        void updateReminder (String id, String date, String hour, String min, String note, String email, String phone, String location );
    }

    public ReminderDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_reminder_detail, container, false);

        getActivity().setTitle("Reminder Details");

        mReminderDate = (TextView) view.findViewById(R.id.reminder_detail_date);
        mReminderTime = (TextView) view.findViewById(R.id.reminder_detail_time);
        mReminderNote = (TextView) view.findViewById(R.id.reminder_detail_note);
        mReminderEmail = (TextView) view.findViewById(R.id.reminder_detail_email);
        mReminderPhone = (TextView) view.findViewById(R.id.reminder_detail_phone);
        mReminderLocation = (TextView) view.findViewById(R.id.reminder_detail_location);

        Button update = (Button) view.findViewById(R.id.reminder_update_btn);
        update.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mListener.updateReminder(id, mReminderDate.getText().toString(), hour, min,
                        mReminderNote.getText().toString(), mReminderEmail.getText().toString(),
                        mReminderPhone.getText().toString(), mReminderLocation.getText().toString());
            }
        });

        return view;
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_menu, menu);
    }

    public void updateView(Reminder reminder) {
        if (reminder != null) {
            mReminderDate.setText(reminder.getDate());
            mReminderTime.setText(reminder.getTime());
            mReminderNote.setText(reminder.getReminderNote());
            mReminderEmail.setText(reminder.getReminderEmail());
            mReminderPhone.setText(reminder.getReminderPhone());
            mReminderLocation.setText(reminder.getReminderLocation());
            id = reminder.getReminderID();
            hour = reminder.getReminderHour();
            min = reminder.getReminderMin();

            Log.i("Routine ID", reminder.getReminderID());
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
            updateView((Reminder) args.getSerializable(REMINDER_ITEM_SELECTED));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UpdateListener) {
            mListener = (UpdateListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RoutineListener");
        }
    }
}
