package com.example.japaneseflash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm triggered! Showing notification...");
        Toast.makeText(context, "Alarm Triggered!", Toast.LENGTH_LONG).show(); // Debugging
        NotificationHelper.showWeatherNotification(context);
    }
}
