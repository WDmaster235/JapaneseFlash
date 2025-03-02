package com.example.japaneseflash;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class HomeFragment extends Fragment {

    public HomeFragment() { }

    // Declare button references
    private MaterialButton btnKanjiMenu;
    private MaterialButton btnSavedCards;
    private MaterialButton btnDailyReminder;
    private MaterialButton btnAbout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate your fragment_home layout (the fanned-out card layout)
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize buttons (make sure IDs match your layout file)
        btnKanjiMenu = view.findViewById(R.id.btn_kanji_menu);
        btnSavedCards = view.findViewById(R.id.btn_saved_cards);
        btnDailyReminder = view.findViewById(R.id.btn_daily_reminder);
        btnAbout = view.findViewById(R.id.btn_about);

        // Set up click listeners
        btnKanjiMenu.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new KanjiMenuFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnSavedCards.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SavedCardsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnDailyReminder.setOnClickListener(v -> scheduleDailyReminder());

        btnAbout.setOnClickListener(v -> {
            // Implement your "About Kanji" action here
            Toast.makeText(getContext(), "About Kanji clicked", Toast.LENGTH_SHORT).show();
        });

        // Add touch (hover) effect to each button
        addTouchEffect(btnKanjiMenu);
        addTouchEffect(btnSavedCards);
        addTouchEffect(btnDailyReminder);
        addTouchEffect(btnAbout);

        return view;
    }

    private void addTouchEffect(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Scale up to 1.1x and bring this view to front
                    v.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).start();
                    v.bringToFront();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Scale back to original size
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start();
                    break;
            }
            // Return false so that onClick events are still triggered.
            return false;
        });
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
