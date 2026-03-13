package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllProfiles extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProfileAdapter profileAdapter;

    private List<Profile> profileList;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allrprofiles);
        Button back_button = (Button) findViewById(R.id.back_button);
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.all_profile_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        profileList = new ArrayList<>();
        //eventAdapter = new EventAdapter(eventList);
        profileAdapter = new ProfileAdapter(profileList);
        recyclerView.setAdapter(profileAdapter);

        fetchAllProfiles();

        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AllProfiles.this, AdminPage.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void fetchAllProfiles() {
        db.collection("users").orderBy("name", Query.Direction.DESCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
            profileList.clear();

            for(QueryDocumentSnapshot document :queryDocumentSnapshots) {
                Profile profile = document.toObject(Profile.class);
                profile.setId(document.getId());
                Boolean isOrg = document.getBoolean("isOrganizer");
                if (isOrg != null && !isOrg) {
                    profileList.add(profile);
                }

            }
            profileAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading events:" + e.getMessage(), Toast.LENGTH_SHORT).show();

        });
    }
}
