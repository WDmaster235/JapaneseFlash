package com.example.japaneseflash;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class KanjiDetailActivity extends AppCompatActivity {

    private TextView kanjiDetailText;
    private TextView meaningDetailText;
    private TextView hiraganaDetailText;
    private TextView katakanaDetailText;
    private TextView kanjiBackground;
    private Button backButton;  // Add a back button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_detail);

        // Retrieve references to UI elements
        kanjiBackground = findViewById(R.id.kanji_background);
        kanjiDetailText = findViewById(R.id.kanji_detail_text);
        meaningDetailText = findViewById(R.id.meaning_detail_text);
        hiraganaDetailText = findViewById(R.id.hiragana_detail_text);
        katakanaDetailText = findViewById(R.id.katakana_detail_text);
        backButton = findViewById(R.id.back_button);

        // Set click listener for Back button
        backButton.setOnClickListener(v -> finish());

        // Retrieve the Kanji character from intent
        String kanjiCharacter = getIntent().getStringExtra("KANJI");
        if (!TextUtils.isEmpty(kanjiCharacter)) {
            fetchKanjiDetails(kanjiCharacter);
        } else {
            Toast.makeText(this, "No Kanji provided", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchKanjiDetails(String character) {
        new GetKanjiDetailsTask().execute("https://kanjiapi.dev/v1/kanji/" + character);
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
                kanji.setKunReadings(parseJsonArray(jsonObject.getJSONArray("kun_readings")));
                kanji.setOnReadings(parseJsonArray(jsonObject.getJSONArray("on_readings")));
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
                Toast.makeText(KanjiDetailActivity.this, "Failed to load Kanji details", Toast.LENGTH_SHORT).show();
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
    }

    private void displayKanjiDetails(Kanji kanji) {
        // Set the big background kanji to the character
        kanjiBackground.setText(kanji.getCharacter());
        // Foreground Kanji
        kanjiDetailText.setText(kanji.getCharacter());

        String meaningText = (kanji.getMeanings() != null && !kanji.getMeanings().isEmpty())
                ? TextUtils.join("\n", kanji.getMeanings())
                : "No meanings available";
        String hiraganaText = (kanji.getKunReadings() != null && !kanji.getKunReadings().isEmpty())
                ? TextUtils.join("\n", kanji.getKunReadings())
                : "No hiragana available";
        String katakanaText = (kanji.getOnReadings() != null && !kanji.getOnReadings().isEmpty())
                ? TextUtils.join("\n", kanji.getOnReadings())
                : "No katakana available";

        meaningDetailText.setText("Meaning:\n" + meaningText);
        hiraganaDetailText.setText("Hiragana:\n" + hiraganaText);
        katakanaDetailText.setText("Katakana:\n" + katakanaText);
    }
}
