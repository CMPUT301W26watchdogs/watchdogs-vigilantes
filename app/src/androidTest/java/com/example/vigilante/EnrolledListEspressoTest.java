// testing the enrolled entrants list view including title, list display, and export button US 02.06.03

package com.example.vigilante;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility;

@RunWith(AndroidJUnit4.class)
public class EnrolledListEspressoTest {

    private static final String TEST_EVENT_ID = "test_event_enrolled";
    private FirebaseFirestore db;

    // signing in with test account and creating test event with accepted attendees
    @Before
    public void setUp() throws Exception {
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("ash@test.com", "ash123"));
        Thread.sleep(1000);

        db = FirebaseFirestore.getInstance();

        // creating a test event owned by the current user
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", "Enrolled List Test");
        eventData.put("organizerId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        Tasks.await(db.collection("events").document(TEST_EVENT_ID).set(eventData));

        // adding an accepted attendee that should appear in enrolled list
        Map<String, Object> acceptedAttendee = new HashMap<>();
        acceptedAttendee.put("name", "Enrolled User");
        acceptedAttendee.put("email", "enrolled@test.com");
        acceptedAttendee.put("userId", "enrolled-uid-1");
        acceptedAttendee.put("status", "accepted");
        Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("enrolled-uid-1").set(acceptedAttendee));

        // adding a pending attendee that should NOT appear in enrolled list
        Map<String, Object> pendingAttendee = new HashMap<>();
        pendingAttendee.put("name", "Pending User");
        pendingAttendee.put("email", "pending@test.com");
        pendingAttendee.put("userId", "enrolled-uid-2");
        pendingAttendee.put("status", "pending");
        Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("enrolled-uid-2").set(pendingAttendee));
    }

    // launching viewAttendee in "enrolled" mode for the test event
    private ActivityScenario<viewAttendee> launchEnrolledView() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), viewAttendee.class);
        intent.putExtra("EVENT_ID", TEST_EVENT_ID);
        intent.putExtra("type", "enrolled");
        return ActivityScenario.launch(intent);
    }

    // checking that the enrolled list view shows the correct title US 02.06.03
    @Test
    public void enrolledView_showsCorrectTitle() {
        try (ActivityScenario<viewAttendee> scenario = launchEnrolledView()) {
            Thread.sleep(2000);
            onView(withId(R.id.title_waiting_list)).check(matches(withText("Enrolled Entrants")));
        } catch (InterruptedException e) {}
    }

    // making sure the export CSV button is visible in enrolled view US 02.06.03
    @Test
    public void enrolledView_showsExportCsvButton() {
        try (ActivityScenario<viewAttendee> scenario = launchEnrolledView()) {
            Thread.sleep(2000);
            onView(withId(R.id.export_csv_button)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {}
    }

    // verifying the recycler view is displayed for enrolled entrants US 02.06.03
    @Test
    public void enrolledView_showsRecyclerView() {
        try (ActivityScenario<viewAttendee> scenario = launchEnrolledView()) {
            Thread.sleep(2000);
            onView(withId(R.id.attendees_recycler_view)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {}
    }

    // verifying draw replacement button is hidden in enrolled view US 02.06.03
    @Test
    public void enrolledView_hidesDrawReplacementButton() {
        try (ActivityScenario<viewAttendee> scenario = launchEnrolledView()) {
            Thread.sleep(2000);
            onView(withId(R.id.draw_replacement_button)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        } catch (InterruptedException e) {}
    }

    // cleaning up test event and attendee data from Firestore
    @After
    public void tearDown() throws Exception {
        db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("enrolled-uid-1").delete();
        db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("enrolled-uid-2").delete();
        db.collection("events").document(TEST_EVENT_ID).delete();
    }
}
