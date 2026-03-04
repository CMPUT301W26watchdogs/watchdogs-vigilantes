package com.example.vigilante;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EventHistoryAdapter extends RecyclerView.Adapter<EventHistoryAdapter.ViewHolder>{

    private ArrayList<EventHistory> eventList;

    public EventHistoryAdapter(ArrayList<EventHistory> eventList){
        this.eventList = eventList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView status;

        public ViewHolder(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.eventTitle);
            status = itemView.findViewById(R.id.eventStatus);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_history,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){

        EventHistory event = eventList.get(position);

        holder.title.setText(event.getEventTitle());
        holder.status.setText("Status: " + event.getStatus());
    }

    @Override
    public int getItemCount(){
        return eventList.size();
    }
}