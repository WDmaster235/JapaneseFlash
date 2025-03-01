package com.example.japaneseflash;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    public HomeFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate your fragment_home layout
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Existing buttons for Kanji Menu and Saved Cards
        view.findViewById(R.id.btn_kanji_menu).setOnClickListener(v -> {
            // Navigate to KanjiMenuFragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new KanjiMenuFragment())
                    .addToBackStack(null)
                    .commit();
        });

        view.findViewById(R.id.btn_saved_cards).setOnClickListener(v -> {
            // Navigate to SavedCardsFragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SavedCardsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // New button for daily reminder
        view.findViewById(R.id.btn_daily_reminder).setOnClickListener(v -> scheduleDailyReminder());

        return view;
    }

    private void scheduleDailyReminder() {
        // Get the AlarmManager service
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        // Create an Intent to trigger NotificationReceiver
        Intent intent = new Intent(getActivity(), NotificationReceiver.class);
        // Use a unique request code (here 1) so this PendingIntent doesn't conflict with others.
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getActivity(),
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Set the first trigger 5 seconds from now.
        long triggerAtMillis = System.currentTimeMillis() + 5000;

        try {
            // Try to schedule an exact alarm.
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } catch (SecurityException e) {
            // Fallback to an inexact alarm if exact alarms are not allowed.
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }

        Toast.makeText(getActivity(), "Daily reminder set (fires every 5 seconds)", Toast.LENGTH_SHORT).show();
    }
}
