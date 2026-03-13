// shows a Google Map with markers at the locations entrants joined the waiting list from — US 02.02.02

package com.example.vigilante;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

// implements OnMapReadyCallback so this activity receives the GoogleMap object once it's ready
public class EntrantMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrant_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // getting the event ID passed from the previous screen
        eventId = getIntent().getStringExtra("event_id");

        // finding the map fragment declared in the layout and request the GoogleMap asynchronously
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // calls onMapReady() once the map is initialised
        }

        // back button for closing this screen
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    // called by the Maps SDK once the GoogleMap is ready to use
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng edmonton = new LatLng(53.5461, -113.4938); // default centre

        if (eventId != null) {
            // querying Firestore for entrant locations from the event's waitingList subcollection
            FirebaseFirestore.getInstance()
                    .collection("events").document(eventId).collection("waitingList")
                    .get()
                    .addOnSuccessListener(snapshots -> {
                        boolean hasMarkers = false;
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            Double lat = doc.getDouble("latitude");
                            Double lng = doc.getDouble("longitude");
                            String name = doc.getString("name") != null ? doc.getString("name") : "Entrant";
                            if (lat != null && lng != null) {
                                // placing a marker for each entrant who has location data
                                map.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(name));
                                hasMarkers = true;
                            }
                        }
                        if (!hasMarkers) {
                            // no entrants with location data then fallback would be to show placeholder markers
                            addPlaceholderMarkers();
                        }
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(edmonton, 12f));
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Could not load entrant locations", Toast.LENGTH_SHORT).show();
                        addPlaceholderMarkers();
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(edmonton, 12f));
                    });
        } else {
            // no event ID then show placeholder markers
            addPlaceholderMarkers();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(edmonton, 12f));
        }
    }

    // fallback placeholder markers for when Firestore data is not available
    private void addPlaceholderMarkers() {
        map.addMarker(new MarkerOptions().position(new LatLng(53.5461, -113.4938)).title("Alice Johnson"));
        map.addMarker(new MarkerOptions().position(new LatLng(53.5501, -113.4800)).title("Bob Smith"));
        map.addMarker(new MarkerOptions().position(new LatLng(53.5420, -113.5050)).title("Carol Davis"));
        map.addMarker(new MarkerOptions().position(new LatLng(53.5530, -113.4700)).title("Dan Lee"));
        map.addMarker(new MarkerOptions().position(new LatLng(53.5390, -113.5100)).title("Eva Brown"));
    }
}
