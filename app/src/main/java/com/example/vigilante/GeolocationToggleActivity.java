package com.example.vigilante;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GeolocationToggleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_geolocation_toggle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SwitchCompat geolocationSwitch = findViewById(R.id.geolocationSwitch);
        TextView geolocationStatus = findViewById(R.id.geolocationStatus);

        geolocationSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                geolocationStatus.setText(isChecked
                        ? "Geolocation is required"
                        : "Geolocation is not required"));

        findViewById(R.id.saveButton).setOnClickListener(v -> {
            boolean required = geolocationSwitch.isChecked();
            Toast.makeText(this,
                    required ? "Geolocation required (placeholder)" : "Geolocation not required (placeholder)",
                    Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());
    }
}
