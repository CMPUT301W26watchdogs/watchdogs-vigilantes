package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventHistoryAdapter adapter;
    private List<Map<String, String>> historyList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_history);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyList = new ArrayList<>();
        adapter = new EventHistoryAdapter(historyList);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.backArrow).setOnClickListener(v -> finish());

        loadHistory();
        setupBottomNav();
    }

    private void loadHistory() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();

        db.collection("events").get().addOnSuccessListener(eventSnapshots -> {
            for (QueryDocumentSnapshot eventDoc : eventSnapshots) {
                String eventId = eventDoc.getId();
                String eventTitle = eventDoc.getString("title");
                String eventDate = eventDoc.getString("registrationEnd");

                db.collection("events").document(eventId)
                        .collection("attendees").document(userId)
                        .get()
                        .addOnSuccessListener(attendeeDoc -> {
                            if (attendeeDoc.exists()) {
                                String status = attendeeDoc.getString("status");
                                Map<String, String> entry = new HashMap<>();
                                entry.put("eventId", eventId);
                                entry.put("title", eventTitle != null ? eventTitle : "Unknown Event");
                                entry.put("date", eventDate != null ? eventDate : "");
                                entry.put("status", status != null ? status : "unknown");
                                historyList.add(entry);
                                adapter.notifyDataSetChanged();
                            }
                        });
            }
        });
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_events) {
                Intent intent = new Intent(this, AllEventsActivity.class);
                intent.putExtra("type", "all");
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomePage.class));
                finish();
                return true;
            } else if (id == R.id.nav_alerts) {
                startActivity(new Intent(this, NotificationsActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfilePage.class));
                finish();
                return true;
            }
            return false;
        });
    }
}
