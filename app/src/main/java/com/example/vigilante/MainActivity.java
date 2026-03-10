package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button historyButton = findViewById(R.id.historyButton);

        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EventHistoryActivity.class);
            startActivity(intent);
        });
    }
}