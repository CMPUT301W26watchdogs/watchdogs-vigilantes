package com.example.vigilante;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<Event> eventList;

    public ImageAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NotNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ImageViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.titleText.setText(event.getTitle());

        Glide.with(holder.itemView.getContext())
                .load(event.getPosterUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_delete)
                .into(holder.posterImage);

        holder.removeButton.setOnClickListener(v -> {
            showRemoveDialog(v.getContext(), event, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImage;
        TextView titleText;
        Button removeButton;

        public ImageViewHolder(@NotNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.admin_image_poster);
            titleText = itemView.findViewById(R.id.admin_image_title);
            removeButton = itemView.findViewById(R.id.admin_image_remove_button);
        }
    }

    /**
     * showing a confirmation dialog before removing the poster image
     * from both Firestore and Firebase Storage
     *
     * @param context the current context for showing the dialog
     * @param event the event whose poster is being removed
     * @param position the adapter position for updating the list after removal
     */
    private void showRemoveDialog(Context context, Event event, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Remove Image?");
        builder.setMessage("Remove poster image from \"" + event.getTitle() + "\"?");

        builder.setPositiveButton("Remove", (dialog, which) -> {
            String posterUrl = event.getPosterUrl();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("events").document(event.getId())
                    .update("posterUrl", FieldValue.delete())
                    .addOnSuccessListener(aVoid -> {
                        deleteFromStorage(posterUrl);
                        Toast.makeText(context, "Image Removed!", Toast.LENGTH_SHORT).show();
                        eventList.remove(position);
                        notifyItemRemoved(position);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * deleting the actual image file from Firebase Storage using its download url
     *
     * @param url the Firebase Storage download url of the image to delete
     */
    private void deleteFromStorage(String url) {
        if (url == null || url.isEmpty()) return;
        try {
            StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(url);
            ref.delete();
        } catch (IllegalArgumentException ignored) {
        }
    }
}
