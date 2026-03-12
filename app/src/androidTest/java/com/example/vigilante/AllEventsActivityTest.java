// tests for the all events screen — US 01.01.03 entrant sees list of events to join

package com.example.vigilante;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class AllEventsActivityTest {

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
