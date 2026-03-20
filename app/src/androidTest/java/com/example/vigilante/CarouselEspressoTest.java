// espresso tests for the home page carousel UI elements and swipe interactions (Wildcard)

package com.example.vigilante;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
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
public class CarouselEspressoTest {

    @Rule
    public ActivityScenarioRule<HomePage> activityRule =
            new ActivityScenarioRule<>(HomePage.class);

    // verifying the carousel ViewPager2 is displayed on the home page (Wildcard)
    @Test
    public void testCarouselIsDisplayed() {
        onView(withId(R.id.eventCarousel)).check(matches(isDisplayed()));
    }

    // verifying the dot indicators container is displayed below the carousel (Wildcard)
    @Test
    public void testDotsContainerIsDisplayed() {
        onView(withId(R.id.carouselDots)).check(matches(isDisplayed()));
    }

    // verifying the featured events label is displayed above the carousel (Wildcard)
    @Test
    public void testFeaturedLabelIsDisplayed() {
        onView(withText("FEATURED EVENTS")).check(matches(isDisplayed()));
    }

    // verifying the scan QR button is still visible on the home page (Wildcard)
    @Test
    public void testScanButtonIsDisplayed() {
        onView(withId(R.id.scanQrButton)).check(matches(isDisplayed()));
    }

    // verifying the brand header is still displayed at the top (Wildcard)
    @Test
    public void testBrandHeaderIsDisplayed() {
        onView(withText("VIGILANTE")).check(matches(isDisplayed()));
    }

    // verifying swiping left on the carousel does not crash the activity (Wildcard)
    @Test
    public void testSwipeLeftOnCarousel() {
        onView(withId(R.id.eventCarousel)).perform(swipeLeft());
        onView(withId(R.id.eventCarousel)).check(matches(isDisplayed()));
    }

    // verifying swiping right on the carousel does not crash the activity (Wildcard)
    @Test
    public void testSwipeRightOnCarousel() {
        onView(withId(R.id.eventCarousel)).perform(swipeRight());
        onView(withId(R.id.eventCarousel)).check(matches(isDisplayed()));
    }

    // verifying the browse events card is still accessible on the home page (Wildcard)
    @Test
    public void testEventsCardIsDisplayed() {
        onView(withId(R.id.eventsCard)).check(matches(isDisplayed()));
    }

    // verifying the alerts card is still accessible on the home page (Wildcard)
    @Test
    public void testProfileCardIsDisplayed() {
        onView(withId(R.id.profileCard)).check(matches(isDisplayed()));
    }

    // verifying the bottom navigation bar is still displayed (Wildcard)
    @Test
    public void testBottomNavIsDisplayed() {
        onView(withId(R.id.bottomNav)).check(matches(isDisplayed()));
    }
}
