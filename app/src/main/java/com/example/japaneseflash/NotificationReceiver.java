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
        Log.d(TAG, "Alarm triggered! Displaying daily notification...");
        Toast.makeText(context, "Alarm Triggered!", Toast.LENGTH_LONG).show();

        if (intent != null && "com.example.japaneseflash.NOTIFY".equals(intent.getAction())) {
            Log.d(TAG, "Correct intent action received!");
            NotificationHelper.displayNotification(context);
        } else {
            Log.e(TAG, "Incorrect intent received! Alarm might not be working.");
        }
    }
}
