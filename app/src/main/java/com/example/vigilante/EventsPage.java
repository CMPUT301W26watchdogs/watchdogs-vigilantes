package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class EventsPage extends AppCompatActivity {
    ListView eventsList;
    //LinearLayout eventLayout;
    ArrayAdapter<String> eventsAdapter;
    ArrayList<String> dataList;

    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    /*
    As a prototype, we want a ListView of all events in the firebase displayed to the user
    We want to click on an event to be shown another view with the event information
    This view will have a button to register for the event or return to the previous view
    Upon registering for an event, the users information will be added to the firebase event document
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_page);

        // Event detail fields
        // eventLayout = findViewById(R.id.event_field);

        // Event list
        eventsList = findViewById(R.id.eventListRecyclerView);
        dataList = new ArrayList<>();
        eventsAdapter = new ArrayAdapter<>(this, R.layout.item_event, dataList);
        eventsList.setAdapter(eventsAdapter);

        // Populate list of events with database entries
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        eventsRef.addSnapshotListener((value, error) -> {
            if (error != null){
                Log.e("Firestore", error.toString());
            }
            if (value != null && !value.isEmpty()){
                for (QueryDocumentSnapshot document : value){
                    // Get event details from database
                    String eventName = document.getString("title");
                    String eventDescription = document.getString("description");
                    Timestamp eventDate = document.getTimestamp("timestamp");
                    dataList.add(eventName);
                }
                eventsAdapter.notifyDataSetChanged();
            }
        });


        // Clicking on an event will take you to the event detail page
        eventsList.setOnItemClickListener((parent, view, position, id) -> {
            String selectedEvent = eventsAdapter.getItem(position);
            Intent intent = new Intent(EventsPage.this, EventDetailActivity.class);
            intent.putExtra("event", selectedEvent);
            startActivity(intent);
        });

        // Clicking the back button will take you back to the main page
        final Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(EventsPage.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
