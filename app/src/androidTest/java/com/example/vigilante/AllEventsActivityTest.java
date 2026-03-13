// tests for the all events screen — US 01.01.03 entrant sees list of events to join

package com.example.vigilante;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import com.google.firebase.auth.FirebaseAuth;

@RunWith(AndroidJUnit4.class)
public class AllEventsActivityTest {

    @Before
    public void setUp() throws InterruptedException {
        // Sign in with a test account before any test runs
        // If you're using real Firebase, use a real test email/password
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            mAuth.signInWithEmailAndPassword("ash@test.com", "ash123");

            // Testing is fast; Firebase is slow. We need to wait for the login to finish.
            Thread.sleep(2000);
        }
    }

    @Test
    public void header_isDisplayed() {
        try (ActivityScenario<AllEventsActivity> scenario = ActivityScenario.launch(AllEventsActivity.class)) {
            onView(withId(R.id.all_events_header)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void recyclerView_isDisplayed() {
        // verifying that the events list container is present on the screen
        try (ActivityScenario<AllEventsActivity> scenario = ActivityScenario.launch(AllEventsActivity.class)) {
            onView(withId(R.id.all_events_recycler_view)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void backButton_isDisplayed() {
        try (ActivityScenario<AllEventsActivity> scenario = ActivityScenario.launch(AllEventsActivity.class)) {
            onView(withId(R.id.back_button)).check(matches(isDisplayed()));
        }
    }
}
