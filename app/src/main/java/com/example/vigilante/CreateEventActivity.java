// form screen for creating event

package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
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

public class CreateEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // getting references to all the input fields in the form
        EditText titleField = findViewById(R.id.fieldTitle);
        EditText descriptionField = findViewById(R.id.fieldDescription);
        EditText dateField = findViewById(R.id.fieldDate);
        EditText locationField = findViewById(R.id.fieldLocation);
        EditText capacityField = findViewById(R.id.fieldCapacity);
        EditText priceField = findViewById(R.id.fieldPrice);
        EditText regStartField = findViewById(R.id.fieldRegistrationStart);
        EditText regEndField = findViewById(R.id.fieldRegistrationEnd);

        // Firestore instance for saving events
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        findViewById(R.id.createEventButton).setOnClickListener(v -> {
            // reading and trimming whitespace from each field
            String title = titleField.getText().toString().trim();
            String description = descriptionField.getText().toString().trim();
            String date = dateField.getText().toString().trim();
            String location = locationField.getText().toString().trim();
            String capacity = capacityField.getText().toString().trim();
            String price = priceField.getText().toString().trim();
            String regStart = regStartField.getText().toString().trim();
            String regEnd = regEndField.getText().toString().trim();

            // validating that all required fields are filled — price is optional since event can be free
            if (title.isEmpty() || description.isEmpty() || date.isEmpty() || location.isEmpty()
                    || capacity.isEmpty() || regStart.isEmpty() || regEnd.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return; // stopping here, not proceeding to next screen
            }

            // building a map of event data to save to Firestore
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("title", title);
            eventData.put("description", description);
            eventData.put("date", date);
            eventData.put("location", location);
            eventData.put("capacity", capacity);
            eventData.put("price", price);
            eventData.put("registrationStart", regStart);
            eventData.put("registrationEnd", regEnd);
            eventData.put("timestamp", com.google.firebase.Timestamp.now());

            // attaching the organizer's Firebase Auth UID if logged in
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                eventData.put("organizerId", currentUser.getUid());
            }

            // saving to Firestore "events" collection — Firestore auto-generates the document ID
            db.collection("events").add(eventData)
                    .addOnSuccessListener(docRef -> {
                        // using Firestore-generated doc ID as the event ID
                        String eventId = docRef.getId();
                        Event event = new Event(eventId, title, description, date, location, capacity, price, regStart, regEnd);
                        if (currentUser != null) {
                            event.setOrganizerId(currentUser.getUid());
                        }

                        // passing the Event to the next screen via intent — Event implements Serializable so this works
                        Intent intent = new Intent(this, EventCreatedActivity.class);
                        intent.putExtra("event", event);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to save event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        // cancel button — discarding the form and go back
        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());
    }
}
