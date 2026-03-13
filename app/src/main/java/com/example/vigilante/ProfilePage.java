// displays and allows editing of the current user profile — name, email, phone with Firestore update and account deletion — US 01.02.01, US 01.02.02, US 01.02.04

package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfilePage extends AppCompatActivity {

    private TextView nameTv, emailTv, phoneTv;
    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile_page);
        Button back_button = (Button) findViewById(R.id.back_button);
        Button signout_button = (Button) findViewById(R.id.signout_account_button);
        Button addEventBtn = findViewById(R.id.add_event_button);
        Button myEventsBtn = findViewById(R.id.my_event_button);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Gemini March 6th 2026 ,how do i retrieve data from firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        nameTv =findViewById(R.id.name_text);
        emailTv = findViewById(R.id.email_text);
        phoneTv = findViewById(R.id.phonenumber_text);

        if(mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
                if(documentSnapshot.exists()){
                    String name = documentSnapshot.getString("name");
                    String email = documentSnapshot.getString("email");
                    String phone = documentSnapshot.getString("phone");

                    nameTv.setText(name);
                    emailTv.setText(email);

                    Boolean isOrganizer = documentSnapshot.getBoolean("isOrganizer");

                    if(Boolean.TRUE.equals(isOrganizer)) {
                        addEventBtn.setVisibility(View.VISIBLE);
                        myEventsBtn.setVisibility(View.VISIBLE);
                    } else {
                        addEventBtn.setVisibility(View.GONE);
                        myEventsBtn.setVisibility(View.GONE);
                    }

                    if(phone != null && !phone.isEmpty()){
                        phoneTv.setText(phone);
                    } else {
                        phoneTv.setText("Not Provided");
                    }
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
            });
        }



        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfilePage.this, HomePage.class));
                finish();
            }
        });
        addEventBtn.setOnClickListener(view -> {
            startActivity(new Intent(ProfilePage.this, AddEvent.class));
            finish();
        });

        myEventsBtn.setOnClickListener(view -> {
            Intent intent = new Intent(ProfilePage.this, AllEventsActivity.class);
            //startActivity(new Intent(ProfilePage.this, MyEventsOrg.class));
            intent.putExtra("type", "myactivityorg");
            startActivity(intent);
            //finish();
        });
//Gemini March 6th 2026 , how do i update information in Firebase Database
    nameTv.setOnClickListener(v -> showUpdateDialog("Name", nameTv.getText().toString()));
    phoneTv.setOnClickListener(v -> showUpdateDialog("Phone", phoneTv.getText().toString()));

    findViewById(R.id.UpdateInfo_button).setOnClickListener(v -> {
        final String[] options = {"Name", "Email", "Phone"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose field to update");

        builder.setItems(options, (dialog, which) -> {
            String chosenField = options[which];
            if (chosenField.equals("Name")) showUpdateDialog("Name", nameTv.getText().toString());
            else if (chosenField.equals("Email")) showUpdateDialog("Email", emailTv.getText().toString());
            else if (chosenField.equals("Phone")) showUpdateDialog("Phone", phoneTv.getText().toString());
        });
        builder.show();
    });

        signout_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfilePage.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button deleteBtn = findViewById(R.id.delete_account_button);
        deleteBtn.setOnClickListener(v -> showDeleteConfirmationDialog());
    }
//Gemini March 6th 2026 , how do i update information in Firebase Database
    private void showUpdateDialog(String fieldName, String currentValue){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update" + fieldName);

        final EditText input = new EditText((this));
        input.setInputType(fieldName.equals("phone") ? InputType.TYPE_CLASS_PHONE: InputType.TYPE_CLASS_TEXT);
        input.setText(currentValue.equals("Not Provided") ? "" : currentValue);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which)-> {
            String newValue = input.getText().toString().trim();
            if(!newValue.isEmpty()) {
                processUpdate(fieldName.toLowerCase(), newValue);

            }
        });
        builder.setNegativeButton("Cancel", (dialog, which)-> dialog.cancel());
        builder.show();
    }

    private void processUpdate(String key, String newValue) {
        String userId = mAuth.getCurrentUser().getUid();
        if (key.equals("email")) {
            mAuth.getCurrentUser().verifyBeforeUpdateEmail(newValue).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(this, "Verification sent! Email will update once verified.", Toast.LENGTH_LONG).show();
                    updateFirestoreField(userId, key, newValue);
                } else {
                    Toast.makeText(this, "Email Update Failed(Re-login required)", Toast.LENGTH_LONG).show();
                    Log.e("AUTH_UPDATE", "Failed: " + task.getException().getMessage());
                    Toast.makeText(this, "Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            updateFirestoreField(userId, key , newValue);
        }
    }

    private void updateFirestoreField(String uid, String key, String value){
        db.collection("users").document(uid).update(key,value).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, key + "updated!", Toast.LENGTH_SHORT).show();
            if(key.equals("name")) nameTv.setText(value);
            if(key.equals("email")) emailTv.setText(value);
            if(key.equals("phone")) phoneTv.setText(value);
        });
    }
    //Gemini March 7th 2026 , how do i delete data from firebase
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(("Delete Account"));
        builder.setMessage("Are you sure you want to delete your account ? This action cannot be undone all your data will be removed");
        builder.setPositiveButton("Delete", (dialog, main) -> {
            deleteUserAccount();
        });
        builder.setNegativeButton("Cancel",  (dialog, main) -> {
            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteUserAccount() {
        FirebaseUser user = mAuth.getCurrentUser();

        if(user !=null) {
            String userId = user.getUid();

            db.collection("users").document(userId).delete().addOnSuccessListener(aVoid -> {
                user.delete().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                    Toast.makeText(ProfilePage.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfilePage.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else{
                    Toast.makeText(ProfilePage.this, "Error: Requires Recent Login", Toast.LENGTH_LONG).show();

                }

                });
            }).addOnFailureListener(e-> Toast.makeText(ProfilePage.this, "Failed to delete database record", Toast.LENGTH_SHORT).show());
        }
    }
}