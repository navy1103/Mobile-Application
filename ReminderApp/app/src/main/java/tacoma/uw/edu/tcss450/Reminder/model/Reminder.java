package tacoma.uw.edu.tcss450.Reminder.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Reminder implements Serializable {
    //The field of each Routine object
    private String rDate, rUsername, rHour, rMin, rNote, rEmail, rPhone, rLocation, rID;

    //These values must match the json names that we use on the web service.
    public static final String DATE = "setDate", USERNAME = "username", HOUR = "setHour", MIN = "setMin";
    public static final String NOTE = "note", EMAIL = "email", PHONE = "phone", LOCATION = "location", ID = "id";

    /**
     * The constructor
     * @param id of reminder
     * @param date of reminder
     * @param hour of reminder
     * @param min of reminder
     * @param note of reminder
     * @param email of reminder
     * @param phone of reminder
     * @param location of reminder
     * @param username of reminder
     */
    public Reminder(String id, String date, String hour, String min, String note, String email, String phone, String location, String username){
        this.rID = id;
        this.rDate = date;
        this.rHour = hour;
        this.rMin = min;
        this.rNote = note;
        this.rEmail = email;
        this.rPhone = phone;
        this.rLocation = location;
        this.rUsername = username;
    }

    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns course list if success.
     * @param routineJSON is a String
     * @return reason or null if successful.
     */
    public static String parseReminderJSON(String routineJSON, List<Reminder> routinesList) {
        String reason = null;
        if (routineJSON != null) {
            try {
                JSONArray arr = new JSONArray(routineJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Reminder reminder = new Reminder(obj.getString(Reminder.ID),
                            obj.getString(Reminder.DATE), obj.getString(Reminder.HOUR),
                            obj.getString(Reminder.MIN), obj.getString(Reminder.NOTE),
                            obj.getString(Reminder.EMAIL), obj.getString(Reminder.PHONE),
                            obj.getString(Reminder.LOCATION), obj.getString(Reminder.USERNAME));
                    routinesList.add(reminder);
                }
            } catch (JSONException e) {
                //reason =  "Unable to parse data, Reason: " + e.getMessage();
                reason = "You dont have any reminder in the list!";
            }
        }
        return reason;
    }

    /**
     * Get the date
     * @return The date as string
     */
    public String getDate(){return rDate;}

    /**
     * Get the time and set as 00:00 am/pm
     * @return the time in am/pm
     */
    public String getTime(){
        String toReturn = "";
        int hour = Integer.parseInt(rHour);
        if( hour > 11){
            if(hour > 12) hour -= 12;

            toReturn =  hour + " : " + rMin + " PM";
        } else {
            toReturn =  hour + " : " + rMin + " AM";
        }
        return toReturn;
    }

    /**
     * Get an hour
     * @return an hour
     */
    public String getReminderHour() {
        return rHour;
    }

    /**
     * Get a minute
     * @return minute
     */
    public String getReminderMin() {
        return rMin;
    }

    /**
     * Get reminder note
     * @return a reminder note
     */
    public String getReminderNote() {
        return rNote;
    }

    /**
     * Get reminder email
     * @return an email
     */
    public String getReminderEmail() {
        return rEmail;
    }

    /**
     * Get the phone #
     * @return phone #
     */
    public String getReminderPhone() {
        return rPhone;
    }

    /**
     * Get the location
     * @return a location
     */
    public String getReminderLocation() {
        return rLocation;
    }

    /**
     * Get the reminder id
     * @return reminder id
     */
    public String getReminderID() {
        return rID;
    }

    /**
     * Get the username
     * @return username as string
     */
    public String getUsername() {return rUsername;}
}
