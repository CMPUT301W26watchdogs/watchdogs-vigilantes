package com.example.vigilante;

import android.os.Bundle;

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

public class EntrantMapActivity extends AppCompatActivity implements OnMapReadyCallback {

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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng edmonton = new LatLng(53.5461, -113.4938);

        googleMap.addMarker(new MarkerOptions().position(new LatLng(53.5461, -113.4938)).title("Alice Johnson"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(53.5501, -113.4800)).title("Bob Smith"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(53.5420, -113.5050)).title("Carol Davis"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(53.5530, -113.4700)).title("Dan Lee"));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(53.5390, -113.5100)).title("Eva Brown"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(edmonton, 12f));
    }
}
