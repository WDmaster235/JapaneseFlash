package com.example.japaneseflash;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 100;
    private static final String CHANNEL_ID = "default_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Create notification channel for Android Oreo and above.
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)  // Ensure you have an icon in your drawable resources.
                .setContentTitle("Daily Reminder")
                .setContentText("Time to learn something new!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Show the notification.
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        // Reschedule the next alarm 5 seconds later.
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent newIntent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                newIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long nextTriggerAtMillis = System.currentTimeMillis() + 600000;
        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTriggerAtMillis, pendingIntent);
        } catch (SecurityException e) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextTriggerAtMillis, pendingIntent);
        }
    }
}
