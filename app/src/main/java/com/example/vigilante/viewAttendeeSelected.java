package com.example.vigilante;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class viewAttendeeSelected extends AppCompatActivity {

    private RecyclerView recyclerView;

    private ProfileAdapter attendeeAdapter;
    private List<Profile> attendeeList;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_list);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.attendees_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendeeList = new ArrayList<>();
        Button back_button = (Button) findViewById(R.id.back_button);
        attendeeAdapter = new ProfileAdapter(attendeeList);
        recyclerView.setAdapter(attendeeAdapter);
        String eventId = getIntent().getStringExtra("EVENT_ID");

        db.collection("events").document(eventId).collection("attendees").whereEqualTo("status", "selected").get().addOnSuccessListener(queryDocumentSnapshots -> {
            attendeeList.clear();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Profile attendee = document.toObject(Profile.class);
                attendeeList.add(attendee);

            }
            attendeeAdapter.notifyDataSetChanged();
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
