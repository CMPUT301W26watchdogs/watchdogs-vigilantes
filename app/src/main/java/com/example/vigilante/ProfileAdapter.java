package com.example.vigilante;

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
        holder.titleText.setText(profile.getTitle());
        holder.descriptionText.setText(profile.getDescription());
        holder.deleteProfile.setVisibility(View.VISIBLE);
        holder.deleteProfile.setOnClickListener(v -> {
            showDeleteDialog(v.getContext(), profile, position);
        });
    }




    @Override
    public int getItemCount() {

        return profileList.size();
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, descriptionText;
        ImageView posterImageView;
        Button deleteProfile;

        public ProfileViewHolder(@NotNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.item_profile_title);
            deleteProfile = itemView.findViewById(R.id.deleteProfile);
        }
    }


    private void showDeleteDialog(Context context, Profile profile, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        builder.setTitle("Delete Profile ?");

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            db.collection("users").document(profile.getId()).delete().addOnSuccessListener(aVoid -> {
                Toast.makeText(context, "Profile Deleted Successfully!", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener( e -> {
                Toast.makeText(context, "Error while deleting the profile " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}