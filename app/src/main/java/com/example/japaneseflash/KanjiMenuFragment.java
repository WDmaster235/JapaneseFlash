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
        // Inflate the layout you provided
        View view = inflater.inflate(R.layout.fragment_kanji_menu, container, false);

        // Set up the listener for each button with the proper API category string.
        view.findViewById(R.id.joyokanji_button).setOnClickListener(v -> {
            startFlashcardFragment("joyo"); // Corresponds to /v1/kanji/joyo or jouyou kanji.
        });
        view.findViewById(R.id.grade1_button).setOnClickListener(v -> {
            startFlashcardFragment("grade-1"); // Grade 1 Kyōiku Kanji.
        });
        view.findViewById(R.id.grade2_button).setOnClickListener(v -> {
            startFlashcardFragment("grade-2");
        });
        view.findViewById(R.id.grade3_button).setOnClickListener(v -> {
            startFlashcardFragment("grade-3");
        });
        view.findViewById(R.id.grade4_button).setOnClickListener(v -> {
            startFlashcardFragment("grade-4");
        });
        view.findViewById(R.id.grade5_button).setOnClickListener(v -> {
            startFlashcardFragment("grade-5");
        });
        view.findViewById(R.id.grade6_button).setOnClickListener(v -> {
            startFlashcardFragment("grade-6");
        });

        return view;
    }

    /**
     * Starts the KanjiFlashcardFragment and passes the chosen category as an argument.
     *
     * @param category The API endpoint suffix for the selected Kanji category.
     */
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
