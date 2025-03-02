package com.example.japaneseflash;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        // Inflate the layout
        return inflater.inflate(R.layout.activity_saved_cards, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Retrofit and KanjiApiService
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://your-api-url.com/") // Replace with your actual API URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(KanjiApiService.class);

        recyclerView = view.findViewById(R.id.kanji_recycler_view);
        // Use GridLayoutManager for a grid of 3 columns
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        kanjiAdapter = new KanjiAdapter(savedKanjiList, apiService);
        recyclerView.setAdapter(kanjiAdapter);

        // Set up the SearchView (search by meaning)
        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchKanji(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // When the search field is cleared, fetch all saved cards again.
                if (TextUtils.isEmpty(newText)) {
                    fetchAllSavedCards();
                }
                return false;
            }
        });

        // Set item click listener for RecyclerView items
        kanjiAdapter.setOnItemClickListener(kanji -> {
            Intent intent = new Intent(getActivity(), KanjiDetailActivity.class);
            intent.putExtra("KANJI", kanji.getCharacter());
            intent.putExtra("MEANINGS", kanji.getMeanings() != null
                    ? kanji.getMeanings().toArray(new String[0]) : new String[0]);
            // Use kun_readings for Hiragana (typically written in hiragana)
            intent.putExtra("HIRAGANA", kanji.getKunReadings() != null
                    ? kanji.getKunReadings().toArray(new String[0]) : new String[0]);
            // Use on_readings for Katakana (typically written in katakana)
            intent.putExtra("KATAKANA", kanji.getOnReadings() != null
                    ? kanji.getOnReadings().toArray(new String[0]) : new String[0]);
            startActivity(intent);
        });

        // Initially, fetch all saved Kanji cards.
        fetchAllSavedCards();
    }

    /**
     * Fetches all saved Kanji cards for the current user.
     */
    private void fetchAllSavedCards() {
        String userId = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : null;
        if (userId == null) {
            Log.e(TAG, "User is not authenticated.");
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("users").document(userId).collection("savedCards")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        savedKanjiList.clear();
                        for (DocumentSnapshot document : querySnapshot) {
                            Kanji kanji = document.toObject(Kanji.class);
                            if (kanji != null) {
                                savedKanjiList.add(kanji);
                            }
                        }
                        kanjiAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Failed to fetch saved cards: " + task.getException());
                        Toast.makeText(getActivity(), "Failed to fetch saved cards", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Searches saved Kanji cards by meaning.
     *
     * @param query The search query.
     */
    private void searchKanji(String query) {
        String userId = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : null;
        if (userId == null) {
            Log.e(TAG, "User is not authenticated.");
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        // If query is empty, fetch all saved cards.
        if (TextUtils.isEmpty(query)) {
            fetchAllSavedCards();
            return;
        }
        // Otherwise, perform a search using whereArrayContains (searching in meanings)
        db.collection("users").document(userId).collection("savedCards")
                .whereArrayContains("meanings", query)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        savedKanjiList.clear();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (DocumentSnapshot document : querySnapshot) {
                                Kanji kanji = document.toObject(Kanji.class);
                                if (kanji != null) {
                                    savedKanjiList.add(kanji);
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "No results found", Toast.LENGTH_SHORT).show();
                        }
                        kanjiAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Failed to fetch saved cards: " + task.getException());
                        Toast.makeText(getActivity(), "Failed to fetch saved cards", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
