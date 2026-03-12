// tests for the lottery info screen — US 01.05.05 entrant sees lottery criteria and status

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
public class LotteryInfoActivityTest {

    // helper: building an intent with an event ID for the lottery info screen
    private Intent intentWithEventId(String eventId) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LotteryInfoActivity.class);
        intent.putExtra("event_id", eventId);
        return intent;
    }

    @Test
    public void pageTitle_showsLotteryInformation() {
        try (ActivityScenario<LotteryInfoActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.pageTitle)).check(matches(withText("Lottery Information")));
        }
    }

    @Test
    public void eventNameView_isDisplayed() {
        try (ActivityScenario<LotteryInfoActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.lotteryEventName)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void lotteryStatus_isDisplayed() {
        // verifying the entrant's lottery status field is present
        try (ActivityScenario<LotteryInfoActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.lotteryStatus)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void drawDate_isDisplayed() {
        // verifying the draw date field (registration end date) is visible
        try (ActivityScenario<LotteryInfoActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.lotteryDrawDate)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void totalSpots_isDisplayed() {
        // verifying the total capacity / spots field is present
        try (ActivityScenario<LotteryInfoActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.lotteryTotalSpots)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void backButton_isDisplayed() {
        try (ActivityScenario<LotteryInfoActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.backButton)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void differentEventIds_showSameStructure() {
        // verifying the same layout structure appears regardless of which event ID is passed
        try (ActivityScenario<LotteryInfoActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_b"))) {
            onView(withId(R.id.pageTitle)).check(matches(withText("Lottery Information")));
            onView(withId(R.id.lotteryEventName)).check(matches(isDisplayed()));
            onView(withId(R.id.lotteryStatus)).check(matches(isDisplayed()));
        }
    }
}
