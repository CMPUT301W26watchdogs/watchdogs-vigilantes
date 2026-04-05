package com.example.vigilante;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditPosterActivity extends AppCompatActivity {

    private String eventId;
    private Uri selectedImageUri;
    private ImageView posterPreview;

    // The Launcher to open the Gallery
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    posterPreview.setImageURI(selectedImageUri); // Show a preview!
                }
            }
    );

    /**
     * setting up the poster editing screen with gallery picker and upload confirmation button
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_poster); // Make sure to create this XML layout

        // 1. Get the Event ID passed from the Adapter
        eventId = getIntent().getStringExtra("EVENT_ID");

        Button btnChooseImage = findViewById(R.id.btn_choose_image);
        Button btnConfirmUpload = findViewById(R.id.btn_confirm_upload);
        posterPreview = findViewById(R.id.poster_preview_image);

        // 2. Open Gallery Button
        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            galleryLauncher.launch(intent);
        });

        // 3. Confirm & Upload Button
        btnConfirmUpload.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                uploadToFirebase();
            } else {
                Toast.makeText(this, "Please choose an image first!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * uploading the selected image to Firebase Storage and updating
     * the event's posterUrl field in Firestore with the download link
     */
    private void uploadToFirebase() {
        Toast.makeText(this, "Uploading...", Toast.LENGTH_LONG).show();

        StorageReference fileRef = FirebaseStorage.getInstance()
                .getReference("event_posters")
                .child(System.currentTimeMillis() + ".jpg");

        fileRef.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                // 4. Save the new URL to this specific Event in Firestore
                FirebaseFirestore.getInstance().collection("events").document(eventId)
                        .update("posterUrl", uri.toString())
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
                            finish(); // Closes this screen and goes back to the list!
                        });
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Upload failed.", Toast.LENGTH_SHORT).show();
        });
    }
}