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

    private Intent intentWithEventId(String eventId) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LotteryInfoActivity.class);
        intent.putExtra("event_id", eventId);
        return intent;
    }

    @Test
    public void pageTitle_isDisplayed() {
        try (ActivityScenario<LotteryInfoActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.pageTitle)).check(matches(withText("Lottery Information")));
        }
    }

    @Test
    public void eventName_showsCorrectName() {
        try (ActivityScenario<LotteryInfoActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.lotteryEventName)).check(matches(withText("Beginner Swimming Lessons")));
        }
    }

    @Test
    public void lotteryStatus_isDisplayed() {
        try (ActivityScenario<LotteryInfoActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.lotteryStatus)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void drawDate_isDisplayed() {
        try (ActivityScenario<LotteryInfoActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.lotteryDrawDate)).check(matches(withText("January 11, 2026")));
        }
    }

    @Test
    public void totalSpots_showsCapacity() {
        try (ActivityScenario<LotteryInfoActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_a"))) {
            onView(withId(R.id.lotteryTotalSpots)).check(matches(withText("20")));
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
    public void differentEvent_showsCorrectInfo() {
        try (ActivityScenario<LotteryInfoActivity> scenario =
                     ActivityScenario.launch(intentWithEventId("event_b"))) {
            onView(withId(R.id.lotteryEventName)).check(matches(withText("Interpretive Dance Workshop")));
            onView(withId(R.id.lotteryTotalSpots)).check(matches(withText("60")));
        }
    }
}
