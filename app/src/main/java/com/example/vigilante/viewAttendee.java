// lists all attendees for an event from the Firestore attendees subcollection — US 02.02.01

package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
        Button notifySelectedButton = findViewById(R.id.notify_selected_button);
        Button drawLotteryButton = findViewById(R.id.draw_lottery_button);
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
            drawLotteryButton.setVisibility(View.VISIBLE);
            notifySelectedButton.setVisibility(View.GONE);
            loadAttendees(null);
        } else if ("cancelled".equals(type)) {
            titleText.setText("Cancelled Entrants");
            mapButton.setVisibility(View.GONE);
            cancelAllButton.setVisibility(View.GONE);
            drawLotteryButton.setVisibility(View.GONE);
            notifySelectedButton.setVisibility(View.GONE);
            loadAttendees("cancelled");
        } else if ("selected".equals(type)) {
            titleText.setText("Selected Entrants");
            mapButton.setVisibility(View.GONE);
            cancelAllButton.setVisibility(View.VISIBLE);
            cancelAllButton.setText("Cancel Non-Signups");
            drawLotteryButton.setVisibility(View.GONE);
            notifySelectedButton.setVisibility(View.VISIBLE);
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

        drawLotteryButton.setOnClickListener(v -> showDrawLotteryDialog());

        notifySelectedButton.setOnClickListener(v -> sendNotificationToSelected());

        back_button.setOnClickListener(v -> finish());
    }

    private void showDrawLotteryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Draw Lottery");
        builder.setMessage("Enter the number of entrants to select:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Draw", (dialog, which) -> {
            String numText = input.getText().toString().trim();
            if (!numText.isEmpty()) {
                int numToDraw = Integer.parseInt(numText);
                performLotteryDraw(numToDraw);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void performLotteryDraw(int numToDraw) {
        db.collection("events").document(eventId)
                .collection("attendees")
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(snapshots -> {
                    List<QueryDocumentSnapshot> pending = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        pending.add(doc);
                    }

                    if (pending.isEmpty()) {
                        Toast.makeText(this, "No pending entrants to draw from", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int actualDraw = Math.min(numToDraw, pending.size());
                    Random random = new Random();
                    List<QueryDocumentSnapshot> selected = new ArrayList<>();

                    while (selected.size() < actualDraw) {
                        int index = random.nextInt(pending.size());
                        selected.add(pending.remove(index));
                    }

                    for (QueryDocumentSnapshot doc : selected) {
                        doc.getReference().update("status", "selected");
                    }

                    Toast.makeText(this, actualDraw + " entrants selected!", Toast.LENGTH_SHORT).show();
                    loadAttendees(null);
                });
    }

    private void sendNotificationToSelected() {
        db.collection("events").document(eventId).get().addOnSuccessListener(eventDoc -> {
            String eventTitle = eventDoc.getString("title");

            db.collection("events").document(eventId)
                    .collection("attendees")
                    .whereEqualTo("status", "selected")
                    .get()
                    .addOnSuccessListener(snapshots -> {
                        int count = 0;
                        for (QueryDocumentSnapshot doc : snapshots) {
                            String userId = doc.getString("userId");
                            if (userId == null) continue;

                            db.collection("users").document(userId).get().addOnSuccessListener(userDoc -> {
                                Boolean notificationsEnabled = userDoc.getBoolean("notificationsEnabled");
                                if (Boolean.FALSE.equals(notificationsEnabled)) return;

                                Map<String, Object> notification = new HashMap<>();
                                notification.put("userId", userId);
                                notification.put("eventId", eventId);
                                notification.put("title", "You've been selected!");
                                notification.put("message", "You've been chosen for " + (eventTitle != null ? eventTitle : "an event") + ". Open the event to accept or decline your invitation.");
                                notification.put("timestamp", FieldValue.serverTimestamp());
                                notification.put("read", false);
                                db.collection("notifications").add(notification);
                            });

                            count++;
                        }
                        Toast.makeText(this, count + " selected entrants notified!", Toast.LENGTH_SHORT).show();
                    });
        });
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
