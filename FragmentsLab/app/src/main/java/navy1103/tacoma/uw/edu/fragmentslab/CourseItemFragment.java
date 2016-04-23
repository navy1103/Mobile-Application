package navy1103.tacoma.uw.edu.fragmentslab;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import navy1103.tacoma.uw.edu.fragmentslab.Course.CourseContent;


/**
 * A simple {@link Fragment} subclass.
 */
public class CourseItemFragment extends Fragment {

    public static final String ARG_POSITION = "POSITION" ;
    private int mCurrentPosition = -1;

    public CourseItemFragment() {
        // Required empty public constructor
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
            updateCourseItemView(args.getInt(ARG_POSITION));
        } else if (mCurrentPosition != -1) {
            // Set article based on saved instance state defined during onCreateView
            updateCourseItemView(mCurrentPosition);
        }
    }
    public void updateCourseItemView(int pos) {
        TextView courseIdTextView = (TextView) getActivity().findViewById(R.id.course_id);
        courseIdTextView.setText((CharSequence) CourseContent.ITEMS.get(pos).id);
        TextView courseTitleTextView = (TextView) getActivity().findViewById(R.id.course_title);
        courseTitleTextView.setText((CharSequence) CourseContent.ITEMS.get(pos).content);
        TextView courseShortDescTextView = (TextView) getActivity().findViewById(R.id.course_detail);
        courseShortDescTextView.setText((CharSequence) CourseContent.ITEMS.get(pos).details);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course_item, container, false);
    }

}
