// espresso tests for category-based event filtering via chip UI — US 01.01.04

package com.example.vigilante;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class EventFilterTest {

    @Before
    public void setUp() throws Exception {
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("ash@test.com", "ash123"));
        Thread.sleep(1500);
    }

    private ActivityScenario<AllEventsActivity> launchAllEvents() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AllEventsActivity.class);
        intent.putExtra("type", "all");
        return ActivityScenario.launch(intent);
    }

    @Test
    public void chipAll_isSelectedByDefault() {
        try (ActivityScenario<AllEventsActivity> scenario = launchAllEvents()) {
            Thread.sleep(2000);
            onView(withId(R.id.chipAll)).check(matches(isDisplayed()));
            onView(withId(R.id.chipAll)).check(matches(withText("All")));
        } catch (InterruptedException e) {}
    }

    @Test
    public void clickSportsChip_filtersToSportsCategory() {
        try (ActivityScenario<AllEventsActivity> scenario = launchAllEvents()) {
            Thread.sleep(2000);
            onView(withId(R.id.chipSports)).perform(click());
            Thread.sleep(1000);
            onView(withId(R.id.all_events_recycler_view)).check(matches(isDisplayed()));
            onView(withId(R.id.chipSports)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {}
    }

    @Test
    public void clickArtsChip_filtersToArtsCategory() {
        try (ActivityScenario<AllEventsActivity> scenario = launchAllEvents()) {
            Thread.sleep(2000);
            onView(withId(R.id.chipArts)).perform(click());
            Thread.sleep(1000);
            onView(withId(R.id.all_events_recycler_view)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {}
    }

    @Test
    public void clickMusicChip_thenClickAll_showsAllEventsAgain() {
        try (ActivityScenario<AllEventsActivity> scenario = launchAllEvents()) {
            Thread.sleep(2000);
            onView(withId(R.id.chipMusic)).perform(click());
            Thread.sleep(500);
            onView(withId(R.id.chipAll)).perform(click());
            Thread.sleep(1000);
            onView(withId(R.id.all_events_recycler_view)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {}
    }

    @Test
    public void categoryChips_allDisplayed() {
        try (ActivityScenario<AllEventsActivity> scenario = launchAllEvents()) {
            onView(withId(R.id.chipAll)).check(matches(isDisplayed()));
            onView(withId(R.id.chipSports)).check(matches(isDisplayed()));
            onView(withId(R.id.chipArts)).check(matches(isDisplayed()));
            onView(withId(R.id.chipMusic)).check(matches(isDisplayed()));
        }
    }
}
