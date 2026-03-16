package com.example.vigilante;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import java.io.OutputStream;

public class AddEvent extends AppCompatActivity {

    private EditText titleInput, descriptionInput, posterUrlInput, maxEntrantsField;

    private String selectedStartDate = "";
    private String selectedEndDate = "";

    private CheckBox geolocationCheck;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_event);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_add_event), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        Button publish_button = (Button) findViewById(R.id.publish_button);
        Button back_button = (Button) findViewById(R.id.back_button);
        titleInput = findViewById(R.id.event_title_input);
        descriptionInput = findViewById(R.id.event_description);
        posterUrlInput = findViewById(R.id.et_poster_url);
        TextView startDateDisplay = findViewById(R.id.startDateDisplay);
        TextView endDateDisplay = findViewById(R.id.endDateDisplay);
        geolocationCheck = findViewById(R.id.geolocation_checkbox);
        maxEntrantsField = findViewById(R.id.fieldMaxEntrants);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.pickStartDateButton).setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                selectedStartDate = day + "/" + (month + 1) + "/" + year;
                startDateDisplay.setText(selectedStartDate);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        findViewById(R.id.pickEndDateButton).setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                selectedEndDate = day + "/" + (month + 1) + "/" + year;
                endDateDisplay.setText(selectedEndDate);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        findViewById(R.id.backArrow).setOnClickListener(v -> finish());

        publish_button.setOnClickListener(view -> saveEventToFirestore());

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setupBottomNav();
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_events) {
                Intent intent = new Intent(this, AllEventsActivity.class);
                intent.putExtra("type", "all");
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomePage.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfilePage.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void saveEventToFirestore() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String imageUrl = posterUrlInput.getText().toString().trim();

        boolean geolocationCheckChecked = geolocationCheck.isChecked();

        String maxText = maxEntrantsField.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || maxText.isEmpty()) {
            Toast.makeText(AddEvent.this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUrl.isEmpty()) {
            imageUrl = "https://yourdefaultimage.com/placeholder.jpg";
        }
        int max = Integer.parseInt(maxText);

        if (selectedStartDate.isEmpty() || selectedEndDate.isEmpty()) {
            Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show();
            return;
        }

        String organizerId = mAuth.getCurrentUser().getUid();

        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("title", title);
        eventMap.put("description", description);
        eventMap.put("posterUrl", imageUrl);
        eventMap.put("organizerId", organizerId);
        eventMap.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());
        eventMap.put("registrationStart", selectedStartDate);
        eventMap.put("registrationEnd", selectedEndDate);
        eventMap.put("geolocationRequired", geolocationCheckChecked);
        eventMap.put("waitingListLimit", max);

        db.collection("events").add(eventMap).addOnSuccessListener(documentReference -> {
            String newEventId = documentReference.getId();

            Bitmap qrBitmap = generateQrCode(newEventId);

            if (qrBitmap != null) {
                saveQrCodeToGallery(qrBitmap, title);
                showQrCodeDialog(qrBitmap);
            } else {
                Toast.makeText(getApplicationContext(), "Event Created, but QR failed", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddEvent.this, ProfilePage.class));
                finish();
            }

        }).addOnFailureListener(e -> {
            Toast.makeText(AddEvent.this, "Error saving event: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private Bitmap generateQrCode(String content) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 400, 400);
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveQrCodeToGallery(Bitmap bitmap, String eventTitle) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "QR_" + eventTitle.replaceAll("\\s+", "_") + ".png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/VigilanteQR");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try {
            if (uri != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                if (outputStream != null) outputStream.close();
                Toast.makeText(this, "QR Code saved to gallery!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    private void showQrCodeDialog(Bitmap qrBitmap) {
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(qrBitmap);
        imageView.setPadding(50, 50, 50, 50);

        new AlertDialog.Builder(this)
                .setTitle("Event Created Successfully!")
                .setMessage("Here is your event QR Code. A copy has been saved to your device's photo gallery.")
                .setView(imageView)
                .setPositiveButton("Done", (dialog, which) -> {
                    startActivity(new Intent(AddEvent.this, ProfilePage.class));
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}
