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

    @Override
    public void onBindViewHolder(@NotNull HistoryViewHolder holder, int position) {
        Map<String, String> entry = historyList.get(position);
        holder.titleText.setText(entry.get("title"));
        holder.dateText.setText(entry.get("date"));

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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
            intent.putExtra("event_id", entry.get("eventId"));
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

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
