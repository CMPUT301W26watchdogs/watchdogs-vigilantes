// RecyclerView adapter binding Entrant objects to rows in the waiting list and selected entrants screens US 02.06.04

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

// RecyclerView adapter binding a list of Entrant objects to the waiting list RecyclerView US 02.06.04
public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.ViewHolder> {

    private final List<Entrant> entrants; // the data set this adapter displays
    private final String eventId;

    /** constructing the entrant adapter with a list of entrants and no event id */
    public EntrantAdapter(List<Entrant> entrants) {
        this.entrants = entrants;
        this.eventId = null;
    }

    /** constructing the entrant adapter with a list of entrants and the event id for cancel functionality */
    public EntrantAdapter(List<Entrant> entrants, String eventId) {
        this.entrants = entrants;
        this.eventId = eventId;
    }

    /** inflating the entrant item layout and wrapping it in a view holder */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entrant, parent, false); // inflate the single-row layout
        return new ViewHolder(view);
    }

    /** binding entrant data to the row and setting up the cancel button with a confirmation dialog */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get the entrant for this row
        Entrant entrant = entrants.get(position);
        holder.nameText.setText(entrant.getName());
        holder.emailText.setText(entrant.getEmail());
        holder.statusText.setText(entrant.getStatus()); // e.g. "Waiting", "Selected"

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
                                        int pos = entrants.indexOf(entrant);
                                        if (pos != -1) {
                                            entrants.remove(pos);
                                            notifyItemRemoved(pos);
                                        }
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

    // tells RecyclerView how many rows to draw
    @Override
    public int getItemCount() {
        return entrants.size();
    }

    // ViewHolder caches the text view references for each row so findViewById isn't called repeatedly
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
