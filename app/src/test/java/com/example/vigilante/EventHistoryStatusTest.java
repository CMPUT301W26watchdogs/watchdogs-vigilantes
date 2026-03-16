package com.example.vigilante;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class EventHistoryStatusTest {

    private String capitalizeStatus(String status) {
        if (status == null || status.isEmpty()) return status;
        return status.substring(0, 1).toUpperCase() + status.substring(1);
    }

    private Map<String, String> buildHistoryEntry(String title, String date, String status) {
        Map<String, String> entry = new HashMap<>();
        entry.put("title", title);
        entry.put("date", date);
        entry.put("status", status);
        return entry;
    }

    @Test
    public void capitalizeStatus_pending() {
        assertEquals("Pending", capitalizeStatus("pending"));
    }

    @Test
    public void capitalizeStatus_selected() {
        assertEquals("Selected", capitalizeStatus("selected"));
    }

    @Test
    public void capitalizeStatus_accepted() {
        assertEquals("Accepted", capitalizeStatus("accepted"));
    }

    @Test
    public void capitalizeStatus_declined() {
        assertEquals("Declined", capitalizeStatus("declined"));
    }

    @Test
    public void capitalizeStatus_cancelled() {
        assertEquals("Cancelled", capitalizeStatus("cancelled"));
    }

    @Test
    public void capitalizeStatus_emptyString() {
        assertEquals("", capitalizeStatus(""));
    }

    @Test
    public void capitalizeStatus_null() {
        assertNull(capitalizeStatus(null));
    }

    @Test
    public void historyEntry_containsAllFields() {
        Map<String, String> entry = buildHistoryEntry("Swimming", "Mar 15", "selected");
        assertEquals("Swimming", entry.get("title"));
        assertEquals("Mar 15", entry.get("date"));
        assertEquals("selected", entry.get("status"));
    }

    @Test
    public void historyEntry_nullTitle_storesNull() {
        Map<String, String> entry = buildHistoryEntry(null, "Mar 15", "pending");
        assertNull(entry.get("title"));
    }

    @Test
    public void historyEntry_unknownStatus_stillStored() {
        Map<String, String> entry = buildHistoryEntry("Event", "Mar 15", "unknown");
        assertEquals("unknown", entry.get("status"));
        assertEquals("Unknown", capitalizeStatus("unknown"));
    }
}
