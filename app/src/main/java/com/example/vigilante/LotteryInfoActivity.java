// shows lottery details for an event — entrant status, draw date and total spots from Firestore — US 01.05.05

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
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
        TextView lotteryWaitlistCount = findViewById(R.id.lotteryWaitlistCount);
        TextView lotteryCriteria = findViewById(R.id.lotteryCriteria);

        if (eventId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // loading event name and capacity from the event document
            db.collection("events").document(eventId).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            lotteryEventName.setText(doc.getString("title") != null ? doc.getString("title") : "Unknown Event");
                            lotteryDrawDate.setText(doc.getString("registrationEnd") != null ? doc.getString("registrationEnd") : "N/A");

                            Long waitLimit = doc.getLong("waitingListLimit");
                            if (waitLimit != null) {
                                lotteryTotalSpots.setText(String.valueOf(waitLimit));
                            } else {
                                lotteryTotalSpots.setText(doc.getString("capacity") != null ? doc.getString("capacity") : "N/A");
                            }

                            String spotsText = lotteryTotalSpots.getText().toString();

                            StringBuilder criteria = new StringBuilder();
                            criteria.append("1. All entrants who join the waiting list before the registration deadline have an equal chance of being selected.\n\n");
                            criteria.append("2. After the registration period closes, the system randomly selects ");
                            criteria.append(spotsText.equals("N/A") ? "a set number of" : spotsText);
                            criteria.append(" entrants from the waiting list.\n\n");
                            criteria.append("3. Selected entrants are notified and must confirm their spot. If a selected entrant declines or does not respond, the system automatically draws a replacement from the remaining waitlist.\n\n");
                            criteria.append("4. The selection is fully random — no priority is given based on sign-up time or any other factor.\n\n");
                            criteria.append("5. You can cancel your waitlist entry at any time before the draw.");

                            lotteryCriteria.setText(criteria.toString());
                        } else {
                            lotteryEventName.setText("Event Not Found");
                            lotteryTotalSpots.setText("-");
                            lotteryDrawDate.setText("-");
                        }
                    })
                    .addOnFailureListener(e -> {
                        lotteryEventName.setText("Error loading event");
                        lotteryTotalSpots.setText("-");
                        lotteryDrawDate.setText("-");
                    });

            db.collection("events").document(eventId)
                    .collection("attendees")
                    .whereEqualTo("status", "pending")
                    .get()
                    .addOnSuccessListener(snapshots -> {
                        int count = 0;
                        for (QueryDocumentSnapshot ignored : snapshots) {
                            count++;
                        }
                        lotteryWaitlistCount.setText(String.valueOf(count));
                    })
                    .addOnFailureListener(e -> lotteryWaitlistCount.setText("0"));

            // checking the current user's status in the attendees subcollection
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                db.collection("events").document(eventId)
                        .collection("attendees").document(currentUser.getUid())
                        .get()
                        .addOnSuccessListener(doc -> {
                            if (doc.exists() && doc.getString("status") != null) {
                                String status = doc.getString("status");
                                lotteryStatus.setText(status.substring(0, 1).toUpperCase() + status.substring(1));
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
            lotteryWaitlistCount.setText("0");
            lotteryCriteria.setText("");
        }

        // back button for closing this screen and returning to EventDetailActivity
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }
}
