package com.example.vigilante;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WaitingListLimitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_waiting_list_limit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // getting the event ID passed from the previous screen
        String eventId = getIntent().getStringExtra("event_id");

        SwitchCompat limitSwitch = findViewById(R.id.limitSwitch); // toggle for enabling a cap
        TextInputLayout limitLayout = findViewById(R.id.layoutMaxEntrants); // wrapper that contains the number field
        TextInputEditText maxEntrantsField = findViewById(R.id.fieldMaxEntrants); // the number input itself

        // showing the number field only when the switch is on; hide it when off
        limitSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                limitLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        findViewById(R.id.saveButton).setOnClickListener(v -> {
            if (eventId == null) {
                Toast.makeText(this, "No event selected", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updates = new HashMap<>();

            if (limitSwitch.isChecked()) { // limit is enabled so validating the number
                String maxText = maxEntrantsField.getText().toString().trim();
                if (maxText.isEmpty()) {
                    Toast.makeText(this, "Please enter a maximum number of entrants", Toast.LENGTH_SHORT).show();
                    return; // don't save if field is blank
                }
                int max = Integer.parseInt(maxText); // safe here because the field is numeric input type
                if (max < 1) {
                    Toast.makeText(this, "Limit must be at least 1", Toast.LENGTH_SHORT).show();
                    return;
                }
                updates.put("waitingListLimit", max);
                updates.put("hasWaitingListLimit", true);
            } else {
                // switch is off — remove the limit
                updates.put("waitingListLimit", null);
                updates.put("hasWaitingListLimit", false);
            }

            // save the waiting list limit setting to the event document in Firestore
            FirebaseFirestore.getInstance().collection("events").document(eventId)
                    .update(updates)
                    .addOnSuccessListener(unused -> {
                        String msg = limitSwitch.isChecked()
                                ? "Waiting list limited to " + maxEntrantsField.getText().toString().trim() + " entrants"
                                : "No waiting list limit set";
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        // cancel — discard changes and go back
        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());
    }
}
