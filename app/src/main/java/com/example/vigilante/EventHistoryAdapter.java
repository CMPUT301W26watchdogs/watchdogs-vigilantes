// RecyclerView adapter for event history cards displaying event title, date and a color coded status badge US 01.02.03

package com.example.vigilante;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * This class is the adapter for showing event history cards in a RecyclerView with color-coded status badges.
 */
public class EventHistoryAdapter extends RecyclerView.Adapter<EventHistoryAdapter.HistoryViewHolder> {

    private List<Map<String, String>> historyList;

    public EventHistoryAdapter(List<Map<String, String>> historyList) {
        this.historyList = historyList;
    }

    @NotNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_history, parent, false);
        return new HistoryViewHolder(view);
    }

    // Citation: Ved, March 11 2025, https://developer.android.com/develop/ui/views/layout/recyclerview#implement-adapter
    @Override
    public void onBindViewHolder(@NotNull HistoryViewHolder holder, int position) {
        Map<String, String> entry = historyList.get(position);
        // populating the card with event title and date
        holder.titleText.setText(entry.get("title"));
        holder.dateText.setText(entry.get("date"));

        // applying color coded badge based on the entrant's status in the event US 01.02.03
        String status = entry.get("status");
        if (status != null) {
            holder.statusText.setText(status.substring(0, 1).toUpperCase() + status.substring(1));
            switch (status) {
                case "selected":
                    holder.statusText.setBackgroundResource(R.drawable.bg_status_selected);
                    holder.statusText.setTextColor(holder.itemView.getContext().getColor(R.color.white));
                    break;
                case "accepted":
                    holder.statusText.setBackgroundResource(R.drawable.bg_status_badge);
                    holder.statusText.setTextColor(holder.itemView.getContext().getColor(R.color.white));
                    break;
                case "pending":
                    holder.statusText.setBackgroundResource(R.drawable.bg_status_waiting);
                    holder.statusText.setTextColor(holder.itemView.getContext().getColor(R.color.white));
                    break;
                case "declined":
                case "cancelled":
                    holder.statusText.setBackgroundResource(R.drawable.bg_status_closed);
                    holder.statusText.setTextColor(holder.itemView.getContext().getColor(R.color.white));
                    break;
                default:
                    holder.statusText.setBackgroundResource(R.drawable.bg_status_closed);
                    holder.statusText.setTextColor(holder.itemView.getContext().getColor(R.color.white));
                    break;
            }
        }

        // clicking a history card opens the event detail screen
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
            intent.putExtra("event_id", entry.get("eventId"));
            v.getContext().startActivity(intent);
        });
    }

    /**
     * This function returns the number of history entries in the list.
     */
    @Override
    public int getItemCount() {
        return historyList.size();
    }

    /**
     * This class is a ViewHolder that holds the event title, date and status badge views.
     */
    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, dateText, statusText;

        public HistoryViewHolder(@NotNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.historyEventTitle);
            dateText = itemView.findViewById(R.id.historyEventDate);
            statusText = itemView.findViewById(R.id.historyEventStatus);
        }
    }
}
