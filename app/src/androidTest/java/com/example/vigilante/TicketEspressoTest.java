// espresso tests for the confirmation ticket download button visibility on the event detail screen (Wildcard)

package com.example.vigilante;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TicketEspressoTest {

    // launching EventDetailActivity with a test event ID so the detail screen opens
    private static Intent createIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventDetailActivity.class);
        intent.putExtra("event_id", "test_event_123");
        return intent;
    }

    @Rule
    public ActivityScenarioRule<EventDetailActivity> activityRule =
            new ActivityScenarioRule<>(createIntent());

    // verifying the download ticket button exists in the layout (Wildcard)
    @Test
    public void testDownloadTicketButtonExists() {
        onView(withId(R.id.downloadTicketButton)).check(matches(not(isDisplayed())));
    }

    // verifying the download ticket button text says Download Ticket (Wildcard)
    @Test
    public void testDownloadTicketButtonHasCorrectText() {
        onView(withId(R.id.downloadTicketButton)).check(matches(withText("Download Ticket")));
    }

    // verifying the event title text view is displayed on the detail screen (Wildcard)
    @Test
    public void testEventTitleIsDisplayed() {
        onView(withId(R.id.eventTitle)).check(matches(isDisplayed()));
    }

    // verifying the event date text view is displayed (Wildcard)
    @Test
    public void testEventDateIsDisplayed() {
        onView(withId(R.id.eventDate)).check(matches(isDisplayed()));
    }

    // verifying the event location text view is displayed (Wildcard)
    @Test
    public void testEventLocationIsDisplayed() {
        onView(withId(R.id.eventLocation)).check(matches(isDisplayed()));
    }

    // verifying the register button is displayed on the detail screen (Wildcard)
    @Test
    public void testRegisterButtonIsDisplayed() {
        onView(withId(R.id.registerButton)).check(matches(isDisplayed()));
    }

    // verifying the sign up status text view is displayed (Wildcard)
    @Test
    public void testSignUpStatusIsDisplayed() {
        onView(withId(R.id.signUpStatus)).check(matches(isDisplayed()));
    }

    // verifying the back button is displayed at the top (Wildcard)
    @Test
    public void testBackButtonIsDisplayed() {
        onView(withId(R.id.backButton)).check(matches(isDisplayed()));
    }

    // verifying the event poster image is displayed (Wildcard)
    @Test
    public void testEventPosterIsDisplayed() {
        onView(withId(R.id.eventPoster)).check(matches(isDisplayed()));
    }

    // verifying the bottom navigation bar is displayed (Wildcard)
    @Test
    public void testBottomNavIsDisplayed() {
        onView(withId(R.id.bottomNav)).check(matches(isDisplayed()));
    }
}
