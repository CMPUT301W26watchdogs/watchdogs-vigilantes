// RecyclerView adapter for profile cards showing name and a delete button for admin to remove profiles US 03.02.01

package com.example.vigilante;

import static androidx.core.app.ActivityCompat.recreate;

import android.content.Context;
import android.text.InputType;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
*This class is the engine function for the profiles, it grabs the profiles from firebase and views it to the user
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private List<Profile> profileList;

    private boolean isInviteMode;
    private String eventId;

    public ProfileAdapter(List<Profile> profileList, boolean isInviteMode, String eventId) {
        this.profileList = profileList;
        this.isInviteMode = isInviteMode;
        this.eventId = eventId;

    }

    @NotNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ProfileViewHolder holder, int position) {
        Profile profile = profileList.get(position);
        holder.nameText.setText(profile.getName());
        if (isInviteMode) {
            holder.deleteProfile.setVisibility(View.GONE);
            holder.inviteProfile.setVisibility(View.VISIBLE);
            holder.inviteCoorg.setVisibility(View.VISIBLE);
        } else {
            holder.deleteProfile.setVisibility(View.VISIBLE);
            holder.inviteProfile.setVisibility(View.GONE);
            holder.inviteCoorg.setVisibility(View.GONE);
        }
        holder.deleteProfile.setOnClickListener(v -> {
            showDeleteDialog(v.getContext(), profile, position);
        });


        holder.inviteProfile.setOnClickListener(v -> {inviteSingleUser(v.getContext(), profile);});

        holder.inviteCoorg.setOnClickListener(v -> {inviteCoOrganizer(v.getContext(), profile);});

    }

    /**
* This function returns the number for profiles in firebase
 */
    @Override
    public int getItemCount() {

        return profileList.size();
    }
    /**
* This function holds the profiles in the recylerview and shows it to the user.
 */
    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, emailText;
        ImageView posterImageView;
        Button deleteProfile, inviteProfile, inviteCoorg;

        public ProfileViewHolder(@NotNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.item_profile_title);
            deleteProfile = itemView.findViewById(R.id.deleteProfile);
            inviteProfile = itemView.findViewById(R.id.inviteProfile);
            inviteCoorg = itemView.findViewById(R.id.inviteCoorg);
        }
    }

    /**
* This helper function allows the admin to delete user profiles.
 */
    private void showDeleteDialog(Context context, Profile profile, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        builder.setTitle("Delete Profile ?");

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            db.collection("users").document(profile.getId()).delete().addOnSuccessListener(aVoid -> {
                Toast.makeText(context, "Profile Deleted Successfully!", Toast.LENGTH_SHORT).show();
               notifyItemChanged(position);
            }).addOnFailureListener( e -> {
                Toast.makeText(context, "Error while deleting the profile " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
//Gemini, April 2nd 2026 , how do i send notification to single user
    // 1. Adds the specific user to the event, then triggers their notification
    private void inviteSingleUser(Context context, Profile profile) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(context, "Error: Missing Event ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add user to the event's attendee list as 'selected'
        Map<String, Object> attendeeData = new HashMap<>();
        attendeeData.put("userId", profile.getId());
        attendeeData.put("name", profile.getName());
        attendeeData.put("email", profile.getEmail());
        attendeeData.put("status", "selected");
        attendeeData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("events").document(eventId)
                .collection("attendees").document(profile.getId())
                .set(attendeeData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, profile.getName() + " invited!", Toast.LENGTH_SHORT).show();
                    // Now that they are in the database, send them a notification
                    sendDirectInvitation(profile);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to invite: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // 2. Checks preferences and prepares the notification text
    private void sendDirectInvitation(Profile profile) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the event title
        db.collection("events").document(eventId).get().addOnSuccessListener(eventDoc -> {
            String eventTitle = eventDoc.getString("title");

            // Check if this specific user allows notifications
            db.collection("users").document(profile.getId()).get().addOnSuccessListener(userDoc -> {
                Boolean notificationsEnabled = userDoc.getBoolean("notificationsEnabled");

                // If they opted out, stop here
                if (Boolean.FALSE.equals(notificationsEnabled)) return;

                // Otherwise, send it!
                sendNotification(profile.getId(), eventId, "You've been invited!",
                        "You've been chosen for " + (eventTitle != null ? eventTitle : "an event") + ". Open the event to accept or decline your invitation.");
            });
        });
    }

    // 3. The exact same helper you already had to write to the notifications collection
    private void sendNotification(String userId, String eventId, String title, String message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", userId);
        notification.put("eventId", eventId);
        notification.put("title", title);
        notification.put("message", message);
        notification.put("timestamp", FieldValue.serverTimestamp());
        notification.put("read", false);

        db.collection("notifications").add(notification);
    }
    //Gemini April 2nd 2026, help send an notification for coorganizer invitation.
    private void inviteCoOrganizer(Context context, Profile profile) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 1. Add them to the attendees list with an 'invited_coorg' status
        Map<String, Object> coorgData = new HashMap<>();
        coorgData.put("userId", profile.getId());
        coorgData.put("name", profile.getName());
        coorgData.put("email", profile.getEmail());
        coorgData.put("status", "invited_coorg"); // SPECIAL STATUS
        coorgData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("events").document(eventId)
                .collection("attendees").document(profile.getId())
                .set(coorgData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, profile.getName() + " invited as Co-Organizer!", Toast.LENGTH_SHORT).show();

                    // 2. Send the notification (satisfies User Story #2)
                    sendNotification(profile.getId(), eventId, "Co-Organizer Invitation",
                            "You have been invited to help organize an event!");
                });
    }

}
