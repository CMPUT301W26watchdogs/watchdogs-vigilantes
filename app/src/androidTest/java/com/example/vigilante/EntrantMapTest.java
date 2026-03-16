package com.example.vigilante;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class EntrantMapTest {

    private static final String TEST_EVENT_ID = "test_event_map";
    private FirebaseFirestore db;

    @Before
    public void setUp() throws Exception {
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("ash@test.com", "ash123"));
        Thread.sleep(1000);

        db = FirebaseFirestore.getInstance();

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", "Map Test Event");
        eventData.put("organizerId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        Tasks.await(db.collection("events").document(TEST_EVENT_ID).set(eventData));

        Map<String, Object> attendee1 = new HashMap<>();
        attendee1.put("name", "User With Location");
        attendee1.put("email", "loc@test.com");
        attendee1.put("userId", "map-uid-1");
        attendee1.put("status", "pending");
        attendee1.put("latitude", 53.5461);
        attendee1.put("longitude", -113.4938);
        Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("map-uid-1").set(attendee1));

        Map<String, Object> attendee2 = new HashMap<>();
        attendee2.put("name", "User Without Location");
        attendee2.put("email", "noloc@test.com");
        attendee2.put("userId", "map-uid-2");
        attendee2.put("status", "pending");
        Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("map-uid-2").set(attendee2));
    }

    @Test
    public void waitingView_mapButtonOpensMapActivity() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), viewAttendee.class);
        intent.putExtra("EVENT_ID", TEST_EVENT_ID);
        intent.putExtra("type", "waiting");
        try (ActivityScenario<viewAttendee> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(2000);
            onView(withId(R.id.map_button)).check(matches(isDisplayed()));
            onView(withId(R.id.map_button)).perform(click());
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
    }

    @Test
    public void waitingView_showsEntrantsWithLocations() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), viewAttendee.class);
        intent.putExtra("EVENT_ID", TEST_EVENT_ID);
        intent.putExtra("type", "waiting");
        try (ActivityScenario<viewAttendee> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(2000);
            onView(withId(R.id.attendees_recycler_view)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {}
    }

    @After
    public void tearDown() throws Exception {
        db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("map-uid-1").delete();
        db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("map-uid-2").delete();
        db.collection("events").document(TEST_EVENT_ID).delete();
    }
}
