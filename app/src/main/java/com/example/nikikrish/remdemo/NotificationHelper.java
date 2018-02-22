package com.example.nikikrish.remdemo;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.joda.time.DateTime;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Nikikrish on 21-Feb-18.
 */

public class NotificationHelper {

    private static PendingIntent pi;
    private static AlarmManager alarmManager;

    static void setNotification(Class<?> cls,Context context,long time){

        Intent intent = new Intent(context, cls);
        intent.setAction("Reminder");
        intent.putExtra("reminderTime",time);
        pi = PendingIntent.getBroadcast(context, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.e(" Current time",System.currentTimeMillis()+" "+time);
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        SharedPreferences preferences = context.getSharedPreferences("CallBackPreference",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("reminderTime",time);
        editor.apply();

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pi);
       // NotificationHelper.showNotification(AlarmReceiver.class,context,System.currentTimeMillis());
    }

    static void cancelNotification(){
        pi.cancel();
        alarmManager.cancel(pi);

    }

    static void showNotification(Context context, Class<?> cls, String[] values){

        int REQUEST_CODE = 100;
        SharedPreferences preferences = context.getSharedPreferences("CallBackPreference",0);

        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        for(String s:values){
            Log.i("Notification Handler",s+" Handler");
        }
        String title = String.format("Call %s back !!",values[1]);
        String callTime = new DateTime().withMillis(Long.parseLong(values[3])).toString("HH:mm");
        String content = String.format("You tried calling %s at %s ",values[1],callTime);
        Log.e("Content",content);

        Intent callIntent = new Intent(context,AlarmReceiver.class);
        callIntent.setAction("NewCall");
        callIntent.putExtra("reminderTime",preferences.getLong("reminderTime",0));
        PendingIntent piCall = PendingIntent.getBroadcast(context,REQUEST_CODE,callIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent snoozeIntent = new Intent(context,AlarmReceiver.class);
        snoozeIntent.setAction("SnoozeNotification");
        snoozeIntent.putExtra("reminderTime",preferences.getLong("reminderTime",0));
        PendingIntent piSnooze = PendingIntent.getBroadcast(context,REQUEST_CODE,snoozeIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent deleteIntent = new Intent(context,AlarmReceiver.class);
        deleteIntent.setAction("DeleteNotification");
        deleteIntent.putExtra("reminderTime",preferences.getLong("reminderTime",0));
        PendingIntent piDelete = PendingIntent.getBroadcast(context,REQUEST_CODE,deleteIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent contentIntent = new Intent(context,MainActivity.class);
        PendingIntent piContentIntent = PendingIntent.getActivity(context,REQUEST_CODE,contentIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        Intent notificationIntent = new Intent(context,cls);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(REQUEST_CODE,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Notification notification = builder.setContentTitle(title)
                .setTicker("Call em Back")
                .setContentText(content)
                .setSound(ringtone)
                .setStyle(new NotificationCompat.BigTextStyle(builder))
                .setContentIntent(piContentIntent)
                .addAction(R.drawable.ic_call_black_24dp,"Call",piCall)
                .addAction(R.drawable.snooze,"Snooze",piSnooze)
                .addAction(R.drawable.delete,"Delete",piDelete)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(REQUEST_CODE, notification);

    }
}
