package com.example.nikikrish.remdemo;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.WINDOW_SERVICE;
import static android.graphics.PixelFormat.TRANSPARENT;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //getCallDetails(context);
        Log.i("Intent Received",intent.getAction()+" ");
        DbHandler db;
        String[] details;
        long reminderTime;
        switch (intent.getAction()){
            case "Reminder":
                reminderTime = intent.getLongExtra("reminderTime",0);
                Log.e("ReminderTime",reminderTime+" ");
                db = new DbHandler(context);
                details = db.getNotificationDetail(reminderTime);
                db.disableNotification(reminderTime);
                for(String t : details){
                    Log.e("AR details",t);
                }
                //Trigger the notification
                NotificationHelper.showNotification(context, MainActivity.class,
                        details);
                NotificationHelper.cancelNotification();
                db.close();
                break;
            case "NewCall":
                long schedule = intent.getLongExtra("reminderTime",0);
                db = new DbHandler(context);
                details = db.getNotificationDetail(schedule);
                db.close();
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(100);
                Intent i = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:"+details[2].trim()));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                break;
            case "SnoozeNotification":
                schedule = intent.getLongExtra("reminderTime",0);
                db = new DbHandler(context);
                DateTime dateTime = new DateTime().plusMinutes(15).withSecondOfMinute(0);
                db.snoozeNotification(schedule,dateTime.getMillis());
                NotificationHelper.setNotification(AlarmReceiver.class,context,new DateTime().plusMinutes(15).getMillis());
                Toast t = Toast.makeText(context,"Snoozed for 15 Minutes",Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
                t.show();
                db.close();

                manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(100);
                break;
            case "DeleteNotification":
                schedule = intent.getLongExtra("reminderTime",0);
                db = new DbHandler(context);
                db.deleteNotification(schedule);
                db.close();
                t = Toast.makeText(context,"Reminder Deleted",Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
                t.show();
                db.close();

                manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(100);
                break;
            default:
                Log.e("Intent",intent.getAction()+" AReceiver");
        }
    }
    }
