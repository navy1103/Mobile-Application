package tacoma.uw.edu.tcss450.Modules.Reminder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Routine class implement Serializable. This allows us to the pass the object as a parameter.
 */
public class Routine implements Serializable {
    private String mtoDate, mbyTime, mnote, mID;
    //These values must match the json names that we use on the web service.
    public static final String TODATE = "toDate", BYTIME = "byTime", NOTE = "note", ID = "id";

    Routine(String toDate, String byTime, String note, String id){
        this.mtoDate = toDate;
        this.mbyTime = byTime;
        this.mnote = note;
        this.mID = id;
    }

    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns course list if success.
     * @param routineJSON
     * @return reason or null if successful.
     */
    public static String parseRoutineJSON(String routineJSON, List<Routine> routinesList) {
        String reason = null;
        if (routineJSON != null) {
            try {
                JSONArray arr = new JSONArray(routineJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Routine routine = new Routine(obj.getString(Routine.TODATE), obj.getString(Routine.BYTIME)
                            , obj.getString(Routine.NOTE), obj.getString(Routine.ID));
                    routinesList.add(routine);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }

        }
        return reason;
    }


    public String getRoutineToDate() {
        return mtoDate;
    }

    public String getRoutineNote() {
        return mnote;
    }

    public String getRoutineTime() {
        return mbyTime;
    }

    public String getID() {return mID;}
}
