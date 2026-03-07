package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EventDetailActivity extends AppCompatActivity {

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

        String eventId = getIntent().getStringExtra("event_id");

        TextView title = findViewById(R.id.eventTitle);
        TextView description = findViewById(R.id.eventDescription);
        TextView date = findViewById(R.id.eventDate);
        TextView location = findViewById(R.id.eventLocation);
        TextView capacity = findViewById(R.id.eventCapacity);
        TextView price = findViewById(R.id.eventPrice);
        TextView registration = findViewById(R.id.eventRegistration);

        if ("event_a".equals(eventId)) {
            title.setText("Beginner Swimming Lessons");
            description.setText("Learn the basics of swimming in a fun and safe environment. Perfect for beginners of all ages.");
            date.setText("Mondays, Jan 15 - Mar 15, 2026");
            location.setText("Downtown Community Centre Pool");
            capacity.setText("20 spots");
            price.setText("$45.00");
            registration.setText("Open until Jan 10, 2026");
        } else if ("event_b".equals(eventId)) {
            title.setText("Interpretive Dance Workshop");
            description.setText("Weekly dance classes covering safety basics and creative expression through movement.");
            date.setText("Wednesdays, Feb 1 - Apr 1, 2026");
            location.setText("Riverside Recreation Centre");
            capacity.setText("60 spots");
            price.setText("$60.00");
            registration.setText("Open until Jan 25, 2026");
        } else {
            title.setText("Unknown Event");
            description.setText("No event found for this QR code.");
            date.setText("-");
            location.setText("-");
            capacity.setText("-");
            price.setText("-");
            registration.setText("-");
        }

        findViewById(R.id.registerButton).setOnClickListener(v ->
                Toast.makeText(this, "Joined waiting list (placeholder)", Toast.LENGTH_SHORT).show());

        findViewById(R.id.lotteryInfoButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, LotteryInfoActivity.class);
            intent.putExtra("event_id", eventId);
            startActivity(intent);
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }
}
