package com.example.vigilante;

import androidx.test.core.app.ActivityScenario;
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

    @Test
    public void pageTitle_isDisplayed() {
        try (ActivityScenario<EntrantMapActivity> scenario =
                     ActivityScenario.launch(EntrantMapActivity.class)) {
            onView(withId(R.id.pageTitle)).check(matches(withText("Entrant Locations")));
        }
    }

    @Test
    public void mapFragment_isDisplayed() {
        try (ActivityScenario<EntrantMapActivity> scenario =
                     ActivityScenario.launch(EntrantMapActivity.class)) {
            onView(withId(R.id.mapFragment)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void backButton_isDisplayed() {
        try (ActivityScenario<EntrantMapActivity> scenario =
                     ActivityScenario.launch(EntrantMapActivity.class)) {
            onView(withId(R.id.backButton)).check(matches(isDisplayed()));
        }
    }
}
