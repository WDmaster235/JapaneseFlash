package com.example.japaneseflash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class KanjiAdapter extends RecyclerView.Adapter<KanjiAdapter.KanjiViewHolder> {

    private List<Kanji> kanjiList;
    private OnItemClickListener onItemClickListener;
    private KanjiApiService apiService;

    public KanjiAdapter(List<Kanji> kanjiList, KanjiApiService apiService) {
        this.kanjiList = kanjiList;
        this.apiService = apiService;
    }

    @Override
    public KanjiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kanji_item, parent, false);
        return new KanjiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(KanjiViewHolder holder, int position) {
        Kanji kanji = kanjiList.get(position);
        holder.kanjiText.setText(kanji.getCharacter());
        // Display the first meaning as a preview
        holder.meaningText.setText(kanji.getMeanings().get(0));

        // When a Kanji square is clicked, pass the Kanji data to the listener.
        holder.kanjiSquare.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(kanji);
                // Optionally fetch readings asynchronously.
                fetchReadings(kanji);
            }
        });
    }

    @Override
    public int getItemCount() {
        return kanjiList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void fetchReadings(Kanji kanji) {
        // Fetch Hiragana readings (using the API's hiragana endpoint)
        Call<List<String>> hiraganaCall = apiService.getHiraganaReadings(kanji.getCharacter());
        hiraganaCall.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Store as kun_readings (typically written in hiragana)
                    kanji.setKunReadings(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                // Handle failure if needed
            }
        });

        // Fetch Katakana readings (using the API's katakana endpoint)
        Call<List<String>> katakanaCall = apiService.getKatakanaReadings(kanji.getCharacter());
        katakanaCall.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Store as on_readings (typically written in katakana)
                    kanji.setOnReadings(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                // Handle failure if needed
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(Kanji kanji);
    }

    public static class KanjiViewHolder extends RecyclerView.ViewHolder {
        TextView kanjiText;
        TextView meaningText;
        RelativeLayout kanjiSquare;

        public KanjiViewHolder(View itemView) {
            super(itemView);
            kanjiText = itemView.findViewById(R.id.kanji_text);
            meaningText = itemView.findViewById(R.id.meaning_text);
            kanjiSquare = itemView.findViewById(R.id.kanji_square);
        }
    }
}
