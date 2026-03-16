// espresso tests for lottery criteria display — verifies criteria text, spot count and waitlist count — US 01.05.05

package com.example.vigilante;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;

@RunWith(AndroidJUnit4.class)
public class LotteryCriteriaTest {

    private static final String TEST_EVENT_ID = "test_event_lottery_criteria";
    private FirebaseFirestore db;

    @Before
    public void setUp() throws Exception {
        // signing in with test account and creating test event with 5 pending attendees
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("ash@test.com", "ash123"));
        Thread.sleep(1000);

        db = FirebaseFirestore.getInstance();

        // creating a test event with waitingListLimit of 15 and registration end date
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", "Lottery Criteria Test Event");
        eventData.put("registrationEnd", "Mar 15");
        eventData.put("waitingListLimit", 15);
        Tasks.await(db.collection("events").document(TEST_EVENT_ID).set(eventData));

        // adding 5 pending attendees to the test event
        for (int i = 0; i < 5; i++) {
            Map<String, Object> attendee = new HashMap<>();
            attendee.put("name", "User " + i);
            attendee.put("email", "user" + i + "@test.com");
            attendee.put("userId", "criteria-uid-" + i);
            attendee.put("status", "pending");
            Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                    .collection("attendees").document("criteria-uid-" + i).set(attendee));
        }
    }

    @Test
    public void lotteryInfo_displaysCriteriaSection() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LotteryInfoActivity.class);
        intent.putExtra("event_id", TEST_EVENT_ID);
        try (ActivityScenario<LotteryInfoActivity> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(3000);
            // verifying the lottery criteria section is displayed — US 01.05.05
            onView(withId(R.id.lotteryCriteria)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {}
    }

    @Test
    public void lotteryInfo_criteriaContainsEqualChanceRule() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LotteryInfoActivity.class);
        intent.putExtra("event_id", TEST_EVENT_ID);
        try (ActivityScenario<LotteryInfoActivity> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(3000);
            // verifying criteria mentions "equal chance" for all entrants — US 01.05.05
            onView(withId(R.id.lotteryCriteria)).check(matches(withText(containsString("equal chance"))));
        } catch (InterruptedException e) {}
    }

    @Test
    public void lotteryInfo_criteriaContainsRandomSelectionRule() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LotteryInfoActivity.class);
        intent.putExtra("event_id", TEST_EVENT_ID);
        try (ActivityScenario<LotteryInfoActivity> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(3000);
            // verifying criteria mentions "randomly selects" process — US 01.05.05
            onView(withId(R.id.lotteryCriteria)).check(matches(withText(containsString("randomly selects"))));
        } catch (InterruptedException e) {}
    }

    @Test
    public void lotteryInfo_criteriaContainsReplacementRule() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LotteryInfoActivity.class);
        intent.putExtra("event_id", TEST_EVENT_ID);
        try (ActivityScenario<LotteryInfoActivity> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(3000);
            // verifying criteria mentions "replacement" draw for declined spots — US 01.05.05
            onView(withId(R.id.lotteryCriteria)).check(matches(withText(containsString("replacement"))));
        } catch (InterruptedException e) {}
    }

    @Test
    public void lotteryInfo_criteriaShowsCorrectSpotCount() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LotteryInfoActivity.class);
        intent.putExtra("event_id", TEST_EVENT_ID);
        try (ActivityScenario<LotteryInfoActivity> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(3000);
            // verifying total spots shows the waitingListLimit value of 15 — US 01.05.05
            onView(withId(R.id.lotteryTotalSpots)).check(matches(withText("15")));
        } catch (InterruptedException e) {}
    }

    @Test
    public void lotteryInfo_showsWaitlistCount() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LotteryInfoActivity.class);
        intent.putExtra("event_id", TEST_EVENT_ID);
        try (ActivityScenario<LotteryInfoActivity> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(3000);
            // verifying the waitlist count shows 5 pending attendees — US 01.05.05
            onView(withId(R.id.lotteryWaitlistCount)).check(matches(withText("5")));
        } catch (InterruptedException e) {}
    }

    @Test
    public void lotteryInfo_showsEventName() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LotteryInfoActivity.class);
        intent.putExtra("event_id", TEST_EVENT_ID);
        try (ActivityScenario<LotteryInfoActivity> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(3000);
            // verifying the event name is displayed correctly — US 01.05.05
            onView(withId(R.id.lotteryEventName)).check(matches(withText("Lottery Criteria Test Event")));
        } catch (InterruptedException e) {}
    }

    @Test
    public void lotteryInfo_showsDrawDate() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LotteryInfoActivity.class);
        intent.putExtra("event_id", TEST_EVENT_ID);
        try (ActivityScenario<LotteryInfoActivity> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(3000);
            // verifying the draw date matches the registration end date — US 01.05.05
            onView(withId(R.id.lotteryDrawDate)).check(matches(withText("Mar 15")));
        } catch (InterruptedException e) {}
    }

    @After
    public void tearDown() throws Exception {
        // cleaning up test attendee and event data from Firestore
        for (int i = 0; i < 5; i++) {
            db.collection("events").document(TEST_EVENT_ID)
                    .collection("attendees").document("criteria-uid-" + i).delete();
        }
        db.collection("events").document(TEST_EVENT_ID).delete();
    }
}
