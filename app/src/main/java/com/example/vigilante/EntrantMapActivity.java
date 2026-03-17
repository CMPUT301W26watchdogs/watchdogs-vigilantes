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
        // default centre
        LatLng edmonton = new LatLng(53.5461, -113.4938);

        if (eventId != null) {
            // querying Firestore for entrant locations from the event's attendees subcollection
            FirebaseFirestore.getInstance()
                    .collection("events").document(eventId).collection("attendees")
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
                        // no entrants with location data then fallback would be to show placeholder markers
                        if (!hasMarkers) {
                            Toast.makeText(this, "No location data available for entrants", Toast.LENGTH_SHORT).show();
                        }
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(edmonton, 12f));
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Could not load entrant locations", Toast.LENGTH_SHORT).show();
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(edmonton, 12f));
                    });
        } else { // no event ID then show placeholder markers
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(edmonton, 12f));
        }
    }
}
