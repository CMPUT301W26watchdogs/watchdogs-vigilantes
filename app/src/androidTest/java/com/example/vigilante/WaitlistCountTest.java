// testing the waitlist count display and verifying that cancelled entrants are excluded from the count US 01.05.04

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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class WaitlistCountTest {

    private static final String TEST_EVENT_ID = "test_event_waitlist_count";
    private FirebaseFirestore db;

    // signing in with test account and creating test event with mixed status attendees
    @Before
    public void setUp() throws Exception {
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("ash@test.com", "ash123"));
        Thread.sleep(1000);

        db = FirebaseFirestore.getInstance();

        // creating a test event with a waiting list limit of 20
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", "Waitlist Count Test");
        eventData.put("description", "Testing waitlist count display");
        eventData.put("waitingListLimit", 20);
        eventData.put("registrationStart", "Jan 1");
        eventData.put("registrationEnd", "Mar 1");
        Tasks.await(db.collection("events").document(TEST_EVENT_ID).set(eventData));

        // adding 3 pending attendees to the test event
        for (int i = 0; i < 3; i++) {
            Map<String, Object> attendee = new HashMap<>();
            attendee.put("name", "Pending User " + i);
            attendee.put("email", "pending" + i + "@test.com");
            attendee.put("userId", "waitlist-uid-" + i);
            attendee.put("status", "pending");
            Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                    .collection("attendees").document("waitlist-uid-" + i).set(attendee));
        }

        // adding a cancelled attendee to verify it's excluded from count
        Map<String, Object> cancelledAttendee = new HashMap<>();
        cancelledAttendee.put("name", "Cancelled User");
        cancelledAttendee.put("email", "cancelled@test.com");
        cancelledAttendee.put("userId", "waitlist-uid-cancelled");
        cancelledAttendee.put("status", "cancelled");
        Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("waitlist-uid-cancelled").set(cancelledAttendee));
    }

    // verifying the waitlist count shows 3 and excludes cancelled entrants US 01.05.04
    @Test
    public void eventDetail_displaysCorrectWaitlistCount() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventDetailActivity.class);
        intent.putExtra("event_id", TEST_EVENT_ID);
        try (ActivityScenario<EventDetailActivity> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(3000);
            onView(withId(R.id.waitlistCount)).check(matches(withText("3")));
        } catch (InterruptedException e) {}
    }

    // verifying the event capacity is displayed correctly US 01.05.04
    @Test
    public void eventDetail_displaysCapacity() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventDetailActivity.class);
        intent.putExtra("event_id", TEST_EVENT_ID);
        try (ActivityScenario<EventDetailActivity> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(3000);
            onView(withId(R.id.eventCapacity)).check(matches(withText("20")));
        } catch (InterruptedException e) {}
    }

    // verifying the event title matches the test data US 01.05.04
    @Test
    public void eventDetail_displaysEventTitle() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventDetailActivity.class);
        intent.putExtra("event_id", TEST_EVENT_ID);
        try (ActivityScenario<EventDetailActivity> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(3000);
            onView(withId(R.id.eventTitle)).check(matches(withText("Waitlist Count Test")));
        } catch (InterruptedException e) {}
    }

    // verifying cancelled entrants are excluded from the waitlist count US 01.05.04
    @Test
    public void eventDetail_waitlistCountExcludesCancelledEntrants() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventDetailActivity.class);
        intent.putExtra("event_id", TEST_EVENT_ID);
        try (ActivityScenario<EventDetailActivity> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(3000);
            onView(withId(R.id.waitlistCount)).check(matches(withText("3")));
        } catch (InterruptedException e) {}
    }

    // cleaning up test attendee and event data from Firestore
    @After
    public void tearDown() throws Exception {
        for (int i = 0; i < 3; i++) {
            db.collection("events").document(TEST_EVENT_ID)
                    .collection("attendees").document("waitlist-uid-" + i).delete();
        }
        db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("waitlist-uid-cancelled").delete();
        db.collection("events").document(TEST_EVENT_ID).delete();
    }
}
