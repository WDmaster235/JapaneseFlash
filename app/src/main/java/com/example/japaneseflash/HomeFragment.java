package com.example.japaneseflash;

import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class HomeFragment extends Fragment {

    public HomeFragment() { }

    private MaterialButton btnKanjiMenu;
    private MaterialButton btnSavedCards;
    private MaterialButton btnDailyReminder;
    private MaterialButton btnAbout;

    private View currentActiveCard = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnKanjiMenu = view.findViewById(R.id.btn_kanji_menu);
        btnSavedCards = view.findViewById(R.id.btn_saved_cards);
        btnDailyReminder = view.findViewById(R.id.btn_daily_reminder);
        btnAbout = view.findViewById(R.id.btn_about);

        btnKanjiMenu.setOnClickListener(v -> navigateToKanjiMenu());
        btnSavedCards.setOnClickListener(v -> navigateToSavedCards());
        btnDailyReminder.setOnClickListener(v -> scheduleDailyReminder());
        btnAbout.setOnClickListener(v -> navigateToAbout());

        addClickEffect(btnKanjiMenu);
        addClickEffect(btnSavedCards);
        addClickEffect(btnDailyReminder);
        addClickEffect(btnAbout);

        return view;
    }

    private void navigateToKanjiMenu() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new KanjiMenuFragment())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToSavedCards() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SavedCardsFragment())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToAbout() {
        Toast.makeText(getContext(), "About Kanji clicked", Toast.LENGTH_SHORT).show();
    }

    private void scheduleDailyReminder() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getActivity(),
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long triggerAtMillis = System.currentTimeMillis() + 5000;

        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } catch (SecurityException e) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }

        Toast.makeText(getActivity(), "Daily reminder set (fires every 5 seconds)", Toast.LENGTH_SHORT).show();
    }

    private void addClickEffect(View card) {
        card.setOnClickListener(v -> {
            if (v != currentActiveCard) {
                if (currentActiveCard != null) {
                    moveCard(currentActiveCard, 0);
                }
                moveCard(v, -dpToPx(200));
                currentActiveCard = v;
            }
        });
    }

    private void moveCard(View card, float translationY) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(card, "translationY", translationY);
        animator.setDuration(200);
        animator.start();
    }

    private float dpToPx(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
