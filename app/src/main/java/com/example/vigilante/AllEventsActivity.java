// displaying all events from Firestore in a RecyclerView for entrants to browse and sign up US 01.01.03

package com.example.vigilante;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is called to show all the events available in firebase with category filtering
 */
public class AllEventsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;

    private List<Event> eventList;
    private List<Event> allEventsList;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String searchQuery = "";
    private String activeFilter = "All";
    private TextView chipAll, chipSports, chipArts, chipMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allevents);
        Button back_button = (Button) findViewById(R.id.back_button);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //Gemini March 8th 2026 , help add event list from firebase
        recyclerView = findViewById(R.id.all_events_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();
        allEventsList = new ArrayList<>();

        chipAll = findViewById(R.id.chipAll);
        chipSports = findViewById(R.id.chipSports);
        chipArts = findViewById(R.id.chipArts);
        chipMusic = findViewById(R.id.chipMusic);

        setupChipListeners();

        // updating the header and subtitle based on which type of event listing this is
        TextView header = findViewById(R.id.all_events_header);
        TextView subtitle = findViewById(R.id.eventsSubtitle);

        String type = getIntent().getStringExtra("type");
        if (type == null) {
            type = "all";
        }
        if (type.equals("all")) {
            header.setText("Upcoming Events");
            subtitle.setText("Explore events available near you");
            eventAdapter = new EventAdapter(eventList, false, false, true);
            recyclerView.setAdapter(eventAdapter);
            fetchAllEvents();
        } else if (type.equals("myactivityorg")) {
            header.setText("Your Events");
            subtitle.setText("Events you are organizing");
            eventAdapter = new EventAdapter(eventList, true, false, false);
            recyclerView.setAdapter(eventAdapter);
            fetchMyOrgEvents();
        } else if (type.equals("admin")) {
            header.setText("All Events");
            subtitle.setText("Manage all events on the platform");
            eventAdapter = new EventAdapter(eventList, false, true, false);
            recyclerView.setAdapter(eventAdapter);
            fetchAdminEvents();
        }

        EditText searchBar = findViewById(R.id.searchEditText);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().toLowerCase().trim();
                applyFilter();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {

            }
        });


        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        setupBottomNav();
    }

    // setting up category chip filter listeners for filtering events by type US 01.01.04
    // Citation: Ved, March 10 2025, Claude referred to https://developer.android.com/develop/ui/views/touch-and-input/click-handlers
    private void setupChipListeners() {
        View.OnClickListener chipListener = v -> {
            int id = v.getId();
            if (id == R.id.chipAll) activeFilter = "All";
            else if (id == R.id.chipSports) activeFilter = "Sports";
            else if (id == R.id.chipArts) activeFilter = "Arts";
            else if (id == R.id.chipMusic) activeFilter = "Music";

            updateChipStyles();
            applyFilter();
        };

        chipAll.setOnClickListener(chipListener);
        chipSports.setOnClickListener(chipListener);
        chipArts.setOnClickListener(chipListener);
        chipMusic.setOnClickListener(chipListener);
    }

    private void updateChipStyles() {
        chipAll.setBackgroundResource(activeFilter.equals("All") ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
        chipAll.setTextColor(getColor(activeFilter.equals("All") ? R.color.white : R.color.text_primary));

        chipSports.setBackgroundResource(activeFilter.equals("Sports") ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
        chipSports.setTextColor(getColor(activeFilter.equals("Sports") ? R.color.white : R.color.text_primary));

        chipArts.setBackgroundResource(activeFilter.equals("Arts") ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
        chipArts.setTextColor(getColor(activeFilter.equals("Arts") ? R.color.white : R.color.text_primary));

        chipMusic.setBackgroundResource(activeFilter.equals("Music") ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
        chipMusic.setTextColor(getColor(activeFilter.equals("Music") ? R.color.white : R.color.text_primary));
    }
    // Gemini March 31st 2026 "How to implement a search bar"
    // applies the selected category filter to the full event list
    private void applyFilter() {
        eventList.clear();
        for (Event event : allEventsList) {
            // Check category chip
            boolean matchesCategory = activeFilter.equals("All") ||
                    (event.getCategory() != null && event.getCategory().equalsIgnoreCase(activeFilter));
            // Check search query
            boolean matchesSearch = searchQuery.isEmpty() ||
                    (event.getTitle() != null && event.getTitle().toLowerCase().contains(searchQuery)) ||
                    (event.getCategory() != null && event.getCategory().toLowerCase().contains(searchQuery)) ||
                    (event.getLocation() != null && event.getLocation().toLowerCase().contains(searchQuery));
            if (matchesCategory && matchesSearch) {
                eventList.add(event);
            }
        }
        eventAdapter.notifyDataSetChanged();
    }

    /**
     * Old applyFilter() method:
     * private void applyFilter() {
     *         eventList.clear();
     *         if (activeFilter.equals("All")) {
     *             eventList.addAll(allEventsList);
     *         } else {
     *             for (Event event : allEventsList) {
     *                 String cat = event.getCategory();
     *                 if (cat != null && cat.equalsIgnoreCase(activeFilter)) {
     *                     eventList.add(event);
     *                 }
     *             }
     *         }
     *         eventAdapter.notifyDataSetChanged();
     *     }
     */

    private void setupBottomNav() {
        LiquidGlassNavBar navBar = findViewById(R.id.bottomNav);
        navBar.setSelectedTab(0);
        boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
        navBar.setOnTabSelectedListener(position -> {
            if (position == 1) {
                if(isAdmin){
                    Intent intent = new Intent(this, AdminPage.class);
                    intent.putExtra("IS_ADMIN", isAdmin);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(this, HomePage.class);
                    intent.putExtra("IS_ADMIN", isAdmin);
                    startActivity(intent);
                }
                finish();
            } else if (position == 2) {
                Intent intent = new Intent(this, NotificationsActivity.class);
                intent.putExtra("IS_ADMIN", isAdmin);
                startActivity(intent);
                finish();
            } else if (position == 3) {
                Intent intent = new Intent(this, ProfilePage.class);
                intent.putExtra("IS_ADMIN", isAdmin);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * This function fetches events for a user with user specific options
     */
    private void fetchAllEvents() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String safeUid = (currentUser != null) ? currentUser.getUid() : "espresso_test_user";
        db.collection("events").orderBy("timestamp", Query.Direction.DESCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
            eventList.clear();
            allEventsList.clear();

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Event event = document.toObject(Event.class);
                if(!Boolean.TRUE.equals(event.getIsPrivate())) {
                    event.setId(document.getId());
                    //event.setcurrentUser(currentUser.getUid());
                    event.setcurrentUser(safeUid);
                    allEventsList.add(event);
                }
            }
            applyFilter();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading events:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * This function fetches events created by that organizer with organizer specific options
     */
    private void fetchMyOrgEvents() {
        FirebaseUser organizerId = mAuth.getCurrentUser();
        db.collection("events").whereEqualTo("organizerId", organizerId.getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            eventList.clear();
            allEventsList.clear();

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Event event = document.toObject(Event.class);
                event.setId(document.getId());
                allEventsList.add(event);
            }
            applyFilter();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading events:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * This function fetches events for admin with admin specific options
     */
    private void fetchAdminEvents() {
        db.collection("events").get().addOnSuccessListener(queryDocumentSnapshots -> {
            eventList.clear();
            allEventsList.clear();

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Event event = document.toObject(Event.class);
                event.setId(document.getId());
                allEventsList.add(event);
            }
            applyFilter();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error loading events:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
