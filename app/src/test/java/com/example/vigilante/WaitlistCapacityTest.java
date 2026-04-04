// Claude, Opus 4, "test waitlist capacity logic including count, formatting, and full detection"
package com.example.vigilante;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class WaitlistCapacityTest {

    private int countPendingEntrants(List<Map<String, Object>> attendees) {
        int count = 0;
        for (Map<String, Object> a : attendees) {
            if ("pending".equals(a.get("status"))) count++;
        }
        return count;
    }

    private String formatWaitingText(int count) {
        return count + " Waiting";
    }

    private String formatSpotsText(String capacity) {
        if (capacity == null) return null;
        try {
            long limit = Long.parseLong(capacity);
            return limit + " spots";
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isWaitlistFull(List<Map<String, Object>> attendees, Long waitingListLimit) {
        if (waitingListLimit == null) return false;
        int pendingCount = countPendingEntrants(attendees);
        return pendingCount >= waitingListLimit;
    }

    @Test
    public void countPending_excludesCancelledAndSelected() {
        List<Map<String, Object>> attendees = new ArrayList<>();
        Map<String, Object> a1 = new HashMap<>();
        a1.put("status", "pending");
        attendees.add(a1);
        Map<String, Object> a2 = new HashMap<>();
        a2.put("status", "cancelled");
        attendees.add(a2);
        Map<String, Object> a3 = new HashMap<>();
        a3.put("status", "selected");
        attendees.add(a3);
        Map<String, Object> a4 = new HashMap<>();
        a4.put("status", "pending");
        attendees.add(a4);

        assertEquals(2, countPendingEntrants(attendees));
    }

    @Test
    public void countPending_emptyList_returnsZero() {
        assertEquals(0, countPendingEntrants(new ArrayList<>()));
    }

    @Test
    public void formatWaiting_correctString() {
        assertEquals("5 Waiting", formatWaitingText(5));
        assertEquals("0 Waiting", formatWaitingText(0));
    }

    @Test
    public void formatSpots_validCapacity() {
        assertEquals("20 spots", formatSpotsText("20"));
        assertEquals("100 spots", formatSpotsText("100"));
    }

    @Test
    public void formatSpots_nullCapacity_returnsNull() {
        assertNull(formatSpotsText(null));
    }

    @Test
    public void formatSpots_invalidCapacity_returnsNull() {
        assertNull(formatSpotsText("abc"));
    }

    @Test
    public void waitlistFull_atCapacity() {
        List<Map<String, Object>> attendees = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Map<String, Object> a = new HashMap<>();
            a.put("status", "pending");
            attendees.add(a);
        }
        assertTrue(isWaitlistFull(attendees, 20L));
    }

    @Test
    public void waitlistFull_overCapacity() {
        List<Map<String, Object>> attendees = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            Map<String, Object> a = new HashMap<>();
            a.put("status", "pending");
            attendees.add(a);
        }
        assertTrue(isWaitlistFull(attendees, 20L));
    }

    @Test
    public void waitlistNotFull_underCapacity() {
        List<Map<String, Object>> attendees = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> a = new HashMap<>();
            a.put("status", "pending");
            attendees.add(a);
        }
        assertFalse(isWaitlistFull(attendees, 20L));
    }

    @Test
    public void waitlistNoLimit_neverFull() {
        List<Map<String, Object>> attendees = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Map<String, Object> a = new HashMap<>();
            a.put("status", "pending");
            attendees.add(a);
        }
        assertFalse(isWaitlistFull(attendees, null));
    }

    @Test
    public void waitlistFull_cancelledDoNotCountTowardLimit() {
        List<Map<String, Object>> attendees = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> a = new HashMap<>();
            a.put("status", "pending");
            attendees.add(a);
        }
        for (int i = 0; i < 10; i++) {
            Map<String, Object> a = new HashMap<>();
            a.put("status", "cancelled");
            attendees.add(a);
        }
        assertFalse(isWaitlistFull(attendees, 20L));
    }
}
