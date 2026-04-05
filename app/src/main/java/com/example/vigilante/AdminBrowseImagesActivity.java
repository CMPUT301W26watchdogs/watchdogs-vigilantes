package com.example.vigilante;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminBrowseImagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<Event> imageEventList;
    private TextView emptyText;

    /**
     * setting up the admin image browser with a grid RecyclerView
     * showing all event poster images from Firestore
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_browse_images);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_browse_images_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        findViewById(R.id.backArrow).setOnClickListener(v -> finish());

        emptyText = findViewById(R.id.emptyText);
        recyclerView = findViewById(R.id.images_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        imageEventList = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageEventList);
        recyclerView.setAdapter(imageAdapter);

        fetchImages();
    }

    /**
     * querying all events from Firestore and collecting only those
     * with a non empty poster url to display in the image grid
     */
    private void fetchImages() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").get().addOnSuccessListener(queryDocumentSnapshots -> {
            imageEventList.clear();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Event event = document.toObject(Event.class);
                event.setId(document.getId());
                if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
                    imageEventList.add(event);
                }
            }
            imageAdapter.notifyDataSetChanged();

            if (imageEventList.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load images: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
