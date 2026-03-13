package com.example.vigilante;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

public class SendNotificationActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String eventTitle = "AI Conference";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);
        db = FirebaseFirestore.getInstance();
        Button sendButton = findViewById(R.id.sendNotificationButton);
        sendButton.setOnClickListener(v -> {
            sendNotifications();});}
    private void sendNotifications() {
        db.collection("registrations")
                .whereEqualTo("eventTitle", eventTitle)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String email = doc.getString("userEmail");
                        createNotification(email);}
                    Toast.makeText(this,
                            "Notifications sent",
                            Toast.LENGTH_SHORT).show();});}
    private void createNotification(String email) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("userEmail", email);
        notification.put("eventTitle", eventTitle);
        notification.put("message", "You have a notification for event: " + eventTitle);
        notification.put("timestamp", System.currentTimeMillis());
        db.collection("notifications").add(notification);}}