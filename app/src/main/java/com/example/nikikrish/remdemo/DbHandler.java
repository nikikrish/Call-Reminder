package com.example.nikikrish.remdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DbHandler extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static  final  String DB_NAME ="CallReminder";
    private static final  String TABLE_NAME = "ReminderTable";
    private static final String ID = "id";
    private static final String ContactName = "ContactName";
    private static final String ContactNumber = "ContactNumber";
    private static final String CALL_TIME = "CallTime";
    private static final String ALARM_TIME = "AlarmTime";
    private static final String ENABLED = "Enabled";

    public DbHandler(Context c){
        super(c, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE "+
                TABLE_NAME + " ( "
                + ID +" INTEGER PRIMARY KEY, "
                + ContactName + " TEXT, "
                + ContactNumber+" TEXT, "
                + CALL_TIME +" TEXT, "
                + ALARM_TIME +" INTEGER, "
                + ENABLED +" TEXT )";

        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


    void addNewReminder(UserDetails details){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactName,details.getContactName());
        contentValues.put(ContactNumber,details.getContactNumber());
        contentValues.put(CALL_TIME,details.getCallTime());
        contentValues.put(ALARM_TIME,details.getDuration());
        contentValues.put(ENABLED,"true");

        db.insert(TABLE_NAME,null,contentValues);
        db.close();
    }

    List<UserDetails> getAllReminders(){

        List<UserDetails> userDetailsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+
                TABLE_NAME+" ORDER BY id DESC";

        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();

        while(c.moveToNext())
        {
            if(c.getCount()>0){
                UserDetails details = new UserDetails();
                details.setId(c.getInt(0));
                details.setContactName(c.getString(1));
                details.setContactNumber(c.getString(2));
                details.setCallTime(c.getString(3));
                details.setDuration(c.getString(4));
                details.setEnabled(c.getColumnName(5));
                userDetailsList.add(details);
            }
        }c.close();
        db.close();
        return  userDetailsList;
    }

    //Delete a single Entry
    void deleteReminder(UserDetails d){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, ID +"= ?",new String[]{String.valueOf(d.getId())});
        db.close();
    }


    //update individual logs
    void updateReminder(UserDetails details){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactName,details.getContactName());
        values.put(ContactNumber,details.getContactNumber());
        values.put(CALL_TIME,details.getCallTime());
        values.put(ALARM_TIME,details.getDuration());
        values.put(ENABLED,"true");
        db.update(TABLE_NAME,values, ID +" =?",new String[]{String.valueOf(details.getId())});
        db.close();
    }


    String[] getNotificationDetail(long schedule){

        String[] result=new String[]{};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_NAME,new String[]{ID,ContactName,ContactNumber, CALL_TIME},
                ALARM_TIME +" =? ",new String[]{String.valueOf(schedule)},
                null,
                null,
                null,
                null);
        c.moveToFirst();
        while (c.moveToNext()) {
            if (c.getCount() > 0) {
                Log.e("Cur Dump", DatabaseUtils.dumpCursorToString(c));
                result = new String[]{c.getString(0), c.getString(1), c.getString(2), c.getString(3)};
            }
        }
        c.close();
        return  result;
    }

    void disableNotification(long schedule){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ENABLED,"false");
        db.update(TABLE_NAME,values, ALARM_TIME +" =?",new String[]{String.valueOf(schedule)});
        db.close();
    }

    void snoozeNotification(long schedule, long updateTime){
        SQLiteDatabase helper = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_TIME,updateTime);
        helper.update(TABLE_NAME,values, ALARM_TIME + " =? ",new String[]{String.valueOf(schedule)});
        helper.close();
    }

    void deleteNotification(long schedule){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, ALARM_TIME +" =? ",new String[]{String.valueOf(schedule)});
        db.close();
    }
}
