// displaying user notifications from Firestore in a RecyclerView for lottery results and event updates US 01.04.03

package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    /**
     * setting up the notifications screen with a RecyclerView that displays
     * all notifications belonging to the currently signed in user
     */
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

    /**
     * querying Firestore for all notifications belonging to the current user,
     * sorting them by timestamp descending and populating the RecyclerView
     */
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
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * wiring up the bottom navigation bar with tab listeners
     * for navigating between events, home/admin, notifications and profile
     */
    private void setupBottomNav() {
        LiquidGlassNavBar navBar = findViewById(R.id.bottomNav);
        navBar.setSelectedTab(2);
        boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
        navBar.setOnTabSelectedListener(position -> {
            if (position == 0) {
                Intent intent = new Intent(this, AllEventsActivity.class);
                intent.putExtra("type", "all");
                intent.putExtra("IS_ADMIN", isAdmin);
                startActivity(intent);
                finish();
            } else if (position == 1) {
                if(isAdmin) {
                    Intent intent = new Intent(this, AdminPage.class);
                    intent.putExtra("IS_ADMIN", isAdmin);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(this, HomePage.class);
                    intent.putExtra("IS_ADMIN", isAdmin);
                    startActivity(intent);
                }
                finish();
            } else if (position == 3) {
                Intent intent = new Intent(this, ProfilePage.class);
                intent.putExtra("IS_ADMIN", isAdmin);
                startActivity(intent);
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
            // populating the notification card with title and message
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

            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Show buttons only if it's an invitation and we have an eventId
            String title = entry.get("title");

            boolean isInvitation = title != null && (
                    title.equalsIgnoreCase("You've been selected!") ||
                            title.equalsIgnoreCase("You've been invited!") ||
                            title.equalsIgnoreCase("Co-Organizer Invitation")
            );

            if (isInvitation && eventId != null && !eventId.isEmpty()) {
                holder.buttonRow.setVisibility(View.VISIBLE);

                holder.btnAccept.setOnClickListener(v -> {
                    handleAction(v, eventId, currentUserId, "accepted", position);
                });

                holder.btnDecline.setOnClickListener(v -> {
                    handleAction(v, eventId, currentUserId, "declined", position);
                });
            } else {
                holder.buttonRow.setVisibility(View.GONE);
            }

            // clicking a notification marks it as read and opens the event detail screen US 01.04.03
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
                    boolean isAdmin = ((android.app.Activity) v.getContext()).getIntent().getBooleanExtra("IS_ADMIN", false);
                    intent.putExtra("event_id", eventId);
                    intent.putExtra("IS_ADMIN", isAdmin);
                    v.getContext().startActivity(intent);
                }
            });
        }

        /**
         * processing an accept or decline action on an invitation notification,
         * updating the attendee status in Firestore and removing the notification card
         *
         * @param v the view that was clicked
         * @param eventId the event tied to this invitation
         * @param userId the current user's uid
         * @param action either "accepted" or "declined"
         * @param position the adapter position of this notification card
         */
        private void handleAction(View v, String eventId, String userId, String action, int position) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, String> entry = list.get(position);
            String notifId = entry.get("id");
            String title = entry.get("title");

            if ("declined".equals(action)) {
                db.collection("events").document(eventId)
                        .collection("attendees").document(userId)
                        .delete()
                        .addOnSuccessListener(aVoid -> cleanupNotification(v, notifId, position, "Invitation declined"))
                        .addOnFailureListener(e -> Toast.makeText(v.getContext(), "Failed to decline", Toast.LENGTH_SHORT).show());
            } else {
                // They clicked ACCEPT.
                db.collection("events").document(eventId).get().addOnSuccessListener(eventDoc -> {
                    Boolean isPrivate = eventDoc.getBoolean("isPrivate");

                    Log.d("InviteDebug", "Event isPrivate value: " + isPrivate);

                    // Use addOnCompleteListener to safely check if the attendee document exists
                    db.collection("events").document(eventId)
                            .collection("attendees").document(userId).get()
                            .addOnCompleteListener(task -> {

                                String currentStatus = null;
                                if (task.isSuccessful() && task.getResult().exists()) {
                                    currentStatus = task.getResult().getString("status");
                                }
                                Log.d("InviteDebug", "User's current status before click: " + currentStatus);

                                String tempStatus = "accepted"; // Default fallback
                                String tempToastMsg = "You've joined the event!";

                                // Route user based strictly on the Notification Title
                                if ("Co-Organizer Invitation".equalsIgnoreCase(title)) {
                                    tempStatus = "accepted_coorg";
                                    tempToastMsg = "You are now a Co-Organizer!";
                                }
                                else if ("You've been invited!".equalsIgnoreCase(title)) {
                                    // It's a standard invite to a private event -> send to Waiting List
                                    tempStatus = "pending";
                                    tempToastMsg = "Added to Waiting List!";
                                }
                                else if ("You've been selected!".equalsIgnoreCase(title)) {
                                    // It's a lottery win -> send to Enrolled List
                                    tempStatus = "accepted";
                                    tempToastMsg = "You've joined the event!";
                                }

                                Log.d("InviteDebug", "App decided next status should be: " + tempStatus);

                                final String nextStatus = tempStatus;
                                final String finalToastMsg = tempToastMsg;

                                // Use set() with merge() so it creates the document if it doesn't exist
                                Map<String, Object> updateData = new HashMap<>();
                                updateData.put("status", nextStatus);

                                db.collection("events").document(eventId)
                                        .collection("attendees").document(userId)
                                        .set(updateData, com.google.firebase.firestore.SetOptions.merge())
                                        .addOnSuccessListener(unused -> cleanupNotification(v, notifId, position, finalToastMsg))
                                        .addOnFailureListener(e -> Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            });
                });
            }
        }

        /**
         * deleting the notification document from Firestore and removing
         * the card from the adapter list with a toast confirmation
         */
        private void cleanupNotification(View v, String notifId, int position, String toastMsg) {
            if (notifId != null) {
                FirebaseFirestore.getInstance().collection("notifications").document(notifId).delete();
            }
            list.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(v.getContext(), toastMsg, Toast.LENGTH_SHORT).show();
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
            LinearLayout buttonRow;
            Button btnAccept, btnDecline;

            NotifViewHolder(View itemView) {
                super(itemView);
                titleText = itemView.findViewById(R.id.notifTitle);
                messageText = itemView.findViewById(R.id.notifMessage);
                card = itemView.findViewById(R.id.notifCard);
                eventChip = itemView.findViewById(R.id.notifEventChip);
                unreadDot = itemView.findViewById(R.id.unreadDot);
                buttonRow = itemView.findViewById(R.id.notifButtonRow);
                btnAccept = itemView.findViewById(R.id.btnAccept);
                btnDecline = itemView.findViewById(R.id.btnDecline);
            }
        }
    }
}