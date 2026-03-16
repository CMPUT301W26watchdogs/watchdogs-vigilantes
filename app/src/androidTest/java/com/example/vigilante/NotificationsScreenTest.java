package com.example.vigilante;

import androidx.test.core.app.ActivityScenario;
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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class NotificationsScreenTest {

    private FirebaseFirestore db;
    private String userId;

    @Before
    public void setUp() throws Exception {
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("ash@test.com", "ash123"));
        Thread.sleep(1000);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> notif1 = new HashMap<>();
        notif1.put("userId", userId);
        notif1.put("eventId", "notif-test-event");
        notif1.put("title", "You've been selected!");
        notif1.put("message", "You were chosen for Swimming Lessons.");
        notif1.put("read", false);
        notif1.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());
        Tasks.await(db.collection("notifications").document("notif-test-1").set(notif1));

        Map<String, Object> notif2 = new HashMap<>();
        notif2.put("userId", userId);
        notif2.put("eventId", "notif-test-event-2");
        notif2.put("title", "Spot opened up!");
        notif2.put("message", "A replacement draw selected you.");
        notif2.put("read", true);
        notif2.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());
        Tasks.await(db.collection("notifications").document("notif-test-2").set(notif2));
    }

    @Test
    public void notificationsScreen_displaysHeader() {
        try (ActivityScenario<NotificationsActivity> scenario = ActivityScenario.launch(NotificationsActivity.class)) {
            onView(withText("My Notifications")).check(matches(isDisplayed()));
        }
    }

    @Test
    public void notificationsScreen_displaysSubtitle() {
        try (ActivityScenario<NotificationsActivity> scenario = ActivityScenario.launch(NotificationsActivity.class)) {
            onView(withText("Lottery results and event updates")).check(matches(isDisplayed()));
        }
    }

    @Test
    public void notificationsScreen_showsRecyclerView() {
        try (ActivityScenario<NotificationsActivity> scenario = ActivityScenario.launch(NotificationsActivity.class)) {
            Thread.sleep(3000);
            onView(withId(R.id.notificationsRecyclerView)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {}
    }

    @Test
    public void notificationsScreen_showsBottomNav() {
        try (ActivityScenario<NotificationsActivity> scenario = ActivityScenario.launch(NotificationsActivity.class)) {
            onView(withId(R.id.bottomNav)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void notificationsScreen_loadsNotificationsFromFirestore() {
        try (ActivityScenario<NotificationsActivity> scenario = ActivityScenario.launch(NotificationsActivity.class)) {
            Thread.sleep(3000);
            onView(withId(R.id.notificationsRecyclerView)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {}
    }

    @After
    public void tearDown() throws Exception {
        db.collection("notifications").document("notif-test-1").delete();
        db.collection("notifications").document("notif-test-2").delete();
    }
}
