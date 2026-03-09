package com.example.vigilante;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddEvent extends AppCompatActivity {

    private EditText titleInput , descriptionInput, posterUrlInput;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_event);

        Button publish_button = (Button) findViewById(R.id.publish_button);
        Button back_button = (Button) findViewById(R.id.back_button);
        titleInput = (EditText) findViewById(R.id.event_title_input);
        descriptionInput = (EditText) findViewById(R.id.event_description);
        posterUrlInput = (EditText) findViewById(R.id.et_poster_url);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_add_event), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        publish_button.setOnClickListener(view -> saveEventToFirestore());

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddEvent.this, ProfilePage.class));
                finish();
            }
        });
    }
//Gemini March 7th 2026, how do i add my event to firebase database
    private void saveEventToFirestore(){
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String imageUrl = posterUrlInput.getText().toString().trim();
        if(title.isEmpty() || description.isEmpty()){
            Toast.makeText(AddEvent.this, "Please fill out all required fields",Toast.LENGTH_SHORT).show();
            return;
        }

        if(imageUrl.isEmpty()) {
            imageUrl = "https://yourdefaultimage.com/placeholder.jpg";
        }

        String organizerId = mAuth.getCurrentUser().getUid();

        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("title", title);
        eventMap.put("description", description);
        eventMap.put("posterUrl", imageUrl);
        eventMap.put("organizerId" ,organizerId);
        eventMap.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

        db.collection("events").add(eventMap).addOnSuccessListener(documentReference -> {
            Toast.makeText(getApplicationContext(), "Event Created Successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddEvent.this, ProfilePage.class));
            finish();
        }).addOnFailureListener(e-> {
            Toast.makeText(AddEvent.this, "Error saving event" + e.getMessage(), Toast.LENGTH_LONG).show();

        });

    }
}
