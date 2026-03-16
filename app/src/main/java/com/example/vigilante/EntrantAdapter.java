// RecyclerView adapter that binds Entrant objects to rows in the waiting list and selected entrants screens

package com.example.vigilante;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.ViewHolder> {

    private final List<Entrant> entrants;
    private final String eventId;

    public EntrantAdapter(List<Entrant> entrants) {
        this.entrants = entrants;
        this.eventId = null;
    }

    public EntrantAdapter(List<Entrant> entrants, String eventId) {
        this.entrants = entrants;
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entrant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Entrant entrant = entrants.get(position);
        holder.nameText.setText(entrant.getName());
        holder.emailText.setText(entrant.getEmail());
        holder.statusText.setText(entrant.getStatus());

        if (eventId != null && !"cancelled".equals(entrant.getStatus())) {
            holder.cancelButton.setVisibility(View.VISIBLE);
            holder.cancelButton.setOnClickListener(v -> {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Cancel Entrant")
                        .setMessage("Cancel " + entrant.getName() + " from this event?")
                        .setPositiveButton("Confirm", (dialog, which) -> {
                            FirebaseFirestore.getInstance()
                                    .collection("events").document(eventId)
                                    .collection("attendees").document(entrant.getId())
                                    .update("status", "cancelled")
                                    .addOnSuccessListener(aVoid -> {
                                        entrant.setStatus("cancelled");
                                        holder.statusText.setText("cancelled");
                                        holder.cancelButton.setVisibility(View.GONE);
                                        Toast.makeText(v.getContext(), entrant.getName() + " cancelled", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(v.getContext(), "Failed to cancel: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .setNegativeButton("Back", (dialog, which) -> dialog.cancel())
                        .show();
            });
        } else {
            holder.cancelButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return entrants.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameText;
        final TextView emailText;
        final TextView statusText;
        final MaterialButton cancelButton;

        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.entrantName);
            emailText = itemView.findViewById(R.id.entrantEmail);
            statusText = itemView.findViewById(R.id.entrantStatus);
            cancelButton = itemView.findViewById(R.id.cancelEntrantButton);
        }
    }
}
