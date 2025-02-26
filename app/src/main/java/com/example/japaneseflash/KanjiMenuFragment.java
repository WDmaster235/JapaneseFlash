package com.example.japaneseflash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class KanjiMenuFragment extends Fragment {
    public KanjiMenuFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kanji_menu, container, false);

        view.findViewById(R.id.joyokanji_button).setOnClickListener(v -> {
            startFlashcardFragment("joyokanji");
        });

        view.findViewById(R.id.grade1_button).setOnClickListener(v -> {
            startFlashcardFragment("grade1");
        });

        view.findViewById(R.id.grade1_button).setOnClickListener(v -> {
            startFlashcardFragment("grade2");
        });

        view.findViewById(R.id.grade1_button).setOnClickListener(v -> {
            startFlashcardFragment("grade3");
        });

        view.findViewById(R.id.grade1_button).setOnClickListener(v -> {
            startFlashcardFragment("grade4");
        });

        view.findViewById(R.id.grade1_button).setOnClickListener(v -> {
            startFlashcardFragment("grade5");
        });

        view.findViewById(R.id.grade1_button).setOnClickListener(v -> {
            startFlashcardFragment("grade6");
        });

        // Similarly, set listeners for grade2_button, grade3_button, etc.

        return view;
    }

    private void startFlashcardFragment(String category) {
        Bundle bundle = new Bundle();
        bundle.putString("CATEGORY", category);
        KanjiFlashcardFragment flashcardFragment = new KanjiFlashcardFragment();
        flashcardFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, flashcardFragment)
                .addToBackStack(null)
                .commit();
    }
}
