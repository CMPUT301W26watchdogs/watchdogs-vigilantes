package com.example.vigilante;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class WaitingListLimitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_waiting_list_limit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SwitchCompat limitSwitch = findViewById(R.id.limitSwitch);
        TextInputLayout limitLayout = findViewById(R.id.layoutMaxEntrants);
        TextInputEditText maxEntrantsField = findViewById(R.id.fieldMaxEntrants);

        limitSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                limitLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        findViewById(R.id.saveButton).setOnClickListener(v -> {
            if (limitSwitch.isChecked()) {
                String maxText = maxEntrantsField.getText().toString().trim();
                if (maxText.isEmpty()) {
                    Toast.makeText(this, "Please enter a maximum number of entrants", Toast.LENGTH_SHORT).show();
                    return;
                }
                int max = Integer.parseInt(maxText);
                if (max < 1) {
                    Toast.makeText(this, "Limit must be at least 1", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "Waiting list limited to " + max + " entrants (placeholder)", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No waiting list limit set (placeholder)", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());
    }
}
