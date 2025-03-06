package com.example.japaneseflash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.util.Log;
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

        btnKanjiMenu = view.findViewById(R.id.Exit);
        btnSavedCards = view.findViewById(R.id.btn_saved_cards);
        btnDailyReminder = view.findViewById(R.id.btn_kanji_menu);
        btnAbout = view.findViewById(R.id.btn_about);

        // Save each button's original translation and rotation values (as defined in XML)
        saveOriginalValues(btnKanjiMenu);
        saveOriginalValues(btnSavedCards);
        saveOriginalValues(btnDailyReminder);
        saveOriginalValues(btnAbout);

        // Each button calls handleClick() with its respective action.
        btnKanjiMenu.setOnClickListener(v -> handleClick(v, this::navigateToKanjiMenu));
        btnSavedCards.setOnClickListener(v -> handleClick(v, this::navigateToSavedCards));
        btnDailyReminder.setOnClickListener(v -> handleClick(v, this::setDailyNotification));
        btnAbout.setOnClickListener(v -> handleClick(v, this::navigateToAbout));

        return view;
    }

    /**
     * Stores the original translationX, translationY, and rotation values in the view's tag.
     */
    private void saveOriginalValues(View view) {
        OriginalPosition pos = new OriginalPosition(view.getTranslationX(), view.getTranslationY(), view.getRotation());
        view.setTag(pos);
    }

    /**
     * If the card is not active, raise it.
     * If it is active, perform a special effect then execute the provided action.
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
     * Resets the given card back to its original translation and rotation values.
     */
    private void resetCard(View card) {
        OriginalPosition pos = (OriginalPosition) card.getTag();
        moveCardAndRotateTo(card, pos.origX, pos.origY, pos.origRotation);
    }

    /**
     * Animates a view to the target translationX, translationY, and rotation values.
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
     * For example, schedules for 19:26 every day.
     */
    private void setDailyNotification() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager is null!");
            return;
        }

        // Schedule notification 10 seconds from now for testing
        long triggerTime = System.currentTimeMillis() + 10 * 1000;

        NotificationHelper.showDailyNotification(getActivity(), triggerTime, 0);

        Toast.makeText(getActivity(), "Notification scheduled! Exiting app...", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Alarm set successfully. Exiting app...");

        // Exit the app
        getActivity().finishAffinity();
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
