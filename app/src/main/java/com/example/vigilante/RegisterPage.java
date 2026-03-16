// account registration form — creates a Firebase Auth user and stores the profile in Firestore users collection — US 01.02.01

package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterPage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private boolean isOrganizer = false;
    private TextView roleEntrant, roleOrganizer, roleAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register_page);

        Button register_button = (Button) findViewById(R.id.register_button);
        Button goback_button = (Button) findViewById(R.id.back_button);
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        EditText email_register = (EditText) findViewById(R.id.email_register);
        EditText password_register = (EditText) findViewById(R.id.password_register);
        EditText name_register = (EditText) findViewById(R.id.name_register);
        EditText phone_register = (EditText) findViewById(R.id.phone_number_register);

        roleEntrant = findViewById(R.id.roleEntrant);
        roleOrganizer = findViewById(R.id.roleOrganizer);
        roleAdmin = findViewById(R.id.roleAdmin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registration_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        selectRole(roleEntrant);

        roleEntrant.setOnClickListener(v -> {
            selectRole(roleEntrant);
            isOrganizer = false;
        });

        roleOrganizer.setOnClickListener(v -> {
            selectRole(roleOrganizer);
            isOrganizer = true;
        });

        roleAdmin.setOnClickListener(v -> {
            selectRole(roleAdmin);
            isOrganizer = false;
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_register.getText().toString().trim();
                String password = password_register.getText().toString().trim();
                String name = name_register.getText().toString().trim();
                String phone = phone_register.getText().toString().trim();

                if (email.isEmpty() || password.length() < 6 || name.isEmpty()) {
                    Toast.makeText(RegisterPage.this, "Please fill Name, Email, and 6-char Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("name", name);
                        userMap.put("email", email);
                        userMap.put("isOrganizer", isOrganizer);
                        if (!phone.isEmpty()) {
                            userMap.put("phone", phone);
                        }

                        db.collection("users").document(uid).set(userMap).addOnSuccessListener(aVoid -> {
                            Toast.makeText(RegisterPage.this, "Profile Created!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterPage.this, HomePage.class));
                            finish();
                        }).addOnFailureListener(e -> Log.e("FIRESTORE", "Error: " + e.getMessage()));
                    } else {
                        Toast.makeText(RegisterPage.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        goback_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterPage.this, MainActivity.class));
                finish();
            }
        });
    }

    private void selectRole(TextView selected) {
        roleEntrant.setBackground(getDrawable(R.drawable.bg_role_unselected));
        roleEntrant.setTextColor(getColor(R.color.text_primary));
        roleOrganizer.setBackground(getDrawable(R.drawable.bg_role_unselected));
        roleOrganizer.setTextColor(getColor(R.color.text_primary));
        roleAdmin.setBackground(getDrawable(R.drawable.bg_role_unselected));
        roleAdmin.setTextColor(getColor(R.color.text_primary));

        selected.setBackground(getDrawable(R.drawable.bg_role_selected));
        selected.setTextColor(getColor(R.color.white));
    }
}
