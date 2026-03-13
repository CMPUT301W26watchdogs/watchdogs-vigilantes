// RecyclerView adapter for event cards — handles sign-up, cancel, edit poster and delete actions depending on user role

package com.example.vigilante;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.bumptech.glide.Glide;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.bumptech.glide.Glide;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Gemini March 8th 2026, Help view a list of events from firebase
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private boolean isMyEventsPage;

    private boolean isMyEventsPageAdmin;

    private boolean isMyEventsPageUser;


    public EventAdapter(List<Event> eventList, boolean isMyEventsPage, boolean isMyEventsPageAdmin, boolean isMyEventsPageUser) {
        this.eventList = eventList;
        this.isMyEventsPage = isMyEventsPage;
        this.isMyEventsPageAdmin = isMyEventsPageAdmin;
        this.isMyEventsPageUser = isMyEventsPageUser;
    }

    @NotNull
    @Override
    public EventViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.titleText.setText(event.getTitle());
        holder.descriptionText.setText(event.getDescription());

        if(isMyEventsPageUser){
            holder.signUpEvent.setVisibility(View.VISIBLE);

            // 1. IMPORTANT: Set a default state immediately so it's clickable while loading
            holder.signUpEvent.setEnabled(true);
            holder.signUpEvent.setText("Sign Up");
            holder.signUpEvent.setOnClickListener(v -> {
                showSignUpDialog(v.getContext(), event, position);
            });

            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // 2. Now check the database to see if we should OVERRIDE the default
            db.collection("events").document(event.getId())
                    .collection("attendees").document(currentUserId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists()){
                            String status = documentSnapshot.getString("status");

                            if("pending".equals(status)) {
                                holder.signUpEvent.setText("Cancel SignUp");
                                holder.signUpEvent.setOnClickListener(v -> {
                                    cancelSignUp(v.getContext(), event, position);
                                });

                            } else if ("selected".equals(status)) {
                                holder.signUpEvent.setText("You're Selected");
                                holder.signUpEvent.setEnabled(false); // Only disable for winners!
                            }

                        }
                    });
        }
        else {
            holder.signUpEvent.setVisibility(View.GONE);
        }

        if(isMyEventsPageAdmin){
            holder.deleteEvent.setVisibility(View.VISIBLE);

            holder.deleteEvent.setOnClickListener(v -> {
                showDeleteDialog(v.getContext(), event, position);
            });
        }
        else {
            holder.deleteEvent.setVisibility(View.GONE);
        }
        //Gemini March 8th 2026, view image poster in the list of events
        if (isMyEventsPage) {
            holder.editUrl.setVisibility(View.VISIBLE);
            holder.viewAttendee.setVisibility(View.VISIBLE);
            holder.viewAttendeeCancelled.setVisibility(View.VISIBLE);
            holder.viewAttendeeSelected.setVisibility(View.VISIBLE);

            holder.editUrl.setOnClickListener(v -> {
                showUpdateDialog(v.getContext(), event, position);
            });

            holder.viewAttendee.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), viewAttendee.class); // Address
                intent.putExtra("EVENT_ID", event.getId()); // The letter inside
                intent.putExtra("type", "waiting");
                v.getContext().startActivity(intent); // Send it!
                //finish();
            });

            holder.viewAttendeeCancelled.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), viewAttendee.class); // Address
               // Intent intent = new Intent(v.getContext(), viewAttendeeCancelled.class); // Address
                intent.putExtra("EVENT_ID", event.getId()); // The letter inside
                intent.putExtra("type", "cancelled");
                v.getContext().startActivity(intent); // Send it!
                //finish();
            });

            holder.viewAttendeeSelected.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), viewAttendee.class);
               // Intent intent = new Intent(v.getContext(), viewAttendeeCancelled.class); // Address
                intent.putExtra("EVENT_ID", event.getId());
                intent.putExtra("type", "selected");
                v.getContext().startActivity(intent); // Send it!
                //finish();
            });



        }else {
            holder.editUrl.setVisibility(View.GONE);
        }

        if (event.getPosterUrl() != null) {
            Glide.with(holder.itemView.getContext()).load(event.getPosterUrl()).placeholder(android.R.drawable.ic_menu_gallery).error(android.R.drawable.ic_delete).into(holder.posterImageView);
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, descriptionText;
        ImageView posterImageView;

        Button editUrl, deleteEvent, signUpEvent, viewAttendee, viewAttendeeCancelled, viewAttendeeSelected;

        public EventViewHolder(@NotNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.item_event_title);
            descriptionText = itemView.findViewById(R.id.event_description);
            posterImageView = itemView.findViewById(R.id.item_event_poster);
            editUrl = itemView.findViewById(R.id.editUrl);
            deleteEvent = itemView.findViewById(R.id.deleteEvent);
            signUpEvent = itemView.findViewById(R.id.signUp_button);
            viewAttendee = itemView.findViewById(R.id.viewAttendee);
            viewAttendeeCancelled = itemView.findViewById(R.id.viewAttendeeCancelled);
            viewAttendeeSelected = itemView.findViewById(R.id.viewAttendeeSelected);
        }
    }
//Gemini , March 9th 2026 , help with updating the collection in firebase to update my url
    private void showUpdateDialog(Context context, Event event, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Poster URL");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        input.setText(event.getPosterUrl() != null ? event.getPosterUrl() : "");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newURL = input.getText().toString().trim();
            if (!newURL.isEmpty()) {
                updateEventPosterUrl(context, event, newURL, position);

            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateEventPosterUrl(Context context, Event event, String newURL, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").document(event.getId()).update("posterUrl", newURL).addOnSuccessListener(aVoid -> {
            Toast.makeText(context, "Poster Updated!" ,Toast.LENGTH_SHORT).show();
            //event.setPosterUrl(newURL);

            notifyItemChanged(position);
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        });

    }

    private void showDeleteDialog(Context context, Event event, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        builder.setTitle("Delete Event ?");

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            db.collection("events").document(event.getId()).delete().addOnSuccessListener(aVoid -> {
                Toast.makeText(context, "Event Deleted Successfully!", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener( e -> {
                Toast.makeText(context, "Error while deleting the event " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    private void showSignUpDialog(Context context, Event event, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db.collection("users").document(currentUserId).get().addOnSuccessListener(userDoc -> {
                if (userDoc.exists()) {
                    // Convert doc to Profile object to get the name/email
                    Profile currentUser = userDoc.toObject(Profile.class);

                    // 2. PREPARE the attendee data
                    Map<String, Object> attendeeData = new HashMap<>();
                    attendeeData.put("name", currentUser.getName());   // Critical for the list!
                    attendeeData.put("email", currentUser.getEmail()); // Critical for the list!
                    attendeeData.put("userId", currentUserId);
                    attendeeData.put("status", "pending");
                    attendeeData.put("timestamp", FieldValue.serverTimestamp()); // Logs exact time
                    builder.setTitle("Sign Event ?");

                    db.collection("events").document(event.getId()).collection("attendees").document(event.getCurrentUser()).set(attendeeData).addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Signed Up Successfully!", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(context, "Error while signing up to the event " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void cancelSignUp(Context context, Event event, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> attendeeData = new HashMap<>();
        attendeeData.put("userId", event.getCurrentUser());
        //attendeeData.put("name", currentUser.getName());
        attendeeData.put("status", "cancelled"); // Default status when they first join
        attendeeData.put("timestamp", FieldValue.serverTimestamp()); // Logs exact time
        builder.setTitle("Cancel Event ?");

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            db.collection("events").document(event.getId()).collection("attendees").document(event.getCurrentUser()).set(attendeeData).addOnSuccessListener(aVoid -> {
                Toast.makeText(context, "Cancelled Successfully!", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener( e -> {
                Toast.makeText(context, "Error while cancelling the event " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}