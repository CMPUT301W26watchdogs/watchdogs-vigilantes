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

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    /*
     * QR code scanning using zxing-android-embedded ScanContract with ActivityResultLauncher.
     * https://github.com/journeyapps/zxing-android-embedded
     * https://github.com/journeyapps/zxing-android-embedded/blob/master/sample/src/main/java/example/zxing/MainActivity.java
     * https://github.com/journeyapps/zxing-android-embedded/releases/tag/v4.3.0
     */
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
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.scanQrButton).setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            options.setPrompt("Scan an event QR code");
            options.setBeepEnabled(false);
            options.setOrientationLocked(true);
            scanLauncher.launch(options);
        });

        findViewById(R.id.createEventButton).setOnClickListener(v ->
                startActivity(new Intent(this, CreateEventActivity.class)));

        findViewById(R.id.setRegistrationButton).setOnClickListener(v ->
                startActivity(new Intent(this, RegistrationPeriodActivity.class)));

        findViewById(R.id.viewWaitingListButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, WaitingListActivity.class);
            intent.putExtra("event_id", "event_a");
            startActivity(intent);
        });

        findViewById(R.id.waitingListLimitButton).setOnClickListener(v ->
                startActivity(new Intent(this, WaitingListLimitActivity.class)));

        findViewById(R.id.geolocationToggleButton).setOnClickListener(v ->
                startActivity(new Intent(this, GeolocationToggleActivity.class)));

        findViewById(R.id.entrantMapButton).setOnClickListener(v ->
                startActivity(new Intent(this, EntrantMapActivity.class)));
    }
}
