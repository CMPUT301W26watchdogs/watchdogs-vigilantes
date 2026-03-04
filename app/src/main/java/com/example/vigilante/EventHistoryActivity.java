package com.example.vigilante;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EventHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventHistoryAdapter adapter;
    private ArrayList<EventHistory> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_history);

        recyclerView = findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();
        eventList.add(new EventHistory("AI Conference", "Selected"));
        eventList.add(new EventHistory("Hackathon", "Waiting"));
        eventList.add(new EventHistory("Music Festival", "Not Selected"));

        adapter = new EventHistoryAdapter(eventList);
        recyclerView.setAdapter(adapter);
    }
}