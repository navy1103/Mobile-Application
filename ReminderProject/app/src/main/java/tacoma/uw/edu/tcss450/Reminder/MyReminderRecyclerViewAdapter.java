package tacoma.uw.edu.tcss450.Reminder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import tacoma.uw.edu.tcss450.Reminder.ReminderListFragment.OnListFragmentInteractionListener;
import tacoma.uw.edu.tcss450.Reminder.model.Reminder;
import tacoma.uw.edu.tcss450.reminderproject.R;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Reminder} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyReminderRecyclerViewAdapter extends RecyclerView.Adapter<MyReminderRecyclerViewAdapter.ViewHolder> {

    private final List<Reminder> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyReminderRecyclerViewAdapter(List<Reminder> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNote.setText(mValues.get(position).getReminderNote());
        holder.mDate.setText(mValues.get(position).getDate());
        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.deleteReminder(holder.mItem);
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNote;
        public final TextView mDate;
        public final Button delBtn;
        public Reminder mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNote = (TextView) view.findViewById(R.id.reminder_note);
            mDate = (TextView) view.findViewById(R.id.reminder_date);
            delBtn = (Button) view.findViewById(R.id.delete_btn);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDate.getText() + "'";
        }
    }
}
