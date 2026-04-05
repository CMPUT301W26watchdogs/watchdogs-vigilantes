// main hub after login with featured event carousel, QR code scanner and quick access to events list and profile US 01.06.01 (Wildcard)

package com.example.vigilante;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

/**
* This class shows the home page to our user, it has the option to scan qr
* goto events or profile page.
 */
public class HomePage extends AppCompatActivity {

    // storing the most recently created event ID so organizer screens can use it
    private String lastEventId = null;

    private ViewPager2 carousel;
    private CarouselAdapter carouselAdapter;
    private List<Event> featuredEvents;
    private LinearLayout dotsContainer;
    private TextView noEventsText;

    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;
    private static final long AUTO_SCROLL_DELAY = 4000;

    /*
     * QR code scanning function referenced from zxing-android-embedded ScanContract with ActivityResultLauncher, sources:
     * https://github.com/journeyapps/zxing-android-embedded
     * https://github.com/journeyapps/zxing-android-embedded/blob/master/sample/src/main/java/example/zxing/MainActivity.java
     * https://github.com/journeyapps/zxing-android-embedded/releases/tag/v4.3.0
     */
    // registering a launcher that opens the camera scanner and receives its result
    private final ActivityResultLauncher<ScanOptions> scanLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) { // scan succeeded and the contents is the decoded string
                    Intent intent = new Intent(this, EventDetailActivity.class);
                    intent.putExtra("event_id", result.getContents()); // passing decoded QR string as event ID
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show(); // user cancelled scan
                }
            });

    /** initializing the home page layout with event carousel, quick access cards, qr scanner, and bottom nav */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.homepage);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homepage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        carousel = findViewById(R.id.eventCarousel);
        dotsContainer = findViewById(R.id.carouselDots);
        noEventsText = findViewById(R.id.noEventsText);

        featuredEvents = new ArrayList<>();
        carouselAdapter = new CarouselAdapter(this, featuredEvents);
        carousel.setAdapter(carouselAdapter);

        carousel.setOffscreenPageLimit(3);
        carousel.setClipToPadding(false);
        carousel.setClipChildren(false);
        carousel.setPadding(48, 0, 48, 0);

        carousel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
            }
        });

        loadFeaturedEvents();

        // quick access cards, tapping the events or profile cards also navigates
        findViewById(R.id.eventsCard).setOnClickListener(v -> {
            Intent intent = new Intent(this, AllEventsActivity.class);
            intent.putExtra("type", "all");
            startActivity(intent);
        });

        findViewById(R.id.profileCard).setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationsActivity.class));
        });

        findViewById(R.id.calendarCard).setOnClickListener(v -> {
            startActivity(new Intent(this, CalendarActivity.class));
        });

        findViewById(R.id.scanQrButton).setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE); // only accepting QR codes, not barcodes
            options.setPrompt("Scan an event QR code"); // text shown on the scanner overlay
            options.setBeepEnabled(true);
            options.setOrientationLocked(true); // keep scanner in portrait
            scanLauncher.launch(options); // open the camera scanner
        });

        setupBottomNav();
    }

    /** fetching the five most recent events from firestore and populating the carousel */
    private void loadFeaturedEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(snapshots -> {
                    featuredEvents.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Event event = doc.toObject(Event.class);
                        event.setId(doc.getId());
                        featuredEvents.add(event);
                    }

                    if (featuredEvents.isEmpty()) {
                        carousel.setVisibility(View.GONE);
                        dotsContainer.setVisibility(View.GONE);
                        noEventsText.setVisibility(View.VISIBLE);
                    } else {
                        carousel.setVisibility(View.VISIBLE);
                        dotsContainer.setVisibility(View.VISIBLE);
                        noEventsText.setVisibility(View.GONE);
                        carouselAdapter.updateEvents(featuredEvents);
                        setupDots(featuredEvents.size());
                        startAutoScroll();
                    }
                })
                .addOnFailureListener(e -> {
                    carousel.setVisibility(View.GONE);
                    dotsContainer.setVisibility(View.GONE);
                    noEventsText.setVisibility(View.VISIBLE);
                });
    }

    /** creating the dot indicators below the carousel matching the number of featured events */
    private void setupDots(int count) {
        dotsContainer.removeAllViews();
        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            int size = (i == 0) ? 10 : 8;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(size), dpToPx(size));
            params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            dot.setLayoutParams(params);

            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.OVAL);
            shape.setColor(i == 0 ? getColor(R.color.vigilante_red) : getColor(R.color.divider));
            dot.setBackground(shape);
            dotsContainer.addView(dot);
        }
    }

    /** updating the dot indicators to highlight the currently visible carousel page */
    private void updateDots(int activePosition) {
        for (int i = 0; i < dotsContainer.getChildCount(); i++) {
            View dot = dotsContainer.getChildAt(i);
            boolean isActive = (i == activePosition);

            int size = isActive ? 10 : 8;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(size), dpToPx(size));
            params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            dot.setLayoutParams(params);

            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.OVAL);
            shape.setColor(isActive ? getColor(R.color.vigilante_red) : getColor(R.color.divider));
            dot.setBackground(shape);
        }
    }

    /** starting the automatic carousel rotation on a timed interval */
    private void startAutoScroll() {
        autoScrollHandler = new Handler(Looper.getMainLooper());
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (carousel != null && carouselAdapter.getItemCount() > 1) {
                    int next = (carousel.getCurrentItem() + 1) % carouselAdapter.getItemCount();
                    carousel.setCurrentItem(next, true);
                }
                autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY);
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
    }

    /** stopping the automatic carousel rotation when the activity is paused */
    private void stopAutoScroll() {
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }

    /** pausing the auto scroll when the activity goes to the background */
    @Override
    protected void onPause() {
        super.onPause();
        stopAutoScroll();
    }

    // March 31 2026, Claude Opus 4.6, applying accessibility settings whenever the home page is resumed
    /** reapplying accessibility settings and resuming the carousel auto scroll */
    @Override
    protected void onResume() {
        super.onResume();
        AccessibilityHelper.apply(this);
        if (autoScrollHandler != null && autoScrollRunnable != null && featuredEvents.size() > 1) {
            autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
        }
    }

    /** converting density independent pixels to actual screen pixels */
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    /** configuring the bottom navigation bar with tab listeners for events, notifications, and profile */
    private void setupBottomNav() {
        LiquidGlassNavBar navBar = findViewById(R.id.bottomNav);
        navBar.setSelectedTab(1);
        navBar.setOnTabSelectedListener(position -> {
            if (position == 0) {
                Intent intent = new Intent(this, AllEventsActivity.class);
                intent.putExtra("type", "all");
                startActivity(intent);
            } else if (position == 2) {
                startActivity(new Intent(this, NotificationsActivity.class));
            } else if (position == 3) {
                startActivity(new Intent(this, ProfilePage.class));
            }
        });
    }
}
