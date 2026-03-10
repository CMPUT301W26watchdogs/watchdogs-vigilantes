package com.example.vigilante;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
public class EventHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EventHistoryAdapter adapter;
    private ArrayList<EventHistory> eventList;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_history);
        recyclerView = findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        adapter = new EventHistoryAdapter(eventList);
        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        loadEventHistory();
    }

    private void loadEventHistory() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("registrations")
                .whereEqualTo("userEmail", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String title = doc.getString("eventTitle");
                        String status = doc.getString("status");
                        eventList.add(new EventHistory(title, status));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }
}