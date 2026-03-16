// unit tests for enrolled entrants list filtering logic — US 02.06.03

package com.example.vigilante;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class EnrolledListTest {

    private List<Entrant> allAttendees;

    @Before
    // creating test data with mixed statuses
    public void setUp() {
        allAttendees = new ArrayList<>();

        Entrant e1 = new Entrant("1", "Alice", "alice@test.com", "", "accepted");
        Entrant e2 = new Entrant("2", "Bob", "bob@test.com", "", "pending");
        Entrant e3 = new Entrant("3", "Carol", "carol@test.com", "", "accepted");
        Entrant e4 = new Entrant("4", "Dave", "dave@test.com", "", "selected");
        Entrant e5 = new Entrant("5", "Eve", "eve@test.com", "", "cancelled");
        Entrant e6 = new Entrant("6", "Frank", "frank@test.com", "", "accepted");
        Entrant e7 = new Entrant("7", "Grace", "grace@test.com", "", "declined");

        allAttendees.add(e1);
        allAttendees.add(e2);
        allAttendees.add(e3);
        allAttendees.add(e4);
        allAttendees.add(e5);
        allAttendees.add(e6);
        allAttendees.add(e7);
    }

    // filtering entrants by status — same logic used in loadAttendees()
    private List<Entrant> filterByStatus(String status) {
        List<Entrant> filtered = new ArrayList<>();
        for (Entrant e : allAttendees) {
            if (status.equals(e.getStatus())) {
                filtered.add(e);
            }
        }
        return filtered;
    }

    @Test
    // verifying "accepted" filter returns only enrolled entrants — US 02.06.03
    public void filterAccepted_returnsOnlyEnrolled() {
        List<Entrant> enrolled = filterByStatus("accepted");
        assertEquals(3, enrolled.size());
        for (Entrant e : enrolled) {
            assertEquals("accepted", e.getStatus());
        }
    }

    @Test
    // verifying enrolled list contains correct names — US 02.06.03
    public void filterAccepted_containsCorrectNames() {
        List<Entrant> enrolled = filterByStatus("accepted");
        List<String> names = new ArrayList<>();
        for (Entrant e : enrolled) {
            names.add(e.getName());
        }
        assertTrue(names.contains("Alice"));
        assertTrue(names.contains("Carol"));
        assertTrue(names.contains("Frank"));
        assertFalse(names.contains("Bob"));
    }

    @Test
    // verifying enrolled list excludes pending entrants — US 02.06.03
    public void filterAccepted_excludesPending() {
        List<Entrant> enrolled = filterByStatus("accepted");
        for (Entrant e : enrolled) {
            assertNotEquals("pending", e.getStatus());
        }
    }

    @Test
    // verifying enrolled list excludes cancelled and declined entrants — US 02.06.03
    public void filterAccepted_excludesCancelledAndDeclined() {
        List<Entrant> enrolled = filterByStatus("accepted");
        for (Entrant e : enrolled) {
            assertNotEquals("cancelled", e.getStatus());
            assertNotEquals("declined", e.getStatus());
        }
    }

    @Test
    // verifying enrolled list from empty attendee list returns empty — US 02.06.03
    public void filterAccepted_emptyList_returnsEmpty() {
        allAttendees.clear();
        List<Entrant> enrolled = filterByStatus("accepted");
        assertEquals(0, enrolled.size());
    }

    @Test
    // verifying enrolled list when no one accepted returns empty — US 02.06.03
    public void filterAccepted_noAccepted_returnsEmpty() {
        allAttendees.clear();
        allAttendees.add(new Entrant("1", "Bob", "bob@test.com", "", "pending"));
        allAttendees.add(new Entrant("2", "Eve", "eve@test.com", "", "cancelled"));
        List<Entrant> enrolled = filterByStatus("accepted");
        assertEquals(0, enrolled.size());
    }

    @Test
    // verifying all attendees are accepted returns full list — US 02.06.03
    public void filterAccepted_allAccepted_returnsAll() {
        allAttendees.clear();
        for (int i = 0; i < 5; i++) {
            allAttendees.add(new Entrant(String.valueOf(i), "User " + i, "user" + i + "@test.com", "", "accepted"));
        }
        List<Entrant> enrolled = filterByStatus("accepted");
        assertEquals(5, enrolled.size());
    }
}
