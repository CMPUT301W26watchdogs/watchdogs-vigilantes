package com.example.vigilante;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
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
        onView(withId(R.id.name_register)).perform(replaceText("NewTest User"));
        String uniqueEmail = "t" + (System.currentTimeMillis() % 1000000) + "@test.com";
        onView(withId(R.id.email_register)).perform(replaceText(uniqueEmail));
        onView(withId(R.id.password_register)).perform(replaceText("pass123"));
        onView(withId(R.id.phone_number_register)).perform(replaceText("1234567890"), closeSoftKeyboard());
        onView(withId(R.id.register_button)).perform(click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        onView(withId(R.id.bottomNav)).check(matches(isDisplayed()));
    }

    @Test
    public void testFailureShortPasswordRegistration() {
        onView(withId(R.id.name_register)).perform(replaceText("NewTest User"));
        String uniqueEmail = "t" + (System.currentTimeMillis() % 1000000)+ "@test.com";
        onView(withId(R.id.email_register)).perform(replaceText(uniqueEmail));
        onView(withId(R.id.password_register)).perform(replaceText("pass"));
        onView(withId(R.id.phone_number_register)).perform(replaceText("1234567890"), closeSoftKeyboard());
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
        onView(withId(R.id.name_register)).perform(replaceText("NewTest User"));
        String uniqueEmail = "t" + (System.currentTimeMillis() % 1000000)+ "@test.com";
        onView(withId(R.id.email_register)).perform(replaceText(uniqueEmail));
        onView(withId(R.id.password_register)).perform(replaceText("pass123"), closeSoftKeyboard());
        onView(withId(R.id.register_button)).perform(click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.bottomNav)).check(matches(isDisplayed()));
    }

    @Test
    public void testSuccessOrgRegistration() {
        onView(withId(R.id.name_register)).perform(replaceText("NewTest Org"));
        String uniqueEmail = "o" + (System.currentTimeMillis() % 1000000)+ "@test.com";
        onView(withId(R.id.email_register)).perform(replaceText(uniqueEmail));
        onView(withId(R.id.password_register)).perform(replaceText("pass123"), closeSoftKeyboard());
        onView(withId(R.id.roleOrganizer)).perform(click());
        onView(withId(R.id.register_button)).perform(click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.bottomNav)).check(matches(isDisplayed()));
    }
}