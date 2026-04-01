// RecyclerView adapter for admin image grid showing event posters with remove functionality US 03.06.01, US 03.03.01

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

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Adapter that displays event poster images in a grid and allows admin to remove them.
 */
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

    private void showRemoveDialog(Context context, Event event, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Remove Image?");
        builder.setMessage("Remove poster image from \"" + event.getTitle() + "\"?");

        builder.setPositiveButton("Remove", (dialog, which) -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("events").document(event.getId())
                    .update("posterUrl", FieldValue.delete())
                    .addOnSuccessListener(aVoid -> {
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
}
