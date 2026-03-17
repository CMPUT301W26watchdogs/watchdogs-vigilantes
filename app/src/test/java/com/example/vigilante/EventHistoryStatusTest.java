// unit tests for event history status formatting — capitalization and history entry structure — US 01.02.03

package com.example.vigilante;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class EventHistoryStatusTest {

    private String capitalizeStatus(String status) {
        // capitalizing the first letter of a status string for display
        if (status == null || status.isEmpty()) return status;
        return status.substring(0, 1).toUpperCase() + status.substring(1);
    }

    private Map<String, String> buildHistoryEntry(String title, String date, String status) {
        // building a history entry map with title, date and status fields
        Map<String, String> entry = new HashMap<>();
        entry.put("title", title);
        entry.put("date", date);
        entry.put("status", status);
        return entry;
    }

    @Test
    public void capitalizeStatus_pending() {
        // verifying "pending" capitalizes to "Pending" — US 01.02.03
        assertEquals("Pending", capitalizeStatus("pending"));
    }

    @Test
    public void capitalizeStatus_selected() {
        // verifying "selected" capitalizes to "Selected" — US 01.02.03
        assertEquals("Selected", capitalizeStatus("selected"));
    }

    @Test
    public void capitalizeStatus_accepted() {
        // verifying "accepted" capitalizes to "Accepted" — US 01.02.03
        assertEquals("Accepted", capitalizeStatus("accepted"));
    }

    @Test
    public void capitalizeStatus_declined() {
        // verifying "declined" capitalizes to "Declined" — US 01.02.03
        assertEquals("Declined", capitalizeStatus("declined"));
    }

    @Test
    public void capitalizeStatus_cancelled() {
        // verifying "cancelled" capitalizes to "Cancelled" — US 01.02.03
        assertEquals("Cancelled", capitalizeStatus("cancelled"));
    }

    @Test
    public void capitalizeStatus_emptyString() {
        // verifying empty string returns empty — US 01.02.03
        assertEquals("", capitalizeStatus(""));
    }

    @Test
    public void capitalizeStatus_null() {
        // verifying null input returns null — US 01.02.03
        assertNull(capitalizeStatus(null));
    }

    @Test
    public void historyEntry_containsAllFields() {
        Map<String, String> entry = buildHistoryEntry("Swimming", "Mar 15", "selected");
        // verifying all fields are correctly stored in the history entry map — US 01.02.03
        assertEquals("Swimming", entry.get("title"));
        assertEquals("Mar 15", entry.get("date"));
        assertEquals("selected", entry.get("status"));
    }

    @Test
    public void historyEntry_nullTitle_storesNull() {
        Map<String, String> entry = buildHistoryEntry(null, "Mar 15", "pending");
        // verifying null title is stored as null in the entry — US 01.02.03
        assertNull(entry.get("title"));
    }

    @Test
    public void historyEntry_unknownStatus_stillStored() {
        Map<String, String> entry = buildHistoryEntry("Event", "Mar 15", "unknown");
        // verifying unknown status is stored and can be capitalized — US 01.02.03
        assertEquals("unknown", entry.get("status"));
        assertEquals("Unknown", capitalizeStatus("unknown"));
    }
}
