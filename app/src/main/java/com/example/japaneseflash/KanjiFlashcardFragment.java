package com.example.japaneseflash;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class KanjiFlashcardFragment extends Fragment {
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private TextView kanjiTextView;
    private TextView meaningTextView;
    private List<String> kanjiList = new ArrayList<>();
    private String category; // The API category suffix

    public KanjiFlashcardFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kanji_flashcard, container, false);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        kanjiTextView = view.findViewById(R.id.kanji_text);
        meaningTextView = view.findViewById(R.id.meaning_text);

        // Retrieve the category from the Bundle (default to "joyo" if not provided)
        Bundle bundle = getArguments();
        category = (bundle != null && bundle.containsKey("CATEGORY"))
                ? bundle.getString("CATEGORY")
                : "joyo";

        loadKanjiList();

        view.findViewById(R.id.next_button).setOnClickListener(v -> showRandomKanji());
        view.findViewById(R.id.home_button).setOnClickListener(v -> navigateHome());
        view.findViewById(R.id.save_card_button).setOnClickListener(v -> saveCurrentCard());

        return view;
    }

    private void navigateHome() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }

    private void loadKanjiList() {
        // Use the selected category for the API endpoint.
        String url = "https://kanjiapi.dev/v1/kanji/" + category;
        new GetKanjiListTask().execute(url);
    }

    private class GetKanjiListTask extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... urls) {
            List<String> list = new ArrayList<>();
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.connect();

                InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                reader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                JSONArray jsonArray = new JSONArray(stringBuilder.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(jsonArray.getString(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null) reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            if (result != null && !result.isEmpty()) {
                kanjiList = result;
                showRandomKanji();
            } else {
                Toast.makeText(getActivity(), "Failed to load Kanji", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showRandomKanji() {
        if (kanjiList != null && !kanjiList.isEmpty()) {
            String randomKanji = kanjiList.get((int) (Math.random() * kanjiList.size()));
            // Animate text change (flip-out/in animation)
            kanjiTextView.animate().rotationY(90f).setDuration(300).withEndAction(() -> {
                kanjiTextView.setText(randomKanji);
                kanjiTextView.animate().rotationY(0f).setDuration(300);
            });
            loadKanjiDetails(randomKanji);
        }
    }

    private void loadKanjiDetails(String character) {
        String url = "https://kanjiapi.dev/v1/kanji/" + character;
        new GetKanjiDetailsTask().execute(url);
    }

    private class GetKanjiDetailsTask extends AsyncTask<String, Void, Kanji> {
        @Override
        protected Kanji doInBackground(String... urls) {
            Kanji kanji = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.connect();

                InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                reader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                kanji = new Kanji();
                kanji.setCharacter(jsonObject.getString("kanji"));
                kanji.setMeanings(parseJsonArray(jsonObject.getJSONArray("meanings")));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null) reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return kanji;
        }

        @Override
        protected void onPostExecute(Kanji kanji) {
            if (kanji != null) {
                displayKanjiDetails(kanji);
            } else {
                Toast.makeText(getActivity(), "Failed to load Kanji details", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void displayKanjiDetails(Kanji kanji) {
        if (kanji != null) {
            StringBuilder meanings = new StringBuilder();
            for (String meaning : kanji.getMeanings()) {
                meanings.append(meaning).append("\n");
            }
            meaningTextView.animate().rotationY(90f).setDuration(300).withEndAction(() -> {
                meaningTextView.setText(meanings.toString());
                meaningTextView.animate().rotationY(0f).setDuration(300);
            });
        }
    }

    private List<String> parseJsonArray(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void saveCurrentCard() {
        String currentUserId = mAuth.getCurrentUser().getUid();
        String kanjiCharacter = kanjiTextView.getText().toString();
        String kanjiMeaning = meaningTextView.getText().toString();

        if (kanjiCharacter.isEmpty() || kanjiMeaning.isEmpty()) {
            Toast.makeText(getActivity(), "No card to save!", Toast.LENGTH_SHORT).show();
            return;
        }

        Kanji savedKanji = new Kanji();
        savedKanji.setCharacter(kanjiCharacter);
        savedKanji.setMeanings(List.of(kanjiMeaning.split("\n")));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(currentUserId).collection("savedCards")
                .add(savedKanji)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(getActivity(), "Card saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getActivity(), "Failed to save card!", Toast.LENGTH_SHORT).show());
    }
}
