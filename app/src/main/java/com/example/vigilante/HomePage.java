package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity {

    private String lastEventId = null;

    private final ActivityResultLauncher<ScanOptions> scanLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    Intent intent = new Intent(this, EventDetailActivity.class);
                    intent.putExtra("event_id", result.getContents());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.homepage);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homepage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        findViewById(R.id.eventsCard).setOnClickListener(v -> {
            Intent intent = new Intent(this, AllEventsActivity.class);
            intent.putExtra("type", "all");
            startActivity(intent);
        });

        findViewById(R.id.profileCard).setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationsActivity.class));
        });

        findViewById(R.id.scanQrButton).setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            options.setPrompt("Scan an event QR code");
            options.setBeepEnabled(true);
            options.setOrientationLocked(true);
            scanLauncher.launch(options);
        });

        setupBottomNav();
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setItemIconTintList(null);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_events) {
                Intent intent = new Intent(this, AllEventsActivity.class);
                intent.putExtra("type", "all");
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_alerts) {
                startActivity(new Intent(this, NotificationsActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfilePage.class));
                return true;
            }
            return false;
        });
    }
}
