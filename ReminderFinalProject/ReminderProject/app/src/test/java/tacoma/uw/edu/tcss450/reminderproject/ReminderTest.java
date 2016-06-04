package tacoma.uw.edu.tcss450.reminderproject;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import tacoma.uw.edu.tcss450.Reminder.model.Reminder;

/**
 * Created by Navy on 6/3/2016.
 */
public class ReminderTest extends TestCase {
    private Reminder rem;

    @Before
    public void setUp() {
        rem = new Reminder("101", "2001-1-1", "1", "1", "note","email","phone","location", "test");
    }

    //Reminder(String id, String date, String hour, String min, String note, String email, String phone, String location, String username)
    @Test
    public void testContructor(){
        Reminder reminder = new Reminder("1", "2001-1-1", "1", "1", "note","email","phone","location", "test");
        assertNotNull(reminder);
    }

    @Test
    public void testParseCourseJSON() {
        String courseJSON = "[{\"id\":\"1\",\"date\":\"2001-1-1\",\"hour\":\"1\",\"min\":\"1\",\"note\":\"note\",\"email\":\"email\",\"phone\":\"phone\",\"location\":\"location\",\"username\":\"user\"}," +
                "{\"id\":\"1\",\"date\":\"2001-1-1\",\"hour\":\"1\",\"min\":\"1\",\"note\":\"note\",\"email\":\"email\",\"phone\":\"phone\",\"location\":\"location\",\"username\":\"user\"}]";
        String message =  Reminder.parseReminderJSON(courseJSON, new ArrayList<Reminder>());
        assertTrue("JSON With Valid String", message == null);
    }

    @Test
    public void testGetDate(){
        assertEquals("2001-1-1", rem.getDate());
    }

    @Test
    public void testGetTime(){
        assertEquals("1 : 1 AM", rem.getTime());
    }

    @Test
    public void testGetReminderHour(){
        assertEquals("1", rem.getReminderHour());
    }

    @Test
    public void testGetReminderMin(){
        assertEquals("1", rem.getReminderMin());
    }

    @Test
    public void testGetReminderNote(){
        assertEquals("note", rem.getReminderNote());
    }

    @Test
    public void testGetReminderEmail(){
        assertEquals("email", rem.getReminderEmail());
    }

    @Test
    public void testGetReminderPhone(){
        assertEquals("phone", rem.getReminderPhone());
    }

    @Test
    public void testGetReminderLocation(){
        assertEquals("location", rem.getReminderLocation());
    }

    @Test
    public void testGetReminderID(){
        assertEquals("101", rem.getReminderID());
    }

    @Test
    public void testGetUsername(){
        assertEquals("test", rem.getUsername());
    }
}
