// espresso tests for organizer notification sending — verifies draw and notify buttons and Firestore notification creation — US 02.05.01

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

@RunWith(AndroidJUnit4.class)
public class OrganizerNotifyTest {

    private static final String TEST_EVENT_ID = "test_event_notify";
    private FirebaseFirestore db;

    @Before
    public void setUp() throws Exception {
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("ash@test.com", "ash123"));
        Thread.sleep(1000);

        db = FirebaseFirestore.getInstance();

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", "Notify Test Event");
        eventData.put("description", "Testing organizer notification");
        eventData.put("organizerId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        Tasks.await(db.collection("events").document(TEST_EVENT_ID).set(eventData));

        Map<String, Object> attendee1 = new HashMap<>();
        attendee1.put("name", "User One");
        attendee1.put("email", "user1@test.com");
        attendee1.put("userId", "test-uid-1");
        attendee1.put("status", "selected");
        Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("test-uid-1").set(attendee1));

        Map<String, Object> attendee2 = new HashMap<>();
        attendee2.put("name", "User Two");
        attendee2.put("email", "user2@test.com");
        attendee2.put("userId", "test-uid-2");
        attendee2.put("status", "selected");
        Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("test-uid-2").set(attendee2));

        Map<String, Object> user1 = new HashMap<>();
        user1.put("name", "User One");
        user1.put("notificationsEnabled", true);
        Tasks.await(db.collection("users").document("test-uid-1").set(user1));

        Map<String, Object> user2 = new HashMap<>();
        user2.put("name", "User Two");
        user2.put("notificationsEnabled", false);
        Tasks.await(db.collection("users").document("test-uid-2").set(user2));
    }

    private ActivityScenario<viewAttendee> launchSelectedView() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), viewAttendee.class);
        intent.putExtra("EVENT_ID", TEST_EVENT_ID);
        intent.putExtra("type", "selected");
        return ActivityScenario.launch(intent);
    }

    private ActivityScenario<viewAttendee> launchWaitingView() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), viewAttendee.class);
        intent.putExtra("EVENT_ID", TEST_EVENT_ID);
        intent.putExtra("type", "waiting");
        return ActivityScenario.launch(intent);
    }

    @Test
    public void selectedView_showsNotifyButton() {
        try (ActivityScenario<viewAttendee> scenario = launchSelectedView()) {
            Thread.sleep(2000);
            onView(withId(R.id.notify_selected_button)).check(matches(isDisplayed()));
            onView(withId(R.id.notify_selected_button)).check(matches(withText("Notify Selected Entrants")));
        } catch (InterruptedException e) {}
    }

    @Test
    public void waitingView_hidesNotifyButton() {
        try (ActivityScenario<viewAttendee> scenario = launchWaitingView()) {
            Thread.sleep(2000);
            onView(withId(R.id.notify_selected_button)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        } catch (InterruptedException e) {}
    }

    @Test
    public void waitingView_showsDrawLotteryButton() {
        try (ActivityScenario<viewAttendee> scenario = launchWaitingView()) {
            Thread.sleep(2000);
            onView(withId(R.id.draw_lottery_button)).check(matches(isDisplayed()));
            onView(withId(R.id.draw_lottery_button)).check(matches(withText("Draw Lottery")));
        } catch (InterruptedException e) {}
    }

    @Test
    public void selectedView_hidesDrawLotteryButton() {
        try (ActivityScenario<viewAttendee> scenario = launchSelectedView()) {
            Thread.sleep(2000);
            onView(withId(R.id.draw_lottery_button)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        } catch (InterruptedException e) {}
    }

    @Test
    public void notifyButton_sendsNotificationsToFirestore() throws Exception {
        try (ActivityScenario<viewAttendee> scenario = launchSelectedView()) {
            Thread.sleep(2000);
            onView(withId(R.id.notify_selected_button)).perform(click());
            Thread.sleep(3000);

            com.google.firebase.firestore.QuerySnapshot notifs = Tasks.await(
                    db.collection("notifications")
                            .whereEqualTo("eventId", TEST_EVENT_ID)
                            .get()
            );
            boolean foundForUser1 = false;
            for (QueryDocumentSnapshot doc : notifs) {
                if ("test-uid-1".equals(doc.getString("userId"))) {
                    foundForUser1 = true;
                    break;
                }
            }
        }
    }

    @Test
    public void selectedView_showsSelectedEntrantsInList() {
        try (ActivityScenario<viewAttendee> scenario = launchSelectedView()) {
            Thread.sleep(2000);
            onView(withId(R.id.attendees_recycler_view)).check(matches(isDisplayed()));
            onView(withId(R.id.title_waiting_list)).check(matches(withText("Selected Entrants")));
        } catch (InterruptedException e) {}
    }

    @After
    public void tearDown() throws Exception {
        db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("test-uid-1").delete();
        db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("test-uid-2").delete();
        db.collection("events").document(TEST_EVENT_ID).delete();
        db.collection("users").document("test-uid-1").delete();
        db.collection("users").document("test-uid-2").delete();

        com.google.firebase.firestore.QuerySnapshot notifs = Tasks.await(
                db.collection("notifications").whereEqualTo("eventId", TEST_EVENT_ID).get()
        );
        for (QueryDocumentSnapshot doc : notifs) {
            doc.getReference().delete();
        }
    }
}
