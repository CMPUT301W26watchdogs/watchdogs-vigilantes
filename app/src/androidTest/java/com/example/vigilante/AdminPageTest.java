// tests for the admin dashboard — US 03.04.01 browse events, US 03.05.01 browse profiles,
// US 03.07.01 browse organizers

package com.example.vigilante;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class AdminPageTest {

    @Test
    public void browseEventsButton_isDisplayed() {
        // verifying the browse events button is present — US 03.04.01
        try (ActivityScenario<AdminPage> scenario = ActivityScenario.launch(AdminPage.class)) {
            onView(withId(R.id.browseevents_button)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void browseProfilesButton_isDisplayed() {
        // verifying the browse profiles button is present — US 03.05.01
        try (ActivityScenario<AdminPage> scenario = ActivityScenario.launch(AdminPage.class)) {
            onView(withId(R.id.browseprofiles_button)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void browseOrganizersButton_isDisplayed() {
        // verifying the browse organizers button is present — US 03.07.01
        try (ActivityScenario<AdminPage> scenario = ActivityScenario.launch(AdminPage.class)) {
            onView(withId(R.id.browseorganizers_button)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void signOutButton_isDisplayed() {
        try (ActivityScenario<AdminPage> scenario = ActivityScenario.launch(AdminPage.class)) {
            onView(withId(R.id.signout_button)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void header_isDisplayed() {
        try (ActivityScenario<AdminPage> scenario = ActivityScenario.launch(AdminPage.class)) {
            onView(withId(R.id.title_text_header)).check(matches(isDisplayed()));
        }
    }
}
