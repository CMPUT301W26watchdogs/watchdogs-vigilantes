// listing all attendees for an event from the Firestore attendees subcollection US 02.02.01

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
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
* This class helps organizer see different kinds of attendee list for the event they created.
 */
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
        Button notifyWaitingButton = findViewById(R.id.notify_waiting_button);
        Button drawLotteryButton = findViewById(R.id.draw_lottery_button);
        Button drawReplacementButton = findViewById(R.id.draw_replacement_button);
        Button exportCsvButton = findViewById(R.id.export_csv_button);
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
            // notify waiting list button lets organizer send a message to all pending entrants US 02.07.01
            notifyWaitingButton.setVisibility(View.VISIBLE);
            notifySelectedButton.setVisibility(View.GONE);
            drawReplacementButton.setVisibility(View.GONE);
            exportCsvButton.setVisibility(View.GONE);
            loadAttendees(null);
        } else if ("cancelled".equals(type)) {
            titleText.setText("Cancelled Entrants");
            mapButton.setVisibility(View.GONE);
            cancelAllButton.setVisibility(View.GONE);
            drawLotteryButton.setVisibility(View.GONE);
            notifySelectedButton.setVisibility(View.GONE);
            notifyWaitingButton.setVisibility(View.GONE);
            drawReplacementButton.setVisibility(View.GONE);
            exportCsvButton.setVisibility(View.GONE);
            loadAttendees("cancelled");
        } else if ("selected".equals(type)) {
            titleText.setText("Selected Entrants");
            mapButton.setVisibility(View.GONE);
            cancelAllButton.setVisibility(View.VISIBLE);
            cancelAllButton.setText("Cancel Non-Signups");
            drawLotteryButton.setVisibility(View.GONE);
            // notify selected entrants button US 02.07.02
            notifySelectedButton.setVisibility(View.VISIBLE);
            // drawing replacement from waitlist when selected entrants cancel or decline US 02.05.03
            drawReplacementButton.setVisibility(View.VISIBLE);
            notifyWaitingButton.setVisibility(View.GONE);
            exportCsvButton.setVisibility(View.GONE);
            loadAttendees("selected");
        } else if ("enrolled".equals(type)) {
            // showing the final list of entrants who accepted their invitation US 02.06.03
            titleText.setText("Enrolled Entrants");
            mapButton.setVisibility(View.GONE);
            cancelAllButton.setVisibility(View.GONE);
            drawLotteryButton.setVisibility(View.GONE);
            notifySelectedButton.setVisibility(View.GONE);
            notifyWaitingButton.setVisibility(View.GONE);
            drawReplacementButton.setVisibility(View.GONE);
            // export CSV button for the enrolled list US 02.06.05
            exportCsvButton.setVisibility(View.VISIBLE);
            loadAttendees("accepted");
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

        // draw replacement button draws a single replacement from the pending waitlist US 02.05.03
        drawReplacementButton.setOnClickListener(v -> drawReplacementFromWaitlist());

        notifySelectedButton.setOnClickListener(v -> sendNotificationToSelected());

        // notify waiting list button sends a custom notification to all pending entrants US 02.07.01
        notifyWaitingButton.setOnClickListener(v -> showNotifyWaitingDialog());

        // export CSV button exports the enrolled entrants list as a CSV file US 02.06.05
        exportCsvButton.setOnClickListener(v -> exportEnrolledCsv());

        back_button.setOnClickListener(v -> finish());
    }

    // lottery draw dialog letting organizer specify how many entrants to select US 02.05.01
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

    // performing the lottery draw by randomly selecting entrants from the pending list US 02.05.01
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

    // drawing a replacement entrant from the pending waitlist when a selected entrant cancels or declines US 02.05.03
    private void drawReplacementFromWaitlist() {
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
                        Toast.makeText(this, "No pending entrants available for replacement", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // randomly selecting one replacement from the pending pool
                    int randomIndex = new Random().nextInt(pending.size());
                    QueryDocumentSnapshot chosen = pending.get(randomIndex);
                    chosen.getReference().update("status", "selected");

                    // sending a notification to the newly selected replacement entrant
                    String chosenUserId = chosen.getString("userId");
                    String chosenName = chosen.getString("name");
                    if (chosenUserId != null) {
                        db.collection("events").document(eventId).get().addOnSuccessListener(eventDoc -> {
                            String eventTitle = eventDoc.getString("title");
                            Map<String, Object> notification = new HashMap<>();
                            notification.put("userId", chosenUserId);
                            notification.put("eventId", eventId);
                            notification.put("title", "You've been selected!");
                            notification.put("message", "A spot opened up for " + (eventTitle != null ? eventTitle : "an event") + " and you were drawn from the waitlist. Open the event to accept or decline.");
                            notification.put("timestamp", FieldValue.serverTimestamp());
                            notification.put("read", false);
                            db.collection("notifications").add(notification);
                        });
                    }

                    Toast.makeText(this, (chosenName != null ? chosenName : "An entrant") + " has been drawn as replacement!", Toast.LENGTH_SHORT).show();
                    loadAttendees("selected");
                });
    }

    // sending notifications to all selected entrants, respecting opt out preference US 02.07.02
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

                            // checking user notification preference before sending, respecting opt out
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

    // showing a dialog to compose a notification message for all waiting list entrants US 02.07.01
    private void showNotifyWaitingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notify Waiting List");
        builder.setMessage("Enter a message to send to all entrants on the waiting list:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setHint("e.g. The lottery draw is coming up soon!");
        builder.setView(input);

        builder.setPositiveButton("Send", (dialog, which) -> {
            String message = input.getText().toString().trim();
            if (!message.isEmpty()) {
                sendNotificationToWaiting(message);
            } else {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // sending a notification to all entrants on the waiting list with status pending US 02.07.01
    private void sendNotificationToWaiting(String customMessage) {
        db.collection("events").document(eventId).get().addOnSuccessListener(eventDoc -> {
            String eventTitle = eventDoc.getString("title");

            db.collection("events").document(eventId)
                    .collection("attendees")
                    .whereEqualTo("status", "pending")
                    .get()
                    .addOnSuccessListener(snapshots -> {
                        int count = 0;
                        for (QueryDocumentSnapshot doc : snapshots) {
                            String userId = doc.getString("userId");
                            if (userId == null) continue;

                            // checking user notification preference before sending, respecting opt out
                            db.collection("users").document(userId).get().addOnSuccessListener(userDoc -> {
                                Boolean notificationsEnabled = userDoc.getBoolean("notificationsEnabled");
                                if (Boolean.FALSE.equals(notificationsEnabled)) return;

                                Map<String, Object> notification = new HashMap<>();
                                notification.put("userId", userId);
                                notification.put("eventId", eventId);
                                notification.put("title", "Update for " + (eventTitle != null ? eventTitle : "an event"));
                                notification.put("message", customMessage);
                                notification.put("timestamp", FieldValue.serverTimestamp());
                                notification.put("read", false);
                                db.collection("notifications").add(notification);
                            });

                            count++;
                        }
                        Toast.makeText(this, count + " waiting list entrants notified!", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    // exporting the enrolled entrants list as a CSV file and opening a share dialog US 02.06.05
    private void exportEnrolledCsv() {
        if (attendeeList.isEmpty()) {
            Toast.makeText(this, "No enrolled entrants to export", Toast.LENGTH_SHORT).show();
            return;
        }

        // building CSV content with header row and entrant data
        StringBuilder csv = new StringBuilder();
        csv.append("Name,Email,Status\n");
        for (Entrant entrant : attendeeList) {
            csv.append(escapeCsvField(entrant.getName())).append(",");
            csv.append(escapeCsvField(entrant.getEmail())).append(",");
            csv.append(escapeCsvField(entrant.getStatus())).append("\n");
        }

        try {
            // writing to a temporary file in the app's cache directory
            File cacheDir = new File(getCacheDir(), "exports");
            cacheDir.mkdirs();
            File csvFile = new File(cacheDir, "enrolled_entrants.csv");
            FileWriter writer = new FileWriter(csvFile);
            writer.write(csv.toString());
            writer.close();

            // sharing the CSV file via Android's share intent
            android.net.Uri uri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", csvFile);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/csv");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Export Enrolled Entrants"));
        } catch (IOException e) {
            Toast.makeText(this, "Failed to export CSV: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // escaping a CSV field value by wrapping in quotes if it contains commas, quotes or newlines US 02.06.05
    static String escapeCsvField(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    // building a CSV string from a list of entrants for export US 02.06.05
    static String buildCsvContent(List<Entrant> entrants) {
        StringBuilder csv = new StringBuilder();
        csv.append("Name,Email,Status\n");
        for (Entrant entrant : entrants) {
            csv.append(escapeCsvField(entrant.getName())).append(",");
            csv.append(escapeCsvField(entrant.getEmail())).append(",");
            csv.append(escapeCsvField(entrant.getStatus())).append("\n");
        }
        return csv.toString();
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

    // cancelling all pending or selected entrants for this event US 02.06.04
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
