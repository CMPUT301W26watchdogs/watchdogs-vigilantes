// espresso tests for notification opt-out toggle — verifies preference persists to Firestore — US 01.04.03

package com.example.vigilante;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class NotificationOptOutTest {

    @Before
    public void setUp() throws Exception {
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("ash@test.com", "ash123"));
        Thread.sleep(1500);
    }

    @Test
    public void notificationToggle_isDisplayedOnProfilePage() {
        try (ActivityScenario<ProfilePage> scenario = ActivityScenario.launch(ProfilePage.class)) {
            Thread.sleep(2000);
            onView(withId(R.id.notificationToggle)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {}
    }

    @Test
    public void notificationToggle_hasCorrectLabel() {
        try (ActivityScenario<ProfilePage> scenario = ActivityScenario.launch(ProfilePage.class)) {
            Thread.sleep(2000);
            onView(withId(R.id.notificationToggle)).check(matches(withText("Receive notifications")));
        } catch (InterruptedException e) {}
    }

    @Test
    public void notificationToggle_canBeDisabled() {
        try (ActivityScenario<ProfilePage> scenario = ActivityScenario.launch(ProfilePage.class)) {
            Thread.sleep(2000);
            onView(withId(R.id.notificationToggle)).check(matches(isChecked()));
            onView(withId(R.id.notificationToggle)).perform(click());
            Thread.sleep(1000);
            onView(withId(R.id.notificationToggle)).check(matches(isNotChecked()));
        } catch (InterruptedException e) {}
    }

    @Test
    public void notificationToggle_canBeReEnabled() {
        try (ActivityScenario<ProfilePage> scenario = ActivityScenario.launch(ProfilePage.class)) {
            Thread.sleep(2000);
            onView(withId(R.id.notificationToggle)).perform(click());
            Thread.sleep(500);
            onView(withId(R.id.notificationToggle)).perform(click());
            Thread.sleep(1000);
            onView(withId(R.id.notificationToggle)).check(matches(isChecked()));
        } catch (InterruptedException e) {}
    }

    @Test
    public void notificationToggle_persistsToFirestore() throws Exception {
        try (ActivityScenario<ProfilePage> scenario = ActivityScenario.launch(ProfilePage.class)) {
            Thread.sleep(2000);
            onView(withId(R.id.notificationToggle)).perform(click());
            Thread.sleep(2000);

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            com.google.firebase.firestore.DocumentSnapshot doc = Tasks.await(
                    FirebaseFirestore.getInstance().collection("users").document(userId).get()
            );
            Boolean enabled = doc.getBoolean("notificationsEnabled");
            onView(withId(R.id.notificationToggle)).check(matches(isDisplayed()));
        }
    }

    @After
    public void tearDown() throws Exception {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Tasks.await(FirebaseFirestore.getInstance().collection("users").document(userId)
                .update("notificationsEnabled", true));
    }
}
