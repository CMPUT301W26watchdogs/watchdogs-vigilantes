//Success screen after creating Event showing the generated QR code for the event

package com.example.vigilante;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class EventCreatedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_created);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // retrieving the Event object passed from CreateEventActivity
        Event event = (Event) getIntent().getSerializableExtra("event");

        TextView titleView = findViewById(R.id.createdEventTitle);

        ImageView qrView = findViewById(R.id.generatedQrCode);

        // showing the event title above the QR code
        titleView.setText(event.getTitle());

        // generating and displaying a QR code that encodes the event's UUID
        qrView.setImageBitmap(generateQrCode(event.getId()));

        // done button navigates back to home and clears the activity stack above it
        findViewById(R.id.doneButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // removes CreateEvent and EventCreated from back stack
            startActivity(intent);
        });
    }

    // converting a string into a QR code bitmap using ZXing
    /* sources:
    - https://github.com/journeyapps/zxing-android-embedded
    - https://github.com/journeyapps/zxing-android-embedded/blob/master/sample/src/main/java/example/zxing/MainActivity.java */
    private Bitmap generateQrCode(String content) {
        try {
            // encoding the string into a 400x400 QR code bit matrix
            BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 400, 400);
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            // creating a blank bitmap the same size as the matrix
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    // black pixel for true (module), white pixel for false (background)
                    bitmap.setPixel(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return bitmap;
        } catch (Exception e) {
            return null; // if encoding fails for any reason, return null (QR won't display)
        }
    }
}
