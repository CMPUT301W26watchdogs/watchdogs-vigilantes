// tests for the selected entrants screen — US 02.06.01 organizer views list of chosen entrants

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
public class SelectedEntrantsActivityTest {

    @Test
    public void pageTitle_isDisplayed() {
        // verifying the screen title "Selected Entrants" is visible — US 02.06.01
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SelectedEntrantsActivity.class);
        try (ActivityScenario<SelectedEntrantsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.pageTitle)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void pageTitle_showsCorrectText() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SelectedEntrantsActivity.class);
        try (ActivityScenario<SelectedEntrantsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.pageTitle)).check(matches(withText("Selected Entrants")));
        }
    }

    @Test
    public void recyclerView_isDisplayed() {
        // verifying the selected entrants list container is present on screen
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SelectedEntrantsActivity.class);
        try (ActivityScenario<SelectedEntrantsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.selectedRecyclerView)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void selectedCount_isDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SelectedEntrantsActivity.class);
        try (ActivityScenario<SelectedEntrantsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.selectedCount)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void backButton_isDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SelectedEntrantsActivity.class);
        try (ActivityScenario<SelectedEntrantsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.backButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void withEventId_eventLabelShowsId() {
        // verifying the event ID passed via intent appears in the event label — data flow test
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SelectedEntrantsActivity.class);
        intent.putExtra("event_id", "event-xyz");
        try (ActivityScenario<SelectedEntrantsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.selectedEventLabel)).check(matches(withText("Event: event-xyz")));
        }
    }

    @Test
    public void withoutEventId_showsNoEventIdText() {
        // verifying fallback label when no event ID is passed
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SelectedEntrantsActivity.class);
        try (ActivityScenario<SelectedEntrantsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.selectedEventLabel)).check(matches(withText("Event: Unknown")));
        }
    }

    @Test
    public void differentEventIds_showDifferentLabels() {
        // verifying that data actually flows from the intent into the UI (not hardcoded)
        Intent intentA = new Intent(ApplicationProvider.getApplicationContext(), SelectedEntrantsActivity.class);
        intentA.putExtra("event_id", "event-aaa");
        try (ActivityScenario<SelectedEntrantsActivity> scenarioA = ActivityScenario.launch(intentA)) {
            onView(withId(R.id.selectedEventLabel)).check(matches(withText("Event: event-aaa")));
        }

        Intent intentB = new Intent(ApplicationProvider.getApplicationContext(), SelectedEntrantsActivity.class);
        intentB.putExtra("event_id", "event-bbb");
        try (ActivityScenario<SelectedEntrantsActivity> scenarioB = ActivityScenario.launch(intentB)) {
            onView(withId(R.id.selectedEventLabel)).check(matches(withText("Event: event-bbb")));
        }
    }
}
