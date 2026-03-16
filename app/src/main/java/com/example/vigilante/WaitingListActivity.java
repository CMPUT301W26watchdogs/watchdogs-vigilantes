// organizer view of all entrants on an event waiting list with cancel functionality — US 02.02.01, US 02.06.04

package com.example.vigilante;

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
import java.util.List;

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

        if (eventId != null) {
            // querying Firestore for entrants in this event's waitingList subcollection
            FirebaseFirestore.getInstance()
                    .collection("events").document(eventId).collection("waitingList")
                    .get()
                    .addOnSuccessListener(snapshots -> {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            // build Entrant from Firestore doc fields
                            String id = doc.getId();
                            String name = doc.getString("name") != null ? doc.getString("name") : "Unknown";
                            String email = doc.getString("email") != null ? doc.getString("email") : "";
                            String phone = doc.getString("phone") != null ? doc.getString("phone") : "";
                            String status = doc.getString("status") != null ? doc.getString("status") : "Waiting";
                            entrants.add(new Entrant(id, name, email, phone, status));
                        }
                        // update the count and refresh the list
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
                        // fall back to placeholder data if Firestore query fails
                        Toast.makeText(this, "Could not load waiting list", Toast.LENGTH_SHORT).show();
                        entrants.addAll(getPlaceholderEntrants());
                        countText.setText(entrants.size() + " entrants on waiting list (unknown)");
                        adapter.notifyDataSetChanged();
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
        list.add(new Entrant("1", "Alice Johnson", "alice@email.com", "780-111-2222", "Waiting"));
        list.add(new Entrant("2", "Bob Smith", "bob@email.com", "780-333-4444", "Waiting"));
        list.add(new Entrant("3", "Carol White", "carol@email.com", "780-555-6666", "Waiting"));
        list.add(new Entrant("4", "David Brown", "david@email.com", "780-777-8888", "Waiting"));
        list.add(new Entrant("5", "Emma Davis", "emma@email.com", "780-999-0000", "Waiting"));
        return list;
    }
}
