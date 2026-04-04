// espresso tests for the calendar activity UI elements and interactions (Wildcard)

package com.example.vigilante;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.widget.CalendarView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CalendarEspressoTest {

    @Before
    public void signIn() throws Exception {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Tasks.await(auth.signInWithEmailAndPassword("vedentrant2@test.com", "test123"));
        }
    }

    // verifying the calendar view widget is visible on screen when the activity launches
    @Test
    public void testCalendarViewIsDisplayed() {
        try (ActivityScenario<CalendarActivity> scenario = ActivityScenario.launch(CalendarActivity.class)) {
            onView(allOf(withId(R.id.calendarView), isAssignableFrom(CalendarView.class))).check(matches(isDisplayed()));
        }
    }

    // verifying the title text shows "Event Calendar"
    @Test
    public void testCalendarTitleIsDisplayed() {
        try (ActivityScenario<CalendarActivity> scenario = ActivityScenario.launch(CalendarActivity.class)) {
            onView(withId(R.id.calendarTitle)).check(matches(withText("Event Calendar")));
        }
    }

    // verifying the events recycler view is present in the layout
    @Test
    public void testEventsRecyclerExists() {
        try (ActivityScenario<CalendarActivity> scenario = ActivityScenario.launch(CalendarActivity.class)) {
            onView(withId(R.id.calendarEventsRecycler)).check(matches(isDisplayed()));
        }
    }

    // verifying the selected date label is displayed
    @Test
    public void testSelectedDateLabelIsDisplayed() {
        try (ActivityScenario<CalendarActivity> scenario = ActivityScenario.launch(CalendarActivity.class)) {
            onView(withId(R.id.selectedDateLabel)).check(matches(isDisplayed()));
        }
    }

    // verifying the back arrow is displayed
    @Test
    public void testBackArrowIsDisplayed() {
        try (ActivityScenario<CalendarActivity> scenario = ActivityScenario.launch(CalendarActivity.class)) {
            onView(withId(R.id.backArrow)).check(matches(isDisplayed()));
        }
    }

    // verifying the bottom navigation bar is displayed
    @Test
    public void testBottomNavIsDisplayed() {
        try (ActivityScenario<CalendarActivity> scenario = ActivityScenario.launch(CalendarActivity.class)) {
            onView(withId(R.id.bottomNav)).check(matches(isDisplayed()));
        }
    }
}
