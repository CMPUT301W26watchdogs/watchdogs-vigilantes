package com.example.vigilante;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

import static java.util.function.Predicate.not;

import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.regex.Matcher;

public class ProfileTest {

    @Rule
    public ActivityScenarioRule<ProfilePage> activityRule =
            new ActivityScenarioRule<>(ProfilePage.class);

    @Before
    public void setUp() throws Exception {

        FirebaseAuth.getInstance().signOut();

        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("ash@test.com", "ash123"));

        ActivityScenario.launch(ProfilePage.class);


        Thread.sleep(1500);
    }

    @Test
    public void testProfileDataLoads() {
        // Wait for Firestore (approx 2 seconds)
        try { Thread.sleep(2000); } catch (InterruptedException e) {}


        onView(withId(R.id.name_text)).check(matches(withText("Bash")));

        // Check if Email is displayed correctly
        onView(withId(R.id.email_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testUpdatePhoneDialog(){
        onView(withId(R.id.UpdateInfo_button)).perform(click());

        onView(withText("Phone")).perform(click());

        onView(withClassName(Matchers.equalTo(EditText.class.getName()))).perform(clearText(),typeText("09345678456"));

        try { Thread.sleep(3000);} catch (InterruptedException e) {}

        onView(withText("Save")).perform(click());

        try { Thread.sleep(3000);} catch (InterruptedException e) {}

        onView(withId(R.id.phonenumber_text)).check(matches(withText("09345678456")));
    }

    @Test
    public void testEmailUpdateSendsVerification() {
        onView(withId(R.id.UpdateInfo_button)).perform(click());
        onView(withText("Email")).perform(click());

        String newEmail = "new_test@test.com";

        onView(withClassName(Matchers.equalTo(EditText.class.getName()))).perform(clearText(),typeText(newEmail));

        onView(withId(android.R.id.button1)).perform(click());

        try { Thread.sleep(3000);} catch (InterruptedException e) {}

        onView(withText("Update Email")).check(doesNotExist());

        onView(withId(R.id.email_text)).check(matches(withText(newEmail)));

    }

    @Test
    public  void DeleteAccountTest(){
        onView(withId(R.id.delete_account_button)).perform(click());
        onView(withText("Delete Account")).check(matches(isDisplayed()));
        onView(withText(containsString("Are you sure you want to delete your account ? This action cannot be undone all your data will be removed"))).check(matches(isDisplayed()));
        onView(withId(android.R.id.button2)).perform(click());
        onView(withText(containsString("Are you sure you want to delete your account ? This action cannot be undone all your data will be removed"))).check(doesNotExist());
        onView(withId(R.id.name_text)).check(matches(isDisplayed()));

    }

    @After
    public void tearDown() {
        FirebaseAuth.getInstance().signOut();
    }
}
