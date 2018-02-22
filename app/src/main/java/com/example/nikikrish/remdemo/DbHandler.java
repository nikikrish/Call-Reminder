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

/**
 * Created by Nikikrish on 22-Feb-18.
 */

public class DbHandler extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;
    private static  final  String DB_NAME ="RemindTable";
    private static final  String TABLE_NAME = "Reminder";
    private static final String _id = "id";
    private static final String ContactName = "ContactName";
    private static final String ContactNumber = "ContactNumber";
    private static final String profilePath = "ProfilePath";
    private static final String callTime = "CallTime";
    private static final String duration = "Duration";
    private static final String enabled = "Enabled";

    public DbHandler(Context c){
        super(c, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE "+ TABLE_NAME + " ( "+
                _id +" INTEGER PRIMARY KEY, "
                + ContactName + " TEXT, "
                + ContactNumber+" TEXT, "
                + profilePath +" TEXT, "
                + callTime+" TEXT, "
                + duration+" INTEGER, "
                + enabled+" TEXT )";

        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);

        onCreate(db);

    }

    void addNewReminder(UserDetails details){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactName,details.contactName);
        contentValues.put(ContactNumber,details.contactNumber);
        contentValues.put(callTime,details.callTime);
        contentValues.put(profilePath,details.profilePath);
        contentValues.put(duration,details.duration);
        contentValues.put(enabled,"true");

        db.insert(TABLE_NAME,null,contentValues);
        db.close();
    }

    List<UserDetails> getAllReminders(){

        List<UserDetails> userDetailsList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT id," +
                "ContactName," +
                "ContactNumber," +
                "ProfilePath," +
                "CallTime," +
                "Duration," +
                "Enabled FROM "+
                TABLE_NAME+" ORDER BY id DESC";

        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();

        do{
            if(c.getCount()>0){
                UserDetails details = new UserDetails();
                details.setId(c.getInt(0));
                details.setContactName(c.getString(1));
                details.setContactNumber(c.getString(2));
                details.setProfilePath(c.getString(3));
                details.setCallTime(c.getString(4));
                details.setDuration(c.getString(5));
                details.setEnabled(c.getColumnName(6));
                userDetailsList.add(details);
            }

        }while(c.moveToNext());

        c.close();
        db.close();
        return  userDetailsList;
    }

    //Delete a single Entry
    void deleteReminder(UserDetails d){

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME,_id+"= ?",new String[]{String.valueOf(d.getId())});
        db.close();
    }

    //get the count
    int getCount(){
        int count;
        String query = "SELECT * FROM "+TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query,null);

        count = c.getCount();
        c.close();
        return count;
    }

    //update individual logs
    void updateReminder(UserDetails details){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactName,details.contactName);
        values.put(ContactNumber,details.contactNumber);
        values.put(callTime,details.callTime);
        values.put(duration,details.duration);
        values.put(enabled,"true");
        db.update(TABLE_NAME,values,_id+" =?",new String[]{String.valueOf(details.id)});
        db.close();
    }


    String[] getNotificationDetail(long schedule){

        String[] result=new String[]{};
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.query(TABLE_NAME,new String[]{_id,ContactName,ContactNumber,callTime},
                duration +" =? ",new String[]{String.valueOf(schedule)},null,null,null,null);

        c.moveToFirst();

        do{
            if(c.getCount()>0){
                Log.e("Cur Dump", DatabaseUtils.dumpCursorToString(c));
                result = new String[]{c.getString(0),c.getString(1),c.getString(2),c.getString(3)};
            }
        }while(c.moveToNext());
        c.close();

        //disableNotification(schedule);

        return  result;
    }

    void disableNotification(long schedule){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(enabled,"false");

        db.update(TABLE_NAME,values,duration+" =?",new String[]{String.valueOf(schedule)});
        db.close();
    }

    void snoozeNotification(long schedule, long updateTime){

        SQLiteDatabase helper = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(duration,updateTime);

        helper.update(TABLE_NAME,values,duration+ " =? ",new String[]{String.valueOf(schedule)});
        helper.close();
    }

    void deleteNotification(long schedule){

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME,duration+" =? ",new String[]{String.valueOf(schedule)});
        db.close();
    }
}
