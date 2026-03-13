package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
            Log.d("AUTH", "User is already logged in: " + currentUser.getEmail());
            if(currentUser.getEmail().equals("admin@admin.com")) {
                Intent intent = new Intent(MainActivity.this, AdminPage.class);
                startActivity(intent);
                finish();
            }
            else {
                Intent intent = new Intent(MainActivity.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        EditText editTextEmail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        EditText editTextPassword = (EditText) findViewById(R.id.editTextTextPassword);
        Button login_button = (Button) findViewById(R.id.login_button);
        Button register_button = (Button) findViewById(R.id.register_button);
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String textEmail = editTextEmail.getText().toString().trim();
                String textPassword = editTextPassword.getText().toString().trim();
                //Gemini, Feb 28th 2026 , how to use firebase to autheticate my application and be identified by my device
                if(textEmail.isEmpty() || textPassword.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter email and password.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(MainActivity.this, task ->{
                    if(task.isSuccessful()){
                        Log.d("AUTH", "signInWithEmail:success");
                        if("admin@admin.com".equals(textEmail)){

                            Intent intent = new Intent(MainActivity.this, AdminPage.class);
                            startActivity(intent);
                            finish();

                        }
                        else {
                            Intent intent = new Intent(MainActivity.this, HomePage.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Log.w("AUTH", "signInWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed. Try again", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterPage.class));
                finish();
            }
        });
        Button notifyButton = findViewById(R.id.notifyButton);
        notifyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SendNotificationActivity.class);
            startActivity(intent);});
    }
}