package com.example.japaneseflash;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;
import java.util.List;

public class SavedCardsFragment extends Fragment {

    private static final String TAG = "SavedCardsFragment";
    private RecyclerView recyclerView;
    private KanjiAdapter kanjiAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<Kanji> savedKanjiList = new ArrayList<>();
    private KanjiApiService apiService;

    public SavedCardsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout (change the name if you prefer a fragment-specific layout file)
        return inflater.inflate(R.layout.activity_saved_cards, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Retrofit and KanjiApiService
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://your-api-url.com/") // Replace with your actual API URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(KanjiApiService.class);

        recyclerView = view.findViewById(R.id.kanji_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // Display in 3 columns

        kanjiAdapter = new KanjiAdapter(savedKanjiList, apiService);
        recyclerView.setAdapter(kanjiAdapter);

        // Set up search bar
        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchKanji(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Set item click listener for RecyclerView
        // In KanjiAdapter, within the setOnItemClickListener callback:
        kanjiAdapter.setOnItemClickListener(kanji -> {
            // When a Kanji square is clicked, navigate to KanjiDetailActivity
            Intent intent = new Intent(getActivity(), KanjiDetailActivity.class);
            intent.putExtra("KANJI", kanji.getCharacter());
            intent.putExtra("MEANINGS", kanji.getMeanings() != null
                    ? kanji.getMeanings().toArray(new String[0]) : new String[0]);
            // Use kun_readings for Hiragana (they are typically written in hiragana)
            intent.putExtra("HIRAGANA", kanji.getKunReadings() != null
                    ? kanji.getKunReadings().toArray(new String[0]) : new String[0]);
            // Use on_readings for Katakana (they are typically written in katakana)
            intent.putExtra("KATAKANA", kanji.getOnReadings() != null
                    ? kanji.getOnReadings().toArray(new String[0]) : new String[0]);

            startActivity(intent);
        });

    }

    private void searchKanji(String query) {
        if (TextUtils.isEmpty(query)) {
            Toast.makeText(getActivity(), "Please enter a search term", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (userId == null) {
            Log.e(TAG, "User is not authenticated.");
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query Firestore to fetch saved kanji cards based on the search term
        db.collection("users").document(userId).collection("savedCards")
                .whereArrayContains("meanings", query) // Search within meanings
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot.isEmpty()) {
                            Toast.makeText(getActivity(), "No results found", Toast.LENGTH_SHORT).show();
                        }
                        savedKanjiList.clear();
                        for (DocumentSnapshot document : querySnapshot) {
                            Kanji kanji = document.toObject(Kanji.class);
                            if (kanji != null) {
                                savedKanjiList.add(kanji);
                            }
                        }
                        kanjiAdapter.notifyDataSetChanged(); // Update the RecyclerView
                    } else {
                        Log.e(TAG, "Failed to fetch saved cards: " + task.getException());
                        Toast.makeText(getActivity(), "Failed to fetch saved cards", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
