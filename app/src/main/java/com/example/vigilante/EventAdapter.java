package com.example.vigilante;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
//Gemini March 8th 2026, Help view a list of events from firebase
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;

    public EventAdapter(List<Event> eventList){
        this.eventList = eventList;
    }

    @NotNull
    @Override
    public EventViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.titleText.setText(event.getTitle());
        holder.descriptionText.setText(event.getDescription());
    }

    @Override
    public int getItemCount(){
        return eventList.size();
    }
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, descriptionText;

        public EventViewHolder(@NotNull View itemView) {
            super(itemView);
            titleText  = itemView.findViewById(R.id.item_event_title);
            descriptionText = itemView.findViewById(R.id.event_description);


        }
    }
}
