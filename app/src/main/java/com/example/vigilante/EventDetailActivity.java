//  displays event detailed info page and handles entrant sign-up

package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String eventId;
    private Button joinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // getting the event ID string that was passed from MainActivity after scanning the QR code
        String eventId = getIntent().getStringExtra("event_id");

        // getting references to all the text views that display event details
        TextView title = findViewById(R.id.eventTitle);
        TextView description = findViewById(R.id.eventDescription);
        TextView date = findViewById(R.id.eventDate);
        TextView location = findViewById(R.id.eventLocation);
        TextView capacity = findViewById(R.id.eventCapacity);
        TextView price = findViewById(R.id.eventPrice);
        TextView registration = findViewById(R.id.eventRegistration);
        TextView signUpStatus = findViewById(R.id.signUpStatus);
        Button registerButton = findViewById(R.id.registerButton);

        // querying Firestore for the event document using the scanned event ID
        if (eventId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("events").document(eventId)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            // populating the text views with data from Firestore
                            title.setText(doc.getString("title") != null ? doc.getString("title") : "Untitled Event");
                            description.setText(doc.getString("description") != null ? doc.getString("description") : "");
                            date.setText(doc.getString("date") != null ? doc.getString("date") : "-");
                            location.setText(doc.getString("location") != null ? doc.getString("location") : "-");
                            capacity.setText(doc.getString("capacity") != null ? doc.getString("capacity") : "-");
                            price.setText(doc.getString("price") != null ? doc.getString("price") : "Free");
                            String regStart = doc.getString("registrationStart");
                            String regEnd = doc.getString("registrationEnd");
                            if (regStart != null && regEnd != null) {
                                registration.setText(regStart + " — " + regEnd);
                            } else {
                                registration.setText("-");
                            }
                        } else {
                            // document doesn't exist in Firestore
                            title.setText("Event Not Found");
                            description.setText("No event found for this QR code in the database.");
                            date.setText("-");
                            location.setText("-");
                            capacity.setText("-");
                            price.setText("-");
                            registration.setText("-");
                        }
                    })
                    .addOnFailureListener(e -> {
                        title.setText("Error Loading Event");
                        description.setText(e.getMessage());
                    });

            // checking if the current user is already signed up for this event
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                db.collection("events").document(eventId)
                        .collection("waitingList").document(currentUser.getUid())
                        .get()
                        .addOnSuccessListener(doc -> {
                            if (doc.exists()) {
                                // user is already signed up — showing their status and disabling the button
                                String status = doc.getString("status") != null ? doc.getString("status") : "Signed Up";
                                signUpStatus.setText("Your status: " + status);
                                registerButton.setText("Already Signed Up");
                                registerButton.setEnabled(false);
                            } else {
                                // user has not signed up yet
                                signUpStatus.setText("You have not signed up for this event");
                            }
                        })
                        .addOnFailureListener(e ->
                                signUpStatus.setText("Could not check sign-up status"));
            } else {
                signUpStatus.setText("Log in to sign up");
                registerButton.setEnabled(false);
            }
        } else {
            title.setText("No Event ID");
            description.setText("No event ID was provided.");
        }

        // signing up the current user for the event by writing to the waitingList subcollection
        registerButton.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null || eventId == null) {
                Toast.makeText(this, "You must be logged in to sign up", Toast.LENGTH_SHORT).show();
                return;
            }
            // building a map with the entrant's data from their Firebase Auth profile
            Map<String, Object> entrantData = new HashMap<>();
            entrantData.put("name", currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Unknown");
            entrantData.put("email", currentUser.getEmail() != null ? currentUser.getEmail() : "");
            entrantData.put("status", "Waiting");

            // writing to events/{eventId}/waitingList/{userId}
            FirebaseFirestore.getInstance()
                    .collection("events").document(eventId)
                    .collection("waitingList").document(currentUser.getUid())
                    .set(entrantData)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Signed up successfully!", Toast.LENGTH_SHORT).show();
                        // updating the button and status after successful sign-up
                        signUpStatus.setText("Your status: Waiting");
                        registerButton.setText("Already Signed Up");
                        registerButton.setEnabled(false);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to sign up: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        // opening lottery info screen, passing the same event ID so it can show the right data
        findViewById(R.id.lotteryInfoButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, LotteryInfoActivity.class);
            intent.putExtra("event_id", eventId);
            startActivity(intent);
        });

        // closing this screen and return to the previous one
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }
}