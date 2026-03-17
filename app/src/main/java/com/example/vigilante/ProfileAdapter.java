// RecyclerView adapter for profile cards — shows name and a delete button for admin to remove profiles — US 03.02.01

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
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
*This class is the engine function for the profiles, it grabs the profiles from firebase and views it to the user
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private List<Profile> profileList;

    public ProfileAdapter(List<Profile> profileList) {
        this.profileList = profileList;

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
        holder.deleteProfile.setVisibility(View.VISIBLE);
        holder.deleteProfile.setOnClickListener(v -> {
            showDeleteDialog(v.getContext(), profile, position);
        });
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
        Button deleteProfile;

        public ProfileViewHolder(@NotNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.item_profile_title);
            deleteProfile = itemView.findViewById(R.id.deleteProfile);
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
}
