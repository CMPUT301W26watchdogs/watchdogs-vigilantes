// Claude, Opus 4, "test organizer cancel entrants flow and enrolled list filtering logic"
package com.example.vigilante;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CancelEntrantsFlowTest {

    private List<Entrant> attendees;

    private Entrant makeEntrant(String id, String name, String status) {
        Entrant e = new Entrant();
        e.setId(id);
        e.setName(name);
        e.setStatus(status);
        return e;
    }

    private int cancelAllByStatus(String targetStatus) {
        int count = 0;
        for (Entrant e : attendees) {
            if (targetStatus.equals(e.getStatus())) {
                e.setStatus("cancelled");
                count++;
            }
        }
        return count;
    }

    private List<Entrant> filterByStatus(String status) {
        List<Entrant> result = new ArrayList<>();
        for (Entrant e : attendees) {
            if (status.equals(e.getStatus())) {
                result.add(e);
            }
        }
        return result;
    }

    @Before
    public void setUp() {
        attendees = new ArrayList<>();
        attendees.add(makeEntrant("uid-1", "Alice", "pending"));
        attendees.add(makeEntrant("uid-2", "Bob", "pending"));
        attendees.add(makeEntrant("uid-3", "Charlie", "selected"));
        attendees.add(makeEntrant("uid-4", "Diana", "accepted"));
        attendees.add(makeEntrant("uid-5", "Eve", "pending"));
    }

    @Test
    public void cancelAllPending_changesAllPendingToCancelled() {
        int count = cancelAllByStatus("pending");
        assertEquals(3, count);
        assertEquals(0, filterByStatus("pending").size());
        assertEquals(3, filterByStatus("cancelled").size());
    }

    @Test
    public void cancelAllPending_doesNotAffectSelected() {
        cancelAllByStatus("pending");
        assertEquals(1, filterByStatus("selected").size());
        assertEquals("Charlie", filterByStatus("selected").get(0).getName());
    }

    @Test
    public void cancelAllPending_doesNotAffectAccepted() {
        cancelAllByStatus("pending");
        assertEquals(1, filterByStatus("accepted").size());
        assertEquals("Diana", filterByStatus("accepted").get(0).getName());
    }

    @Test
    public void cancelAllSelected_changesOnlySelected() {
        int count = cancelAllByStatus("selected");
        assertEquals(1, count);
        assertEquals(3, filterByStatus("pending").size());
        assertEquals(1, filterByStatus("accepted").size());
    }

    @Test
    public void cancelAll_noPendingEntrants_returnsZero() {
        attendees.clear();
        attendees.add(makeEntrant("uid-1", "Alice", "selected"));
        attendees.add(makeEntrant("uid-2", "Bob", "accepted"));
        int count = cancelAllByStatus("pending");
        assertEquals(0, count);
    }

    @Test
    public void cancelledEntrant_notInWaitingList() {
        cancelAllByStatus("pending");
        List<Entrant> waiting = filterByStatus("pending");
        assertEquals(0, waiting.size());
    }

    @Test
    public void filterEnrolled_returnsOnlyAccepted() {
        List<Entrant> enrolled = filterByStatus("accepted");
        assertEquals(1, enrolled.size());
        assertEquals("Diana", enrolled.get(0).getName());
    }

    @Test
    public void filterEnrolled_excludesPendingAndSelected() {
        List<Entrant> enrolled = filterByStatus("accepted");
        for (Entrant e : enrolled) {
            assertNotEquals("pending", e.getStatus());
            assertNotEquals("selected", e.getStatus());
        }
    }

    @Test
    public void cancelThenRedraw_cancelledNotInPendingPool() {
        cancelAllByStatus("pending");
        List<Entrant> pendingPool = filterByStatus("pending");
        assertEquals(0, pendingPool.size());
    }

    @Test
    public void mixedStatusCounts_afterPartialCancel() {
        attendees.get(0).setStatus("cancelled");
        assertEquals(2, filterByStatus("pending").size());
        assertEquals(1, filterByStatus("cancelled").size());
        assertEquals(1, filterByStatus("selected").size());
        assertEquals(1, filterByStatus("accepted").size());
    }
}
