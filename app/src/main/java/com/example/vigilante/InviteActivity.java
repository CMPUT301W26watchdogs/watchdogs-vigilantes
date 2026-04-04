package com.example.vigilante;

import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class InviteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProfileAdapter profileAdapter;

    private List<Profile> profileList;      // filtered list
    private List<Profile> allProfileList;   // full list

    private FirebaseFirestore db;

    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_activity);

        Button back_button = findViewById(R.id.back_button);
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.all_profile_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        profileList = new ArrayList<>();
        allProfileList = new ArrayList<>();

        String currentEventId =  getIntent().getStringExtra("event_id");

        profileAdapter =  new ProfileAdapter(profileList, true, currentEventId);

        recyclerView.setAdapter(profileAdapter);

        fetchAllProfiles();

        EditText searchBar = findViewById(R.id.searchEditText);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().toLowerCase().trim();
                applyFilter();
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        back_button.setOnClickListener(v -> finish());
    }

    private void fetchAllProfiles() {
        db.collection("users")
                .orderBy("name", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    allProfileList.clear(); // ONLY clear master list

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Profile profile = document.toObject(Profile.class);
                        profile.setId(document.getId());


                        allProfileList.add(profile);
                    }

                    applyFilter(); // populate profileList
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void applyFilter() {
        profileList.clear(); // only clear filtered list

        for (Profile profile : allProfileList) {
            boolean matchesSearch = searchQuery.isEmpty()
                    || (profile.getName() != null && profile.getName().toLowerCase().contains(searchQuery))
                    || (profile.getEmail() != null && profile.getEmail().toLowerCase().contains(searchQuery))
                    || (profile.getPhone() != null && profile.getPhone().toLowerCase().contains(searchQuery));

            if (matchesSearch) {
                profileList.add(profile);
            }
        }

        profileAdapter.notifyDataSetChanged();
    }
}