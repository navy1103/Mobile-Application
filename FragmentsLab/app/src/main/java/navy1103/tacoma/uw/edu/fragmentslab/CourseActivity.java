package navy1103.tacoma.uw.edu.fragmentslab;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import navy1103.tacoma.uw.edu.fragmentslab.Course.CourseContent;

public class CourseActivity extends AppCompatActivity  implements CourseListFragment.OnListFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        if (findViewById(R.id.fragment_container)!= null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new CourseListFragment())
                    .commit();

        }

    }

    @Override
    //public void onListFragmentInteraction(CourseContent.CourseItem item) {
    public void onListFragmentInteraction(int position) {
        // Capture the student fragment from the activity layout
        CourseItemFragment courseItemFragment = (CourseItemFragment)
                getSupportFragmentManager().findFragmentById(R.id.course_item_frag);

        if (courseItemFragment != null) {
            // If courseItem frag is available, we're in two-pane layout...

            // Call a method in the student fragment to update its content
            courseItemFragment.updateCourseItemView(position);

        } else {
            // If the frag is not available, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected student
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back

            courseItemFragment = new CourseItemFragment();
            Bundle args = new Bundle();
            args.putInt(CourseItemFragment.ARG_POSITION, position);
            courseItemFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, courseItemFragment)
                    .addToBackStack(null);

            // Commit the transaction
            transaction.commit();

        }

    }
}
