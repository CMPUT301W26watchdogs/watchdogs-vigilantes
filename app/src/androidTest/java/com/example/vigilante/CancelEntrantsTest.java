// espresso tests for cancel all entrants — verifies Firestore status updates to cancelled — US 02.06.04

package com.example.vigilante;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
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
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class CancelEntrantsTest {

    private static final String TEST_EVENT_ID = "test_event_cancel";
    private FirebaseFirestore db;

    @Before
    public void setUp() throws Exception {
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("ash@test.com", "ash123"));
        Thread.sleep(1000);

        db = FirebaseFirestore.getInstance();

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", "Cancel Test Event");
        eventData.put("organizerId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        Tasks.await(db.collection("events").document(TEST_EVENT_ID).set(eventData));

        Map<String, Object> attendee1 = new HashMap<>();
        attendee1.put("name", "Pending User");
        attendee1.put("email", "pending@test.com");
        attendee1.put("userId", "cancel-test-uid-1");
        attendee1.put("status", "pending");
        Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("cancel-test-uid-1").set(attendee1));

        Map<String, Object> attendee2 = new HashMap<>();
        attendee2.put("name", "Another Pending");
        attendee2.put("email", "pending2@test.com");
        attendee2.put("userId", "cancel-test-uid-2");
        attendee2.put("status", "pending");
        Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("cancel-test-uid-2").set(attendee2));
    }

    private ActivityScenario<viewAttendee> launchWaitingView() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), viewAttendee.class);
        intent.putExtra("EVENT_ID", TEST_EVENT_ID);
        intent.putExtra("type", "waiting");
        return ActivityScenario.launch(intent);
    }

    @Test
    public void waitingView_showsCancelAllButton() {
        try (ActivityScenario<viewAttendee> scenario = launchWaitingView()) {
            Thread.sleep(2000);
            onView(withId(R.id.cancel_all_button)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {}
    }

    @Test
    public void waitingView_showsMapButton() {
        try (ActivityScenario<viewAttendee> scenario = launchWaitingView()) {
            Thread.sleep(2000);
            onView(withId(R.id.map_button)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {}
    }

    @Test
    public void waitingView_displaysWaitingListTitle() {
        try (ActivityScenario<viewAttendee> scenario = launchWaitingView()) {
            Thread.sleep(2000);
            onView(withId(R.id.title_waiting_list)).check(matches(withText("Waiting List")));
        } catch (InterruptedException e) {}
    }

    @Test
    public void waitingView_showsEntrantsInRecyclerView() {
        try (ActivityScenario<viewAttendee> scenario = launchWaitingView()) {
            Thread.sleep(2000);
            onView(withId(R.id.attendees_recycler_view)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {}
    }

    @Test
    public void cancelAll_updatesFirestoreStatusToCancelled() throws Exception {
        try (ActivityScenario<viewAttendee> scenario = launchWaitingView()) {
            Thread.sleep(2000);
            onView(withId(R.id.cancel_all_button)).perform(click());
            Thread.sleep(1000);
            onView(withText("Confirm")).perform(click());
            Thread.sleep(3000);

            DocumentSnapshot doc1 = Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                    .collection("attendees").document("cancel-test-uid-1").get());
            assertEquals("cancelled", doc1.getString("status"));

            DocumentSnapshot doc2 = Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                    .collection("attendees").document("cancel-test-uid-2").get());
            assertEquals("cancelled", doc2.getString("status"));
        }
    }

    @After
    public void tearDown() throws Exception {
        db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("cancel-test-uid-1").delete();
        db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("cancel-test-uid-2").delete();
        db.collection("events").document(TEST_EVENT_ID).delete();
    }
}
