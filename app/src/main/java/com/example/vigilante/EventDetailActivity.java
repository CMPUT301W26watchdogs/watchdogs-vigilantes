//  displays event detailed info page and handles entrant sign-up

package com.example.vigilante;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.content.ContentValues;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class allows user to view event by scanning a qr code from their homescreen
 */

// Gemini, 2026-04-02, Organizers should be able to delete comments on their own events. Admins should be able to delete comments on any event.
public class EventDetailActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private String eventId;
    private boolean geolocationRequired = false;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    private List<Comment> commentList;
    private CommentAdapter commentAdapter;
    private EditText commentInput;
    private String organizerId;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_detail);

        isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);

        // If intent not passed, check if current user is admin based on email
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (!isAdmin && currentUser != null && "admin@admin.com".equals(currentUser.getEmail())) {
            isAdmin = true;
        }

        recyclerView = findViewById(R.id.commentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // getting the event ID string that was passed from MainActivity after scanning the QR code
        eventId = getIntent().getStringExtra("event_id");

        commentList = new ArrayList<>();

        commentAdapter = new CommentAdapter(commentList, false, comment -> deleteComment(comment));
        recyclerView.setAdapter(commentAdapter);
        // getting references to all the text views that display event details
        TextView title = findViewById(R.id.eventTitle);
        TextView description = findViewById(R.id.eventDescription);
        TextView date = findViewById(R.id.eventDate);
        TextView location = findViewById(R.id.eventLocation);
        TextView capacity = findViewById(R.id.eventCapacity);
        TextView price = findViewById(R.id.eventPrice);
        TextView registration = findViewById(R.id.eventRegistration);
        TextView signUpStatus = findViewById(R.id.signUpStatus);
        Button registerButton = findViewById(R.id.registerButton);
        Button acceptButton = findViewById(R.id.acceptButton);
        Button declineButton = findViewById(R.id.declineButton);
        Button downloadTicketButton = findViewById(R.id.downloadTicketButton);
        ImageView posterImage = findViewById(R.id.eventPoster);
        TextView regStartDate = findViewById(R.id.regStartDate);
        TextView regEndDate = findViewById(R.id.regEndDate);
        TextView waitlistCount = findViewById(R.id.waitlistCount);

        commentInput = findViewById(R.id.comment_description);
        Button postCommentButton = findViewById(R.id.send_comment_button);



        // querying Firestore for the event document using the scanned event ID
        if (eventId != null) {
            db = FirebaseFirestore.getInstance();

            // querying Firestore for the event document using the scanned event ID
            fetchAllComments(eventId);
            db.collection("events").document(eventId)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            // populating the text views with data from Firestore
                            title.setText(doc.getString("title") != null ? doc.getString("title") : "Untitled Event");
                            description.setText(doc.getString("description") != null ? doc.getString("description") : "");
                            date.setText(doc.getString("date") != null ? doc.getString("date") : "TBD");
                            location.setText(doc.getString("location") != null ? doc.getString("location") : "TBD");

                            organizerId = doc.getString("organizerId");
                            checkCommentPermissions();

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
                            // document doesn't exist in Firestore
                            title.setText("Event Not Found");
                            description.setText("No event found for this QR code in the database.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        title.setText("Error Loading Event");
                        description.setText(e.getMessage());
                    });

            loadWaitlistCount(waitlistCount);

            // checking if the current user is already signed up for this event
            //Gemini , march 13th 2026, synchronize sign up button at eventdetail and eventadapter
            FirebaseUser currentUserCheck = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUserCheck != null) {
                db.collection("events").document(eventId)
                        .collection("attendees").document(currentUserCheck.getUid())
                        .get()
                        .addOnSuccessListener(doc -> {
                            if (doc.exists()) {
                                String status = doc.getString("status");
                                // March 31 2026, Claude Opus 4.6, made lottery info button visible when user has any attendee status
                                findViewById(R.id.lotteryInfoButton).setVisibility(View.VISIBLE);

                                // --- CO-ORGANIZER CHECK ADDED HERE ---
                                if ("accepted_coorg".equals(status)) {
                                    signUpStatus.setText("Your status: Co-Organizer");
                                    registerButton.setText("You are a Co-Organizer");
                                    registerButton.setEnabled(false);
                                    registerButton.setVisibility(View.VISIBLE);
                                    acceptButton.setVisibility(View.GONE);
                                    declineButton.setVisibility(View.GONE);
                                } else if ("pending".equals(status)) {
                                    signUpStatus.setText("Your status: Pending");
                                    registerButton.setText("Cancel SignUp");
                                    registerButton.setVisibility(View.VISIBLE);
                                    acceptButton.setVisibility(View.GONE);
                                    declineButton.setVisibility(View.GONE);
                                    registerButton.setOnClickListener(v -> cancelSignUp(eventId, currentUserCheck.getUid()));
                                } else if ("selected".equals(status)) {
                                    signUpStatus.setText("You've been selected! Accept or decline your invitation.");
                                    registerButton.setVisibility(View.GONE);
                                    acceptButton.setVisibility(View.VISIBLE);
                                    declineButton.setVisibility(View.VISIBLE);
                                    acceptButton.setOnClickListener(v -> acceptInvitation(eventId, currentUserCheck.getUid()));
                                    declineButton.setOnClickListener(v -> declineInvitation(eventId, currentUserCheck.getUid()));
                                } else if ("accepted".equals(status)) {
                                    signUpStatus.setText("Your status: Accepted. You're in!");
                                    registerButton.setText("Enrolled");
                                    registerButton.setEnabled(false);
                                    registerButton.setVisibility(View.VISIBLE);
                                    acceptButton.setVisibility(View.GONE);
                                    declineButton.setVisibility(View.GONE);

                                    // showing the download ticket button for accepted entrants (Wildcard)
                                    downloadTicketButton.setVisibility(View.VISIBLE);
                                    downloadTicketButton.setOnClickListener(v ->
                                            generateAndSaveTicket(title.getText().toString(),
                                                    date.getText().toString(),
                                                    location.getText().toString(),
                                                    currentUserCheck.getUid()));
                                } else if ("cancelled".equals(status) || "declined".equals(status)) {
                                    signUpStatus.setText("You are no longer on the waitlist.");
                                    registerButton.setText("Sign Up");
                                    registerButton.setVisibility(View.VISIBLE);
                                    acceptButton.setVisibility(View.GONE);
                                    declineButton.setVisibility(View.GONE);
                                    registerButton.setOnClickListener(v -> performSignUp(eventId, currentUserCheck.getUid()));
                                }
                            } else {
                                signUpStatus.setText("You have not signed up for this event");
                                registerButton.setVisibility(View.VISIBLE);
                                acceptButton.setVisibility(View.GONE);
                                declineButton.setVisibility(View.GONE);
                                registerButton.setOnClickListener(v -> performSignUp(eventId, currentUserCheck.getUid()));
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

        // opening lottery info screen, passing the same event ID so it can show the right data
        findViewById(R.id.lotteryInfoButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, LotteryInfoActivity.class);
            intent.putExtra("event_id", eventId);
            startActivity(intent);
        });


        postCommentButton.setOnClickListener(v -> {
            FirebaseUser currentUserPost = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUserPost != null) {
                db.collection("users").document(currentUserPost.getUid()).get().addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        String name = userDoc.getString("name");
                        postComment(eventId, currentUserPost.getUid(), name);
                    }
                });
            }
        });

        // closing this screen and return to the previous one
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        setupBottomNav();
    }

    private void checkCommentPermissions() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            boolean isOrganizer = currentUser.getUid().equals(organizerId);
            commentAdapter.setCanDelete(isAdmin || isOrganizer);
        }
    }

    private void deleteComment(Comment comment) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Comment")
                .setMessage("Are you sure you want to delete this comment?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (eventId != null && comment.getId() != null) {
                        db.collection("events").document(eventId)
                                .collection("comments").document(comment.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Comment deleted", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete comment", Toast.LENGTH_SHORT).show());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // handling the accept invitation flow by setting status to accepted US 01.05.01
    private void acceptInvitation(String eventId, String userId) {
        db.collection("events").document(eventId)
                .collection("attendees").document(userId)
                .update("status", "accepted")
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "You've accepted the invitation!", Toast.LENGTH_SHORT).show();
                    recreate();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // handling the decline invitation flow by setting status to declined and triggering replacement draw US 01.05.01
    private void declineInvitation(String eventId, String userId) {
        db.collection("events").document(eventId)
                .collection("attendees").document(userId)
                .update("status", "declined")
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Invitation declined.", Toast.LENGTH_SHORT).show();
                    drawReplacementFromWaitlist(eventId);
                    recreate();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // randomly selecting a replacement entrant from pending waitlist after a decline US 01.05.01
    // Citation: Ved, March 13 2025, Claude referred to https://docs.oracle.com/javase/8/docs/api/java/util/Random.html#nextInt-int-
    private void drawReplacementFromWaitlist(String eventId) {
        db.collection("events").document(eventId)
                .collection("attendees")
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(snapshots -> {
                    List<QueryDocumentSnapshot> pending = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        pending.add(doc);
                    }
                    if (!pending.isEmpty()) {
                        int randomIndex = new Random().nextInt(pending.size());
                        QueryDocumentSnapshot chosen = pending.get(randomIndex);
                        chosen.getReference().update("status", "selected");

                        String chosenUserId = chosen.getString("userId");
                        if (chosenUserId != null) {
                            Map<String, Object> notification = new HashMap<>();
                            notification.put("userId", chosenUserId);
                            notification.put("eventId", eventId);
                            notification.put("title", "You've been selected!");
                            notification.put("message", "A spot opened up and you were drawn from the waitlist. Open the event to accept or decline.");
                            notification.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());
                            notification.put("read", false);
                            db.collection("notifications").add(notification);
                        }

                        Toast.makeText(this, "A replacement has been drawn from the waitlist.", Toast.LENGTH_SHORT).show();
                    }
                });
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

    // generating a PDF ticket and saving it to the device's Downloads folder (Wildcard)
    // Citation: Ved, March 16 2025, Claude referred to https://developer.android.com/reference/android/graphics/pdf/PdfDocument
    private void generateAndSaveTicket(String eventTitle, String eventDate, String eventLocation, String userId) {
        // fetching the attendee's name from Firestore to put on the ticket
        db.collection("users").document(userId).get().addOnSuccessListener(userDoc -> {
            String attendeeName = "Attendee";
            if (userDoc.exists() && userDoc.getString("name") != null) {
                attendeeName = userDoc.getString("name");
            }

            // using the event ID as the ticket number
            String ticketId = eventId != null ? eventId : "000000";

            TicketGenerator generator = new TicketGenerator(eventTitle, eventDate, eventLocation, attendeeName, ticketId);
            PdfDocument document = generator.generate();

            try {
                // saving to Downloads using MediaStore on Android 10+ or direct file write on older versions
                String fileName = "ticket_" + ticketId.substring(0, Math.min(8, ticketId.length())) + ".pdf";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                    values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                    values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                    Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                    if (uri != null) {
                        OutputStream out = getContentResolver().openOutputStream(uri);
                        if (out != null) {
                            document.writeTo(out);
                            out.close();
                            Toast.makeText(this, "Ticket saved to Downloads!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File file = new File(downloadsDir, fileName);
                    FileOutputStream out = new FileOutputStream(file);
                    document.writeTo(out);
                    out.close();
                    Toast.makeText(this, "Ticket saved to Downloads!", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(this, "Could not save ticket: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                document.close();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Could not load user info for ticket", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupBottomNav() {
        LiquidGlassNavBar navBar = findViewById(R.id.bottomNav);
        navBar.setOnTabSelectedListener(position -> {
            if (position == 0) {
                Intent intent = new Intent(this, AllEventsActivity.class);
                intent.putExtra("type", "all");
                intent.putExtra("IS_ADMIN", isAdmin);
                startActivity(intent);
                finish();
            } else if (position == 1) {
                if(isAdmin){
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
            } else if (position == 2) {
                Intent intent = new Intent(this, NotificationsActivity.class);
                intent.putExtra("IS_ADMIN", isAdmin);
                startActivity(intent);
                finish();
            } else if (position == 3) {
                Intent intent = new Intent(this, ProfilePage.class);
                intent.putExtra("IS_ADMIN", isAdmin);
                startActivity(intent);
                finish();
            }
        });
    }

    //Gemini , march 13th 2026, synchronize sign up button at eventdetail and eventadapter
    /**
     * This function helps user to directly sign up from event details page
     */
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
                attendeeData.put("status", "pending"); // MATCHES ADAPTER
                attendeeData.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

                if (latitude != null && longitude != null) { // geolocation data if available
                    attendeeData.put("latitude", latitude);
                    attendeeData.put("longitude", longitude);
                }

                db.collection("events").document(eventId)
                        .collection("attendees").document(userId)
                        .set(attendeeData)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Signed up successfully!", Toast.LENGTH_SHORT).show();
                            recreate(); // Recreate activity to refresh the UI state easily
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

    //Gemini , march 13th 2026, synchronize sign up button at eventdetail and eventadapter
    /**
     * This function helps user to directly cancel sign up from event details page
     */
    private void cancelSignUp(String eventId, String userId) {
        Map<String, Object> attendeeData = new HashMap<>();
        attendeeData.put("userId", userId);
        attendeeData.put("status", "cancelled"); // MATCHES ADAPTER
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

    private void postComment(String eventId, String userId, String userName){
        String commentText = commentInput.getText().toString().trim();
        if (commentText.isEmpty()) {
            Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("userId", userId);
        commentData.put("name", userName);
        commentData.put("commentText", commentText);
        commentData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("events").document(eventId).collection("comments").add(commentData).addOnSuccessListener(documentReference -> {
            commentInput.setText("");
            Toast.makeText(this, "Comment posted!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to post comment!" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
    //Gemini March 21st 2026, help me retrieve comments from firebase
    private void fetchAllComments(String eventId) {
        db.collection("events").document(eventId).collection("comments").orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener((value,error)-> {
            if(error != null){
                Toast.makeText(this, "Error loading comments:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            if(value != null) {
                commentList.clear();

                for (QueryDocumentSnapshot document : value) {
                    Comment comment = document.toObject(Comment.class);
                    comment.setId(document.getId());
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();

                if (!commentList.isEmpty()) {
                    recyclerView.scrollToPosition(commentList.size() - 1);
                }
            }
        });
    }
}