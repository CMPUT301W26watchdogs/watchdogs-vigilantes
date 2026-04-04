// showing a calendar view of all events and registered events based on the device's time zone (Wildcard)

package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView eventsRecycler;
    private EventAdapter eventAdapter;
    private TextView selectedDateLabel;
    private TextView noEventsText;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private List<Event> allEventsList;
    private List<Event> displayList;

    private String selectedDateStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        calendarView = findViewById(R.id.calendarView);
        eventsRecycler = findViewById(R.id.calendarEventsRecycler);
        selectedDateLabel = findViewById(R.id.selectedDateLabel);
        noEventsText = findViewById(R.id.noEventsText);

        eventsRecycler.setLayoutManager(new LinearLayoutManager(this));

        allEventsList = new ArrayList<>();
        displayList = new ArrayList<>();

        // setting up adapter in user browsing mode so they can tap events to see details
        eventAdapter = new EventAdapter(displayList, false, false, true, false);
        eventsRecycler.setAdapter(eventAdapter);

        // setting the initial selected date to today using the device's time zone
        Calendar today = Calendar.getInstance(TimeZone.getDefault());
        selectedDateStr = normalizeDate(
                today.get(Calendar.DAY_OF_MONTH),
                today.get(Calendar.MONTH) + 1,
                today.get(Calendar.YEAR));

        selectedDateLabel.setText("Events on " + selectedDateStr);

        // listening for date changes on the calendar widget
        // Citation: Ved, March 18 2025, Claude referred to https://developer.android.com/reference/android/widget/CalendarView#setOnDateChangeListener(android.widget.CalendarView.OnDateChangeListener)
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDateStr = normalizeDate(dayOfMonth, month + 1, year);
            selectedDateLabel.setText("Events on " + selectedDateStr);
            filterEventsForDate();
        });

        // back arrow navigates back to the home page
        findViewById(R.id.backArrow).setOnClickListener(v -> {
            startActivity(new Intent(CalendarActivity.this, HomePage.class));
            finish();
        });

        // fetching all events from Firestore then loading the user's registered events
        fetchAllEvents();

        setupBottomNav();
    }

    // fetching all events from Firestore and storing them locally for calendar filtering
    // Citation: Ved, March 18 2025, Claude referred to https://firebase.google.com/docs/firestore/query-data/get-data#get_all_documents_in_a_collection
    private void fetchAllEvents() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        db.collection("events").get().addOnSuccessListener(snapshots -> {
            allEventsList.clear();
            for (QueryDocumentSnapshot doc : snapshots) {
                Event event = doc.toObject(Event.class);
                event.setId(doc.getId());
                if (currentUser != null) {
                    event.setcurrentUser(currentUser.getUid());
                }
                allEventsList.add(event);
            }
            filterEventsForDate();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading events: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // filtering events to only show ones whose registration start date matches the selected calendar date
    private void filterEventsForDate() {
        displayList.clear();

        for (Event event : allEventsList) {
            String regStart = event.getRegistrationStart();
            if (regStart != null && normalizeEventDate(regStart).equals(selectedDateStr)) {
                displayList.add(event);
            }
        }

        eventAdapter.notifyDataSetChanged();

        // showing or hiding the empty state message
        if (displayList.isEmpty()) {
            noEventsText.setVisibility(View.VISIBLE);
            eventsRecycler.setVisibility(View.GONE);
        } else {
            noEventsText.setVisibility(View.GONE);
            eventsRecycler.setVisibility(View.VISIBLE);
        }
    }

    // normalizing a date from day, month, year ints to a consistent "d/M/yyyy" string for comparison
    static String normalizeDate(int day, int month, int year) {
        return day + "/" + month + "/" + year;
    }

    // normalizing an event's date string to match our comparison format
    // handles both "d/M/yyyy" and potential extra whitespace
    // Citation: Ved, March 18 2025, Claude referred to https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
    static String normalizeEventDate(String dateStr) {
        if (dateStr == null) return "";
        String trimmed = dateStr.trim();

        // parsing the date string and reformatting to ensure consistent format
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
            inputFormat.setTimeZone(TimeZone.getDefault());
            Date date = inputFormat.parse(trimmed);
            if (date != null) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                cal.setTime(date);
                return cal.get(Calendar.DAY_OF_MONTH) + "/" +
                        (cal.get(Calendar.MONTH) + 1) + "/" +
                        cal.get(Calendar.YEAR);
            }
        } catch (ParseException e) {
            // if parsing fails just return the trimmed string as is
        }
        return trimmed;
    }

    private void setupBottomNav() {
        LiquidGlassNavBar navBar = findViewById(R.id.bottomNav);
        navBar.setOnTabSelectedListener(position -> {
            if (position == 0) {
                Intent intent = new Intent(this, AllEventsActivity.class);
                intent.putExtra("type", "all");
                startActivity(intent);
                finish();
            } else if (position == 1) {
                startActivity(new Intent(this, HomePage.class));
                finish();
            } else if (position == 2) {
                startActivity(new Intent(this, NotificationsActivity.class));
                finish();
            } else if (position == 3) {
                startActivity(new Intent(this, ProfilePage.class));
                finish();
            }
        });
    }
}
