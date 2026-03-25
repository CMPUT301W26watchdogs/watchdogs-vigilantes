// testing replacement draw from waiting pool including draw button and Firestore status update US 02.05.03

package com.example.vigilante;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ReplacementDrawEspressoTest {

    private static final String TEST_EVENT_ID = "test_event_replacement";
    private FirebaseFirestore db;

    // signing in with test account and creating test event with selected and pending attendees
    @Before
    public void setUp() throws Exception {
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("ash@test.com", "ash123"));
        Thread.sleep(1000);

        db = FirebaseFirestore.getInstance();

        // creating a test event owned by the current user
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", "Replacement Draw Test");
        eventData.put("organizerId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        Tasks.await(db.collection("events").document(TEST_EVENT_ID).set(eventData));

        // adding a pending attendee to serve as the replacement pool
        Map<String, Object> pendingAttendee = new HashMap<>();
        pendingAttendee.put("name", "Waiting User");
        pendingAttendee.put("email", "waiting@test.com");
        pendingAttendee.put("userId", "replacement-uid-pending");
        pendingAttendee.put("status", "pending");
        Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("replacement-uid-pending").set(pendingAttendee));

        // adding a selected attendee who would be replaced
        Map<String, Object> selectedAttendee = new HashMap<>();
        selectedAttendee.put("name", "Selected User");
        selectedAttendee.put("email", "selected@test.com");
        selectedAttendee.put("userId", "replacement-uid-selected");
        selectedAttendee.put("status", "selected");
        Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("replacement-uid-selected").set(selectedAttendee));
    }

    // launching viewAttendee in "selected" mode for the test event
    private ActivityScenario<viewAttendee> launchSelectedView() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), viewAttendee.class);
        intent.putExtra("EVENT_ID", TEST_EVENT_ID);
        intent.putExtra("type", "selected");
        return ActivityScenario.launch(intent);
    }

    // verifying the "Draw Replacement" button is visible in the selected view US 02.05.03
    @Test
    public void selectedView_showsDrawReplacementButton() {
        try (ActivityScenario<viewAttendee> scenario = launchSelectedView()) {
            Thread.sleep(2000);
            onView(withId(R.id.draw_replacement_button)).check(matches(isDisplayed()));
            onView(withId(R.id.draw_replacement_button)).check(matches(withText("Draw Replacement")));
        } catch (InterruptedException e) {}
    }

    // verifying the draw replacement button is hidden in waiting view US 02.05.03
    @Test
    public void waitingView_hidesDrawReplacementButton() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), viewAttendee.class);
        intent.putExtra("EVENT_ID", TEST_EVENT_ID);
        intent.putExtra("type", "waiting");
        try (ActivityScenario<viewAttendee> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(2000);
            onView(withId(R.id.draw_replacement_button)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        } catch (InterruptedException e) {}
    }

    // verifying clicking draw replacement changes a pending entrant to selected in Firestore US 02.05.03
    @Test
    public void drawReplacement_selectsPendingEntrant() throws Exception {
        try (ActivityScenario<viewAttendee> scenario = launchSelectedView()) {
            Thread.sleep(2000);
            onView(withId(R.id.draw_replacement_button)).perform(click());
            Thread.sleep(3000);

            // checking Firestore to verify the pending entrant was promoted to selected
            com.google.firebase.firestore.DocumentSnapshot doc = Tasks.await(
                    db.collection("events").document(TEST_EVENT_ID)
                            .collection("attendees").document("replacement-uid-pending").get()
            );
            assertEquals("selected", doc.getString("status"));
        }
    }

    // cleaning up test event and attendee data from Firestore
    @After
    public void tearDown() throws Exception {
        db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("replacement-uid-pending").delete();
        db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("replacement-uid-selected").delete();
        db.collection("events").document(TEST_EVENT_ID).delete();

        // cleaning up any notifications created
        com.google.firebase.firestore.QuerySnapshot notifs = Tasks.await(
                db.collection("notifications").whereEqualTo("eventId", TEST_EVENT_ID).get()
        );
        for (QueryDocumentSnapshot doc : notifs) {
            doc.getReference().delete();
        }
    }
}
