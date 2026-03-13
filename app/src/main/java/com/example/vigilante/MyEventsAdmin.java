// admin view of all events in the system — uses EventAdapter with admin delete permissions — US 03.04.01

package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyEventsAdmin extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;

    private List<Event> eventList;

    private FirebaseFirestore db;

    private FirebaseAuth mAuth;

    private ImageView imageTv;

    private  String organizerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allevents);

        Button back_button = (Button) findViewById(R.id.back_button);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.all_events_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, false, true, false);
        recyclerView.setAdapter(eventAdapter);

        fetchMyEvents();

        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MyEventsAdmin.this, AdminPage.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchMyEvents() {
        organizerId = mAuth.getCurrentUser().getUid();
        db.collection("events").get().addOnSuccessListener(queryDocumentSnapshots -> {
            eventList.clear();

            for(QueryDocumentSnapshot document :queryDocumentSnapshots) {
                Event event = document.toObject(Event.class);
                event.setId(document.getId());
                eventList.add(event);

            }
            eventAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading events:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

}
