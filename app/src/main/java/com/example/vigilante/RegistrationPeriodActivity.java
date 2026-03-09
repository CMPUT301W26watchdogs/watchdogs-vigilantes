package com.example.vigilante;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistrationPeriodActivity extends AppCompatActivity {

    // storing the picked dates as strings so they survive across button clicks
    private String selectedStartDate = "";
    private String selectedEndDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration_period);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // getting the event ID passed from the previous screen
        String eventId = getIntent().getStringExtra("event_id");

        // text views that display the selected dates below each button
        TextView startDateDisplay = findViewById(R.id.startDateDisplay);
        TextView endDateDisplay = findViewById(R.id.endDateDisplay);

        /* DatePickerDialog used to let the organizer select registration open/close dates
         * https://developer.android.com/reference/android/app/DatePickerDialog */
        findViewById(R.id.pickStartDateButton).setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance(); // initializing to today so the dialog opens at today's date
            new DatePickerDialog(this, (view, year, month, day) -> {
                // month is 0-indexed so added 1 for display
                selectedStartDate = day + "/" + (month + 1) + "/" + year;
                startDateDisplay.setText(selectedStartDate); // show the chosen date on screen
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        findViewById(R.id.pickEndDateButton).setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                selectedEndDate = day + "/" + (month + 1) + "/" + year;
                endDateDisplay.setText(selectedEndDate);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        findViewById(R.id.saveButton).setOnClickListener(v -> {
            // both dates must be selected before saving
            if (selectedStartDate.isEmpty() || selectedEndDate.isEmpty()) {
                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show();
                return;
            }

            if (eventId == null) {
                Toast.makeText(this, "No event selected", Toast.LENGTH_SHORT).show();
                return;
            }

            // saving the registration dates to the event document in Firestore
            Map<String, Object> updates = new HashMap<>();
            updates.put("registrationStart", selectedStartDate);
            updates.put("registrationEnd", selectedEndDate);

            FirebaseFirestore.getInstance().collection("events").document(eventId)
                    .update(updates)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "Registration period saved\n" + selectedStartDate + " — " + selectedEndDate, Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        // cancelling any selections and go back
        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());
    }
}
