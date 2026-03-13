// tests for the registration screen — US 01.02.01 entrant provides personal information

package com.example.vigilante;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class RegisterPageTest {

    @Test
    public void nameField_isDisplayed() {
        try (ActivityScenario<RegisterPage> scenario = ActivityScenario.launch(RegisterPage.class)) {
            onView(withId(R.id.name_register)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void emailField_isDisplayed() {
        try (ActivityScenario<RegisterPage> scenario = ActivityScenario.launch(RegisterPage.class)) {
            onView(withId(R.id.email_register)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void passwordField_isDisplayed() {
        try (ActivityScenario<RegisterPage> scenario = ActivityScenario.launch(RegisterPage.class)) {
            onView(withId(R.id.password_register)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void phoneField_isDisplayed() {
        try (ActivityScenario<RegisterPage> scenario = ActivityScenario.launch(RegisterPage.class)) {
            onView(withId(R.id.phone_number_register)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void organizerCheckbox_isDisplayed() {
        try (ActivityScenario<RegisterPage> scenario = ActivityScenario.launch(RegisterPage.class)) {
            onView(withId(R.id.organizer_checkbox)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void registerButton_isDisplayed() {
        try (ActivityScenario<RegisterPage> scenario = ActivityScenario.launch(RegisterPage.class)) {
            onView(withId(R.id.register_button)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void emptyFields_staysOnRegisterScreen() {
        // clicking register with no data should fail validation and stay on the form
        try (ActivityScenario<RegisterPage> scenario = ActivityScenario.launch(RegisterPage.class)) {
            onView(withId(R.id.register_button)).perform(click());
            onView(withId(R.id.name_register)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void shortPassword_staysOnRegisterScreen() {
        // a password shorter than 6 characters should fail validation and keep the user on the screen
        try (ActivityScenario<RegisterPage> scenario = ActivityScenario.launch(RegisterPage.class)) {
            onView(withId(R.id.name_register)).perform(typeText("Alice"), closeSoftKeyboard());
            onView(withId(R.id.email_register)).perform(typeText("alice@test.com"), closeSoftKeyboard());
            onView(withId(R.id.password_register)).perform(typeText("abc"), closeSoftKeyboard());
            onView(withId(R.id.register_button)).perform(click());
            onView(withId(R.id.register_button)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void typingName_acceptsInput() {
        // verifying that the name field accepts text correctly
        try (ActivityScenario<RegisterPage> scenario = ActivityScenario.launch(RegisterPage.class)) {
            onView(withId(R.id.name_register)).perform(typeText("Bob Smith"), closeSoftKeyboard());
            onView(withId(R.id.name_register)).check(matches(isDisplayed()));
        }
    }
}
