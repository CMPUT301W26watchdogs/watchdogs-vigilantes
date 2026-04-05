package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity for admins to view a log of all notifications stored on Firestore.
 */

// Gemini, 2026-04-02, Add an activity for admins to view a log of all notifications stored on Firestore. Should be accessed from the admin panel.
public class NotificationLogActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LogAdapter adapter;
    private List<Map<String, String>> notificationList;
    private FirebaseFirestore db;

    /**
     * setting up the admin notification log screen showing all notifications
     * stored in Firestore ordered by most recent first
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_log);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.logRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationList = new ArrayList<>();
        adapter = new LogAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.backArrow).setOnClickListener(v -> finish());

        loadAllNotifications();
        setupBottomNav();
    }

    /**
     * querying all notifications from Firestore regardless of user,
     * ordered by timestamp descending for the admin log view
     */
    private void loadAllNotifications() {
        db.collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshots -> {
                    notificationList.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Map<String, String> entry = new HashMap<>();
                        entry.put("id", doc.getId());
                        entry.put("title", doc.getString("title") != null ? doc.getString("title") : "Notification");
                        entry.put("message", doc.getString("message") != null ? doc.getString("message") : "");
                        entry.put("eventId", doc.getString("eventId") != null ? doc.getString("eventId") : "");
                        entry.put("userId", doc.getString("userId") != null ? doc.getString("userId") : "Unknown User");
                        notificationList.add(entry);
                    }
                    adapter.notifyDataSetChanged();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load notification log", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * wiring up the bottom navigation bar with tab listeners
     * for navigating between admin screens
     */
    private void setupBottomNav() {
        LiquidGlassNavBar navBar = findViewById(R.id.bottomNav);
        if (navBar != null) {
            navBar.setSelectedTab(2); // Notifications tab
            navBar.setOnTabSelectedListener(position -> {
                if (position == 0) {
                    Intent intent = new Intent(this, AllEventsActivity.class);
                    intent.putExtra("type", "all");
                    intent.putExtra("IS_ADMIN", true);
                    startActivity(intent);
                    finish();
                } else if (position == 1) {
                    Intent intent = new Intent(this, AdminPage.class);
                    intent.putExtra("IS_ADMIN", true);
                    startActivity(intent);
                    finish();
                } else if (position == 3) {
                    Intent intent = new Intent(this, ProfilePage.class);
                    intent.putExtra("IS_ADMIN", true);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    static class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

        private List<Map<String, String>> list;

        LogAdapter(List<Map<String, String>> list) {
            this.list = list;
        }

        @Override
        public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
            return new LogViewHolder(view);
        }

        @Override
        public void onBindViewHolder(LogViewHolder holder, int position) {
            Map<String, String> entry = list.get(position);
            holder.titleText.setText(entry.get("title"));

            String message = entry.get("message");
            String userId = entry.get("userId");
            
            // Set initial message with ID
            holder.messageText.setText("To: " + userId + "\n" + message);

            // Fetch user name to supplement the ID
            if (userId != null && !userId.equals("Unknown User")) {
                FirebaseFirestore.getInstance().collection("users").document(userId).get()
                        .addOnSuccessListener(userDoc -> {
                            if (userDoc.exists()) {
                                String name = userDoc.getString("name");
                                if (name != null) {
                                    holder.messageText.setText("To: " + name + " (" + userId + ")\n" + message);
                                }
                            }
                        });
            }

            String eventId = entry.get("eventId");
            if (eventId != null && !eventId.isEmpty()) {
                holder.eventChip.setVisibility(View.VISIBLE);
                holder.eventChip.setText("Loading Event...");
                FirebaseFirestore.getInstance().collection("events").document(eventId).get()
                        .addOnSuccessListener(doc -> {
                            if (doc.exists() && doc.getString("title") != null) {
                                holder.eventChip.setText(doc.getString("title"));
                            } else {
                                holder.eventChip.setText("Unknown Event");
                            }
                        });
            } else {
                holder.eventChip.setVisibility(View.GONE);
            }

            // In admin log, we don't need unread dot or buttons
            holder.unreadDot.setVisibility(View.GONE);
            holder.buttonRow.setVisibility(View.GONE);
            holder.card.setCardBackgroundColor(holder.itemView.getContext().getColor(R.color.card_background));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class LogViewHolder extends RecyclerView.ViewHolder {
            TextView titleText, messageText;
            MaterialCardView card;
            Chip eventChip;
            View unreadDot;
            View buttonRow;

            LogViewHolder(View itemView) {
                super(itemView);
                titleText = itemView.findViewById(R.id.notifTitle);
                messageText = itemView.findViewById(R.id.notifMessage);
                card = itemView.findViewById(R.id.notifCard);
                eventChip = itemView.findViewById(R.id.notifEventChip);
                unreadDot = itemView.findViewById(R.id.unreadDot);
                buttonRow = itemView.findViewById(R.id.notifButtonRow);
            }
        }
    }
}
