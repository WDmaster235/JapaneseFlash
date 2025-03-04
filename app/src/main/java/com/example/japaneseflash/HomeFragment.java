package com.example.japaneseflash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    public HomeFragment() { }

    private MaterialButton btnKanjiMenu;
    private MaterialButton btnSavedCards;
    private MaterialButton btnDailyReminder;
    private MaterialButton btnAbout;

    // Holds the button that is currently "up"
    private View currentActiveCard = null;

    private static final String TAG = "HomeFragment";

    // Container class to store original translation and rotation values.
    private static class OriginalPosition {
        float origX;
        float origY;
        float origRotation;

        OriginalPosition(float origX, float origY, float origRotation) {
            this.origX = origX;
            this.origY = origY;
            this.origRotation = origRotation;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnKanjiMenu = view.findViewById(R.id.btn_kanji_menu);
        btnSavedCards = view.findViewById(R.id.btn_saved_cards);
        btnDailyReminder = view.findViewById(R.id.btn_daily_reminder);
        btnAbout = view.findViewById(R.id.btn_about);

        // Save original values
        saveOriginalValues(btnKanjiMenu);
        saveOriginalValues(btnSavedCards);
        saveOriginalValues(btnDailyReminder);
        saveOriginalValues(btnAbout);

        // Set up click listeners with handleClick() and corresponding navigation or action.
        btnKanjiMenu.setOnClickListener(v -> handleClick(v, this::navigateToKanjiMenu));
        btnSavedCards.setOnClickListener(v -> handleClick(v, this::navigateToSavedCards));
        btnDailyReminder.setOnClickListener(v -> handleClick(v, this::setDailyNotification));
        btnAbout.setOnClickListener(v -> handleClick(v, this::navigateToAbout));

        return view;
    }

    /**
     * Save original translationX, translationY, and rotation values in the view's tag.
     */
    private void saveOriginalValues(View view) {
        OriginalPosition pos = new OriginalPosition(view.getTranslationX(), view.getTranslationY(), view.getRotation());
        view.setTag(pos);
    }

    /**
     * If the card is not active, raise it. If it is active, perform a special effect then execute the action.
     */
    private void handleClick(View card, Runnable action) {
        if (card != currentActiveCard) {
            if (currentActiveCard != null) {
                resetCard(currentActiveCard);
            }
            moveCardAndRotateTo(card, 0f, dpToPx(-200), 0f);
            currentActiveCard = card;
        } else {
            animateSpecialEffect(card, action);
        }
    }

    /**
     * Resets the given card to its original translation and rotation values.
     */
    private void resetCard(View card) {
        OriginalPosition pos = (OriginalPosition) card.getTag();
        moveCardAndRotateTo(card, pos.origX, pos.origY, pos.origRotation);
    }

    /**
     * Animates a view to the target translationX, translationY, and rotation.
     */
    private void moveCardAndRotateTo(View card, float targetX, float targetY, float targetRotation) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(card, "translationX", targetX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(card, "translationY", targetY);
        ObjectAnimator animR = ObjectAnimator.ofFloat(card, "rotation", targetRotation);
        animX.setDuration(200);
        animY.setDuration(200);
        animR.setDuration(200);
        animX.start();
        animY.start();
        animR.start();
    }

    private void navigateToKanjiMenu() {
        Log.d(TAG, "Navigating to KanjiMenuFragment");
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new KanjiMenuFragment())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToSavedCards() {
        Log.d(TAG, "Navigating to SavedCardsFragment");
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SavedCardsFragment())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToAbout() {
        Log.d(TAG, "Navigating to AboutFragment");
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AboutFragment())
                .addToBackStack(null)
                .commit();
    }

    /**
     * Schedules a daily notification at a specific time.
     * For example, schedules for 9:43 AM every day.
     */
    private void setDailyNotification() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager is null!");
            return;
        }

        // For Android S and above, check if exact alarms are allowed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(getActivity(), "Please enable exact alarms in settings!", Toast.LENGTH_LONG).show();
            Intent settingsIntent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(settingsIntent);
            return;
        }

        Calendar calendar = Calendar.getInstance();
        // Set desired time, e.g., 9:43 AM local time.
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 15);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // If the desired time has already passed today, schedule for tomorrow.
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        long scheduledTime = calendar.getTimeInMillis();
        Log.d(TAG, "Scheduling daily notification for: " + calendar.getTime());

        Intent intent = new Intent(getActivity(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getActivity(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, scheduledTime, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, scheduledTime, pendingIntent);
        }

        Toast.makeText(getActivity(), "Daily Notification Set for " + calendar.getTime(), Toast.LENGTH_LONG).show();
        Log.d(TAG, "Alarm set successfully.");
    }


    /**
     * Special effect: a quick scale pulse effect on the active button.
     * After the effect, executes the provided action.
     */
    private void animateSpecialEffect(View view, Runnable onEffectEnd) {
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f);
        scaleUpX.setDuration(150);
        scaleUpY.setDuration(150);
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1.1f, 1f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.1f, 1f);
        scaleDownX.setDuration(150);
        scaleDownY.setDuration(150);

        scaleUpX.start();
        scaleUpY.start();

        scaleUpX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                scaleDownX.start();
                scaleDownY.start();
            }
        });

        scaleDownX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                onEffectEnd.run();
            }
        });
    }

    // Convert dp to pixel value.
    private float dpToPx(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
