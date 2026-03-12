// tests for the entrant map screen — US 02.02.02 organizer sees where entrants joined from

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
public class EntrantMapActivityTest {

    // helper: building an intent with an event ID for the map screen
    private Intent intentWithEventId(String eventId) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EntrantMapActivity.class);
        intent.putExtra("event_id", eventId);
        return intent;
    }

    @Test
    public void pageTitle_isDisplayed() {
        try (ActivityScenario<EntrantMapActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.mapTitle)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void pageTitle_showsEntrantLocations() {
        try (ActivityScenario<EntrantMapActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.mapTitle)).check(matches(withText("Entrant Locations")));
        }
    }

    @Test
    public void mapFragment_isDisplayed() {
        // verifying the Google Maps fragment container is present in the layout
        try (ActivityScenario<EntrantMapActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.mapFragment)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void backButton_isDisplayed() {
        try (ActivityScenario<EntrantMapActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.backButton)).check(matches(isDisplayed()));
        }
    }
}
