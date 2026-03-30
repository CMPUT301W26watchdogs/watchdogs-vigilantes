// main hub after login with QR code scanner to join events, quick access to events list and profile US 01.06.01

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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import com.google.firebase.auth.FirebaseAuth;

/**
* This class shows the home page to our user, it has the option to scan qr
* goto events or profile page.
 */
public class HomePage extends AppCompatActivity {

    // storing the most recently created event ID so organizer screens can use it
    private String lastEventId = null;

    /*
     * QR code scanning function referenced from zxing-android-embedded ScanContract with ActivityResultLauncher, sources:
     * https://github.com/journeyapps/zxing-android-embedded
     * https://github.com/journeyapps/zxing-android-embedded/blob/master/sample/src/main/java/example/zxing/MainActivity.java
     * https://github.com/journeyapps/zxing-android-embedded/releases/tag/v4.3.0
     */
    // registering a launcher that opens the camera scanner and receives its result
    private final ActivityResultLauncher<ScanOptions> scanLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) { // scan succeeded and the contents is the decoded string
                    Intent intent = new Intent(this, EventDetailActivity.class);
                    intent.putExtra("event_id", result.getContents()); // passing decoded QR string as event ID
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show(); // user cancelled scan
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

        // quick access cards, tapping the events or profile cards also navigates
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
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE); // only accepting QR codes, not barcodes
            options.setPrompt("Scan an event QR code"); // text shown on the scanner overlay
            options.setBeepEnabled(true);
            options.setOrientationLocked(true); // keep scanner in portrait
            scanLauncher.launch(options); // open the camera scanner
        });

        setupBottomNav();
    }

    private void setupBottomNav() {
        LiquidGlassNavBar navBar = findViewById(R.id.bottomNav);
        navBar.setSelectedTab(1);
        navBar.setOnTabSelectedListener(position -> {
            if (position == 0) {
                Intent intent = new Intent(this, AllEventsActivity.class);
                intent.putExtra("type", "all");
                startActivity(intent);
            } else if (position == 2) {
                startActivity(new Intent(this, NotificationsActivity.class));
            } else if (position == 3) {
                startActivity(new Intent(this, ProfilePage.class));
            }
        });
    }
}
