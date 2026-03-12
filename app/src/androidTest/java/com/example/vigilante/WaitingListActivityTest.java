// tests for the waiting list screen — US 02.02.01 organizer views list of entrants

package com.example.vigilante;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class WaitingListActivityTest {

    // helper: building an intent with an event ID for the waiting list screen
    private Intent intentWithEventId(String eventId) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), WaitingListActivity.class);
        intent.putExtra("event_id", eventId);
        return intent;
    }

    @Test
    public void pageTitle_showsWaitingList() {
        try (ActivityScenario<WaitingListActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.pageTitle)).check(matches(withText("Waiting List")));
        }
    }

    @Test
    public void eventLabel_showsCorrectEventId() {
        // verifying that the event label displays the event ID passed via intent
        try (ActivityScenario<WaitingListActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.waitingListEventLabel)).check(matches(withText("Event: event_a")));
        }
    }

    @Test
    public void differentEventId_showsCorrectLabel() {
        // verifying that a different event ID is correctly displayed in the label
        try (ActivityScenario<WaitingListActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_xyz"))) {
            onView(withId(R.id.waitingListEventLabel)).check(matches(withText("Event: event_xyz")));
        }
    }

    @Test
    public void entrantCount_isDisplayed() {
        try (ActivityScenario<WaitingListActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.entrantCount)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void recyclerView_isDisplayed() {
        try (ActivityScenario<WaitingListActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.waitingListRecyclerView)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void backButton_isDisplayed() {
        try (ActivityScenario<WaitingListActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.backButton)).check(matches(isDisplayed()));
        }
    }
}
