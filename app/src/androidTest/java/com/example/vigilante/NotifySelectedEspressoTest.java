// espresso tests for notify selected entrants button — verifies button visibility and Firestore notification creation — US 02.07.02

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
import static androidx.test.espresso.matcher.ViewMatchers.Visibility;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class NotifySelectedEspressoTest {

    private static final String TEST_EVENT_ID = "test_event_notify_selected";
    private FirebaseFirestore db;

    @Before
    // signing in and creating test event with a selected attendee
    public void setUp() throws Exception {
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("ash@test.com", "ash123"));
        Thread.sleep(1000);

        db = FirebaseFirestore.getInstance();

        // creating a test event
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", "Notify Selected Test");
        eventData.put("organizerId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        Tasks.await(db.collection("events").document(TEST_EVENT_ID).set(eventData));

        // adding a selected attendee
        Map<String, Object> selectedAttendee = new HashMap<>();
        selectedAttendee.put("name", "Selected Entrant");
        selectedAttendee.put("email", "selected@test.com");
        selectedAttendee.put("userId", "notify-sel-uid-1");
        selectedAttendee.put("status", "selected");
        Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("notify-sel-uid-1").set(selectedAttendee));
    }

    // launching viewAttendee in "selected" mode for the test event
    private ActivityScenario<viewAttendee> launchSelectedView() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), viewAttendee.class);
        intent.putExtra("EVENT_ID", TEST_EVENT_ID);
        intent.putExtra("type", "selected");
        return ActivityScenario.launch(intent);
    }

    @Test
    // verifying the notify selected button is visible in selected view — US 02.07.02
    public void selectedView_showsNotifySelectedButton() {
        try (ActivityScenario<viewAttendee> scenario = launchSelectedView()) {
            Thread.sleep(2000);
            onView(withId(R.id.notify_selected_button)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {}
    }

    @Test
    // verifying notify selected button is hidden in waiting view — US 02.07.02
    public void waitingView_hidesNotifySelectedButton() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), viewAttendee.class);
        intent.putExtra("EVENT_ID", TEST_EVENT_ID);
        intent.putExtra("type", "waiting");
        try (ActivityScenario<viewAttendee> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(2000);
            onView(withId(R.id.notify_selected_button)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        } catch (InterruptedException e) {}
    }

    @Test
    // verifying clicking notify selected creates a notification in Firestore — US 02.07.02
    public void notifySelected_createsNotificationInFirestore() throws Exception {
        try (ActivityScenario<viewAttendee> scenario = launchSelectedView()) {
            Thread.sleep(2000);
            // clicking the notify selected button
            onView(withId(R.id.notify_selected_button)).perform(click());
            Thread.sleep(3000);

            // verifying a notification was created in Firestore for the selected attendee
            com.google.firebase.firestore.QuerySnapshot notifs = Tasks.await(
                    db.collection("notifications")
                            .whereEqualTo("eventId", TEST_EVENT_ID)
                            .whereEqualTo("userId", "notify-sel-uid-1")
                            .get()
            );
            assertTrue(notifs.size() > 0);
        }
    }

    @After
    // cleaning up test event, attendees, and notifications from Firestore
    public void tearDown() throws Exception {
        db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("notify-sel-uid-1").delete();
        db.collection("events").document(TEST_EVENT_ID).delete();

        // cleaning up notifications
        com.google.firebase.firestore.QuerySnapshot notifs = Tasks.await(
                db.collection("notifications").whereEqualTo("eventId", TEST_EVENT_ID).get()
        );
        for (QueryDocumentSnapshot doc : notifs) {
            doc.getReference().delete();
        }
    }
}
