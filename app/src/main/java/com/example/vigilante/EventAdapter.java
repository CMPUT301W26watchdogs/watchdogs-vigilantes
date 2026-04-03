// RecyclerView adapter for event cards handling sign up, cancel, edit poster and delete actions depending on user role

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Gemini, 2026-03-08, Help view a list of events from firebase
// Gemini, 2026-04-02, Organizers should be able to delete comments on their own events. Admins should be able to delete comments on any event.
/**
* This class is the engine for event class it uses event class to show the user all the events and options
 */
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
        holder.inviteButton.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
            intent.putExtra("event_id", event.getId());
            intent.putExtra("IS_ADMIN", isMyEventsPageAdmin);
            v.getContext().startActivity(intent);
        });

        if (isMyEventsPageUser) {
            holder.signUpEvent.setVisibility(View.VISIBLE);
            holder.signUpEvent.setEnabled(true);
            holder.signUpEvent.setText("Sign Up");
            holder.signUpEvent.setOnClickListener(v -> {
                showSignUpDialog(v.getContext(), event, position);
            });

            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // checking if this entrant already signed up for this event
            db.collection("events").document(event.getId())
                    .collection("attendees").document(currentUserId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String status = documentSnapshot.getString("status");

                            if ("pending".equals(status)) {
                                holder.signUpEvent.setText("Cancel SignUp");
                                holder.statusBadge.setText("JOINED");
                                holder.statusBadge.setBackgroundResource(R.drawable.bg_status_waiting);
                                holder.signUpEvent.setOnClickListener(v -> {
                                    cancelSignUp(v.getContext(), event, position);
                                });
                            } else if ("selected".equals(status)) {
                                holder.signUpEvent.setText("View Invitation");
                                holder.signUpEvent.setEnabled(true);
                                holder.statusBadge.setText("SELECTED");
                                holder.statusBadge.setBackgroundResource(R.drawable.bg_status_selected);
                                holder.signUpEvent.setOnClickListener(v -> {
                                    Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
                                    intent.putExtra("event_id", event.getId());
                                    intent.putExtra("IS_ADMIN", isMyEventsPageAdmin);
                                    v.getContext().startActivity(intent);
                                });
                            } else if ("accepted".equals(status)) {
                                holder.signUpEvent.setText("Enrolled");
                                holder.signUpEvent.setEnabled(false); // Only disable for winners!
                                holder.statusBadge.setText("ENROLLED");
                                holder.statusBadge.setBackgroundResource(R.drawable.bg_status_badge);
                            } else if ("declined".equals(status)) {
                                holder.signUpEvent.setText("Sign Up");
                                holder.statusBadge.setText("DECLINED");
                                holder.statusBadge.setBackgroundResource(R.drawable.bg_status_closed);
                            }
                        }
                    });
        } else {
            holder.signUpEvent.setVisibility(View.GONE);
        }

        if (isMyEventsPageAdmin) {
            holder.deleteEvent.setVisibility(View.VISIBLE);
            holder.deleteEvent.setOnClickListener(v -> {
                showDeleteDialog(v.getContext(), event, position);
            });
        } else {
            holder.deleteEvent.setVisibility(View.GONE);
        }

        if (isMyEventsPage) {
            holder.orgButtonRow1.setVisibility(View.VISIBLE);
            holder.orgButtonRow2.setVisibility(View.VISIBLE);
            if(Boolean.TRUE.equals(event.getIsPrivate())) {
                holder.inviteButton.setVisibility(View.VISIBLE);

                holder.inviteButton.setOnClickListener(v -> {
                    Intent intent = new Intent(v.getContext(), InviteActivity.class);
                    intent.putExtra("event_id", event.getId());
                    v.getContext().startActivity(intent);
                });
            }

            holder.editUrl.setOnClickListener(v -> {
                //showUpdateDialog(v.getContext(), event, position);
                Intent intent = new Intent(v.getContext(), EditPosterActivity.class);
                intent.putExtra("EVENT_ID", event.getId());
                v.getContext().startActivity(intent);
            });

            holder.viewAttendee.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), viewAttendee.class);
                intent.putExtra("EVENT_ID", event.getId());
                intent.putExtra("type", "waiting");
                v.getContext().startActivity(intent);
            });

            holder.viewAttendeeCancelled.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), viewAttendee.class);
                intent.putExtra("EVENT_ID", event.getId());
                intent.putExtra("type", "cancelled");
                v.getContext().startActivity(intent);
            });

            holder.viewAttendeeSelected.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), viewAttendee.class);
                intent.putExtra("EVENT_ID", event.getId());
                intent.putExtra("type", "selected");
                v.getContext().startActivity(intent);
            });

            // enrolled button showing the final list of entrants who accepted US 02.06.03
            holder.viewAttendeeEnrolled.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), viewAttendee.class);
                intent.putExtra("EVENT_ID", event.getId());
                intent.putExtra("type", "enrolled");
                v.getContext().startActivity(intent);
            });
        } else {
            holder.orgButtonRow1.setVisibility(View.GONE);
            holder.orgButtonRow2.setVisibility(View.GONE);
        }

        //Gemini March 8th 2026, view image poster in the list of events
        if (event.getPosterUrl() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(event.getPosterUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_delete)
                    .into(holder.posterImageView);
        }

        FirebaseFirestore countDb = FirebaseFirestore.getInstance();
        countDb.collection("events").document(event.getId())
                .collection("attendees")
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(snapshots -> {
                    int count = snapshots.size();
                    holder.waitingCount.setText(count + " Waiting");
                    Long limit = null;
                    if (event.getCapacity() != null) {
                        try {
                            limit = Long.parseLong(event.getCapacity());
                        } catch (NumberFormatException ignored) {}
                    }
                    if (limit != null) {
                        holder.spotsCount.setText(limit + " spots");
                    }
                });

        String regStart = event.getRegistrationStart();
        String location = event.getLocation();
        StringBuilder infoBuilder = new StringBuilder();
        if (regStart != null && !regStart.isEmpty()) {
            infoBuilder.append(regStart);
        }
        if (location != null && !location.isEmpty()) {
            if (infoBuilder.length() > 0) infoBuilder.append(" - ");
            infoBuilder.append(location);
        }
        if (infoBuilder.length() > 0) {
            holder.eventLocationInfo.setText(infoBuilder.toString());
        }
    }

    /**
* This function returns the number of events
 */
    @Override
    public int getItemCount() {
        return eventList.size();
    }
    /**
* This class is a RecyclerView which holds and views our events
 */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, descriptionText, statusBadge, eventLocationInfo, waitingCount, spotsCount;
        ImageView posterImageView;
        Button editUrl, deleteEvent, signUpEvent, viewAttendee, viewAttendeeCancelled, viewAttendeeSelected, viewAttendeeEnrolled, inviteButton;
        LinearLayout orgButtonRow1, orgButtonRow2;

        public EventViewHolder(@NotNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.item_event_title);
            descriptionText = itemView.findViewById(R.id.event_description);
            posterImageView = itemView.findViewById(R.id.item_event_poster);
            statusBadge = itemView.findViewById(R.id.statusBadge);
            eventLocationInfo = itemView.findViewById(R.id.eventLocationInfo);
            waitingCount = itemView.findViewById(R.id.waitingCount);
            spotsCount = itemView.findViewById(R.id.spotsCount);
            editUrl = itemView.findViewById(R.id.editUrl);
            deleteEvent = itemView.findViewById(R.id.deleteEvent);
            signUpEvent = itemView.findViewById(R.id.signUp_button);
            viewAttendee = itemView.findViewById(R.id.viewAttendee);
            viewAttendeeCancelled = itemView.findViewById(R.id.viewAttendeeCancelled);
            viewAttendeeSelected = itemView.findViewById(R.id.viewAttendeeSelected);
            viewAttendeeEnrolled = itemView.findViewById(R.id.viewAttendeeEnrolled);
            orgButtonRow1 = itemView.findViewById(R.id.orgButtonRow1);
            orgButtonRow2 = itemView.findViewById(R.id.orgButtonRow2);
            inviteButton = itemView.findViewById(R.id.inviteButton);
        }
    }

    //Gemini , March 9th 2026 , help with updating the collection in firebase to update my url
    /**
    * This is a helper function which shows the dialog box to update the url of our poster image
     */
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

    /**
* This is the helper function which helps us update our url in firebase
 */
    private void updateEventPosterUrl(Context context, Event event, String newURL, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").document(event.getId()).update("posterUrl", newURL).addOnSuccessListener(aVoid -> {
            Toast.makeText(context, "Poster Updated!", Toast.LENGTH_SHORT).show();
            event.setPosterUrl(newURL);
            notifyItemChanged(position);
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    /**
* This function helps admin to delete events
 */
    private void showDeleteDialog(Context context, Event event, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        builder.setTitle("Delete Event?");

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            db.collection("events").document(event.getId()).delete().addOnSuccessListener(aVoid -> {
                Toast.makeText(context, "Event Deleted Successfully!", Toast.LENGTH_SHORT).show();
                notifyItemChanged(position);
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Error while deleting the event " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
* This function allows user to sign up to an event.
 */
    private void showSignUpDialog(Context context, Event event, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        builder.setTitle("Sign up for this event?");
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db.collection("users").document(currentUserId).get().addOnSuccessListener(userDoc -> {
                if (userDoc.exists()) {
                    Profile currentUser = userDoc.toObject(Profile.class);

                    // building the attendee document for this sign up
                    Map<String, Object> attendeeData = new HashMap<>();
                    attendeeData.put("name", currentUser.getName());
                    attendeeData.put("email", currentUser.getEmail());
                    attendeeData.put("userId", currentUserId);
                    attendeeData.put("status", "pending");
                    attendeeData.put("timestamp", FieldValue.serverTimestamp());

                    db.collection("events").document(event.getId()).collection("attendees").document(event.getCurrentUser()).set(attendeeData).addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Signed Up Successfully!", Toast.LENGTH_SHORT).show();
                        notifyItemChanged(position);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(context, "Error while signing up to the event " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
* This function allows user to cancel registration from an event.
 */
    private void cancelSignUp(Context context, Event event, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> attendeeData = new HashMap<>();
        attendeeData.put("userId", event.getCurrentUser());
        attendeeData.put("status", "cancelled"); // Default status when they first join
        attendeeData.put("timestamp", FieldValue.serverTimestamp()); // Logs exact time
        builder.setTitle("Cancel Event?");

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            db.collection("events").document(event.getId()).collection("attendees").document(event.getCurrentUser()).set(attendeeData).addOnSuccessListener(aVoid -> {
                Toast.makeText(context, "Cancelled Successfully!", Toast.LENGTH_SHORT).show();
                notifyItemChanged(position);
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Error while cancelling the event " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
