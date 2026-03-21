//  displays event detailed info page and handles entrant sign-up

package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
* This class allows user to view event by scanning a qr code from their homescreen
 */
public class EventDetailActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private String eventId;
    private Button joinButton;

    private List<Comment> commentList;
    private CommentAdapter commentAdapter;
    private EditText commentInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_detail);

        recyclerView = findViewById(R.id.commentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // getting the event ID string that was passed from MainActivity after scanning the QR code
        String eventId = getIntent().getStringExtra("event_id");

        commentList = new ArrayList<>();

        commentAdapter = new CommentAdapter(commentList);
        recyclerView.setAdapter(commentAdapter);
        // getting references to all the text views that display event details
        TextView title = findViewById(R.id.eventTitle);
        TextView description = findViewById(R.id.eventDescription);
        TextView date = findViewById(R.id.eventDate);
       // TextView location = findViewById(R.id.eventLocation);
        TextView capacity = findViewById(R.id.eventCapacity);
        //TextView price = findViewById(R.id.eventPrice);
        TextView registration = findViewById(R.id.eventRegistration);
        TextView signUpStatus = findViewById(R.id.signUpStatus);
        Button registerButton = findViewById(R.id.registerButton);

        commentInput = findViewById(R.id.comment_description);
        Button postCommentButton = findViewById(R.id.send_comment_button);



        // querying Firestore for the event document using the scanned event ID
        if (eventId != null) {
            db = FirebaseFirestore.getInstance();
            fetchAllComments(eventId);
            db.collection("events").document(eventId)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            // populating the text views with data from Firestore
                            title.setText(doc.getString("title") != null ? doc.getString("title") : "Untitled Event");
                            description.setText(doc.getString("description") != null ? doc.getString("description") : "");
                            date.setText(doc.getString("date") != null ? doc.getString("date") : "-");
                            //location.setText(doc.getString("location") != null ? doc.getString("location") : "-");
                            capacity.setText(doc.getString("capacity") != null ? doc.getString("capacity") : "-");
                           // price.setText(doc.getString("price") != null ? doc.getString("price") : "Free");
                            String regStart = doc.getString("registrationStart");
                            String regEnd = doc.getString("registrationEnd");
                            if (regStart != null && regEnd != null) {
                                registration.setText(regStart + " — " + regEnd);
                            } else {
                                registration.setText("-");
                            }
                        } else {
                            // document doesn't exist in Firestore
                            title.setText("Event Not Found");
                            description.setText("No event found for this QR code in the database.");
                            date.setText("-");
                            //location.setText("-");
                            capacity.setText("-");
                            //price.setText("-");
                            registration.setText("-");
                        }
                    })
                    .addOnFailureListener(e -> {
                        title.setText("Error Loading Event");
                        description.setText(e.getMessage());
                    });

            // checking if the current user is already signed up for this event
            //Gemini , march 13th 2026, synchronize sign up button at eventdetail and eventadapter
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                db.collection("events").document(eventId)
                        .collection("attendees").document(currentUser.getUid()) // MATCHES ADAPTER
                        .get()
                        .addOnSuccessListener(doc -> {
                            if (doc.exists()) {
                                String status = doc.getString("status");

                                if ("pending".equals(status)) {
                                    signUpStatus.setText("Your status: Pending");
                                    registerButton.setText("Cancel SignUp");
                                    // Set click listener to cancel (similar to adapter)
                                    registerButton.setOnClickListener(v -> cancelSignUp(eventId, currentUser.getUid()));
                                } else if ("selected".equals(status)) {
                                    signUpStatus.setText("Your status: Selected");
                                    registerButton.setText("You're Selected!");
                                    registerButton.setEnabled(false);
                                } else if ("cancelled".equals(status)) {
                                    signUpStatus.setText("You cancelled your sign-up.");
                                    registerButton.setText("Sign Up");
                                    // Allow them to sign up again
                                    registerButton.setOnClickListener(v -> performSignUp(eventId, currentUser.getUid()));
                                }
                            } else {
                                // user has not signed up yet
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


        // opening lottery info screen, passing the same event ID so it can show the right data
        findViewById(R.id.lotteryInfoButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, LotteryInfoActivity.class);
            intent.putExtra("event_id", eventId);
            startActivity(intent);
        });


        postCommentButton.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            db.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(userDoc -> {
                        if (userDoc.exists()) {
                            String name = userDoc.getString("name");
                            postComment(eventId, currentUser.getUid(), name);
                        }
                    });

        });

        // closing this screen and return to the previous one
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }
    //Gemini , march 13th 2026, synchronize sign up button at eventdetail and eventadapter
    /**
    * This function helps user to directly sign up from event details page
     */
    private void performSignUp(String eventId, String userId) {
        db.collection("users").document(userId).get().addOnSuccessListener(userDoc -> {
            if (userDoc.exists()) {
                // Get data from your users collection
                String name = userDoc.getString("name");
                String email = userDoc.getString("email");

                Map<String, Object> attendeeData = new HashMap<>();
                attendeeData.put("name", name != null ? name : "Unknown");
                attendeeData.put("email", email != null ? email : "");
                attendeeData.put("userId", userId);
                attendeeData.put("status", "pending"); // MATCHES ADAPTER
                attendeeData.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

                db.collection("events").document(eventId)
                        .collection("attendees").document(userId) // MATCHES ADAPTER
                        .set(attendeeData)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Signed up successfully!", Toast.LENGTH_SHORT).show();
                            // Recreate activity to refresh the UI state easily
                            recreate();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to sign up: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "User profile not found. Please complete profile.", Toast.LENGTH_SHORT).show();
            }
        });
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
                    recreate(); // Refresh UI
                })
                .addOnFailureListener( e -> {
                    Toast.makeText(this, "Error cancelling: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void postComment(String eventId, String userId, String userName){
        String comment = commentInput.getText().toString().trim();
        if (comment.isEmpty()) {
            Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("userId", userId);
        commentData.put("name", userName);
        commentData.put("commentText", comment);
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
            }
            if(value != null) {
                commentList.clear();

                for (QueryDocumentSnapshot document : value) {
                    Comment comment = document.toObject(Comment.class);
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