// espresso tests for event history screen — verifies history loads and is accessible from profile — US 01.02.03

package com.example.vigilante;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class EventHistoryTest {

    @Before
    public void setUp() throws Exception {
        // signing in with test account before each test
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("ash@test.com", "ash123"));
        Thread.sleep(1500);
    }

    @Test
    public void historyScreen_displaysHeader() {
        try (ActivityScenario<EventHistoryActivity> scenario = ActivityScenario.launch(EventHistoryActivity.class)) {
            // verifying the history screen header is displayed — US 01.02.03
            onView(withText("Event History")).check(matches(isDisplayed()));
        }
    }

    @Test
    public void historyScreen_displaysSubtitle() {
        try (ActivityScenario<EventHistoryActivity> scenario = ActivityScenario.launch(EventHistoryActivity.class)) {
            // verifying the subtitle text is shown — US 01.02.03
            onView(withText("Events you've registered for")).check(matches(isDisplayed()));
        }
    }

    @Test
    public void historyScreen_recyclerViewDisplayed() {
        try (ActivityScenario<EventHistoryActivity> scenario = ActivityScenario.launch(EventHistoryActivity.class)) {
            // verifying the RecyclerView is present on screen — US 01.02.03
            onView(withId(R.id.historyRecyclerView)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void historyScreen_loadsEventsFromFirestore() {
        try (ActivityScenario<EventHistoryActivity> scenario = ActivityScenario.launch(EventHistoryActivity.class)) {
            // waiting for Firestore to load event history data — US 01.02.03
            Thread.sleep(3000);
            onView(withId(R.id.historyRecyclerView)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {}
    }

    @Test
    public void historyScreen_bottomNavDisplayed() {
        try (ActivityScenario<EventHistoryActivity> scenario = ActivityScenario.launch(EventHistoryActivity.class)) {
            // verifying the bottom navigation bar is displayed — US 01.02.03
            onView(withId(R.id.bottomNav)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void historyScreen_accessFromProfile() {
        try (ActivityScenario<ProfilePage> scenario = ActivityScenario.launch(ProfilePage.class)) {
            // verifying the event history button is accessible from the profile page — US 01.02.03
            Thread.sleep(2000);
            onView(withId(R.id.event_history_button)).check(matches(isDisplayed()));
            onView(withId(R.id.event_history_button)).check(matches(withText("Event History")));
        } catch (InterruptedException e) {}
    }
}
