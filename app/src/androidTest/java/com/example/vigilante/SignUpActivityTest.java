package com.example.vigilante;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class SignUpActivityTest {

    private Intent intentWithEventId(String eventId) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventDetailActivity.class);
        intent.putExtra("event_id", eventId);
        return intent;
    }

    @Test
    public void signUpButton_isDisplayed() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.registerButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void signUpButton_showsCorrectText() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.registerButton)).check(matches(withText("Sign Up")));
        }
    }

    @Test
    public void signUpStatus_isDisplayed() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.signUpStatus)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void eventDetails_areDisplayed() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.eventTitle)).check(matches(isDisplayed()));
            onView(withId(R.id.eventDescription)).check(matches(isDisplayed()));
            onView(withId(R.id.eventDate)).check(matches(isDisplayed()));
            onView(withId(R.id.eventLocation)).check(matches(isDisplayed()));
            onView(withId(R.id.eventCapacity)).check(matches(isDisplayed()));
            onView(withId(R.id.eventPrice)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void lotteryInfoButton_isDisplayed() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.lotteryInfoButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void backButton_isDisplayed() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.backButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void noEventId_showsNoEventMessage() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventDetailActivity.class);
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventTitle)).check(matches(withText("No Event ID")));
        }
    }

    @Test
    public void signUpButton_isClickable() {
        try (ActivityScenario<EventDetailActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.registerButton)).check(matches(isEnabled()));
        }
    }
}
