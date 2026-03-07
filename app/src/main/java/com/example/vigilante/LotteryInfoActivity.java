package com.example.vigilante;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LotteryInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lottery_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String eventId = getIntent().getStringExtra("event_id");

        TextView lotteryEventName = findViewById(R.id.lotteryEventName);
        TextView lotteryStatus = findViewById(R.id.lotteryStatus);
        TextView lotteryDrawDate = findViewById(R.id.lotteryDrawDate);
        TextView lotteryTotalSpots = findViewById(R.id.lotteryTotalSpots);

        if ("event_a".equals(eventId)) {
            lotteryEventName.setText("Beginner Swimming Lessons");
            lotteryStatus.setText("Waiting");
            lotteryDrawDate.setText("January 11, 2026");
            lotteryTotalSpots.setText("20");
        } else if ("event_b".equals(eventId)) {
            lotteryEventName.setText("Interpretive Dance Workshop");
            lotteryStatus.setText("Waiting");
            lotteryDrawDate.setText("January 26, 2026");
            lotteryTotalSpots.setText("60");
        } else {
            lotteryEventName.setText("Unknown Event");
            lotteryStatus.setText("N/A");
            lotteryDrawDate.setText("N/A");
            lotteryTotalSpots.setText("N/A");
        }

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }
}
