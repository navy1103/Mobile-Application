package tacoma.uw.edu.tcss450.Reminder.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * The local database class
 */
public class ReminderDB {
    /**
     * Database version
     */
    public static final int DB_VERSION = 1;
    /**
     * Database name
     */
    public static final String DB_NAME = "Reminder.db";
    /**
     * Database table name
     */
    private static final String REMINDER_TABLE = "Reminder";

    private ReminderDBHelper mReminderDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    /**
     * Constructor
     * @param context
     */
    public ReminderDB(Context context) {
        mReminderDBHelper = new ReminderDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mReminderDBHelper.getWritableDatabase();
    }


    /**
     * Inserts the reminder into the local sqlite table. Returns true if successful, false otherwise.
     * @param id is reminder id
     * @param setDate is date of reminder
     * @param setHour at hour to trigger alarm
     * @param setMin at min to trigger alarm
     * @param note is reminder note
     * @param email is email
     * @param phone is phone
     * @param location is location
     * @param username is username
     * @return database
     */
    public boolean insertReminder(String id, String setDate, String setHour, String setMin, String note,
                                  String email, String phone, String location, String username) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("setDate", setDate);
        contentValues.put("setHour", setHour);
        contentValues.put("setMin", setMin);
        contentValues.put("note", note);
        contentValues.put("email", email);
        contentValues.put("phone", phone);
        contentValues.put("location", location);
        contentValues.put("username", username);

        long rowId = mSQLiteDatabase.insert("Reminder", null, contentValues);
        return rowId != -1;
    }

    /**
     * Returns the list of reminders from the local Course table.
     * @return list
     */
    public List<Reminder> getReminder() {

        String[] columns = {"id", "setDate", "setHour", "setMin", "note", "email", "phone", "location", "username"};

        Cursor c = mSQLiteDatabase.query(
                REMINDER_TABLE,  // The table to query
                columns,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();
        List<Reminder> list = new ArrayList<Reminder>();
        for (int i=0; i<c.getCount(); i++) {
            String id = c.getString(0);
            String setDate = c.getString(1);
            String setHour = c.getString(2);
            String setMin = c.getString(3);
            String note = c.getString(4);
            String email = c.getString(5);
            String phone = c.getString(6);
            String location = c.getString(7);
            String username = c.getString(8);
            Reminder reminder = new Reminder(id, setDate, setHour, setMin, note, email, phone, location, username);
            list.add(reminder);
            c.moveToNext();
        }

        return list;
    }

    /**
     * Delete all the data from the REMINDER_TABLE
     */
    public void deleteReminder() {
        mSQLiteDatabase.delete(REMINDER_TABLE, null, null);
    }


    public void closeDB() {
        mSQLiteDatabase.close();
    }

    class ReminderDBHelper extends SQLiteOpenHelper {

        private static final String CREATE_REMINDER_SQL =
                "CREATE TABLE IF NOT EXISTS Reminder "
                        + "(id TEXT PRIMARY KEY, setDate TEXT, setHour TEXT, setMin TEXT, note TEXT, email TEXT, phone TEXT, location TEXT, username TEXT)";


        private static final String DROP_REMINDER_SQL =
                "DROP TABLE IF EXISTS Course";

        public ReminderDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_REMINDER_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int j) {
            sqLiteDatabase.execSQL(DROP_REMINDER_SQL);
            onCreate(sqLiteDatabase);
        }

    }
}
