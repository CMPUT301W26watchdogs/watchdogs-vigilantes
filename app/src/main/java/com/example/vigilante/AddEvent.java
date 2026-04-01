// organizer event creation form collecting title, description, dates, poster url, geolocation, category and max entrants then writing to Firestore US 02.01.01, US 02.01.04, US 02.02.03, US 02.03.01, US 01.01.04

package com.example.vigilante;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

/**
 * This class helps an organizer to add new events
 */
public class AddEvent extends AppCompatActivity {

    private EditText titleInput, descriptionInput, posterUrlInput, maxEntrantsField;
    private AutoCompleteTextView categoryInput;

    private String selectedStartDate = "";
    private String selectedEndDate = "";

    private CheckBox geolocationCheck;

    private CheckBox privateEventCheck;

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
        // text views that display the selected dates below each button
        TextView startDateDisplay = findViewById(R.id.startDateDisplay);
        TextView endDateDisplay = findViewById(R.id.endDateDisplay);
        geolocationCheck = findViewById(R.id.geolocation_checkbox);
        privateEventCheck = findViewById(R.id.private_event_checkbox);
        maxEntrantsField = findViewById(R.id.fieldMaxEntrants); // the number input itself
        categoryInput = findViewById(R.id.event_category);
        String[] categories = {"Sports", "Arts", "Music", "Education", "Technology", "Social", "Other"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        categoryInput.setAdapter(categoryAdapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        /* DatePickerDialog used to let the organizer select registration open/close dates
         * https://developer.android.com/reference/android/app/DatePickerDialog */
        findViewById(R.id.pickStartDateButton).setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                // month is 0-indexed so added 1 for display
                selectedStartDate = day + "/" + (month + 1) + "/" + year;
                // show the chosen date on screen
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
        LiquidGlassNavBar navBar = findViewById(R.id.bottomNav);
        boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
        navBar.setOnTabSelectedListener(position -> {
            if (position == 0) {
                Intent intent = new Intent(this, AllEventsActivity.class);
                intent.putExtra("type", "all");
                intent.putExtra("IS_ADMIN", isAdmin);
                startActivity(intent);
                finish();
            } else if (position == 1) {
                if(isAdmin){
                    Intent intent = new Intent(this, AdminPage.class);
                    intent.putExtra("IS_ADMIN", isAdmin);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(this, HomePage.class);
                    intent.putExtra("IS_ADMIN", isAdmin);
                    startActivity(intent);
                }
                finish();
            } else if (position == 2) {
                Intent intent = new Intent(this, NotificationsActivity.class);
                intent.putExtra("IS_ADMIN", isAdmin);
                startActivity(intent);
                finish();
            } else if (position == 3) {
                Intent intent = new Intent(this, ProfilePage.class);
                intent.putExtra("IS_ADMIN", isAdmin);
                startActivity(intent);
                finish();
            }
        });
    }

    //Gemini March 7th 2026, how do i add my event to firebase database
    /*
     * This helper functions allows us to save the event information in firebase
     */
    private void saveEventToFirestore() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String imageUrl = posterUrlInput.getText().toString().trim();

        boolean geolocationCheckChecked = geolocationCheck.isChecked();
        boolean isPrivateEvent = privateEventCheck.isChecked();

        String maxText = maxEntrantsField.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || maxText.isEmpty()) {
            Toast.makeText(AddEvent.this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUrl.isEmpty()) {
            imageUrl = "https://yourdefaultimage.com/placeholder.jpg";
        }
        int max = Integer.parseInt(maxText);

        // both dates must be selected before saving
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
        eventMap.put("isPrivate", isPrivateEvent);
        eventMap.put("waitingListLimit", max);

        // saving the event category so entrants can filter by type US 01.01.04
        String category = categoryInput.getText().toString().trim();
        if (!category.isEmpty()) {
            eventMap.put("category", category);
        }

        db.collection("events").add(eventMap).addOnSuccessListener(documentReference -> {
            String newEventId = documentReference.getId();

            Bitmap qrBitmap = generateQrCode(newEventId);


            if (qrBitmap != null) {
                if(isPrivateEvent){
                    Toast.makeText(getApplicationContext(), "Event Created, No QR for Private Event", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    saveQrCodeToGallery(qrBitmap, title);
                    showQrCodeDialog(qrBitmap);

                }
            } else {
                Toast.makeText(getApplicationContext(), "Event Created, but QR failed", Toast.LENGTH_SHORT).show();
                boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
                Intent intent = new Intent(AddEvent.this, ProfilePage.class);
                intent.putExtra("IS_ADMIN", isAdmin);
                startActivity(intent);

                finish();
            }

        }).addOnFailureListener(e -> {
            Toast.makeText(AddEvent.this, "Error saving event: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    //Gemini march 13th 2026, help generate qr for the event published.
    /*
     * This helper function is called after saveEventToFirestore is completed and it generates a qr code, saves it the
     * organizers devices and shows it to organizer as well.
     */
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

    //Gemini march 13th 2026, help generate qr for the event published and store it to device.
    /*
     * This event is called by generateQrCode which helps us to store the qrcode in the device.
     */
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

    /*
     * This helper function shows the generated qr code once the event has been created.
     */
    private void showQrCodeDialog(Bitmap qrBitmap) {
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(qrBitmap);
        imageView.setPadding(50, 50, 50, 50);

        new AlertDialog.Builder(this)
                .setTitle("Event Created Successfully!")
                .setMessage("Here is your event QR Code. A copy has been saved to your device's photo gallery.")
                .setView(imageView)
                .setPositiveButton("Done", (dialog, which) -> {
                    boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
                    Intent intent = new Intent(AddEvent.this, ProfilePage.class);
                    intent.putExtra("IS_ADMIN", isAdmin);
                    startActivity(intent);

                    finish();
                })
                .setCancelable(false)
                .show();
    }
}
