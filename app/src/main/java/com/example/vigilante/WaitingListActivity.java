package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaitingListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_waiting_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // getting the event ID passed from MainActivity (used to label which event's list is shown)
        String eventId = getIntent().getStringExtra("event_id");

        // showing the event ID as a label at the top of the screen
        TextView eventLabel = findViewById(R.id.waitingListEventLabel);
        eventLabel.setText("Event: " + (eventId != null ? eventId : "Unknown"));

        TextView countText = findViewById(R.id.entrantCount);
        RecyclerView recyclerView = findViewById(R.id.waitingListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // the list that the adapter will display — starts empty, filled by Firestore query
        List<Entrant> entrants = new ArrayList<>();
        EntrantAdapter adapter = new EntrantAdapter(entrants);
        recyclerView.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (eventId != null) {
            // attaching the cancel listener so the organizer can cancel individual entrants — US 02.06.04
            adapter.setCancelListener((entrant, position) -> {
                // writing status "Cancelled" to this entrant's doc in the waitingList subcollection
                Map<String, Object> update = new HashMap<>();
                update.put("status", "cancelled");
                db.collection("events").document(eventId)
                        .collection("attendees").document(entrant.getId())
                        .update(update)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, entrant.getName() + " cancelled", Toast.LENGTH_SHORT).show();
                            // reflecting the status change in the local list immediately
                            entrant.setStatus("cancelled");
                            adapter.notifyItemChanged(position);
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Could not cancel entrant", Toast.LENGTH_SHORT).show());
            });

            // querying Firestore for entrants in this event's waitingList subcollection
            db.collection("events").document(eventId).collection("attendees")
                    .get()
                    .addOnSuccessListener(snapshots -> {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            // building Entrant from Firestore doc fields
                            String id = doc.getId();
                            String name = doc.getString("name") != null ? doc.getString("name") : "Unknown";
                            String email = doc.getString("email") != null ? doc.getString("email") : "";
                            String phone = doc.getString("phone") != null ? doc.getString("phone") : "";
                            String status = doc.getString("status") != null ? doc.getString("status") : "pending";
                            entrants.add(new Entrant(id, name, email, phone, status));
                        }
                        // updating the count and refreshing the list
                        countText.setText(entrants.size() + " entrants on waiting list");
                        adapter.notifyDataSetChanged();

                        // if no entrants found in Firestore, fall back to placeholder data
                        if (entrants.isEmpty()) {
                            entrants.addAll(getPlaceholderEntrants());
                            countText.setText(entrants.size() + " entrants on waiting list (placeholder)");
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Could not load waiting list", Toast.LENGTH_SHORT).show();
                        // falling back to placeholder data if Firestore query fails
                        entrants.addAll(getPlaceholderEntrants());
                        countText.setText(entrants.size() + " entrants on waiting list (unknown)");
                        adapter.notifyDataSetChanged();
                    });

            // navigating to SelectedEntrantsActivity so the organizer can view chosen entrants — US 02.06.01
            findViewById(R.id.viewSelectedButton).setOnClickListener(v -> {
                Intent intent = new Intent(this, SelectedEntrantsActivity.class);
                intent.putExtra("event_id", eventId);
                startActivity(intent);
            });
        } else {
            // no event ID — show placeholder data
            entrants.addAll(getPlaceholderEntrants());
            countText.setText(entrants.size() + " entrants on waiting list (unknown)");
            adapter.notifyDataSetChanged();
        }

        // back button — closing this screen
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    // returning 5 hardcoded entrants as fallback when Firestore data is not available, only for independent testing
    private List<Entrant> getPlaceholderEntrants() {
        List<Entrant> list = new ArrayList<>();
        list.add(new Entrant("1", "Alice Johnson", "alice@email.com", "780-111-2222", "pending"));
        list.add(new Entrant("2", "Bob Smith", "bob@email.com", "780-333-4444", "pending"));
        list.add(new Entrant("3", "Carol White", "carol@email.com", "780-555-6666", "pending"));
        list.add(new Entrant("4", "David Brown", "david@email.com", "780-777-8888", "pending"));
        list.add(new Entrant("5", "Emma Davis", "emma@email.com", "780-999-0000", "pending"));
        return list;
    }
}
