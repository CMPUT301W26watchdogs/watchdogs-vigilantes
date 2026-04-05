// admin dashboard navigation hub with buttons to browse events, profiles, organizers and sign out US 03.04.01, US 03.05.01, US 03.07.01

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

/**
 * This class is used to view the AdminPage when admin logs in which is different than other user's homepage
 */
public class AdminPage extends AppCompatActivity {

    /** setting up the admin dashboard with navigation buttons for events, profiles, organizers, images, and sign out */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.adminpage);

        Button signout_button = (Button) findViewById(R.id.signout_button);
        Button browseevents_button = (Button) findViewById(R.id.browseevents_button);
        Button browseprofiles_button = (Button) findViewById(R.id.browseprofiles_button);
        Button browseorganizers_button = (Button) findViewById(R.id.browseorganizers_button);
        Button browseimages_button = (Button) findViewById(R.id.browseimages_button);
        Button notiflog_button = (Button) findViewById(R.id.notiflog_button);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
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
                //Intent intent = new Intent(AdminPage.this, MyEventsAdmin.class);
                Intent intent = new Intent(AdminPage.this, AllEventsActivity.class);
                intent.putExtra("type", "admin");
                intent.putExtra("IS_ADMIN", true);
                startActivity(intent);
            }
        });

        browseprofiles_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AdminPage.this, AllProfiles.class);
                intent.putExtra("type", "admin");
                intent.putExtra("IS_ADMIN", true);
                startActivity(intent);
                //finish();
            }
        });

        browseorganizers_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AdminPage.this, AllProfiles.class);
                intent.putExtra("type", "org");
                intent.putExtra("IS_ADMIN", true);
                startActivity(intent);
                //finish();
            }
        });

        browseimages_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AdminPage.this, AdminBrowseImagesActivity.class);
                intent.putExtra("IS_ADMIN", true);
                startActivity(intent);
            }
        });

        notiflog_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AdminPage.this, NotificationLogActivity.class);
                intent.putExtra("IS_ADMIN", true);
                startActivity(intent);
            }
        });

        setupBottomNav();
    }
    /** configuring the bottom navigation bar with admin specific tab routing */
    private void setupBottomNav() {
        LiquidGlassNavBar navBar = findViewById(R.id.bottomNav);

        if (navBar != null) {
            // Highlights the home icon (index 1) since this acts as the admin's home
            navBar.setSelectedTab(1);
            navBar.setOnTabSelectedListener(position -> {
                if (position == 0) {
                    Intent intent = new Intent(this, AllEventsActivity.class);
                    intent.putExtra("type", "all");
                    intent.putExtra("IS_ADMIN", true);
                    startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent(this, NotificationsActivity.class);
                    intent.putExtra("IS_ADMIN", true);
                    startActivity(intent);
                } else if (position == 3) {
                    Intent intent = new Intent(this, ProfilePage.class);
                    intent.putExtra("IS_ADMIN", true);
                    startActivity(intent);
                }
            });
        }
    }
}
