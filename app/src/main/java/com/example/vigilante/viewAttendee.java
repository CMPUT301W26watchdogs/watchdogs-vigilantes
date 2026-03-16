package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class viewAttendee extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EntrantAdapter attendeeAdapter;
    private List<Entrant> attendeeList;
    private FirebaseFirestore db;
    private String eventId;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_list);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.attendees_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendeeList = new ArrayList<>();
        Button back_button = (Button) findViewById(R.id.back_button);
        Button mapButton = findViewById(R.id.map_button);
        Button cancelAllButton = findViewById(R.id.cancel_all_button);
        TextView titleText = findViewById(R.id.title_waiting_list);

        eventId = getIntent().getStringExtra("EVENT_ID");
        type = getIntent().getStringExtra("type");

        boolean showCancelButton = "waiting".equals(type) || "selected".equals(type);

        attendeeAdapter = new EntrantAdapter(attendeeList, showCancelButton ? eventId : null);
        recyclerView.setAdapter(attendeeAdapter);

        if ("waiting".equals(type)) {
            titleText.setText("Waiting List");
            mapButton.setVisibility(View.VISIBLE);
            cancelAllButton.setVisibility(View.VISIBLE);
            loadAttendees(null);
        } else if ("cancelled".equals(type)) {
            titleText.setText("Cancelled Entrants");
            mapButton.setVisibility(View.GONE);
            cancelAllButton.setVisibility(View.GONE);
            loadAttendees("cancelled");
        } else if ("selected".equals(type)) {
            titleText.setText("Selected Entrants");
            mapButton.setVisibility(View.GONE);
            cancelAllButton.setVisibility(View.VISIBLE);
            cancelAllButton.setText("Cancel Non-Signups");
            loadAttendees("selected");
        }

        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EntrantMapActivity.class);
            intent.putExtra("event_id", eventId);
            startActivity(intent);
        });

        cancelAllButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Cancel Entrants")
                    .setMessage("Cancel all entrants in this list who have not confirmed their sign-up?")
                    .setPositiveButton("Confirm", (dialog, which) -> cancelPendingEntrants())
                    .setNegativeButton("Back", (dialog, which) -> dialog.cancel())
                    .show();
        });

        back_button.setOnClickListener(v -> finish());
    }

    private void loadAttendees(String statusFilter) {
        com.google.firebase.firestore.Query query = db.collection("events").document(eventId).collection("attendees");

        if (statusFilter != null) {
            query = query.whereEqualTo("status", statusFilter);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            attendeeList.clear();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Entrant entrant = new Entrant();
                entrant.setId(document.getId());
                entrant.setName(document.getString("name") != null ? document.getString("name") : "Unknown");
                entrant.setEmail(document.getString("email") != null ? document.getString("email") : "");
                entrant.setStatus(document.getString("status") != null ? document.getString("status") : "pending");
                attendeeList.add(entrant);
            }
            attendeeAdapter.notifyDataSetChanged();
        });
    }

    private void cancelPendingEntrants() {
        String targetStatus = "selected".equals(type) ? "selected" : "pending";

        db.collection("events").document(eventId).collection("attendees")
                .whereEqualTo("status", targetStatus)
                .get()
                .addOnSuccessListener(snapshots -> {
                    int count = 0;
                    for (QueryDocumentSnapshot doc : snapshots) {
                        doc.getReference().update("status", "cancelled");
                        count++;
                    }
                    Toast.makeText(this, count + " entrants cancelled", Toast.LENGTH_SHORT).show();
                    loadAttendees("selected".equals(type) ? "selected" : null);
                });
    }
}
