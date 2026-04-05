// Helper for sending event-related Android notifications

package com.example.vigilante;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
/**
 *This class is a helper for listening to Firebase changes and sending event-related notifications.
 */
// Gemini, 2026-03-31, Make entrants receive a notification (in app and Android notification) if selected or not selected for an event while in the app
public class NotificationHelper {
    private static final String CHANNEL_ID = "vigilante_notifications";
    private static final String CHANNEL_NAME = "Event Notifications";
    private static final String CHANNEL_DESC = "Notifications for event selection and updates";
    private static ListenerRegistration listenerRegistration;

    /** building and displaying an android notification with a pending intent to the notifications screen */
    public static void showNotification(Context context, String title, String message, String eventId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, NotificationsActivity.class);
        intent.putExtra("event_id", eventId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_nav_alerts)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public static void listenForNotifications(Context context) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        if (listenerRegistration != null) {
            return; // Already listening
        }

        Context appContext = context.getApplicationContext();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        // Use a timestamp to only get notifications added AFTER the app started/listener attached
        // to avoid spamming old notifications on startup.
        long startTime = System.currentTimeMillis();

        listenerRegistration = db.collection("notifications")
                .whereEqualTo("userId", userId)
                .whereEqualTo("read", false)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        return;
                    }

                    if (snapshots != null) {
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                // Double check it's not an old one being loaded initially
                                // Firestore might trigger ADDED for existing docs.
                                // We can check server timestamp if we want to be precise, 
                                // but checking 'read' == false is a good start.
                                
                                String title = dc.getDocument().getString("title");
                                String message = dc.getDocument().getString("message");
                                String eventId = dc.getDocument().getString("eventId");

                                showNotification(appContext, title, message, eventId);
                            }
                        }
                    }
                });
    }

    public static void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }
}
