// admin dashboard — navigation hub with buttons to browse events, profiles, organizers and sign out — US 03.04.01, US 03.05.01, US 03.07.01

package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class AdminPage extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.adminpage);

        Button signout_button = (Button) findViewById(R.id.signout_button);
        Button browseevents_button = (Button) findViewById(R.id.browseevents_button);
        Button browseprofiles_button = (Button) findViewById(R.id.browseprofiles_button);
        Button browseorganizers_button = (Button) findViewById(R.id.browseorganizers_button);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.backArrow).setOnClickListener(v -> finish());

        signout_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AdminPage.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        browseevents_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AdminPage.this, AllEventsActivity.class);
                intent.putExtra("type", "admin");
                startActivity(intent);
            }
        });

        browseprofiles_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AdminPage.this, AllProfiles.class);
                intent.putExtra("type", "all");
                startActivity(intent);
                finish();
            }
        });

        browseorganizers_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AdminPage.this, AllProfiles.class);
                intent.putExtra("type", "org");
                startActivity(intent);
                finish();
            }
        });
    }
}
