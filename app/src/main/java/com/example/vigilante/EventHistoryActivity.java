// shows the current user's event participation history with color-coded status badges — US 01.02.03

package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class shows the user their event participation history with color-coded status badges.
 */
public class EventHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventHistoryAdapter adapter;
    private List<Map<String, String>> historyList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_history);

        // setting up RecyclerView with adapter and empty list for history entries
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyList = new ArrayList<>();
        adapter = new EventHistoryAdapter(historyList);
        recyclerView.setAdapter(adapter);

        // back arrow — closing this screen
        findViewById(R.id.backArrow).setOnClickListener(v -> finish());

        loadHistory();
        setupBottomNav();
    }

    // loads the user's event history by checking every event's attendees subcollection — US 01.02.03
    private void loadHistory() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();

        // querying all events, then checking if the current user is in each event's attendees
        db.collection("events").get().addOnSuccessListener(eventSnapshots -> {
            for (QueryDocumentSnapshot eventDoc : eventSnapshots) {
                String eventId = eventDoc.getId();
                String eventTitle = eventDoc.getString("title");
                String eventDate = eventDoc.getString("registrationEnd");

                // checking if the user is an attendee in this specific event
                db.collection("events").document(eventId)
                        .collection("attendees").document(userId)
                        .get()
                        .addOnSuccessListener(attendeeDoc -> {
                            if (attendeeDoc.exists()) {
                                // building a history entry map with event title, date, and user's status
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
        LiquidGlassNavBar navBar = findViewById(R.id.bottomNav);
        navBar.setOnTabSelectedListener(position -> {
            if (position == 0) {
                Intent intent = new Intent(this, AllEventsActivity.class);
                intent.putExtra("type", "all");
                startActivity(intent);
                finish();
            } else if (position == 1) {
                startActivity(new Intent(this, HomePage.class));
                finish();
            } else if (position == 2) {
                startActivity(new Intent(this, NotificationsActivity.class));
                finish();
            } else if (position == 3) {
                startActivity(new Intent(this, ProfilePage.class));
                finish();
            }
        });
    }
}
