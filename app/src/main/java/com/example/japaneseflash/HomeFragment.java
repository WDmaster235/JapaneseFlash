package com.example.japaneseflash;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.os.Build;
import android.provider.Settings;

import java.util.Calendar;

public class HomeFragment extends Fragment {
    public HomeFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate your fragment_home layout
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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

        return view;
    }
}
