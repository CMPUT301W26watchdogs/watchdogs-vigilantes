// testing event history status formatting including capitalization and history entry structure US 01.02.03

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

    // verifying pending capitalizes to Pending US 01.02.03
    @Test
    public void capitalizeStatus_pending() {
        assertEquals("Pending", capitalizeStatus("pending"));
    }

    // verifying selected capitalizes to Selected US 01.02.03
    @Test
    public void capitalizeStatus_selected() {
        assertEquals("Selected", capitalizeStatus("selected"));
    }

    // verifying accepted capitalizes to Accepted US 01.02.03
    @Test
    public void capitalizeStatus_accepted() {
        assertEquals("Accepted", capitalizeStatus("accepted"));
    }

    // verifying declined capitalizes to Declined US 01.02.03
    @Test
    public void capitalizeStatus_declined() {
        assertEquals("Declined", capitalizeStatus("declined"));
    }

    // verifying cancelled capitalizes to Cancelled US 01.02.03
    @Test
    public void capitalizeStatus_cancelled() {
        assertEquals("Cancelled", capitalizeStatus("cancelled"));
    }

    // verifying empty string returns empty US 01.02.03
    @Test
    public void capitalizeStatus_emptyString() {
        assertEquals("", capitalizeStatus(""));
    }

    // verifying null input returns null US 01.02.03
    @Test
    public void capitalizeStatus_null() {
        assertNull(capitalizeStatus(null));
    }

    // verifying all fields are correctly stored in the history entry map US 01.02.03
    @Test
    public void historyEntry_containsAllFields() {
        Map<String, String> entry = buildHistoryEntry("Swimming", "Mar 15", "selected");
        assertEquals("Swimming", entry.get("title"));
        assertEquals("Mar 15", entry.get("date"));
        assertEquals("selected", entry.get("status"));
    }

    // verifying null title is stored as null in the entry US 01.02.03
    @Test
    public void historyEntry_nullTitle_storesNull() {
        Map<String, String> entry = buildHistoryEntry(null, "Mar 15", "pending");
        assertNull(entry.get("title"));
    }

    // verifying unknown status is stored and can be capitalized US 01.02.03
    @Test
    public void historyEntry_unknownStatus_stillStored() {
        Map<String, String> entry = buildHistoryEntry("Event", "Mar 15", "unknown");
        assertEquals("unknown", entry.get("status"));
        assertEquals("Unknown", capitalizeStatus("unknown"));
    }
}
