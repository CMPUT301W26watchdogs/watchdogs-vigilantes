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

    // handler and runnable for auto scrolling the carousel every few seconds
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

        // setting up the featured events carousel
        carousel = findViewById(R.id.eventCarousel);
        dotsContainer = findViewById(R.id.carouselDots);
        noEventsText = findViewById(R.id.noEventsText);

        featuredEvents = new ArrayList<>();
        carouselAdapter = new CarouselAdapter(this, featuredEvents);
        carousel.setAdapter(carouselAdapter);

        // adding some horizontal padding so the next and previous cards peek from the edges
        carousel.setOffscreenPageLimit(3);
        carousel.setClipToPadding(false);
        carousel.setClipChildren(false);
        carousel.setPadding(48, 0, 48, 0);

        // listening for page changes to update the dot indicators
        carousel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
            }
        });

        // fetching featured events from Firestore for the carousel
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

    // querying Firestore for the most recent events to show in the carousel
    // Citation: Ved, March 15 2025, Claude referred to https://firebase.google.com/docs/firestore/query-data/order-limit-data#order_and_limit_data
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
                        // hiding the carousel and showing a placeholder when no events exist
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

    // creating dot indicators below the carousel, one for each page
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
            // first dot starts as the active color
            shape.setColor(i == 0 ? getColor(R.color.vigilante_red) : getColor(R.color.divider));
            dot.setBackground(shape);
            dotsContainer.addView(dot);
        }
    }

    // highlighting the active dot and dimming the rest when the carousel page changes
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

    // starting auto scroll so the carousel moves to the next page automatically
    // Citation: Ved, March 15 2025, Claude referred to https://developer.android.com/reference/android/os/Handler#postDelayed(java.lang.Runnable,%20long)
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

    // stopping auto scroll when the activity is paused so it doesn't run in the background
    private void stopAutoScroll() {
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoScroll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // restarting auto scroll when coming back to the home screen
        if (autoScrollHandler != null && autoScrollRunnable != null && featuredEvents.size() > 1) {
            autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
        }
    }

    // converting dp to pixels for programmatic layout params
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

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
