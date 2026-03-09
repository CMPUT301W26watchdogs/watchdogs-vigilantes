package com.example.vigilante;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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

        // getting the event ID passed from EventDetailActivity via the "Lottery Info" button
        String eventId = getIntent().getStringExtra("event_id");

        // getting references to the text views that display lottery details
        TextView lotteryEventName = findViewById(R.id.lotteryEventName);
        TextView lotteryStatus = findViewById(R.id.lotteryStatus);
        TextView lotteryDrawDate = findViewById(R.id.lotteryDrawDate);
        TextView lotteryTotalSpots = findViewById(R.id.lotteryTotalSpots);

        if (eventId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // loading event name and capacity from the event document
            db.collection("events").document(eventId).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            lotteryEventName.setText(doc.getString("title") != null ? doc.getString("title") : "Unknown Event");
                            lotteryTotalSpots.setText(doc.getString("capacity") != null ? doc.getString("capacity") : "N/A");
                            lotteryDrawDate.setText(doc.getString("registrationEnd") != null ? doc.getString("registrationEnd") : "N/A");
                        } else {
                            lotteryEventName.setText("Event Not Found");
                            lotteryTotalSpots.setText("N/A");
                            lotteryDrawDate.setText("N/A");
                        }
                    })
                    .addOnFailureListener(e -> {
                        lotteryEventName.setText("Error loading event");
                        lotteryTotalSpots.setText("N/A");
                        lotteryDrawDate.setText("N/A");
                    });

            // checking the current user's status in the waitingList subcollection
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                db.collection("events").document(eventId)
                        .collection("waitingList").document(currentUser.getUid())
                        .get()
                        .addOnSuccessListener(doc -> {
                            if (doc.exists() && doc.getString("status") != null) {
                                lotteryStatus.setText(doc.getString("status"));
                            } else {
                                lotteryStatus.setText("Not on waiting list");
                            }
                        })
                        .addOnFailureListener(e -> lotteryStatus.setText("N/A"));
            } else {
                lotteryStatus.setText("Not logged in");
            }
        } else {
            // no event ID provided
            lotteryEventName.setText("Unknown Event");
            lotteryStatus.setText("N/A");
            lotteryDrawDate.setText("N/A");
            lotteryTotalSpots.setText("N/A");
        }

        // back button for closing this screen and returning to EventDetailActivity
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }
}
