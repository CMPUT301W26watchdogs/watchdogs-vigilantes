package com.example.vigilante;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class CreateEventActivityTest {

    @Test
    public void titleField_isDisplayed() {
        try (ActivityScenario<CreateEventActivity> scenario =
                     ActivityScenario.launch(CreateEventActivity.class)) {
            onView(withId(R.id.fieldTitle)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void descriptionField_isDisplayed() {
        try (ActivityScenario<CreateEventActivity> scenario =
                     ActivityScenario.launch(CreateEventActivity.class)) {
            onView(withId(R.id.fieldDescription)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void capacityField_isDisplayed() {
        try (ActivityScenario<CreateEventActivity> scenario =
                     ActivityScenario.launch(CreateEventActivity.class)) {
            onView(withId(R.id.fieldCapacity)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void createButton_isDisplayed() {
        try (ActivityScenario<CreateEventActivity> scenario =
                     ActivityScenario.launch(CreateEventActivity.class)) {
            onView(withId(R.id.createEventButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void submitWithEmptyFields_staysOnForm() {
        try (ActivityScenario<CreateEventActivity> scenario =
                     ActivityScenario.launch(CreateEventActivity.class)) {
            onView(withId(R.id.createEventButton)).perform(click());
            onView(withId(R.id.fieldTitle)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void cancelButton_isDisplayed() {
        try (ActivityScenario<CreateEventActivity> scenario =
                     ActivityScenario.launch(CreateEventActivity.class)) {
            onView(withId(R.id.cancelButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void typeInTitleField_updatesText() {
        try (ActivityScenario<CreateEventActivity> scenario =
                     ActivityScenario.launch(CreateEventActivity.class)) {
            onView(withId(R.id.fieldTitle)).perform(typeText("Dance Class"));
            onView(withId(R.id.fieldTitle)).check(matches(isDisplayed()));
        }
    }
}
