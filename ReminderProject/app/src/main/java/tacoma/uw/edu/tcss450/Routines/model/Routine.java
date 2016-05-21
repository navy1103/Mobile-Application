package tacoma.uw.edu.tcss450.Routines.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Routine class implement Serializable. This allows us to the pass the object as a parameter.
 */
public class Routine implements Serializable {
    //The field of each Routine object
    private String mSetHour, mSetMin, mNote, mID;

    //These values must match the json names that we use on the web service.
    public static final String HOUR = "setHour", MIN = "setMin", NOTE = "note", ID = "id";

    /**
     * Constructor of the Routine class
     * @param setHour is the date of reminder
     * @param setMin is the time of reminder
     * @param note is the note for reminder
     * @param id is the id in the database
     */
    Routine(String setHour, String setMin, String note, String id){
        this.mSetHour = setHour;
        this.mSetMin = setMin;
        this.mNote = note;
        this.mID = id;
    }

    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns course list if success.
     * @param routineJSON is a String
     * @return reason or null if successful.
     */
    public static String parseRoutineJSON(String routineJSON, List<Routine> routinesList) {
        String reason = null;
        if (routineJSON != null) {
            try {
                JSONArray arr = new JSONArray(routineJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Routine routine = new Routine(obj.getString(Routine.HOUR), obj.getString(Routine.MIN)
                            , obj.getString(Routine.NOTE), obj.getString(Routine.ID));
                    routinesList.add(routine);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }
        }
        return reason;
    }

    /**
     * Get date of reminder
     * @return Date as string
     */
    public String getRoutineHour() {
        return mSetHour;
    }

    /**
     * Get note of reminder
     * @return Note as string
     */
    public String getRoutineNote() {
        return mNote;
    }

    /**
     * Get time of reminder
     * @return Date as string
     */
    public String getRoutineMin() {
        return mSetMin;
    }

    /**
     * Get id of reminder
     * @return Date as string
     */
    public String getID() {return mID;}
}
