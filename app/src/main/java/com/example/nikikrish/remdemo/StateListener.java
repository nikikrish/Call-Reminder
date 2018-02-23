package com.example.nikikrish.remdemo;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.Calendar;

import static android.content.Context.WINDOW_SERVICE;
import static android.graphics.PixelFormat.TRANSPARENT;

public class StateListener extends BroadcastReceiver {
    public StateListener() {
        super();
    }
    Context mContext;
    DbHandler db;
    DateTime dateTime = new DateTime();

    @Override
    public void onReceive(final Context context, Intent intent) {
        mContext = context;

        Bundle bundle = intent.getExtras();
            final String number = bundle.getString("incoming_number");
            if((bundle.get("state").equals("IDLE"))){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String callLogDetails[];
                        callLogDetails = getCallDetails(mContext,number);
                        //find the difference between current time and time at which call was made
                        long difference = ((System.currentTimeMillis()/1000)-(Long.parseLong(callLogDetails[1])/1000));
                        if (callLogDetails[0].equals("0")){
                            //display prompt for that number
                            displayPrompt(number);
                            }

                    }
                },1000);

            }

    }

    private String getContact(String number){

        //project only the name number and profile uri
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        String name ="";

        //return values that match the number
        Cursor cursor = mContext.getContentResolver().
                query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                ContactsContract.CommonDataKinds.Phone.NUMBER+ " =? ",
                new String[]{number},null);

        cursor.moveToFirst();

        if(cursor.getCount()>0){

            name  = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            cursor.close();
            return name;
        }
        return name;
    }


    private String[] getCallDetails(Context context,String phNumber) {

        String[] projection = new String[]{
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION,
                CallLog.Calls.TYPE,
                CallLog.Calls.CACHED_NAME
        };

        @SuppressLint("MissingPermission")
        Cursor cursor = context.getContentResolver()
                .query(CallLog.Calls.CONTENT_URI,
                projection,
                CallLog.Calls.TYPE + " =? "+" AND "+ CallLog.Calls.NUMBER + " =? ",
                new String[]{"2",phNumber},
                CallLog.Calls.DATE + " DESC");

        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        Log.e("Call Details",cursor.getColumnCount()+" name "+ duration);
        String[] temp = new String[0];
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            temp = new String[]{
                    cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION)),
                    cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE))};

        }
        //if cursor is empty return dummy values
        else{
            return new String[]{"",""};
        }
        cursor.close();
        return temp;
    }

    private void displayPrompt(String number){

        final String contactName = getContact(number);
        final String contactNumber = number;
        final WindowManager windowManager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                                        WindowManager.LayoutParams.MATCH_PARENT,
                                        WindowManager.LayoutParams.WRAP_CONTENT,
                                        WindowManager.LayoutParams.TYPE_PHONE ,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                        TRANSPARENT);

        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.x = 0;
        params.y = 0;
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.format = PixelFormat.TRANSLUCENT;


        final LinearLayout ly = new LinearLayout(mContext);
        ly.setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.popup, ly,false);

        ImageView closeButton;
        final TextView userName,userPhoneNumber;
        Button fifteenBtn,thirtyBtn,customBtn;

        userName = view.findViewById(R.id.userName);
        userPhoneNumber = view.findViewById(R.id.userPhoneNumber);
        fifteenBtn = view.findViewById(R.id.fifteenButton);
        thirtyBtn = view.findViewById(R.id.thirtyButton);
        customBtn = view.findViewById(R.id.customButton);
        closeButton = view.findViewById(R.id.closeButton);

        if(contactName.isEmpty()){
            userName.setText(contactNumber);
            userPhoneNumber.setVisibility(View.INVISIBLE);
        }else{
            userName.setText(contactName);
            userPhoneNumber.setText(contactNumber);
        }
        fifteenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTime = new DateTime().plusMinutes(15);
                createNewReminder("Fifteen",dateTime.getMillis(),contactName,contactNumber);
                windowManager.removeViewImmediate(ly);
            }
        });
        thirtyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTime = new DateTime().plusMinutes(30);
                createNewReminder("Thirty",dateTime.getMillis(),contactName,contactNumber);
                windowManager.removeViewImmediate(ly);


            }
        });
        customBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog pickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        //create a DateTime object for the time selected from the picker
                        dateTime = new DateTime().withHourOfDay(hour).withMinuteOfHour(minute).withSecondOfMinute(0);
                       createNewReminder("Custom",dateTime.getMillis(),contactName,contactNumber);
                        windowManager.removeViewImmediate(ly);
                    }
                },Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                        Calendar.getInstance().get(Calendar.MINUTE),
                        false);
                //set LayoutParams type as TYPE_PHONE
                pickerDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
                //show picker
                pickerDialog.show();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                windowManager.removeViewImmediate(ly);
            }
        });

        ly.addView(view);

        windowManager.addView(ly, params);

    }

    private void createNewReminder(String type,long time,String contactName,String contactNumber){
        db = new DbHandler(mContext);
        if(contactName.isEmpty()){
            db.addNewReminder(
                    new UserDetails("unknown",
                            contactNumber,
                            String.valueOf(System.currentTimeMillis()),
                            String.valueOf(time)));

        }else{
            db.addNewReminder(
                    new UserDetails(contactName,
                            contactNumber,
                            String.valueOf(System.currentTimeMillis()),
                            String.valueOf(time)));

        }

        Toast.makeText(mContext,"Reminder Set for "+type+" Minutes",Toast.LENGTH_LONG).show();
        NotificationHelper.setNotification(AlarmReceiver.class,mContext,dateTime.getMillis());

    }
}
