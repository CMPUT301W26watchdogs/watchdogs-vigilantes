package com.example.vigilante;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ProfileStatsCalculationTest {

    private List<Map<String, Object>> userAttendeeRecords;

    private Map<String, Object> makeRecord(String eventId, String status) {
        Map<String, Object> record = new HashMap<>();
        record.put("eventId", eventId);
        record.put("status", status);
        return record;
    }

    private int[] calculateStats() {
        int totalEvents = 0;
        int selectedCount = 0;
        int waitingCount = 0;

        for (Map<String, Object> record : userAttendeeRecords) {
            String status = (String) record.get("status");
            totalEvents++;
            if ("selected".equals(status) || "accepted".equals(status)) {
                selectedCount++;
            }
            if ("pending".equals(status)) {
                waitingCount++;
            }
        }
        return new int[]{totalEvents, selectedCount, waitingCount};
    }

    @Before
    public void setUp() {
        userAttendeeRecords = new ArrayList<>();
    }

    @Test
    public void noRecords_allStatsZero() {
        int[] stats = calculateStats();
        assertEquals(0, stats[0]);
        assertEquals(0, stats[1]);
        assertEquals(0, stats[2]);
    }

    @Test
    public void onlyPending_waitingCountMatches() {
        userAttendeeRecords.add(makeRecord("e1", "pending"));
        userAttendeeRecords.add(makeRecord("e2", "pending"));
        userAttendeeRecords.add(makeRecord("e3", "pending"));
        int[] stats = calculateStats();
        assertEquals(3, stats[0]);
        assertEquals(0, stats[1]);
        assertEquals(3, stats[2]);
    }

    @Test
    public void selectedAndAccepted_bothCountAsSelected() {
        userAttendeeRecords.add(makeRecord("e1", "selected"));
        userAttendeeRecords.add(makeRecord("e2", "accepted"));
        int[] stats = calculateStats();
        assertEquals(2, stats[0]);
        assertEquals(2, stats[1]);
        assertEquals(0, stats[2]);
    }

    @Test
    public void mixedStatuses_correctCounts() {
        userAttendeeRecords.add(makeRecord("e1", "pending"));
        userAttendeeRecords.add(makeRecord("e2", "selected"));
        userAttendeeRecords.add(makeRecord("e3", "accepted"));
        userAttendeeRecords.add(makeRecord("e4", "declined"));
        userAttendeeRecords.add(makeRecord("e5", "cancelled"));
        int[] stats = calculateStats();
        assertEquals(5, stats[0]);
        assertEquals(2, stats[1]);
        assertEquals(1, stats[2]);
    }

    @Test
    public void declinedAndCancelled_notCountedAsSelectedOrWaiting() {
        userAttendeeRecords.add(makeRecord("e1", "declined"));
        userAttendeeRecords.add(makeRecord("e2", "cancelled"));
        int[] stats = calculateStats();
        assertEquals(2, stats[0]);
        assertEquals(0, stats[1]);
        assertEquals(0, stats[2]);
    }

    @Test
    public void totalEvents_countsAllStatuses() {
        userAttendeeRecords.add(makeRecord("e1", "pending"));
        userAttendeeRecords.add(makeRecord("e2", "selected"));
        userAttendeeRecords.add(makeRecord("e3", "accepted"));
        userAttendeeRecords.add(makeRecord("e4", "declined"));
        int[] stats = calculateStats();
        assertEquals(4, stats[0]);
    }

    @Test
    public void manyPendingOneSelected_correctRatio() {
        for (int i = 0; i < 10; i++) {
            userAttendeeRecords.add(makeRecord("e" + i, "pending"));
        }
        userAttendeeRecords.add(makeRecord("e10", "selected"));
        int[] stats = calculateStats();
        assertEquals(11, stats[0]);
        assertEquals(1, stats[1]);
        assertEquals(10, stats[2]);
    }
}
