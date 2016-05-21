package navy1103.tacoma.uw.edu.webservicelab;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import navy1103.tacoma.uw.edu.webservicelab.model.Course;

/**
 * Created by Navy on 5/11/2016.
 */
public class CourseTest extends TestCase {
    private Course mCourse;

    @Before
    public void setUp() {
        mCourse = new Course("CSS360", "description", "long description", "prereqs");
    }

    @Test
    public void testSetNullCourseId() {
        try {
            mCourse.setCourseId(null);
            fail("Course Id can be set to null");
        }
        catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testConstructor() {
        Course course = new Course ("TCSS450", "Mobile Application Programming", "Android Programming", "TCSS360");

        assertNotNull(course);
    }

    @Test
    public void testParseCourseJSON() {
        String courseJSON = "[{\"id\":\"TCSS450\",\"shortDesc\":\"Mobile App Programming\",\"longDesc\":\"Covers mobile principles\",\"prereqs\":\"TCSS360\"},{\"id\":\"TCSS445\",\"shortDesc\":\"Database Systems Design\",\"longDesc\":\"Covers database principles\",\"prereqs\":\"TCSS342\"}]";
        String message =  Course.parseCourseJSON(courseJSON
                , new ArrayList<Course>());
        assertTrue("JSON With Valid String", message == null);
    }

    @Test
    public void testSetLengthCourseId() {
        try {
            mCourse.setCourseId("15");
            fail("Course Id can be set to less than 5 characters long");
        }
        catch (IllegalArgumentException e) {

        }
    }

    @Test
    public void testGetCourseId() {
        assertEquals("CSS360", mCourse.getCourseId());
    }

}
