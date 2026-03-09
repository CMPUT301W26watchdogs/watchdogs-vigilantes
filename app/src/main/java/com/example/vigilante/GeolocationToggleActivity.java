package com.example.vigilante;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GeolocationToggleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_geolocation_toggle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // getting the event ID passed from the previous screen
        String eventId = getIntent().getStringExtra("event_id");

        SwitchCompat geolocationSwitch = findViewById(R.id.geolocationSwitch); // toggle for requiring location
        TextView geolocationStatus = findViewById(R.id.geolocationStatus);     // label that reflects current setting

        // updating the status label live whenever the switch changes
        geolocationSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                geolocationStatus.setText(isChecked
                        ? "Geolocation is required"       // switch on
                        : "Geolocation is not required")); // switch off

        findViewById(R.id.saveButton).setOnClickListener(v -> {
            if (eventId == null) {
                Toast.makeText(this, "No event selected", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean required = geolocationSwitch.isChecked(); // reading current switch state

            // saving the geolocation requirement setting to the event document in Firestore
            Map<String, Object> updates = new HashMap<>();
            updates.put("geolocationRequired", required);

            FirebaseFirestore.getInstance().collection("events").document(eventId)
                    .update(updates)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this,
                                    required ? "Geolocation required — saved" : "Geolocation not required — saved",
                                    Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        // cancelling discard changes and go back
        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());
    }
}
