// tests for event detail screen — US 01.06.01 view event details, US 01.06.02 sign up from event details

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
public class EventDetailActivityTest {

    // helper: building an intent that passes an event ID to the activity
    private Intent intentWithEventId(String eventId) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventDetailActivity.class);
        intent.putExtra("event_id", eventId);
        return intent;
    }

    @Test
    public void eventTitle_isDisplayed() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("test-event-id"))) {
            onView(withId(R.id.eventTitle)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void eventDescription_isDisplayed() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("test-event-id"))) {
            onView(withId(R.id.eventDescription)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void eventDate_isDisplayed() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("test-event-id"))) {
            onView(withId(R.id.eventDate)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void eventLocation_isDisplayed() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("test-event-id"))) {
            onView(withId(R.id.eventLocation)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void signUpStatus_isDisplayed() {
        // verifying that the sign-up status text view is present on the screen
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("test-event-id"))) {
            onView(withId(R.id.signUpStatus)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void registerButton_isDisplayed() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("test-event-id"))) {
            onView(withId(R.id.registerButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void lotteryInfoButton_isDisplayed() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("test-event-id"))) {
            onView(withId(R.id.lotteryInfoButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void backButton_isDisplayed() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("test-event-id"))) {
            onView(withId(R.id.backButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void noEventId_showsNoEventIdTitle() {
        // passing no event ID should result in the "No Event ID" fallback being displayed
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventDetailActivity.class);
        try (ActivityScenario<EventDetailActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventTitle)).check(matches(withText("No Event ID")));
        }
    }

    @Test
    public void noEventId_showsNoEventIdDescription() {
        // verifying the description fallback text when no event ID is provided
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventDetailActivity.class);
        try (ActivityScenario<EventDetailActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventDescription)).check(matches(withText("No event ID was provided.")));
        }
    }

    @Test
    public void registerButton_showsSignUpText() {
        // the sign-up button should display "Sign Up" as its default label
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("test-event-id"))) {
            onView(withId(R.id.registerButton)).check(matches(withText("Sign Up")));
        }
    }
}
