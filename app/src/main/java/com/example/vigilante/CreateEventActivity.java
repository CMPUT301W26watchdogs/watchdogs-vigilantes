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

import java.util.UUID;

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

        EditText titleField = findViewById(R.id.fieldTitle);
        EditText descriptionField = findViewById(R.id.fieldDescription);
        EditText dateField = findViewById(R.id.fieldDate);
        EditText locationField = findViewById(R.id.fieldLocation);
        EditText capacityField = findViewById(R.id.fieldCapacity);
        EditText priceField = findViewById(R.id.fieldPrice);
        EditText regStartField = findViewById(R.id.fieldRegistrationStart);
        EditText regEndField = findViewById(R.id.fieldRegistrationEnd);

        findViewById(R.id.createEventButton).setOnClickListener(v -> {
            String title = titleField.getText().toString().trim();
            String description = descriptionField.getText().toString().trim();
            String date = dateField.getText().toString().trim();
            String location = locationField.getText().toString().trim();
            String capacity = capacityField.getText().toString().trim();
            String price = priceField.getText().toString().trim();
            String regStart = regStartField.getText().toString().trim();
            String regEnd = regEndField.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty() || date.isEmpty() || location.isEmpty()
                    || capacity.isEmpty() || regStart.isEmpty() || regEnd.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String eventId = UUID.randomUUID().toString();
            Event event = new Event(eventId, title, description, date, location, capacity, price, regStart, regEnd);

            Intent intent = new Intent(this, EventCreatedActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
        });

        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());
    }
}
