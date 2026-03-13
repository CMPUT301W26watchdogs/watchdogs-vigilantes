package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllEventsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;

    private List<Event> eventList;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allevents);
        Button back_button = (Button) findViewById(R.id.back_button);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    //Gemini March 8th 2026 , help add event list from firebase
        recyclerView = findViewById(R.id.all_events_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();
        //eventAdapter = new EventAdapter(eventList);
        eventAdapter = new EventAdapter(eventList, false, false, true);
        recyclerView.setAdapter(eventAdapter);

        fetchAllEvents();

        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AllEventsActivity.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void fetchAllEvents() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        db.collection("events").orderBy("timestamp", Query.Direction.DESCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
            eventList.clear();

            for(QueryDocumentSnapshot document :queryDocumentSnapshots) {
                Event event = document.toObject(Event.class);
                event.setId(document.getId());
                event.setcurrentUser(currentUser.getUid());
                eventList.add(event);
            }
            eventAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading events:" + e.getMessage(), Toast.LENGTH_SHORT).show();

        });
    }
}
