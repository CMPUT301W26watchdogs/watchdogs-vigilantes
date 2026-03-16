package com.example.vigilante;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String eventId;
    private boolean geolocationRequired = false;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        eventId = getIntent().getStringExtra("event_id");

        TextView title = findViewById(R.id.eventTitle);
        TextView description = findViewById(R.id.eventDescription);
        TextView date = findViewById(R.id.eventDate);
        TextView location = findViewById(R.id.eventLocation);
        TextView capacity = findViewById(R.id.eventCapacity);
        TextView price = findViewById(R.id.eventPrice);
        TextView registration = findViewById(R.id.eventRegistration);
        TextView signUpStatus = findViewById(R.id.signUpStatus);
        Button registerButton = findViewById(R.id.registerButton);
        ImageView posterImage = findViewById(R.id.eventPoster);
        TextView regStartDate = findViewById(R.id.regStartDate);
        TextView regEndDate = findViewById(R.id.regEndDate);
        TextView waitlistCount = findViewById(R.id.waitlistCount);

        if (eventId != null) {
            db = FirebaseFirestore.getInstance();

            db.collection("events").document(eventId)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            title.setText(doc.getString("title") != null ? doc.getString("title") : "Untitled Event");
                            description.setText(doc.getString("description") != null ? doc.getString("description") : "");
                            date.setText(doc.getString("date") != null ? doc.getString("date") : "TBD");
                            location.setText(doc.getString("location") != null ? doc.getString("location") : "TBD");

                            Long waitLimit = doc.getLong("waitingListLimit");
                            if (waitLimit != null) {
                                capacity.setText(String.valueOf(waitLimit));
                            } else {
                                capacity.setText(doc.getString("capacity") != null ? doc.getString("capacity") : "-");
                            }

                            price.setText(doc.getString("price") != null ? doc.getString("price") : "Free");

                            Boolean geoRequired = doc.getBoolean("geolocationRequired");
                            geolocationRequired = Boolean.TRUE.equals(geoRequired);

                            String regStart = doc.getString("registrationStart");
                            String regEnd = doc.getString("registrationEnd");
                            if (regStart != null && regEnd != null) {
                                registration.setText(regStart + " — " + regEnd);
                                regStartDate.setText(regStart);
                                regEndDate.setText(regEnd);
                            } else {
                                registration.setText("-");
                                regStartDate.setText("-");
                                regEndDate.setText("-");
                            }

                            String posterUrl = doc.getString("posterUrl");
                            if (posterUrl != null && !posterUrl.isEmpty()) {
                                Glide.with(this)
                                        .load(posterUrl)
                                        .placeholder(android.R.drawable.ic_menu_gallery)
                                        .error(android.R.drawable.ic_menu_gallery)
                                        .into(posterImage);
                            }
                        } else {
                            title.setText("Event Not Found");
                            description.setText("No event found for this QR code in the database.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        title.setText("Error Loading Event");
                        description.setText(e.getMessage());
                    });

            loadWaitlistCount(waitlistCount);

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                db.collection("events").document(eventId)
                        .collection("attendees").document(currentUser.getUid())
                        .get()
                        .addOnSuccessListener(doc -> {
                            if (doc.exists()) {
                                String status = doc.getString("status");

                                if ("pending".equals(status)) {
                                    signUpStatus.setText("Your status: Pending");
                                    registerButton.setText("Cancel SignUp");
                                    registerButton.setOnClickListener(v -> cancelSignUp(eventId, currentUser.getUid()));
                                } else if ("selected".equals(status)) {
                                    signUpStatus.setText("Your status: Selected");
                                    registerButton.setText("You're Selected!");
                                    registerButton.setEnabled(false);
                                } else if ("cancelled".equals(status)) {
                                    signUpStatus.setText("You cancelled your sign-up.");
                                    registerButton.setText("Sign Up");
                                    registerButton.setOnClickListener(v -> performSignUp(eventId, currentUser.getUid()));
                                }
                            } else {
                                signUpStatus.setText("You have not signed up for this event");
                                registerButton.setOnClickListener(v -> performSignUp(eventId, currentUser.getUid()));
                            }
                        })
                        .addOnFailureListener(e ->
                                signUpStatus.setText("Could not check sign-up status"));
            } else {
                signUpStatus.setText("Log in to sign up");
                registerButton.setEnabled(false);
            }
        } else {
            title.setText("No Event ID");
            description.setText("No event ID was provided.");
        }

        findViewById(R.id.lotteryInfoButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, LotteryInfoActivity.class);
            intent.putExtra("event_id", eventId);
            startActivity(intent);
        });

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        setupBottomNav();
    }

    private void loadWaitlistCount(TextView waitlistCount) {
        db.collection("events").document(eventId)
                .collection("attendees")
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(snapshots -> {
                    int count = 0;
                    for (QueryDocumentSnapshot ignored : snapshots) {
                        count++;
                    }
                    waitlistCount.setText(String.valueOf(count));
                })
                .addOnFailureListener(e -> waitlistCount.setText("0"));
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_events) {
                Intent intent = new Intent(this, AllEventsActivity.class);
                intent.putExtra("type", "all");
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomePage.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfilePage.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void performSignUp(String eventId, String userId) {
        if (geolocationRequired) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
                return;
            }
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    submitSignUp(eventId, userId, location.getLatitude(), location.getLongitude());
                } else {
                    submitSignUp(eventId, userId, null, null);
                    Toast.makeText(this, "Location unavailable, signing up without location", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                submitSignUp(eventId, userId, null, null);
            });
        } else {
            submitSignUp(eventId, userId, null, null);
        }
    }

    private void submitSignUp(String eventId, String userId, Double latitude, Double longitude) {
        db.collection("users").document(userId).get().addOnSuccessListener(userDoc -> {
            if (userDoc.exists()) {
                String name = userDoc.getString("name");
                String email = userDoc.getString("email");

                Map<String, Object> attendeeData = new HashMap<>();
                attendeeData.put("name", name != null ? name : "Unknown");
                attendeeData.put("email", email != null ? email : "");
                attendeeData.put("userId", userId);
                attendeeData.put("status", "pending");
                attendeeData.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

                if (latitude != null && longitude != null) {
                    attendeeData.put("latitude", latitude);
                    attendeeData.put("longitude", longitude);
                }

                db.collection("events").document(eventId)
                        .collection("attendees").document(userId)
                        .set(attendeeData)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Signed up successfully!", Toast.LENGTH_SHORT).show();
                            recreate();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to sign up: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "User profile not found. Please complete profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    performSignUp(eventId, currentUser.getUid());
                } else {
                    submitSignUp(eventId, currentUser.getUid(), null, null);
                }
            }
        }
    }

    private void cancelSignUp(String eventId, String userId) {
        Map<String, Object> attendeeData = new HashMap<>();
        attendeeData.put("userId", userId);
        attendeeData.put("status", "cancelled");
        attendeeData.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

        db.collection("events").document(eventId)
                .collection("attendees").document(userId)
                .set(attendeeData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cancelled Successfully!", Toast.LENGTH_SHORT).show();
                    recreate();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error cancelling: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
