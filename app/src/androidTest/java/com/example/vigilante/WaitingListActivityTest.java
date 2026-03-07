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

    private Intent intentWithEventId(String eventId) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), WaitingListActivity.class);
        intent.putExtra("event_id", eventId);
        return intent;
    }

    @Test
    public void pageTitle_isDisplayed() {
        try (ActivityScenario<WaitingListActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.pageTitle)).check(matches(withText("Waiting List")));
        }
    }

    @Test
    public void eventLabel_showsEventId() {
        try (ActivityScenario<WaitingListActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.waitingListEventLabel)).check(matches(withText("Event: event_a")));
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
    public void entrantCount_showsFiveEntrants() {
        try (ActivityScenario<WaitingListActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.entrantCount)).check(matches(withText("5 entrants on waiting list")));
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

    @Test
    public void differentEventIds_showCorrectLabel() {
        try (ActivityScenario<WaitingListActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_b"))) {
            onView(withId(R.id.waitingListEventLabel)).check(matches(withText("Event: event_b")));
        }
    }
}
