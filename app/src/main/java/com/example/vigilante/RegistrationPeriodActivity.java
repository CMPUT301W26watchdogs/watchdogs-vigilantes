package com.example.vigilante;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class RegistrationPeriodActivity extends AppCompatActivity {

    private String selectedStartDate = "";
    private String selectedEndDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration_period);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView startDateDisplay = findViewById(R.id.startDateDisplay);
        TextView endDateDisplay = findViewById(R.id.endDateDisplay);

        findViewById(R.id.pickStartDateButton).setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                selectedStartDate = day + "/" + (month + 1) + "/" + year;
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

        findViewById(R.id.saveButton).setOnClickListener(v -> {
            if (selectedStartDate.isEmpty() || selectedEndDate.isEmpty()) {
                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this,
                    "Registration period saved (placeholder)\n" + selectedStartDate + " → " + selectedEndDate,
                    Toast.LENGTH_LONG).show();
        });

        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());
    }
}
