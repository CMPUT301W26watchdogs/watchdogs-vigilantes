package com.example.vigilante;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EntrantAdapter extends RecyclerView.Adapter<EntrantAdapter.ViewHolder> {

    private final List<Entrant> entrants;

    public EntrantAdapter(List<Entrant> entrants) {
        this.entrants = entrants;
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
    }

    @Override
    public int getItemCount() {
        return entrants.size();
    }

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
