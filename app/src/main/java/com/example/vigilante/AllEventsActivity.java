package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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

        recyclerView = findViewById(R.id.all_events_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();

        String type = getIntent().getStringExtra("type");
        if (type.equals("all")) {
            eventAdapter = new EventAdapter(eventList, false, false, true);
            recyclerView.setAdapter(eventAdapter);
            fetchAllEvents();
        } else if (type.equals("myactivityorg")) {
            eventAdapter = new EventAdapter(eventList, true, false, false);
            recyclerView.setAdapter(eventAdapter);
            fetchMyOrgEvents();
        } else if (type.equals("admin")) {
            eventAdapter = new EventAdapter(eventList, false, true, false);
            recyclerView.setAdapter(eventAdapter);
            fetchAdminEvents();
        }

        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        setupBottomNav();
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_events);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_events) {
                return true;
            } else if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomePage.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfilePage.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void fetchAllEvents() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        db.collection("events").orderBy("timestamp", Query.Direction.DESCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
            eventList.clear();

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
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

    private void fetchMyOrgEvents() {
        FirebaseUser organizerId = mAuth.getCurrentUser();
        db.collection("events").whereEqualTo("organizerId", organizerId.getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            eventList.clear();

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Event event = document.toObject(Event.class);
                event.setId(document.getId());
                eventList.add(event);
            }
            eventAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading events:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchAdminEvents() {
        db.collection("events").get().addOnSuccessListener(queryDocumentSnapshots -> {
            eventList.clear();

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
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
