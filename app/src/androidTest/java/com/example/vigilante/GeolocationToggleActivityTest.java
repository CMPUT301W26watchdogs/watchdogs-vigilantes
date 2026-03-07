package com.example.vigilante;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class GeolocationToggleActivityTest {

    @Test
    public void pageTitle_isDisplayed() {
        try (ActivityScenario<GeolocationToggleActivity> scenario =
                     ActivityScenario.launch(GeolocationToggleActivity.class)) {
            onView(withId(R.id.pageTitle)).check(matches(withText("Geolocation Requirement")));
        }
    }

    @Test
    public void geolocationSwitch_isDisplayedAndUncheckedByDefault() {
        try (ActivityScenario<GeolocationToggleActivity> scenario =
                     ActivityScenario.launch(GeolocationToggleActivity.class)) {
            onView(withId(R.id.geolocationSwitch)).check(matches(isDisplayed()));
            onView(withId(R.id.geolocationSwitch)).check(matches(isNotChecked()));
        }
    }

    @Test
    public void statusText_showsNotRequiredByDefault() {
        try (ActivityScenario<GeolocationToggleActivity> scenario =
                     ActivityScenario.launch(GeolocationToggleActivity.class)) {
            onView(withId(R.id.geolocationStatus)).check(matches(withText("Geolocation is not required")));
        }
    }

    @Test
    public void statusText_updatesWhenSwitchEnabled() {
        try (ActivityScenario<GeolocationToggleActivity> scenario =
                     ActivityScenario.launch(GeolocationToggleActivity.class)) {
            onView(withId(R.id.geolocationSwitch)).perform(click());
            onView(withId(R.id.geolocationStatus)).check(matches(withText("Geolocation is required")));
        }
    }

    @Test
    public void statusText_updatesWhenSwitchDisabledAgain() {
        try (ActivityScenario<GeolocationToggleActivity> scenario =
                     ActivityScenario.launch(GeolocationToggleActivity.class)) {
            onView(withId(R.id.geolocationSwitch)).perform(click());
            onView(withId(R.id.geolocationSwitch)).perform(click());
            onView(withId(R.id.geolocationStatus)).check(matches(withText("Geolocation is not required")));
        }
    }

    @Test
    public void saveButton_isDisplayed() {
        try (ActivityScenario<GeolocationToggleActivity> scenario =
                     ActivityScenario.launch(GeolocationToggleActivity.class)) {
            onView(withId(R.id.saveButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void cancelButton_isDisplayed() {
        try (ActivityScenario<GeolocationToggleActivity> scenario =
                     ActivityScenario.launch(GeolocationToggleActivity.class)) {
            onView(withId(R.id.cancelButton)).check(matches(isDisplayed()));
        }
    }
}
