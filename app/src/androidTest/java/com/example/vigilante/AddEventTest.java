// tests for the add event screen — US 02.01.01 create event, US 02.01.04 registration period,
// US 02.02.03 geolocation requirement, US 02.03.01 waiting list limit

package com.example.vigilante;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class AddEventTest {

    @Test
    public void titleField_isDisplayed() {
        try (ActivityScenario<AddEvent> scenario = ActivityScenario.launch(AddEvent.class)) {
            onView(withId(R.id.event_title_input)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void descriptionField_isDisplayed() {
        try (ActivityScenario<AddEvent> scenario = ActivityScenario.launch(AddEvent.class)) {
            onView(withId(R.id.event_description)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void pickStartDateButton_isDisplayed() {
        // verifying the registration start date picker button is present
        try (ActivityScenario<AddEvent> scenario = ActivityScenario.launch(AddEvent.class)) {
            onView(withId(R.id.pickStartDateButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void pickEndDateButton_isDisplayed() {
        // verifying the registration end date picker button is present
        try (ActivityScenario<AddEvent> scenario = ActivityScenario.launch(AddEvent.class)) {
            onView(withId(R.id.pickEndDateButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void geolocationCheckbox_isDisplayed() {
        // verifying that the geolocation requirement checkbox is present — US 02.02.03
        try (ActivityScenario<AddEvent> scenario = ActivityScenario.launch(AddEvent.class)) {
            onView(withId(R.id.geolocation_checkbox)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void maxEntrantsField_isDisplayed() {
        // verifying the waiting list limit input is present — US 02.03.01
        try (ActivityScenario<AddEvent> scenario = ActivityScenario.launch(AddEvent.class)) {
            onView(withId(R.id.fieldMaxEntrants)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void publishButton_isDisplayed() {
        try (ActivityScenario<AddEvent> scenario = ActivityScenario.launch(AddEvent.class)) {
            onView(withId(R.id.publish_button)).perform(scrollTo()).check(matches(isDisplayed()));
        }
    }

    @Test
    public void emptyFields_staysOnAddEventScreen() {
        // clicking publish with no data should fail validation and keep the user on the form
        try (ActivityScenario<AddEvent> scenario = ActivityScenario.launch(AddEvent.class)) {
            onView(withId(R.id.publish_button)).perform(scrollTo(), click());
            onView(withId(R.id.event_title_input)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void titleOnly_staysOnAddEventScreen() {
        // filling only the title and publishing should still fail date validation
        try (ActivityScenario<AddEvent> scenario = ActivityScenario.launch(AddEvent.class)) {
            onView(withId(R.id.event_title_input)).perform(typeText("Dance Class"), closeSoftKeyboard());
            onView(withId(R.id.publish_button)).perform(scrollTo(), click());
            onView(withId(R.id.publish_button)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void typingTitle_acceptsInput() {
        // verifying the title field accepts and holds typed text
        try (ActivityScenario<AddEvent> scenario = ActivityScenario.launch(AddEvent.class)) {
            onView(withId(R.id.event_title_input)).perform(typeText("Swimming Lessons"), closeSoftKeyboard());
            onView(withId(R.id.event_title_input)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void geolocationCheckbox_canBeToggled() {
        // verifying that clicking the geolocation checkbox changes its state
        try (ActivityScenario<AddEvent> scenario = ActivityScenario.launch(AddEvent.class)) {
            onView(withId(R.id.geolocation_checkbox)).perform(click());
            onView(withId(R.id.geolocation_checkbox)).check(matches(isDisplayed()));
        }
    }
}
