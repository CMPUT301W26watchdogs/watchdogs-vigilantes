// RecyclerView adapter that binds Entrant objects to rows in the waiting list and selected entrants screens

package com.example.vigilante;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// RecyclerView adapter — binds a list of Entrant objects to the waiting list RecyclerView
public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.ViewHolder> {

    private final List<Entrant> entrants; // the data set this adapter displays

    public EntrantAdapter(List<Entrant> entrants) {
        this.entrants = entrants;
    }

    // called by RecyclerView when it needs a new row view — inflates the item layout and wraps it in a ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entrant, parent, false); // inflate the single-row layout
        return new ViewHolder(view);
    }

    // called by RecyclerView to fill a row with data at the given position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Entrant entrant = entrants.get(position); // get the entrant for this row
        holder.nameText.setText(entrant.getName());
        holder.emailText.setText(entrant.getEmail());
        holder.statusText.setText(entrant.getStatus()); // e.g. "Waiting", "Selected"
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

        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.entrantName);
            emailText = itemView.findViewById(R.id.entrantEmail);
            statusText = itemView.findViewById(R.id.entrantStatus);
        }
    }
}
