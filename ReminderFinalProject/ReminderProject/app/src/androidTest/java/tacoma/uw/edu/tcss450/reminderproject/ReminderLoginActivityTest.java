package tacoma.uw.edu.tcss450.reminderproject;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

/**
 * Created by Navy on 6/3/2016.
 */
public class ReminderLoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    private Solo solo;

    public ReminderLoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        //tearDown() is run after a test case has finished.
        //finishOpenedActivities() will finish all the activities that have been opened during the test execution.
        solo.finishOpenedActivities();
    }

    public void testLogout() {
        solo.clickOnMenuItem("Log Out");
        boolean textFound = solo.searchText("Username");
        assertTrue("Login fragment loaded", textFound);
        solo.enterText(0, "navy11");
        solo.enterText(1, "12345678");
        solo.clickOnButton("Login");
        boolean worked = solo.searchText("Reminder List");
        assertTrue("Sign in worked!", worked);
    }
}
