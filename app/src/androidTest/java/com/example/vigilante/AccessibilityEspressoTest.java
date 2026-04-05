// espresso tests for the accessibility settings screen UI elements and interactions (Wildcard)
// March 31 2026, Claude Opus 4.6, wrote espresso tests for accessibility settings toggles and UI verification

package com.example.vigilante;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AccessibilityEspressoTest {

    @Rule
    public ActivityScenarioRule<AccessibilityActivity> activityRule =
            new ActivityScenarioRule<>(AccessibilityActivity.class);

    // verifying the color blind radio group is visible on the accessibility screen
    @Test
    public void testColorBlindGroupIsDisplayed() {
        onView(withId(R.id.colorBlindGroup)).check(matches(isDisplayed()));
    }

    // verifying the "None" radio button is displayed by default
    @Test
    public void testNoneRadioButtonDisplayed() {
        onView(withId(R.id.radioNone)).check(matches(isDisplayed()));
    }

    // verifying the deuteranopia radio button is displayed
    @Test
    public void testDeuteranopiaRadioDisplayed() {
        onView(withId(R.id.radioDeuteranopia)).check(matches(isDisplayed()));
    }

    // verifying the protanopia radio button is displayed
    @Test
    public void testProtanopiaRadioDisplayed() {
        onView(withId(R.id.radioProtanopia)).check(matches(isDisplayed()));
    }

    // verifying the tritanopia radio button is displayed
    @Test
    public void testTritanopiaRadioDisplayed() {
        onView(withId(R.id.radioTritanopia)).check(matches(isDisplayed()));
    }

    // verifying the reduce motion toggle is displayed
    @Test
    public void testReduceMotionToggleDisplayed() {
        onView(withId(R.id.reduceMotionToggle)).perform(scrollTo()).check(matches(isDisplayed()));
    }

    // verifying the reset button is displayed
    @Test
    public void testResetButtonDisplayed() {
        onView(withId(R.id.resetButton)).perform(scrollTo()).check(matches(isDisplayed()));
    }

    // verifying tapping the deuteranopia option does not crash
    @Test
    public void testSelectDeuteranopia() {
        onView(withId(R.id.radioDeuteranopia)).perform(click());
        onView(withId(R.id.colorBlindGroup)).check(matches(isDisplayed()));
    }

    // verifying tapping reset to defaults does not crash
    @Test
    public void testResetToDefaults() {
        onView(withId(R.id.reduceMotionToggle)).perform(scrollTo(), click());
        onView(withId(R.id.resetButton)).perform(scrollTo(), click());
        onView(withId(R.id.colorBlindGroup)).check(matches(isDisplayed()));
    }

    // verifying the back arrow is displayed
    @Test
    public void testBackArrowDisplayed() {
        onView(withId(R.id.backArrow)).check(matches(isDisplayed()));
    }
}
