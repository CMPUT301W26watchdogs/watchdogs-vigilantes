// US 1.6.1 - Scan QR button only here, no nav here. Bilateral with EventDetailActivity- displays event info + "Join Waiting List" button after scan

package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

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
        EdgeToEdge.enable(this); // drawing content behind status/nav bars
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom); // avoiding overlap with system bars
            return insets;
        });

        // loading the most recent event from Firestore so organizer buttons have an event to work with
        loadLastEvent();

        findViewById(R.id.scanQrButton).setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE); // only accepting QR codes, not barcodes
            options.setPrompt("Scan an event QR code"); // text shown on the scanner overlay
            options.setBeepEnabled(false); // no sound on successful scan
            options.setOrientationLocked(true); // keep scanner in portrait
            scanLauncher.launch(options); // open the camera scanner
        });

        // opening event creation form
        findViewById(R.id.createEventButton).setOnClickListener(v ->
                startActivity(new Intent(this, CreateEventActivity.class)));

        // opening registration period date picker — passes the last event ID
        findViewById(R.id.setRegistrationButton).setOnClickListener(v ->
                startActivityWithEventId(RegistrationPeriodActivity.class));

        // opening waiting list — passes the last event ID
        findViewById(R.id.viewWaitingListButton).setOnClickListener(v ->
                startActivityWithEventId(WaitingListActivity.class));

        // opening waiting list limit settings — passes the last event ID
        findViewById(R.id.waitingListLimitButton).setOnClickListener(v ->
                startActivityWithEventId(WaitingListLimitActivity.class));

        // opening geolocation requirement toggle — passes the last event ID
        findViewById(R.id.geolocationToggleButton).setOnClickListener(v ->
                startActivityWithEventId(GeolocationToggleActivity.class));

        // opening the map showing where entrants are located — passes the last event ID
        findViewById(R.id.entrantMapButton).setOnClickListener(v ->
                startActivityWithEventId(EntrantMapActivity.class));
    }

    // loading the most recently created event from Firestore to use as default for organizer screens
    private void loadLastEvent() {
        FirebaseFirestore.getInstance().collection("events")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (!snapshots.isEmpty()) {
                        DocumentSnapshot doc = snapshots.getDocuments().get(0);
                        lastEventId = doc.getId();
                    }
                });
    }

    // helper: starts an activity and passes the last event ID via intent
    private void startActivityWithEventId(Class<?> activityClass) {
        if (lastEventId == null) {
            Toast.makeText(this, "No event found — create one first", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("event_id", lastEventId);
        startActivity(intent);
    }
}
