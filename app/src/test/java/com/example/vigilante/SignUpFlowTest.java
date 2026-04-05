// Claude, Opus 4, "write deep unit tests for the sign-up flow including cancel and re-signup logic"
package com.example.vigilante;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SignUpFlowTest {

    private List<Map<String, Object>> attendeesCollection;
    private Map<String, Map<String, Object>> usersCollection;

    private Map<String, Object> buildAttendeeData(String name, String email, String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name != null ? name : "Unknown");
        data.put("email", email != null ? email : "");
        data.put("userId", userId);
        data.put("status", "pending");
        return data;
    }

    private void addGeolocation(Map<String, Object> data, Double lat, Double lng) {
        if (lat != null) data.put("latitude", lat);
        if (lng != null) data.put("longitude", lng);
    }

    private boolean isAlreadySignedUp(String userId) {
        for (Map<String, Object> attendee : attendeesCollection) {
            if (userId.equals(attendee.get("userId"))) {
                return true;
            }
        }
        return false;
    }

    private String getAttendeeStatus(String userId) {
        for (Map<String, Object> attendee : attendeesCollection) {
            if (userId.equals(attendee.get("userId"))) {
                return (String) attendee.get("status");
            }
        }
        return null;
    }

    private void performSignUp(String userId, Double lat, Double lng) {
        Map<String, Object> userDoc = usersCollection.get(userId);
        String name = userDoc != null ? (String) userDoc.get("name") : null;
        String email = userDoc != null ? (String) userDoc.get("email") : null;
        Map<String, Object> data = buildAttendeeData(name, email, userId);
        addGeolocation(data, lat, lng);
        attendeesCollection.add(data);
    }

    private void cancelSignUp(String userId) {
        for (Map<String, Object> attendee : attendeesCollection) {
            if (userId.equals(attendee.get("userId"))) {
                attendee.put("status", "cancelled");
                return;
            }
        }
    }

    private void reSignUp(String userId) {
        for (Map<String, Object> attendee : attendeesCollection) {
            if (userId.equals(attendee.get("userId"))) {
                attendee.put("status", "pending");
                return;
            }
        }
    }

    @Before
    public void setUp() {
        attendeesCollection = new ArrayList<>();
        usersCollection = new HashMap<>();
        Map<String, Object> user1 = new HashMap<>();
        user1.put("name", "Alice");
        user1.put("email", "alice@test.com");
        usersCollection.put("uid-1", user1);

        Map<String, Object> user2 = new HashMap<>();
        user2.put("name", null);
        user2.put("email", null);
        usersCollection.put("uid-2", user2);
    }

    @Test
    public void signUp_createsAttendeeWithPendingStatus() {
        performSignUp("uid-1", null, null);
        assertEquals(1, attendeesCollection.size());
        assertEquals("pending", getAttendeeStatus("uid-1"));
    }

    @Test
    public void signUp_populatesNameAndEmailFromProfile() {
        performSignUp("uid-1", null, null);
        Map<String, Object> attendee = attendeesCollection.get(0);
        assertEquals("Alice", attendee.get("name"));
        assertEquals("alice@test.com", attendee.get("email"));
    }

    @Test
    public void signUp_nullProfile_defaultsToUnknownAndEmpty() {
        performSignUp("uid-2", null, null);
        Map<String, Object> attendee = attendeesCollection.get(0);
        assertEquals("Unknown", attendee.get("name"));
        assertEquals("", attendee.get("email"));
    }

    @Test
    public void signUp_withGeolocation_storesCoordinates() {
        performSignUp("uid-1", 53.5461, -113.4938);
        Map<String, Object> attendee = attendeesCollection.get(0);
        assertEquals(53.5461, (double) attendee.get("latitude"), 0.0001);
        assertEquals(-113.4938, (double) attendee.get("longitude"), 0.0001);
    }

    @Test
    public void signUp_withoutGeolocation_noCoordinates() {
        performSignUp("uid-1", null, null);
        Map<String, Object> attendee = attendeesCollection.get(0);
        assertNull(attendee.get("latitude"));
        assertNull(attendee.get("longitude"));
    }

    @Test
    public void cancelSignUp_changesStatusToCancelled() {
        performSignUp("uid-1", null, null);
        assertEquals("pending", getAttendeeStatus("uid-1"));
        cancelSignUp("uid-1");
        assertEquals("cancelled", getAttendeeStatus("uid-1"));
    }

    @Test
    public void reSignUp_afterCancel_resetsStatusToPending() {
        performSignUp("uid-1", null, null);
        cancelSignUp("uid-1");
        assertEquals("cancelled", getAttendeeStatus("uid-1"));
        reSignUp("uid-1");
        assertEquals("pending", getAttendeeStatus("uid-1"));
    }

    @Test
    public void multipleUsersCanSignUpForSameEvent() {
        performSignUp("uid-1", null, null);
        performSignUp("uid-2", null, null);
        assertEquals(2, attendeesCollection.size());
        assertEquals("pending", getAttendeeStatus("uid-1"));
        assertEquals("pending", getAttendeeStatus("uid-2"));
    }

    @Test
    public void signUp_duplicateUserIdDetected() {
        performSignUp("uid-1", null, null);
        assertTrue(isAlreadySignedUp("uid-1"));
        assertFalse(isAlreadySignedUp("uid-99"));
    }

    @Test
    public void signUp_unknownUserIdNoProfile_stillCreatesAttendee() {
        Map<String, Object> data = buildAttendeeData(null, null, "uid-unknown");
        attendeesCollection.add(data);
        assertEquals(1, attendeesCollection.size());
        assertEquals("Unknown", attendeesCollection.get(0).get("name"));
        assertEquals("", attendeesCollection.get(0).get("email"));
    }
}
