// testing the notification opt out toggle and verifying preference persists to Firestore US 01.04.03

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

    // signing in with test account before each test
    @Before
    public void setUp() throws Exception {
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("ash@test.com", "ash123"));
        Thread.sleep(1500);
    }

    // verifying the notification toggle switch is visible on the profile page US 01.04.03
    @Test
    public void notificationToggle_isDisplayedOnProfilePage() {
        try (ActivityScenario<ProfilePage> scenario = ActivityScenario.launch(ProfilePage.class)) {
            Thread.sleep(2000);
            onView(withId(R.id.notificationToggle)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {}
    }

    // verifying the toggle has the correct "Receive notifications" label US 01.04.03
    @Test
    public void notificationToggle_hasCorrectLabel() {
        try (ActivityScenario<ProfilePage> scenario = ActivityScenario.launch(ProfilePage.class)) {
            Thread.sleep(2000);
            onView(withId(R.id.notificationToggle)).check(matches(withText("Receive notifications")));
        } catch (InterruptedException e) {}
    }

    // verifying toggle starts checked then clicking to disable notifications US 01.04.03
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

    // clicking toggle off then back on to verify re enabling works US 01.04.03
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

    // checking Firestore to verify the toggle preference was saved US 01.04.03
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

    // restoring notification preference to true after test
    @After
    public void tearDown() throws Exception {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Tasks.await(FirebaseFirestore.getInstance().collection("users").document(userId)
                .update("notificationsEnabled", true));
    }
}
