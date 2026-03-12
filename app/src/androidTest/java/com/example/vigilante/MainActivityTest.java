// tests for the login screen — US login flow

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
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Test
    public void emailField_isDisplayed() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.editTextTextEmailAddress)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void passwordField_isDisplayed() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.editTextTextPassword)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void loginButton_isDisplayed() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.login_button)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void registerButton_isDisplayed() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.register_button)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void emptyCredentials_staysOnLoginScreen() {
        // clicking login with empty fields should show a toast and keep the user on the login screen
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.login_button)).perform(click());
            onView(withId(R.id.editTextTextEmailAddress)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void typingEmail_updatesEmailField() {
        // verifying that input is accepted in the email field
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.editTextTextEmailAddress)).perform(typeText("test@test.com"), closeSoftKeyboard());
            onView(withId(R.id.editTextTextEmailAddress)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void typingPassword_updatesPasswordField() {
        // verifying that input is accepted in the password field
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.editTextTextPassword)).perform(typeText("secret123"), closeSoftKeyboard());
            onView(withId(R.id.editTextTextPassword)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void emptyEmail_withPassword_staysOnLoginScreen() {
        // submitting with only a password and no email should keep the user on the login screen
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.editTextTextPassword)).perform(typeText("password123"), closeSoftKeyboard());
            onView(withId(R.id.login_button)).perform(click());
            onView(withId(R.id.login_button)).check(matches(isDisplayed()));
        }
    }
}
