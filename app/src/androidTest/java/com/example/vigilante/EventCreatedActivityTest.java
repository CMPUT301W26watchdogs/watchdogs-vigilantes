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
public class EventCreatedActivityTest {

    private Intent intentWithEvent(String title) {
        Event event = new Event(
                "test-uuid-123", title, "A test description",
                "Jan 2026", "Test Venue", "30", "$20.00",
                "Dec 1 2025", "Dec 20 2025"
        );
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventCreatedActivity.class);
        intent.putExtra("event", event);
        return intent;
    }

    @Test
    public void eventTitle_isDisplayed() {
        try (ActivityScenario<EventCreatedActivity> scenario =
                     ActivityScenario.launch(intentWithEvent("Test Swimming Event"))) {
            onView(withId(R.id.createdEventTitle)).check(matches(withText("Test Swimming Event")));
        }
    }

    @Test
    public void qrCodeImage_isDisplayed() {
        try (ActivityScenario<EventCreatedActivity> scenario =
                     ActivityScenario.launch(intentWithEvent("Dance Workshop"))) {
            onView(withId(R.id.generatedQrCode)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void successLabel_isDisplayed() {
        try (ActivityScenario<EventCreatedActivity> scenario =
                     ActivityScenario.launch(intentWithEvent("Yoga Class"))) {
            onView(withId(R.id.successLabel)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void doneButton_isDisplayed() {
        try (ActivityScenario<EventCreatedActivity> scenario =
                     ActivityScenario.launch(intentWithEvent("Piano Lessons"))) {
            onView(withId(R.id.doneButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void differentEventTitles_displayCorrectly() {
        String[] titles = {"Swimming", "Dance", "Piano"};
        for (String title : titles) {
            try (ActivityScenario<EventCreatedActivity> scenario =
                         ActivityScenario.launch(intentWithEvent(title))) {
                onView(withId(R.id.createdEventTitle)).check(matches(withText(title)));
            }
        }
    }
}
