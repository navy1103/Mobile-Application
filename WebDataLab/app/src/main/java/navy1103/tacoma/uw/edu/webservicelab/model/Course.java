package navy1103.tacoma.uw.edu.webservicelab.model;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import navy1103.tacoma.uw.edu.webservicelab.CourseDetailFragment;
import navy1103.tacoma.uw.edu.webservicelab.R;

/**
 * Course class implement Serializable. This allows us to the pass the object as a parameter.
 */
public class Course implements Serializable {
    private String mcourseID, mshortDescription, mlongDescription, mprereqs;

    //These values must match the json names that we use on the web service.
    public static final String ID = "id", SHORT_DESC = "shortDesc", LONG_DESC = "longDesc", PRE_REQS = "prereqs";

    public Course(String courseID, String shortDescription, String longDescription, String prereqs){
        this.mcourseID = courseID;
        this.mshortDescription = shortDescription;
        this.mlongDescription = longDescription;
        this.mprereqs = prereqs;
    }

    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns course list if success.
     * @param courseJSON
     * @return reason or null if successful.
     */
    public static String parseCourseJSON(String courseJSON, List<Course> courseList) {
        String reason = null;
        if (courseJSON != null) {
            try {
                JSONArray arr = new JSONArray(courseJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Course course = new Course(obj.getString(Course.ID), obj.getString(Course.SHORT_DESC)
                            , obj.getString(Course.LONG_DESC), obj.getString(Course.PRE_REQS));
                    courseList.add(course);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }

        }
        return reason;
    }

    public String getCourseId(){
        return this.mcourseID;
    }

    public String getShortDescription() {
        return mshortDescription;
    }

    public String getLongDescription() { return mlongDescription;    }

    public String getPrereqs() { return mprereqs;}

    public void setCourseId(String courseId) {
        this.mcourseID = courseId;
    }
}
