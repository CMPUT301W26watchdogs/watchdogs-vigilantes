package com.example.vigilante;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RegistrationPeriodActivityTest {

    @Test
    public void startLabel_isDisplayed() {
        try (ActivityScenario<RegistrationPeriodActivity> scenario =
                     ActivityScenario.launch(RegistrationPeriodActivity.class)) {
            onView(withId(R.id.startLabel)).check(matches(withText("Registration Opens")));
        }
    }

    @Test
    public void endLabel_isDisplayed() {
        try (ActivityScenario<RegistrationPeriodActivity> scenario =
                     ActivityScenario.launch(RegistrationPeriodActivity.class)) {
            onView(withId(R.id.endLabel)).check(matches(withText("Registration Closes")));
        }
    }

    @Test
    public void pickStartDateButton_isDisplayed() {
        try (ActivityScenario<RegistrationPeriodActivity> scenario =
                     ActivityScenario.launch(RegistrationPeriodActivity.class)) {
            onView(withId(R.id.pickStartDateButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void pickEndDateButton_isDisplayed() {
        try (ActivityScenario<RegistrationPeriodActivity> scenario =
                     ActivityScenario.launch(RegistrationPeriodActivity.class)) {
            onView(withId(R.id.pickEndDateButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void saveButton_isDisplayed() {
        try (ActivityScenario<RegistrationPeriodActivity> scenario =
                     ActivityScenario.launch(RegistrationPeriodActivity.class)) {
            onView(withId(R.id.saveButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void saveWithNoDates_staysOnScreen() {
        try (ActivityScenario<RegistrationPeriodActivity> scenario =
                     ActivityScenario.launch(RegistrationPeriodActivity.class)) {
            onView(withId(R.id.saveButton)).perform(click());
            onView(withId(R.id.saveButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void cancelButton_isDisplayed() {
        try (ActivityScenario<RegistrationPeriodActivity> scenario =
                     ActivityScenario.launch(RegistrationPeriodActivity.class)) {
            onView(withId(R.id.cancelButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void pageTitle_isDisplayed() {
        try (ActivityScenario<RegistrationPeriodActivity> scenario =
                     ActivityScenario.launch(RegistrationPeriodActivity.class)) {
            onView(withId(R.id.pageTitle)).check(matches(withText("Set Registration Period")));
        }
    }
}
