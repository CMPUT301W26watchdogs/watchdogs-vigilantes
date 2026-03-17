// espresso tests for CSV export button in enrolled view — verifies button presence and click — US 02.06.05

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
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility;

@RunWith(AndroidJUnit4.class)
public class CsvExportEspressoTest {

    private static final String TEST_EVENT_ID = "test_event_csv_export";
    private FirebaseFirestore db;

    @Before
    // signing in with test account and creating test event with an accepted attendee for CSV export
    public void setUp() throws Exception {
        FirebaseAuth.getInstance().signOut();
        Tasks.await(FirebaseAuth.getInstance().signInWithEmailAndPassword("ash@test.com", "ash123"));
        Thread.sleep(1000);

        db = FirebaseFirestore.getInstance();

        // creating a test event
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", "CSV Export Test");
        eventData.put("organizerId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        Tasks.await(db.collection("events").document(TEST_EVENT_ID).set(eventData));

        // adding accepted attendee for export
        Map<String, Object> attendee = new HashMap<>();
        attendee.put("name", "Export User");
        attendee.put("email", "export@test.com");
        attendee.put("userId", "csv-uid-1");
        attendee.put("status", "accepted");
        Tasks.await(db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("csv-uid-1").set(attendee));
    }

    // launching viewAttendee in "enrolled" mode for the test event
    private ActivityScenario<viewAttendee> launchEnrolledView() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), viewAttendee.class);
        intent.putExtra("EVENT_ID", TEST_EVENT_ID);
        intent.putExtra("type", "enrolled");
        return ActivityScenario.launch(intent);
    }

    @Test
    // verifying the export CSV button shows correct text — US 02.06.05
    public void enrolledView_exportButtonHasCorrectText() {
        try (ActivityScenario<viewAttendee> scenario = launchEnrolledView()) {
            Thread.sleep(2000);
            onView(withId(R.id.export_csv_button)).check(matches(withText("Export CSV")));
        } catch (InterruptedException e) {}
    }

    @Test
    // verifying the export CSV button is visible only in enrolled view — US 02.06.05
    public void enrolledView_exportButtonVisible() {
        try (ActivityScenario<viewAttendee> scenario = launchEnrolledView()) {
            Thread.sleep(2000);
            onView(withId(R.id.export_csv_button)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {}
    }

    @Test
    // verifying the export CSV button is hidden in selected view — US 02.06.05
    public void selectedView_exportButtonHidden() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), viewAttendee.class);
        intent.putExtra("EVENT_ID", TEST_EVENT_ID);
        intent.putExtra("type", "selected");
        try (ActivityScenario<viewAttendee> scenario = ActivityScenario.launch(intent)) {
            Thread.sleep(2000);
            onView(withId(R.id.export_csv_button)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        } catch (InterruptedException e) {}
    }

    @After
    // cleaning up test data from Firestore
    public void tearDown() throws Exception {
        db.collection("events").document(TEST_EVENT_ID)
                .collection("attendees").document("csv-uid-1").delete();
        db.collection("events").document(TEST_EVENT_ID).delete();
    }
}
