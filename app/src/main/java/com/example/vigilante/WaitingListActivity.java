package com.example.vigilante;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WaitingListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_waiting_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String eventId = getIntent().getStringExtra("event_id");

        TextView eventLabel = findViewById(R.id.waitingListEventLabel);
        eventLabel.setText("Event: " + (eventId != null ? eventId : "Unknown"));

        List<Entrant> entrants = getPlaceholderEntrants();

        TextView countText = findViewById(R.id.entrantCount);
        countText.setText(entrants.size() + " entrants on waiting list");

        RecyclerView recyclerView = findViewById(R.id.waitingListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new EntrantAdapter(entrants));

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private List<Entrant> getPlaceholderEntrants() {
        List<Entrant> list = new ArrayList<>();
        list.add(new Entrant("1", "Alice Johnson", "alice@email.com", "780-111-2222", "Waiting"));
        list.add(new Entrant("2", "Bob Smith", "bob@email.com", "780-333-4444", "Waiting"));
        list.add(new Entrant("3", "Carol White", "carol@email.com", "780-555-6666", "Waiting"));
        list.add(new Entrant("4", "David Brown", "david@email.com", "780-777-8888", "Waiting"));
        list.add(new Entrant("5", "Emma Davis", "emma@email.com", "780-999-0000", "Waiting"));
        return list;
    }
}
