// testing entrant status transitions including accept, decline, cancel flows and geolocation data US 01.05.01

package com.example.vigilante;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class EntrantStatusFlowTest {

    private Map<String, Object> buildAttendeeData(String name, String email, String userId, String status) {
        // building an attendee data map with null safe defaults for name and email
        Map<String, Object> data = new HashMap<>();
        data.put("name", name != null ? name : "Unknown");
        data.put("email", email != null ? email : "");
        data.put("userId", userId);
        data.put("status", status);
        return data;
    }

    // verifying sign up creates attendee with pending status US 01.05.01
    @Test
    public void signUp_createsAttendeeWithPendingStatus() {
        Map<String, Object> data = buildAttendeeData("Alice", "alice@test.com", "uid-1", "pending");
        assertEquals("pending", data.get("status"));
        assertEquals("Alice", data.get("name"));
    }

    // verifying acceptance changes status from selected to accepted US 01.05.01
    @Test
    public void acceptance_changesStatusFromSelectedToAccepted() {
        Entrant e = new Entrant();
        e.setStatus("selected");
        assertEquals("selected", e.getStatus());
        e.setStatus("accepted");
        assertEquals("accepted", e.getStatus());
    }

    // verifying decline changes status from selected to declined US 01.05.01
    @Test
    public void decline_changesStatusFromSelectedToDeclined() {
        Entrant e = new Entrant();
        e.setStatus("selected");
        e.setStatus("declined");
        assertEquals("declined", e.getStatus());
    }

    // verifying cancellation changes status to cancelled US 02.06.04
    @Test
    public void cancellation_changesStatusToCancelled() {
        Entrant e = new Entrant();
        e.setStatus("pending");
        e.setStatus("cancelled");
        assertEquals("cancelled", e.getStatus());
    }

    // verifying lottery draw changes status from pending to selected US 02.05.01
    @Test
    public void lotterySelection_changesStatusFromPendingToSelected() {
        Entrant e = new Entrant();
        e.setStatus("pending");
        e.setStatus("selected");
        assertEquals("selected", e.getStatus());
    }

    // verifying re signup from declined resets to pending US 01.05.01
    @Test
    public void reSignUp_changesStatusFromDeclinedToPending() {
        Entrant e = new Entrant();
        e.setStatus("declined");
        e.setStatus("pending");
        assertEquals("pending", e.getStatus());
    }

    // verifying re signup from cancelled resets to pending US 01.05.01
    @Test
    public void reSignUp_changesStatusFromCancelledToPending() {
        Entrant e = new Entrant();
        e.setStatus("cancelled");
        e.setStatus("pending");
        assertEquals("pending", e.getStatus());
    }

    // verifying null name defaults to Unknown US 01.05.01
    @Test
    public void attendeeData_nullName_defaultsToUnknown() {
        Map<String, Object> data = buildAttendeeData(null, "email@test.com", "uid-1", "pending");
        assertEquals("Unknown", data.get("name"));
    }

    // verifying null email defaults to empty string US 01.05.01
    @Test
    public void attendeeData_nullEmail_defaultsToEmpty() {
        Map<String, Object> data = buildAttendeeData("Alice", null, "uid-1", "pending");
        assertEquals("", data.get("email"));
    }

    // verifying attendee data can store geolocation coordinates US 02.02.02
    @Test
    public void attendeeData_withGeolocation() {
        Map<String, Object> data = buildAttendeeData("Alice", "alice@test.com", "uid-1", "pending");
        data.put("latitude", 53.5461);
        data.put("longitude", -113.4938);
        assertEquals(53.5461, (double) data.get("latitude"), 0.0001);
        assertEquals(-113.4938, (double) data.get("longitude"), 0.0001);
    }

    // verifying attendee without geolocation has null coordinates US 02.02.02
    @Test
    public void attendeeData_withoutGeolocation() {
        Map<String, Object> data = buildAttendeeData("Alice", "alice@test.com", "uid-1", "pending");
        assertNull(data.get("latitude"));
        assertNull(data.get("longitude"));
    }

    // verifying cancel button is hidden for cancelled entrants US 02.06.04
    @Test
    public void cancelledEntrant_cancelButtonShouldBeHidden() {
        Entrant e = new Entrant();
        e.setStatus("cancelled");
        boolean showCancel = !"cancelled".equals(e.getStatus());
        assertFalse(showCancel);
    }

    // verifying cancel button is visible for pending entrants US 02.06.04
    @Test
    public void pendingEntrant_cancelButtonShouldBeVisible() {
        Entrant e = new Entrant();
        e.setStatus("pending");
        boolean showCancel = !"cancelled".equals(e.getStatus());
        assertTrue(showCancel);
    }
}
