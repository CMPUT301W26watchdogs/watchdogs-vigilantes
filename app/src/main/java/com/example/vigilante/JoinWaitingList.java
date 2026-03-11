package com.example.vigilante;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
public class EventDetailsActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String eventId = "1Ftu8knY2kc7tkt2RMTA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        Button joinButton = findViewById(R.id.joinButton);
        joinButton.setOnClickListener(v -> {
            joinWaitingList(eventId);
        });}
    //Gemini March 10th 2026, how to set base for class
    private void joinWaitingList(String eventId) {
        String email = auth.getCurrentUser().getEmail();
        Map<String, Object> registration = new HashMap<>();
        registration.put("userEmail", email);
        registration.put("eventId", eventId);
        registration.put("status", "waiting");
        db.collection("registrations")
                .add(registration)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(
                            EventDetailsActivity.this,
                            "Joined waiting list",
                            Toast.LENGTH_SHORT).show();})
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            EventDetailsActivity.this,
                            "Join failed",
                            Toast.LENGTH_SHORT).show();});
    }
    private void leaveWaitingList(String eventId) {
        String email = auth.getCurrentUser().getEmail();
        db.collection("registrations")
                .whereEqualTo("userEmail", email)
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (var doc : queryDocumentSnapshots.getDocuments()) {
                        db.collection("registrations")
                                .document(doc.getId())
                                .delete();}
                    Toast.makeText(
                            EventDetailsActivity.this,
                            "Left waiting list",
                            Toast.LENGTH_SHORT).show();})
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            EventDetailsActivity.this,
                            "Failed to leave",
                            Toast.LENGTH_SHORT
                    ).show();})
        ;}}