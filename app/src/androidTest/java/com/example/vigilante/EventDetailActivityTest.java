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

    private Intent intentWithEventId(String eventId) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventDetailActivity.class);
        intent.putExtra("event_id", eventId);
        return intent;
    }

    @Test
    public void eventA_showsCorrectTitle() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.eventTitle)).check(matches(withText("Beginner Swimming Lessons")));
        }
    }

    @Test
    public void eventA_showsCorrectLocation() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.eventLocation)).check(matches(withText("Downtown Community Centre Pool")));
        }
    }

    @Test
    public void eventB_showsCorrectTitle() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_b"))) {
            onView(withId(R.id.eventTitle)).check(matches(withText("Interpretive Dance Workshop")));
        }
    }

    @Test
    public void eventB_showsCorrectLocation() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_b"))) {
            onView(withId(R.id.eventLocation)).check(matches(withText("Riverside Recreation Centre")));
        }
    }

    @Test
    public void unknownEvent_showsUnknownTitle() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("unknown-uuid"))) {
            onView(withId(R.id.eventTitle)).check(matches(withText("Unknown Event")));
        }
    }

    @Test
    public void registerButton_isDisplayed() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.registerButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void backButton_isDisplayed() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.backButton)).check(matches(isDisplayed()));
        }
    }
}
