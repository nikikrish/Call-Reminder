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


class NotificationHelper {

    private static PendingIntent pi;
    private static AlarmManager alarmManager;

    static void setNotification(Class<?> cls,Context context,long time){

        Intent intent = new Intent(context, cls);
        intent.setAction("Reminder");
        intent.putExtra("reminderTime",time);
        pi = PendingIntent.getBroadcast(context, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pi);

        SharedPreferences preferences = context.getSharedPreferences("CallBackPreference",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("reminderTime",time);
        editor.apply();

       }

    static void cancelNotification(){
        pi.cancel();
        alarmManager.cancel(pi);

    }

    static void showNotification(Context context, Class<?> cls, String[] notificationDetails){

        int REQUEST_CODE = 108;

        SharedPreferences preferences = context.getSharedPreferences("CallBackPreference",0);
        for(String s: notificationDetails){
            Log.e("Notify details",s);
        }
        String title = String.format("Call %s back !!",notificationDetails[1]);
        String callTime = new DateTime().withMillis(Long.parseLong(notificationDetails[3])).toString("HH:mm");
        String content = String.format("You tried calling %s at %s ",notificationDetails[1],callTime);


        Intent call = new Intent(context,AlarmReceiver.class);
        Intent snooze = new Intent(context,AlarmReceiver.class);
        Intent delete = new Intent(context,AlarmReceiver.class);
        Intent contentIntent = new Intent(context,MainActivity.class);

        call.setAction("NewCall");
        call.putExtra("reminderTime",preferences.getLong("reminderTime",0));
        snooze.setAction("SnoozeNotification");
        snooze.putExtra("reminderTime",preferences.getLong("reminderTime",0));
        delete.setAction("DeleteNotification");
        delete.putExtra("reminderTime",preferences.getLong("reminderTime",0));

        PendingIntent piCall = PendingIntent.getBroadcast(context,REQUEST_CODE,call,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent piSnooze = PendingIntent.getBroadcast(context,REQUEST_CODE,snooze,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent piDelete = PendingIntent.getBroadcast(context,REQUEST_CODE,delete,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent piContentIntent = PendingIntent.getActivity(context,REQUEST_CODE,contentIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        Intent notificationIntent = new Intent(context,cls);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(cls);
        taskStackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(REQUEST_CODE,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Notification notification = builder.setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle(builder))
                .setContentIntent(piContentIntent)
                .addAction(R.drawable.delete,"Delete",piDelete)
                .addAction(R.drawable.snooze,"Snooze",piSnooze)
                .addAction(R.drawable.ic_call_black_24dp,"Call",piCall)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(REQUEST_CODE, notification);

    }
}
