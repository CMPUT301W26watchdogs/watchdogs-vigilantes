// espresso tests for accept and decline invitation flow — verifies buttons appear and status updates — US 01.05.01

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
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility;
import static org.hamcrest.CoreMatchers.containsString;

@RunWith(AndroidJUnit4.class)
public class AcceptDeclineInvitationTest {

    private static final String TEST_EVENT_ID = "test_event_accept_decline";
    private FirebaseFirestore db;
    private String userId;

    @Before
    public void setUp() throws Exception {
        // signing in with test account and creating test event in Firestore
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("ash@test.com", "ash123"));
        Thread.sleep(1000);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // creating a test event document in Firestore
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", "Test Accept Decline Event");
        eventData.put("description", "Test event for accept/decline");
        eventData.put("registrationStart", "Jan 1");
        eventData.put("registrationEnd", "Mar 1");
        eventData.put("waitingListLimit", 20);
        Tasks.await(db.collection("events").document(TEST_EVENT_ID).set(eventData));
    }

    private ActivityScenario<EventDetailActivity> launchEventDetail() {
        // launching EventDetailActivity with the test event ID
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventDetailActivity.class);
        intent.putExtra("event_id", TEST_EVENT_ID);
        return ActivityScenario.launch(intent);
    }

    @Test
    public void selectedUser_seesAcceptAndDeclineButtons() throws Exception {
        // creating a "selected" attendee and verifying accept/decline buttons appear — US 01.05.01
        Map<String, Object> attendeeData = new HashMap<>();
        attendeeData.put("name", "Bash");
        attendeeData.put("email", "ash@test.com");
        attendeeData.put("userId", userId);
        attendeeData.put("status", "selected");
        Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document(userId).set(attendeeData));

        try (ActivityScenario<EventDetailActivity> scenario = launchEventDetail()) {
            Thread.sleep(3000);
            onView(withId(R.id.acceptButton)).check(matches(isDisplayed()));
            onView(withId(R.id.declineButton)).check(matches(isDisplayed()));
            onView(withId(R.id.signUpStatus)).check(matches(withText(containsString("selected"))));
        }
    }

    @Test
    public void pendingUser_seesSignUpButtonOnly() throws Exception {
        // creating a "pending" attendee and verifying only the Sign Up button shows — US 01.05.01
        Map<String, Object> attendeeData = new HashMap<>();
        attendeeData.put("name", "Bash");
        attendeeData.put("email", "ash@test.com");
        attendeeData.put("userId", userId);
        attendeeData.put("status", "pending");
        Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document(userId).set(attendeeData));

        try (ActivityScenario<EventDetailActivity> scenario = launchEventDetail()) {
            Thread.sleep(3000);
            onView(withId(R.id.registerButton)).check(matches(isDisplayed()));
            onView(withId(R.id.registerButton)).check(matches(withText("Cancel SignUp")));
            onView(withId(R.id.acceptButton)).check(matches(withEffectiveVisibility(Visibility.GONE)));
            onView(withId(R.id.declineButton)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        }
    }

    @Test
    public void acceptedUser_seesEnrolledState() throws Exception {
        // creating an "accepted" attendee and verifying enrolled state — US 01.05.01
        Map<String, Object> attendeeData = new HashMap<>();
        attendeeData.put("name", "Bash");
        attendeeData.put("email", "ash@test.com");
        attendeeData.put("userId", userId);
        attendeeData.put("status", "accepted");
        Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document(userId).set(attendeeData));

        try (ActivityScenario<EventDetailActivity> scenario = launchEventDetail()) {
            Thread.sleep(3000);
            onView(withId(R.id.registerButton)).check(matches(isDisplayed()));
            onView(withId(R.id.registerButton)).check(matches(withText("Enrolled")));
            onView(withId(R.id.signUpStatus)).check(matches(withText(containsString("Accepted"))));
        }
    }

    @Test
    public void declinedUser_canReSignUp() throws Exception {
        // creating a "declined" attendee and verifying re-signup option — US 01.05.01
        Map<String, Object> attendeeData = new HashMap<>();
        attendeeData.put("name", "Bash");
        attendeeData.put("email", "ash@test.com");
        attendeeData.put("userId", userId);
        attendeeData.put("status", "declined");
        Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document(userId).set(attendeeData));

        try (ActivityScenario<EventDetailActivity> scenario = launchEventDetail()) {
            Thread.sleep(3000);
            onView(withId(R.id.registerButton)).check(matches(isDisplayed()));
            onView(withId(R.id.registerButton)).check(matches(withText("Sign Up")));
        }
    }

    @After
    public void tearDown() throws Exception {
        // cleaning up test event and attendee data from Firestore
        db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document(userId).delete();
        db.collection("events").document(TEST_EVENT_ID).delete();
    }
}
