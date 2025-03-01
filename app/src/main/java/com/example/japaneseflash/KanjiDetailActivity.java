package com.example.japaneseflash;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class KanjiDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_detail);

        // Retrieve the data passed via the intent
        String kanji = getIntent().getStringExtra("KANJI");
        String[] meanings = getIntent().getStringArrayExtra("MEANINGS");
        String[] hiragana = getIntent().getStringArrayExtra("HIRAGANA");
        String[] katakana = getIntent().getStringArrayExtra("KATAKANA");

        // Convert the arrays to strings by joining the elements with newlines to display each on its own line
        String meaningText = (meanings != null && meanings.length > 0)
                ? TextUtils.join("\n", meanings)
                : "No meanings available";
        String hiraganaText = (hiragana != null && hiragana.length > 0)
                ? TextUtils.join("\n", hiragana)
                : "No hiragana available";
        String katakanaText = (katakana != null && katakana.length > 0)
                ? TextUtils.join("\n", katakana)
                : "No katakana available";

        // Set the text views with the received data
        TextView kanjiDetailText = findViewById(R.id.kanji_detail_text);
        TextView meaningDetailText = findViewById(R.id.meaning_detail_text);
        TextView hiraganaDetailText = findViewById(R.id.hiragana_detail_text);
        TextView katakanaDetailText = findViewById(R.id.katakana_detail_text);

        kanjiDetailText.setText(kanji);
        meaningDetailText.setText("Meaning:\n" + meaningText);
        hiraganaDetailText.setText("Hiragana:\n" + hiraganaText);
        katakanaDetailText.setText("Katakana:\n" + katakanaText);
    }
}
