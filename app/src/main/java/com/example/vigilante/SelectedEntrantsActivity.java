// displays the list of entrants who have been selected (invited to apply) for an event — US 02.06.01

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

public class SelectedEntrantsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_selected_entrants);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // getting the event ID passed from WaitingListActivity
        String eventId = getIntent().getStringExtra("event_id");

        // showing the event ID as a label at the top of the screen
        TextView eventLabel = findViewById(R.id.selectedEventLabel);
        eventLabel.setText("Event: " + (eventId != null ? eventId : "Unknown"));

        TextView countText = findViewById(R.id.selectedCount);
        RecyclerView recyclerView = findViewById(R.id.selectedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // the list that the adapter will display — starts empty, filled by Firestore query
        List<Entrant> selectedEntrants = new ArrayList<>();
        EntrantAdapter adapter = new EntrantAdapter(selectedEntrants);
        recyclerView.setAdapter(adapter);

        if (eventId != null) {
            // querying only entrants whose status is "selected" in the attendees subcollection — US 02.06.01
            FirebaseFirestore.getInstance()
                    .collection("events").document(eventId)
                    .collection("attendees")
                    .whereEqualTo("status", "selected")
                    .get()
                    .addOnSuccessListener(snapshots -> {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            // building Entrant from Firestore doc fields
                            String id = doc.getId();
                            String name = doc.getString("name") != null ? doc.getString("name") : "Unknown";
                            String email = doc.getString("email") != null ? doc.getString("email") : "";
                            String phone = doc.getString("phone") != null ? doc.getString("phone") : "";
                            String status = doc.getString("status") != null ? doc.getString("status") : "selected";
                            selectedEntrants.add(new Entrant(id, name, email, phone, status));
                        }
                        // updating the count and refreshing the list
                        countText.setText(selectedEntrants.size() + " entrants selected");
                        adapter.notifyDataSetChanged();

                        // showing a message if no entrants have been selected yet
                        if (selectedEntrants.isEmpty()) {
                            countText.setText("No entrants have been selected yet");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Could not load selected entrants", Toast.LENGTH_SHORT).show();
                        countText.setText("Error loading selected entrants");
                    });
        } else {
            // no event ID provided
            countText.setText("No event ID provided");
        }

        // back button — closing this screen and returning to WaitingListActivity
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }
}
