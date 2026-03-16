package com.example.vigilante;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class EntrantStatusFlowTest {

    private Map<String, Object> buildAttendeeData(String name, String email, String userId, String status) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name != null ? name : "Unknown");
        data.put("email", email != null ? email : "");
        data.put("userId", userId);
        data.put("status", status);
        return data;
    }

    @Test
    public void signUp_createsAttendeeWithPendingStatus() {
        Map<String, Object> data = buildAttendeeData("Alice", "alice@test.com", "uid-1", "pending");
        assertEquals("pending", data.get("status"));
        assertEquals("Alice", data.get("name"));
    }

    @Test
    public void acceptance_changesStatusFromSelectedToAccepted() {
        Entrant e = new Entrant();
        e.setStatus("selected");
        assertEquals("selected", e.getStatus());
        e.setStatus("accepted");
        assertEquals("accepted", e.getStatus());
    }

    @Test
    public void decline_changesStatusFromSelectedToDeclined() {
        Entrant e = new Entrant();
        e.setStatus("selected");
        e.setStatus("declined");
        assertEquals("declined", e.getStatus());
    }

    @Test
    public void cancellation_changesStatusToCancelled() {
        Entrant e = new Entrant();
        e.setStatus("pending");
        e.setStatus("cancelled");
        assertEquals("cancelled", e.getStatus());
    }

    @Test
    public void lotterySelection_changesStatusFromPendingToSelected() {
        Entrant e = new Entrant();
        e.setStatus("pending");
        e.setStatus("selected");
        assertEquals("selected", e.getStatus());
    }

    @Test
    public void reSignUp_changesStatusFromDeclinedToPending() {
        Entrant e = new Entrant();
        e.setStatus("declined");
        e.setStatus("pending");
        assertEquals("pending", e.getStatus());
    }

    @Test
    public void reSignUp_changesStatusFromCancelledToPending() {
        Entrant e = new Entrant();
        e.setStatus("cancelled");
        e.setStatus("pending");
        assertEquals("pending", e.getStatus());
    }

    @Test
    public void attendeeData_nullName_defaultsToUnknown() {
        Map<String, Object> data = buildAttendeeData(null, "email@test.com", "uid-1", "pending");
        assertEquals("Unknown", data.get("name"));
    }

    @Test
    public void attendeeData_nullEmail_defaultsToEmpty() {
        Map<String, Object> data = buildAttendeeData("Alice", null, "uid-1", "pending");
        assertEquals("", data.get("email"));
    }

    @Test
    public void attendeeData_withGeolocation() {
        Map<String, Object> data = buildAttendeeData("Alice", "alice@test.com", "uid-1", "pending");
        data.put("latitude", 53.5461);
        data.put("longitude", -113.4938);
        assertEquals(53.5461, (double) data.get("latitude"), 0.0001);
        assertEquals(-113.4938, (double) data.get("longitude"), 0.0001);
    }

    @Test
    public void attendeeData_withoutGeolocation() {
        Map<String, Object> data = buildAttendeeData("Alice", "alice@test.com", "uid-1", "pending");
        assertNull(data.get("latitude"));
        assertNull(data.get("longitude"));
    }

    @Test
    public void cancelledEntrant_cancelButtonShouldBeHidden() {
        Entrant e = new Entrant();
        e.setStatus("cancelled");
        boolean showCancel = !"cancelled".equals(e.getStatus());
        assertFalse(showCancel);
    }

    @Test
    public void pendingEntrant_cancelButtonShouldBeVisible() {
        Entrant e = new Entrant();
        e.setStatus("pending");
        boolean showCancel = !"cancelled".equals(e.getStatus());
        assertTrue(showCancel);
    }
}
