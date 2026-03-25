// displaying user notifications from Firestore in a RecyclerView for lottery results and event updates US 01.04.03

package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class displays the user's notifications from Firestore for lottery results and event updates.
 */
public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Map<String, String>> notificationList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // setting up RecyclerView with adapter and empty list for notifications
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.notificationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        loadNotifications();
        setupBottomNav();
    }

    // querying Firestore for all notifications belonging to the current user, ordered by most recent US 01.04.03
    // Citation: Ved, March 12 2025, Claude referred to https://firebase.google.com/docs/firestore/query-data/order-limit-data
    private void loadNotifications() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        db.collection("notifications")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnSuccessListener(snapshots -> {
                    notificationList.clear();
                    List<QueryDocumentSnapshot> docs = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        docs.add(doc);
                    }
                    Collections.sort(docs, (a, b) -> {
                        com.google.firebase.Timestamp ta = a.getTimestamp("timestamp");
                        com.google.firebase.Timestamp tb = b.getTimestamp("timestamp");
                        if (ta == null && tb == null) return 0;
                        if (ta == null) return 1;
                        if (tb == null) return -1;
                        return tb.compareTo(ta);
                    });
                    for (QueryDocumentSnapshot doc : docs) {
                        Map<String, String> entry = new HashMap<>();
                        entry.put("id", doc.getId());
                        entry.put("title", doc.getString("title") != null ? doc.getString("title") : "Notification");
                        entry.put("message", doc.getString("message") != null ? doc.getString("message") : "");
                        entry.put("eventId", doc.getString("eventId") != null ? doc.getString("eventId") : "");
                        Boolean read = doc.getBoolean("read");
                        entry.put("read", String.valueOf(Boolean.TRUE.equals(read)));
                        notificationList.add(entry);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void setupBottomNav() {
        LiquidGlassNavBar navBar = findViewById(R.id.bottomNav);
        navBar.setSelectedTab(2);
        navBar.setOnTabSelectedListener(position -> {
            if (position == 0) {
                Intent intent = new Intent(this, AllEventsActivity.class);
                intent.putExtra("type", "all");
                startActivity(intent);
                finish();
            } else if (position == 1) {
                startActivity(new Intent(this, HomePage.class));
                finish();
            } else if (position == 3) {
                startActivity(new Intent(this, ProfilePage.class));
                finish();
            }
        });
    }

    /**
     * Inner adapter class for displaying individual notification cards in the RecyclerView.
     */
    static class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifViewHolder> {

        private List<Map<String, String>> list;

        NotificationAdapter(List<Map<String, String>> list) {
            this.list = list;
        }

        @Override
        public NotifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
            return new NotifViewHolder(view);
        }

        @Override
        public void onBindViewHolder(NotifViewHolder holder, int position) {
            Map<String, String> entry = list.get(position);
            holder.titleText.setText(entry.get("title"));
            holder.messageText.setText(entry.get("message"));

            String eventId = entry.get("eventId");
            if (eventId != null && !eventId.isEmpty()) {
                holder.eventChip.setVisibility(View.VISIBLE);
                holder.eventChip.setText("View Event");
                FirebaseFirestore.getInstance().collection("events").document(eventId).get()
                        .addOnSuccessListener(doc -> {
                            if (doc.exists() && doc.getString("title") != null) {
                                holder.eventChip.setText(doc.getString("title"));
                            }
                        });
            } else {
                holder.eventChip.setVisibility(View.GONE);
            }

            boolean isRead = "true".equals(entry.get("read"));
            holder.unreadDot.setVisibility(isRead ? View.GONE : View.VISIBLE);
            holder.card.setCardBackgroundColor(holder.itemView.getContext().getColor(
                    isRead ? R.color.card_background : R.color.surface_gray));

            holder.itemView.setOnClickListener(v -> {
                String notifId = entry.get("id");
                if (notifId != null) {
                    FirebaseFirestore.getInstance().collection("notifications")
                            .document(notifId).update("read", true);
                    entry.put("read", "true");
                    holder.unreadDot.setVisibility(View.GONE);
                    holder.card.setCardBackgroundColor(v.getContext().getColor(R.color.card_background));
                }

                if (eventId != null && !eventId.isEmpty()) {
                    Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
                    intent.putExtra("event_id", eventId);
                    v.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class NotifViewHolder extends RecyclerView.ViewHolder {
            TextView titleText, messageText;
            MaterialCardView card;
            Chip eventChip;
            View unreadDot;

            NotifViewHolder(View itemView) {
                super(itemView);
                titleText = itemView.findViewById(R.id.notifTitle);
                messageText = itemView.findViewById(R.id.notifMessage);
                card = itemView.findViewById(R.id.notifCard);
                eventChip = itemView.findViewById(R.id.notifEventChip);
                unreadDot = itemView.findViewById(R.id.unreadDot);
            }
        }
    }
}
