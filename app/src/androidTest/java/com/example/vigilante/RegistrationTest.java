package com.example.vigilante;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class RegistrationTest {
    @Rule
    public ActivityScenarioRule<RegisterPage> activityRule =
            new ActivityScenarioRule<>(RegisterPage.class);

    @Test
    public void testSuccessfulRegistration() {
        onView(withId(R.id.name_register)).perform(typeText("NewTest User"));
        String uniqueEmail = "test" + System.currentTimeMillis()+ "@example.com";
        onView(withId(R.id.email_register)).perform(typeText(uniqueEmail));
        onView(withId(R.id.password_register)).perform(typeText("pass123"));
        onView(withId(R.id.phone_number_register)).perform(typeText("1234567890"));
        onView(withId(R.id.register_button)).perform(click());
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
        onView(withId(R.id.home_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testFailureShortPasswordRegistration() {
        onView(withId(R.id.name_register)).perform(typeText("NewTest User"));
        String uniqueEmail = "test" + System.currentTimeMillis()+ "@example.com";
        onView(withId(R.id.email_register)).perform(typeText(uniqueEmail));
        onView(withId(R.id.password_register)).perform(typeText("pass"));
        onView(withId(R.id.phone_number_register)).perform(typeText("1234567890"));
        onView(withId(R.id.register_button)).perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.register_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testSuccessWithoutPhoneRegistration() {
        onView(withId(R.id.name_register)).perform(typeText("NewTest User"));
        String uniqueEmail = "test" + System.currentTimeMillis()+ "@example.com";
        onView(withId(R.id.email_register)).perform(typeText(uniqueEmail));
        onView(withId(R.id.password_register)).perform(typeText("pass123"));
        onView(withId(R.id.register_button)).perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.home_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testSuccessOrgRegistration() {
        onView(withId(R.id.name_register)).perform(typeText("NewTest Org"));
        String uniqueEmail = "testorg" + System.currentTimeMillis()+ "@example.com";
        onView(withId(R.id.email_register)).perform(typeText(uniqueEmail));
        onView(withId(R.id.password_register)).perform(typeText("pass123"));
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.organizer_checkbox)).perform(click());
        onView(withId(R.id.organizer_checkbox)).check(matches(isChecked()));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.home_button)).check(matches(isDisplayed()));
    }
}
