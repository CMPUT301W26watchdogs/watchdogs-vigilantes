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
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class WaitingListLimitActivityTest {

    @Test
    public void pageTitle_isDisplayed() {
        try (ActivityScenario<WaitingListLimitActivity> scenario =
                     ActivityScenario.launch(WaitingListLimitActivity.class)) {
            onView(withId(R.id.pageTitle)).check(matches(withText("Waiting List Limit")));
        }
    }

    @Test
    public void limitSwitch_isDisplayedAndUncheckedByDefault() {
        try (ActivityScenario<WaitingListLimitActivity> scenario =
                     ActivityScenario.launch(WaitingListLimitActivity.class)) {
            onView(withId(R.id.limitSwitch)).check(matches(isDisplayed()));
            onView(withId(R.id.limitSwitch)).check(matches(isNotChecked()));
        }
    }

    @Test
    public void maxEntrantsField_hiddenByDefault() {
        try (ActivityScenario<WaitingListLimitActivity> scenario =
                     ActivityScenario.launch(WaitingListLimitActivity.class)) {
            onView(withId(R.id.layoutMaxEntrants)).check(matches(not(isDisplayed())));
        }
    }

    @Test
    public void maxEntrantsField_visibleWhenSwitchEnabled() {
        try (ActivityScenario<WaitingListLimitActivity> scenario =
                     ActivityScenario.launch(WaitingListLimitActivity.class)) {
            onView(withId(R.id.limitSwitch)).perform(click());
            onView(withId(R.id.layoutMaxEntrants)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void saveButton_isDisplayed() {
        try (ActivityScenario<WaitingListLimitActivity> scenario =
                     ActivityScenario.launch(WaitingListLimitActivity.class)) {
            onView(withId(R.id.saveButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void cancelButton_isDisplayed() {
        try (ActivityScenario<WaitingListLimitActivity> scenario =
                     ActivityScenario.launch(WaitingListLimitActivity.class)) {
            onView(withId(R.id.cancelButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void saveWithSwitchOff_staysOnScreen() {
        try (ActivityScenario<WaitingListLimitActivity> scenario =
                     ActivityScenario.launch(WaitingListLimitActivity.class)) {
            onView(withId(R.id.saveButton)).perform(click());
            onView(withId(R.id.pageTitle)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void saveWithSwitchOnAndValidNumber_staysOnScreen() {
        try (ActivityScenario<WaitingListLimitActivity> scenario =
                     ActivityScenario.launch(WaitingListLimitActivity.class)) {
            onView(withId(R.id.limitSwitch)).perform(click());
            onView(withId(R.id.fieldMaxEntrants)).perform(typeText("50"));
            onView(withId(R.id.saveButton)).perform(click());
            onView(withId(R.id.pageTitle)).check(matches(isDisplayed()));
        }
    }
}
