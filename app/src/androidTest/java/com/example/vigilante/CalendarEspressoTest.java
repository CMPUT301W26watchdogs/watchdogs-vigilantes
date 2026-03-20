// espresso tests for the calendar activity UI elements and interactions (Wildcard)

package com.example.vigilante;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CalendarEspressoTest {

    @Rule
    public ActivityScenarioRule<CalendarActivity> activityRule =
            new ActivityScenarioRule<>(CalendarActivity.class);

    // verifying the calendar view widget is visible on screen when the activity launches
    @Test
    public void testCalendarViewIsDisplayed() {
        onView(withId(R.id.calendarView)).check(matches(isDisplayed()));
    }

    // verifying the "All Events" chip is visible on launch
    @Test
    public void testAllEventsChipIsDisplayed() {
        onView(withId(R.id.chipAllEvents)).check(matches(isDisplayed()));
    }

    // verifying the "My Events" chip is visible on launch
    @Test
    public void testMyEventsChipIsDisplayed() {
        onView(withId(R.id.chipMyEvents)).check(matches(isDisplayed()));
    }

    // verifying the title text shows "Event Calendar"
    @Test
    public void testCalendarTitleIsDisplayed() {
        onView(withId(R.id.calendarTitle)).check(matches(withText("Event Calendar")));
    }

    // verifying the events recycler view is present in the layout
    @Test
    public void testEventsRecyclerExists() {
        onView(withId(R.id.calendarEventsRecycler)).check(matches(isDisplayed()));
    }

    // verifying the selected date label is displayed
    @Test
    public void testSelectedDateLabelIsDisplayed() {
        onView(withId(R.id.selectedDateLabel)).check(matches(isDisplayed()));
    }

    // verifying tapping "My Events" chip does not crash and stays on the same screen
    @Test
    public void testMyEventsChipClickable() {
        onView(withId(R.id.chipMyEvents)).perform(click());
        onView(withId(R.id.calendarView)).check(matches(isDisplayed()));
    }

    // verifying tapping "All Events" chip after "My Events" switches back without crashing
    @Test
    public void testToggleBetweenChips() {
        onView(withId(R.id.chipMyEvents)).perform(click());
        onView(withId(R.id.chipAllEvents)).perform(click());
        onView(withId(R.id.calendarView)).check(matches(isDisplayed()));
    }

    // verifying the back arrow is displayed
    @Test
    public void testBackArrowIsDisplayed() {
        onView(withId(R.id.backArrow)).check(matches(isDisplayed()));
    }

    // verifying the bottom navigation bar is displayed
    @Test
    public void testBottomNavIsDisplayed() {
        onView(withId(R.id.bottomNav)).check(matches(isDisplayed()));
    }
}
