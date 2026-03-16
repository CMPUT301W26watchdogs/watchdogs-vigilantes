// espresso tests for notify waiting list button and dialog — US 02.07.01

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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class NotifyWaitingEspressoTest {

    private static final String TEST_EVENT_ID = "test_event_notify_waiting";
    private FirebaseFirestore db;

    @Before
    // signing in and creating test event with a pending attendee
    public void setUp() throws Exception {
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("ash@test.com", "ash123"));
        Thread.sleep(1000);

        db = FirebaseFirestore.getInstance();

        // creating a test event
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", "Notify Waiting Test");
        eventData.put("organizerId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        Tasks.await(db.collection("events").document(TEST_EVENT_ID).set(eventData));

        // adding a pending attendee on the waiting list
        Map<String, Object> pendingAttendee = new HashMap<>();
        pendingAttendee.put("name", "Waiting Entrant");
        pendingAttendee.put("email", "waiting@test.com");
        pendingAttendee.put("userId", "notify-wait-uid-1");
        pendingAttendee.put("status", "pending");
        Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("notify-wait-uid-1").set(pendingAttendee));
    }

    // launching viewAttendee in "waiting" mode for the test event
    private ActivityScenario<viewAttendee> launchWaitingView() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), viewAttendee.class);
        intent.putExtra("EVENT_ID", TEST_EVENT_ID);
        intent.putExtra("type", "waiting");
        return ActivityScenario.launch(intent);
    }

    @Test
    // verifying the notify waiting list button is visible in waiting view — US 02.07.01
    public void waitingView_showsNotifyButton() {
        try (ActivityScenario<viewAttendee> scenario = launchWaitingView()) {
            Thread.sleep(2000);
            onView(withId(R.id.notify_waiting_button)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {}
    }

    @Test
    // verifying notify waiting button is hidden in selected view — US 02.07.01
    public void selectedView_hidesNotifyWaitingButton() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), viewAttendee.class);
        intent.putExtra("EVENT_ID", TEST_EVENT_ID);
        intent.putExtra("type", "selected");
        try (ActivityScenario<viewAttendee> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(2000);
            onView(withId(R.id.notify_waiting_button)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        } catch (InterruptedException e) {}
    }

    @Test
    // verifying clicking notify button opens dialog and sending creates a notification in Firestore — US 02.07.01
    public void notifyWaiting_createsNotificationInFirestore() throws Exception {
        try (ActivityScenario<viewAttendee> scenario = launchWaitingView()) {
            Thread.sleep(2000);
            // clicking the notify waiting button to open the dialog
            onView(withId(R.id.notify_waiting_button)).perform(click());
            Thread.sleep(1000);

            // typing a custom message and clicking Send
            onView(withClassName("android.widget.EditText")).perform(typeText("Draw is coming up!"));
            onView(withText("Send")).perform(click());
            Thread.sleep(3000);

            // verifying a notification was created in Firestore for the pending attendee
            com.google.firebase.firestore.QuerySnapshot notifs = Tasks.await(
                    db.collection("notifications")
                            .whereEqualTo("eventId", TEST_EVENT_ID)
                            .whereEqualTo("userId", "notify-wait-uid-1")
                            .get()
            );
            assertTrue(notifs.size() > 0);
        }
    }

    // helper to match EditText in dialog by class name
    private static org.hamcrest.Matcher<android.view.View> withClassName(String className) {
        return new org.hamcrest.TypeSafeMatcher<android.view.View>() {
            @Override
            protected boolean matchesSafely(android.view.View item) {
                return item.getClass().getName().equals(className);
            }
            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("with class name: " + className);
            }
        };
    }

    @After
    // cleaning up test event, attendees, and notifications from Firestore
    public void tearDown() throws Exception {
        db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("notify-wait-uid-1").delete();
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
